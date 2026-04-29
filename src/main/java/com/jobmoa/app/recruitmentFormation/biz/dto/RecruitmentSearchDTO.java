package com.jobmoa.app.recruitmentFormation.biz.dto;

import lombok.Data;

/**
 * 고용24 채용공고 검색 요청 파라미터 DTO
 * JSP(index.jsp) → /recruitmentInformation/search 로 전달되는 파라미터와 1:1 매핑
 * 파라미터명은 고용24 Open API 파라미터명을 그대로 사용
 */
@Data
public class RecruitmentSearchDTO {

    // ── 필수 ──────────────────────────────────────────────
    private int startPage = 1;        // 시작 페이지 (기본값 1)
    private int display   = 10;       // 페이지당 건수 (기본값 10, 최대 100)

    // ── 지역 / 직종 (다중) ────────────────────────────────
    private String[] region;          // 근무지역 텍스트 (region_nm LIKE 검색용, 최대 5개)
    private String[] occupation;      // 직종 코드 (최대 10개)

    // ── 고용형태 / 급여 ───────────────────────────────────
    private String  empTp;            // 고용형태 (4/10/11/20/21/Y)
    private String  salTp;            // 임금형태 (D/H/M/Y)
    private Integer minPay;           // 최소급여 (salTp 입력 시 필수)
    private Integer maxPay;           // 최대급여 (salTp 입력 시 필수)

    // ── 학력 / 경력 ───────────────────────────────────────
    private String[] education;       // 학력 코드 (00~07, 다중)
    private String[] career;          // 경력 코드 (N/E/Z)

    // ── 근무형태 / 부가조건 ──────────────────────────────
    private String   holidayTp;       // 근무형태 (1=주5일/2=주6일/3=격주/9=기타)
    private String   regDate;         // 등록일 기준 (D-0/W-1/W-2/M-1)

    // ── 정렬 / 키워드 ─────────────────────────────────────
    private String   sortOrderBy = "DESC"; // 정렬방식 (DESC=최신순, ASC=오래된순)
    private String   keyword;         // 키워드 (쉼표 구분 다중)

    // ── MyBatis OGNL 헬퍼 (test="isCareerFiltered()") ────
    /**
     * career 배열에 Z(관계없음)가 없고 필터링 값이 있을 때만 true 반환.
     * Z 포함 = 전체 조회 → SQL 필터 적용 안 함.
     */
    public boolean isCareerFiltered() {
        if (career == null || career.length == 0) return false;
        for (String c : career) {
            if ("Z".equals(c)) return false;
        }
        return true;
    }

    /**
     * education 배열에 00(학력무관)이 없고 필터링 값이 있을 때만 true 반환.
     * 00 포함 = 전체 조회 → SQL 필터 적용 안 함.
     */
    public boolean isEducationFiltered() {
        if (education == null || education.length == 0) return false;
        for (String e : education) {
            if ("00".equals(e)) return false;
        }
        return true;
    }
}