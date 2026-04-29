package com.jobmoa.app.recruitmentFormation.biz;

import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentDetailDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentPostingDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentResultDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 고용24 채용공고 서비스 구현체
 *
 * <p><b>호출 흐름:</b>
 * <ul>
 *   <li>{@link #search} — JOB_POSTING DB 조회 (사용자 검색 / 초기 페이지, API 호출 없음)</li>
 *   <li>{@link #fetchAllForSync} — 고용24 API 호출 (스케줄러 전용)</li>
 * </ul>
 *
 * <p>API URL: https://www.work24.go.kr/cm/openApi/call/wk/callOpenApiSvcInfo210L01.do
 */
@Slf4j
@Service("recruitmentService")
public class RecruitmentServiceImpl implements RecruitmentService {

    private static final String API_URL =
            "https://www.work24.go.kr/cm/openApi/call/wk/callOpenApiSvcInfo210L01.do";

    private static final String DETAIL_API_URL =
            "https://www.work24.go.kr/cm/openApi/call/wk/callOpenApiSvcInfo210D01.do";

    /** 스케줄러 1회 동기화 최대 페이지 수 (100건 × 600페이지 = 최대 60,000건) */
    private static final int MAX_SYNC_PAGES = 600;

    /** 1회 스케줄 실행 시 상세 수집 최대 건수 */
    private static final int MAX_DETAIL_PER_SYNC = 60000;

    @Value("${work24.api.key}")
    private String apiKey;

    @Autowired
    private RecruitmentDAO recruitmentDAO;

    private final RestTemplate restTemplate = new RestTemplate();

    // ════════════════════════════════════════════════════════════
    //  사용자 검색 — DB 조회 (API 호출 없음)
    // ════════════════════════════════════════════════════════════

    @Override
    public RecruitmentResultDTO search(RecruitmentSearchDTO dto) {
        // Z(관계없음) 선택 시 career 필터 전체 해제 → SQL WHERE 조건 생략
        if (dto.getCareer() != null) {
            for (String c : dto.getCareer()) {
                if ("Z".equals(c)) { dto.setCareer(null); break; }
            }
        }
        // 00(학력무관) 선택 시 education 필터 전체 해제
        if (dto.getEducation() != null) {
            for (String e : dto.getEducation()) {
                if ("00".equals(e)) { dto.setEducation(null); break; }
            }
        }

        int total = recruitmentDAO.countPostings(dto);
        List<RecruitmentResultDTO.JobItem> items = recruitmentDAO.searchPostings(dto);

        RecruitmentResultDTO result = new RecruitmentResultDTO();
        result.setTotal(total);
        result.setStartPage(dto.getStartPage());
        result.setDisplay(dto.getDisplay());
        result.setWantedInfo(items != null ? items : new ArrayList<>());
        return result;
    }

    // ════════════════════════════════════════════════════════════
    //  스케줄러 전용 — 고용24 API 전체 동기화
    // ════════════════════════════════════════════════════════════

    @Override
    public List<RecruitmentPostingDTO> fetchAllForSync() {
        List<RecruitmentPostingDTO> all = new ArrayList<>();
        int maxPages = MAX_SYNC_PAGES;

        for (int page = 1; page <= maxPages; page++) {
            URI uri = buildSyncUri(page);
            log.debug("동기화 API 호출 page={}: {}", page, uri);

            String xml;
            try {
                xml = fetchXml(uri);
            } catch (Exception e) {
                log.error("동기화 API 호출 실패 page={}: {}", page, e.getMessage());
                break;
            }

            // XML 유효성 사전 검사 — 비정상 응답(HTML 오류 페이지 등) 조기 차단
            if (!isValidXml(xml)) {
                log.error("[API 응답 비정상] page={} | 응답 전체(최대 500자):\n{}",
                        page, xml.substring(0, Math.min(500, xml.length())));
                break;
            }
            log.debug("API 응답 XML 정상 수신 (page={}, 길이={})", page, xml.length());

            List<RecruitmentPostingDTO> pageItems = parseXmlToPosting(xml);
            if (pageItems.isEmpty()) break;

            all.addAll(pageItems);

            if (page == 1) {
                int total = extractTotal(xml);
                int calcPages = (int) Math.ceil((double) total / 100);
                maxPages = Math.min(calcPages, MAX_SYNC_PAGES);
                log.info("동기화 대상 총 {}건, 예상 {}페이지", total, maxPages);
            }
        }

        log.info("fetchAllForSync 완료: 총 {}건 수집", all.size());
        return all;
    }

    // ════════════════════════════════════════════════════════════
    //  상세정보 수집 — 신규 공고(detail_fetched=0)만 처리
    // ════════════════════════════════════════════════════════════

    @Override
    public void fetchDetailForNewPostings() {
        List<String> newIds = recruitmentDAO.selectNewPostingIds(MAX_DETAIL_PER_SYNC);
        if (newIds.isEmpty()) {
            log.info("[상세수집] 신규 공고 없음 — 상세 수집 생략");
            return;
        }
        log.info("[상세수집] 신규 공고 {}건 상세 수집 시작", newIds.size());

        int success = 0, fail = 0;
        for (int i = 0; i < newIds.size(); i++) {
            String wantedAuthNo = newIds.get(i);
            try {
                RecruitmentDetailDTO detail = fetchDetail(wantedAuthNo);
                recruitmentDAO.updateDetailFetched(detail);
                success++;
            } catch (Exception e) {
                log.warn("[상세수집] 실패 wantedAuthNo={}: {}", wantedAuthNo, e.getMessage());
                fail++;
                // detail_fetched=0 유지 → 다음 스케줄에 자동 재시도
            }
            // 100건 처리마다 1초 대기 (고용24 API Rate Limit 방지)
            if ((i + 1) % 100 == 0) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ignored) {}
            }
        }
        log.info("[상세수집] 완료 — 성공: {}건, 실패: {}건", success, fail);
    }

    /**
     * 고용24 상세 API 단건 호출 → RecruitmentDetailDTO 파싱
     */
    private RecruitmentDetailDTO fetchDetail(String wantedAuthNo) throws Exception {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(DETAIL_API_URL)
                .queryParam("authKey",      apiKey)
                .queryParam("callTp",       "D")
                .queryParam("returnType",   "XML")
                .queryParam("infoSvc",      "VALIDATION")
                .queryParam("wantedAuthNo", wantedAuthNo)
                .encode()
                .build()
                .toUri();

        String xml = fetchXml(uri);

        if (!isValidXml(xml)) {
            throw new IllegalStateException("상세 API 비정상 응답 wantedAuthNo=" + wantedAuthNo
                    + " | 응답 앞 200자: " + xml.substring(0, Math.min(200, xml.length())));
        }

        return parseXmlToDetail(wantedAuthNo, xml);
    }

    /**
     * 상세 API XML 파싱 → RecruitmentDetailDTO
     *
     * <p>고용24 상세 API의 실제 XML 태그명이 다를 경우 getText() 호출부 수정 필요.
     */
    private RecruitmentDetailDTO parseXmlToDetail(String wantedAuthNo, String xml) {
        RecruitmentDetailDTO dto = new RecruitmentDetailDTO();
        dto.setWantedAuthNo(wantedAuthNo);

        try {
            Document doc = parseDocument(xml);

            // 기업 정보 영역 (corpInfo)
            dto.setReperNm(getDocText(doc, "reperNm"));
            dto.setTotPsncnt(getDocInt(doc, "totPsncnt"));
            dto.setCapitalAmt(getDocLong(doc, "capitalAmt"));
            dto.setYrSalesAmt(getDocLong(doc, "yrSalesAmt"));
            dto.setBusiCont(getDocText(doc, "busiCont"));
            dto.setHomePg(getDocText(doc, "homePg"));
            dto.setBusiSize(getDocText(doc, "busiSize"));

            // 구인 상세 조건 영역 (wantedInfo)
            dto.setRecrutPeri(getDocText(doc, "recrutPeri"));
            dto.setRecrutPcnt(getDocInt(doc, "recrutPcnt"));
            dto.setJobsNm(getDocText(doc, "jobsNm"));
            dto.setRelJobsNm(getDocText(doc, "relJobsNm"));
            dto.setJobCont(getDocText(doc, "jobCont"));
            dto.setForLang(getDocText(doc, "forLang"));
            dto.setMajor(getDocText(doc, "major"));
            dto.setCertificate(getDocText(doc, "certificate"));
            dto.setMltsvcExcHope(getDocText(doc, "mltsvcExcHope"));
            dto.setCompAbl(getDocText(doc, "compAbl"));
            dto.setPrefCond(getDocText(doc, "pfCond"));  // 우대조건 API 태그명: pfCond

            // 전형 및 접수
            dto.setSelMthd(getDocText(doc, "selMthd"));
            dto.setRcptMthd(getDocText(doc, "rcptMthd"));
            dto.setSubmitDoc(getDocText(doc, "submitDoc"));
            dto.setEtcHopeCont(getDocText(doc, "etcHopeCont"));

            // 근로 조건 및 복리후생
            dto.setNearLine(getDocText(doc, "nearLine"));
            dto.setWorkTimeCont(getDocText(doc, "workTimeCont"));
            dto.setFourIns(getDocText(doc, "fourIns"));
            dto.setRetirePay(getDocText(doc, "retirePay"));
            dto.setWelfareDesc(getDocText(doc, "welfareCont"));
            dto.setDisableCvntl(getDocText(doc, "disableCvntl"));

            // 기타 코드
            dto.setEnterTpCdDetail(getDocText(doc, "enterTpCd"));
            dto.setSalTpCd(getDocText(doc, "salTpCd"));

            // 키워드 목록 — <keyword> 다중 태그를 쉼표로 결합
            NodeList keywordNodes = doc.getElementsByTagName("srchKeywordNm");
            if (keywordNodes.getLength() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < keywordNodes.getLength(); i++) {
                    String text = keywordNodes.item(i).getTextContent().trim();
                    // 텍스트가 비어있지 않은 경우에만 추가 (안전망)
                    if (!text.isEmpty()) {
                        if (sb.length() > 0) sb.append(",");
                        sb.append(text);
                    }
                }
                // 완성된 문자열(예: "자바,스프링,백엔드")을 DTO에 세팅
                dto.setKeywordList(sb.toString());
            }

        } catch (Exception e) {
            log.error("[상세수집] XML 파싱 실패 wantedAuthNo={}: {} | XML 앞 300자: {}",
                    wantedAuthNo, e.getMessage(), xml.substring(0, Math.min(300, xml.length())));
            // 파싱 실패 시 dto는 wantedAuthNo만 설정된 상태 → updateDetailFetched 에서 NULL로 저장
        }
        return dto;
    }

    // ── Document 레벨 텍스트/숫자 추출 유틸 ─────────────────────

    private String getDocText(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return null;
        String val = nodes.item(0).getTextContent();
        return (val != null && !val.isBlank()) ? val.trim() : null;
    }

    private Integer getDocInt(Document doc, String tag) {
        String val = getDocText(doc, tag);
        if (val == null) return null;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return null; }
    }

    private Long getDocLong(Document doc, String tag) {
        String val = getDocText(doc, tag);
        if (val == null) return null;
        try { return Long.parseLong(val); } catch (NumberFormatException e) { return null; }
    }

    // ── API 호출 (바이트 수신 → 인코딩 처리) ────────────────────
    //
    //  RestTemplate.getForObject(uri, String.class)를 사용하면
    //  Content-Type 헤더에 charset 미지정 시 ISO-8859-1로 디코딩.
    //  한국 공공 API 응답의 UTF-8 BOM(0xEF 0xBB 0xBF)이
    //  깨진 문자(ï»¿)로 변환돼 "프롤로그에서는 콘텐츠가 허용되지 않습니다" 오류 발생.
    //
    //  수정: byte[] 로 수신 후 UTF-8 디코딩 → BOM 제거 → XML 파싱
    // ────────────────────────────────────────────────────────────
    private String fetchXml(URI uri) {
        ResponseEntity<byte[]> response = restTemplate.exchange(
                uri, HttpMethod.GET, null, byte[].class);

        byte[] body = response.getBody();
        if (body == null || body.length == 0) return "";

        // Content-Type 헤더의 charset을 우선 사용, 없으면 UTF-8 시도
        Charset charset = StandardCharsets.UTF_8;
        if (response.getHeaders().getContentType() != null
                && response.getHeaders().getContentType().getCharset() != null) {
            charset = response.getHeaders().getContentType().getCharset();
            log.debug("API 응답 charset: {}", charset);
        }

        String xml = new String(body, charset);

        // UTF-8 BOM (U+FEFF) 제거
        if (xml.startsWith("\uFEFF")) {
            xml = xml.substring(1);
            log.debug("UTF-8 BOM 제거 완료");
        }

        return xml.trim();
    }

    /** XML 응답 유효성 검사 — XML 선언 또는 루트 태그로 시작하는지 확인 */
    private boolean isValidXml(String xml) {
        if (xml == null || xml.isBlank()) return false;
        return xml.startsWith("<?xml") || xml.startsWith("<wantedRoot");
    }

    // ── API URI 빌드 (스케줄러 전용) ─────────────────────────────
    private URI buildSyncUri(int page) {
        return UriComponentsBuilder
                .fromHttpUrl(API_URL)
                .queryParam("authKey",    apiKey)
                .queryParam("callTp",     "L")
                .queryParam("returnType", "XML")
                .queryParam("startPage",  page)
                .queryParam("display",    100)
                .encode()
                .build()
                .toUri();
    }

    // ── XML 파싱 — DB 저장용 ─────────────────────────────────────

    private List<RecruitmentPostingDTO> parseXmlToPosting(String xml) {
        List<RecruitmentPostingDTO> list = new ArrayList<>();
        if (xml == null || xml.isBlank()) return list;

        try {
            Document doc = parseDocument(xml);
            NodeList nodes = doc.getElementsByTagName("wanted");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                RecruitmentPostingDTO dto = new RecruitmentPostingDTO();
                dto.setWantedAuthNo(getText(el, "wantedAuthNo"));
                dto.setCompanyNm(getText(el, "company"));
                dto.setBizRegNo(getText(el, "busino"));
                dto.setIndTpNm(getText(el, "indTpNm"));
                dto.setRecrutTitle(getText(el, "title"));

                // 직종: 코드만 제공, 직종명(jobsNm) 태그 없음 → 빈 문자열 저장
                Integer jobsCd = getIntEl(el, "jobsCd");
                dto.setJobsCd(jobsCd);
                dto.setJobsNm("");

                // 고용형태: 코드만 제공, 명칭 태그 없음 → 코드로 직접 매핑
                Integer empTpCd = getIntEl(el, "empTpCd");
                dto.setEmpTpCd(empTpCd);
                dto.setEmpTpNm(mapEmpTpNm(empTpCd));

                dto.setSalTpNm(getText(el, "salTpNm"));
                dto.setSalDesc(getText(el, "sal"));
                dto.setMinSal(getIntEl(el, "minSal"));
                dto.setMaxSal(getIntEl(el, "maxSal"));
                dto.setMinEdubg(getText(el, "minEdubg"));
                dto.setMaxEdubg(getText(el, "maxEdubg"));
                dto.setCareer(getText(el, "career"));
                dto.setHolidayTpNm(getText(el, "holidayTpNm"));
                dto.setRegionNm(getText(el, "region"));
                dto.setZipCd(getText(el, "zipCd"));
                dto.setStrtNmAddr(getText(el, "strtnmCd"));
                dto.setBasicAddr(getText(el, "basicAddr"));
                dto.setDetailAddr(getText(el, "detailAddr"));

                // ── 날짜 정규화 (API 응답이 DB 컬럼 타입과 다른 형식) ──────
                // regDt:      "26-03-30"          → "2026-03-30"   (YYYY-MM-DD)
                // closeDt:    "26-04-14"           → "2026-04-14"
                //             "채용시까지  26-04-17" → "2026-04-17"  (날짜 부분 추출)
                //             "채용시까지"           → null
                // smodifyDtm: "202603300950"       → "2026-03-30 09:50:00" (YYYYMMDDHHmm)
                dto.setRegDt(normalizeRegDt(getText(el, "regDt")));
                dto.setCloseDt(normalizeCloseDt(getText(el, "closeDt")));
                dto.setSmodifyDtm(normalizeSmodifyDtm(getText(el, "smodifyDtm")));

                dto.setInfoSvc(getText(el, "infoSvc"));
                dto.setWantedInfoUrl(getText(el, "wantedInfoUrl"));
                dto.setMobileInfoUrl(getText(el, "wantedMobileInfoUrl"));
                list.add(dto);
            }
        } catch (Exception e) {
            log.error("DB 저장용 XML 파싱 실패: {} | 응답 앞 500자: {}",
                    e.getMessage(), xml.substring(0, Math.min(500, xml.length())));
        }
        return list;
    }

    // ── 날짜 정규화 유틸 ──────────────────────────────────────────

    private static final Pattern YY_MM_DD = Pattern.compile("(\\d{2})-(\\d{2})-(\\d{2})");

    /**
     * API regDt 정규화: "26-03-30" → "2026-03-30"
     * 형식이 맞지 않으면 null 반환 (DB DATE 컬럼에 NULL 저장)
     */
    private String normalizeRegDt(String raw) {
        if (raw == null || raw.isBlank()) return null;
        raw = raw.trim();
        Matcher m = YY_MM_DD.matcher(raw);
        if (m.find()) {
            return "20" + m.group(1) + "-" + m.group(2) + "-" + m.group(3);
        }
        log.warn("regDt 변환 불가: '{}'", raw);
        return null;
    }

    /**
     * API closeDt 정규화
     * "26-04-14"            → "2026-04-14"
     * "채용시까지  26-04-17" → "2026-04-17" (날짜만 추출)
     * "채용시까지"           → null         (날짜 없음 → DB NULL)
     */
    private String normalizeCloseDt(String raw) {
        if (raw == null || raw.isBlank()) return null;
        Matcher m = YY_MM_DD.matcher(raw);
        if (m.find()) {
            return "20" + m.group(1) + "-" + m.group(2) + "-" + m.group(3);
        }
        // 날짜 패턴 없음 (e.g. "채용시까지") → null
        return null;
    }

    /**
     * API smodifyDtm 정규화: "202603300950" (YYYYMMDDHHmm, 12자리) → "2026-03-30 09:50:00"
     * 형식이 맞지 않으면 null 반환 (DB DATETIME 컬럼에 NULL 저장)
     */
    private String normalizeSmodifyDtm(String raw) {
        if (raw == null || raw.isBlank()) return null;
        raw = raw.trim();
        if (raw.matches("\\d{12}")) {
            return raw.substring(0, 4) + "-" + raw.substring(4, 6) + "-" + raw.substring(6, 8)
                 + " " + raw.substring(8, 10) + ":" + raw.substring(10, 12) + ":00";
        }
        log.warn("smodifyDtm 변환 불가: '{}'", raw);
        return null;
    }

    /**
     * empTpCd(고용형태 코드) → empTpNm(고용형태명) 매핑
     * API 응답에 empTpNm 태그가 없으므로 코드에서 직접 변환
     */
    private String mapEmpTpNm(Integer empTpCd) {
        if (empTpCd == null) return "";
        switch (empTpCd) {
            case 4:  return "파견근로";
            case 10: return "기간의 정함이 없는 근로계약";
            case 11: return "기간의 정함이 없는 근로계약(시간선택제)";
            case 20: return "기간의 정함이 있는 근로계약";
            case 21: return "기간의 정함이 있는 근로계약(시간선택제)";
            default: return "";
        }
    }

    private int extractTotal(String xml) {
        try { return getIntText(parseDocument(xml), "total"); }
        catch (Exception e) { return 0; }
    }

    // ── 공통 유틸 ────────────────────────────────────────────────

    private Document parseDocument(String xml) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    private String getText(Element el, String tag) {
        NodeList nodes = el.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "";
        String val = nodes.item(0).getTextContent();
        return val != null ? val.trim() : "";
    }

    private int getIntText(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return 0;
        try { return Integer.parseInt(nodes.item(0).getTextContent().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private Integer getIntEl(Element el, String tag) {
        String val = getText(el, tag);
        if (val.isEmpty()) return null;
        try { return Integer.parseInt(val); }
        catch (NumberFormatException e) { return null; }
    }
}