package com.jobmoa.app.CounselMain.view.report;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.function.BiConsumer;

/**
 * 전체 참여자 Excel 다운로드 컨트롤러.
 * 상담사 본인 또는 지점 관리자 권한으로 참여자 전체 목록을 Excel 파일로 내보내는 기능을 제공한다.
 * 기본정보, 희망직무, 자격증, 직업훈련 등 다중 시트를 포함한 Excel을 생성한다.
 */
@Slf4j
@Controller
public class ParticipantAllExcel {
    @Autowired
    private ResourceLoader resourceLoader;  // Spring의 ResourceLoader 주입

    @Autowired
    private ParticipantServiceImpl participantService;

    private byte[] templateBytes2;  // 템플릿 파일을 캐싱할 바이트 배열

    /**
     * 서버 시작 시 Excel 템플릿 파일(template2.xlsx)을 메모리에 캐싱한다.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing Excel template...");
        try (InputStream is = resourceLoader.getResource("classpath:excelTemplates/template2.xlsx").getInputStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            templateBytes2 = baos.toByteArray();
            log.info("Excel template loaded into memory successfully, size: {} bytes", templateBytes2.length);
        } catch (IOException e) {
            log.error("엑셀 템플릿 로드 중 오류 발생", e);
            throw new RuntimeException("Failed to load Excel template during initialization", e);
        }
    }

    /**
     * 참여자 전체 목록 Excel 파일을 다운로드한다.
     * 관리자/지점장 권한 여부에 따라 지점 전체 또는 상담사 본인의 참여자 데이터를 조회한다.
     *
     * @param response                HTTP 응답 (Excel 파일 출력용)
     * @param participantDTO          참여자 조회 조건 DTO
     * @param session                 HTTP 세션 (로그인 및 권한 정보 조회용)
     * @param branchManagementPageFlag 지점 관리 페이지에서 요청한 경우 true
     */
    @GetMapping("/participantExcel.login")
    public void participantExcel(HttpServletResponse response, ParticipantDTO participantDTO, HttpSession session, boolean branchManagementPageFlag){
        //branchManagementPageFlag = 지점 관리자 페이지인지 확인
        try{
            //관리자 권한이 없으면 무조건 상담사 본인 참여자만 다운로드 가능
            String condition = "participantExcel";

            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");

            boolean IS_BRANCH_MANAGER = (boolean) session.getAttribute("IS_BRANCH_MANAGER");
            boolean IS_MANAGER = (boolean) session.getAttribute("IS_MANAGER");
            // 로그인 정보가 없다면 반환
            if (loginBean == null) {
                log.error("세션에 로그인 정보가 없습니다.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 정보가 없습니다.");
                return;
            }
            //세션이 있다면 상담사인지 관리자인지 권한 확인
            else if((IS_MANAGER || IS_BRANCH_MANAGER) && branchManagementPageFlag){
//                condition= "participantBranchExcel"; // TODO sql 쿼리문을 통합하여 제거
                participantDTO.setSearchPath("managerSearch");
            }

            participantDTO.setParticipantCondition(condition);
            participantDTO.setParticipantUserid(loginBean.getMemberUserID());
            participantDTO.setParticipantBranch(loginBean.getMemberBranch());
            log.info("participantExcel.login participantDTO : [{}]",participantDTO);

            //엑셀 생성 함수 실행
            createExcel(response,participantDTO,branchManagementPageFlag);
        }
        catch (Exception e){
            log.error("엑셀 다운로드 처리 중 오류 발생", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "엑셀 파일 생성 중 오류가 발생했습니다: " + e.getMessage());
            }
            catch (IOException ex) {
                log.error("오류 응답 전송 실패", ex);
            }

        }
    }

    /**
     * Excel 템플릿을 기반으로 참여자 데이터를 채워 넣고 다중 시트 Excel 파일을 생성하여 응답에 출력한다.
     * 기본정보, 희망직무, 자격증, 직업훈련 시트를 포함한다.
     *
     * @param response                HTTP 응답 (Excel 파일 출력용)
     * @param participantDTO          참여자 조회 조건 DTO
     * @param branchManagementPageFlag 지점 관리 페이지 여부 (파일명 접두어 결정에 사용)
     */
    private void createExcel(HttpServletResponse response,ParticipantDTO participantDTO, boolean branchManagementPageFlag){
        if (templateBytes2 == null) {
            throw new IllegalStateException("템플릿 파일이 로드되지 않았습니다.");
        }
        // 1. 캐싱된 템플릿(XSSF)을 SXSSF 스트리밍 워크북으로 래핑
        //    - 윈도우 100행만 메모리에 유지하고 초과분은 임시파일로 flush → 행 수와 무관하게 상수 메모리
        //    - 템플릿의 헤더 스타일은 그대로 보존됨
        SXSSFWorkbook workbook = null;
        try {
            workbook = new SXSSFWorkbook(new XSSFWorkbook(new ByteArrayInputStream(templateBytes2)), 100);
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트(기본정보) 사용

            // 기본정보 시트 — 1행부터 스트리밍 기록 (전체 List 미적재)
            streamSheet(participantDTO, "participantExcel", sheet, (row, data) -> setProgress(row, 0, data));

            // 희망직무 시트 생성
            Sheet wishJobSheet = workbook.createSheet("희망직무");
            createSheetHeader(wishJobSheet, new String[]{"구직번호", "상담사명", "참여자명", "카테고리_대", "카테고리_중", "희망직무"});
            streamSheet(participantDTO, "participantExcelWishJob", wishJobSheet, this::writeWishJobRow);

            // 자격증 시트 생성
            Sheet certifSheet = workbook.createSheet("자격증");
            createSheetHeader(certifSheet, new String[]{"구직번호", "상담사명", "참여자명", "자격증명"});
            streamSheet(participantDTO, "participantExcelCertificate", certifSheet, this::writeCertificateRow);

            // 직업훈련 시트 생성
            Sheet trainingSheet = workbook.createSheet("직업훈련");
            createSheetHeader(trainingSheet, new String[]{"구직번호", "상담사명", "참여자명", "직업훈련명"});
            streamSheet(participantDTO, "participantExcelTraining", trainingSheet, this::writeTrainingRow);

            // 수식 평가(evaluateAll) 제거: 템플릿에 수식이 없어 불필요하며, SXSSF는 미지원.

            //지점 전체 참여자 다운 요청이면 앞 부분을 지점으로 변경
            String title = participantDTO.getParticipantBranch();
            if(!branchManagementPageFlag){
                title = participantDTO.getParticipantUserid();
            }

            // 파일 다운로드 설정
            String fileName = URLEncoder.encode(title + "_전체참여자_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        } finally {
            if (workbook != null) {
                workbook.dispose(); // SXSSF 임시파일 정리(필수)
                try {
                    workbook.close();
                } catch (IOException ex) {
                    log.warn("SXSSF 워크북 close 실패", ex);
                }
            }
        }
    }

    /**
     * 지정 조건의 참여자 데이터를 스트리밍 조회하며, 시트의 1행부터 한 건씩 기록한다.
     * 전체 결과를 List로 적재하지 않아 대량 다운로드 시에도 메모리가 상수에 가깝게 유지된다.
     *
     * @param participantDTO 조회 조건 DTO (participantCondition이 condition으로 설정됨)
     * @param condition      MyBatis 매퍼 ID(조회 조건)
     * @param sheet          기록 대상 시트
     * @param writer         행 1건을 기록하는 함수 (Row, 데이터)
     */
    private void streamSheet(ParticipantDTO participantDTO, String condition, Sheet sheet,
                             BiConsumer<Row, ParticipantDTO> writer) {
        participantDTO.setParticipantCondition(condition);
        final int[] rowIdx = {1}; // 0행은 헤더, 데이터는 1행부터
        try {
            participantService.selectAllStream(participantDTO,
                    ctx -> writer.accept(sheet.createRow(rowIdx[0]++), ctx.getResultObject()));
        } catch (Exception e) {
            log.error("엑셀 시트 스트리밍 조회 실패 condition: {}", condition, e);
        }
    }

    /**
     * 셀 값을 설정하며 기존 스타일을 유지한다. null인 경우 0으로 설정한다.
     *
     * @param row         대상 행
     * @param columnIndex 열 인덱스
     * @param value       설정할 값 (String, Integer, Double 또는 null)
     */
    private void setCellValue(Row row, int columnIndex, Object value) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex); // 셀이 없으면 생성
        }
        // 기존 스타일 유지
        CellStyle existingStyle = cell.getCellStyle();
        if (value == null) {
            cell.setCellValue(0); // 0으로 설정
        }
        else if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        else {
            // 다른 타입의 경우 문자열로 변환
            cell.setCellValue(String.valueOf(value));
        }
        // 스타일 재적용
        if (existingStyle != null) {
            cell.setCellStyle(existingStyle);
        }
    }

    /**
     * 참여자 전체 정보를 행에 설정한다.
     * 구직번호부터 참여자 수정일까지 전체 컬럼을 순서대로 출력한다.
     *
     * @param row      대상 행
     * @param colIndex 시작 열 인덱스
     * @param data     참여자 데이터
     */
    private void setProgress(Row row, int colIndex, ParticipantDTO data) {
        setCellValue(row, colIndex++, data.getParticipantJobNo());      // 구직번호
        setCellValue(row, colIndex++, data.getParticipantRegDate());    // 등록일
        setCellValue(row, colIndex++, data.getParticipantUserName());   // 전담자
        setCellValue(row, colIndex++, data.getParticipantUserid()); // 전담자_계정
        setCellValue(row, colIndex++, data.getParticipantPartic());     // 참여자
        setCellValue(row, colIndex++, data.getParticipantDob());        // 생년월일
        setCellValue(row, colIndex++, data.getParticipantGender());     // 성별
        setCellValue(row, colIndex++, data.getParticipantRecruit());    // 모집경로
        setCellValue(row, colIndex++, data.getParticipantPartType());   // 참여유형
        setCellValue(row, colIndex++, data.getParticipantSchool());     // 학교명
        setCellValue(row, colIndex++, data.getParticipantSpecialty());  // 전공
        setCellValue(row, colIndex++, data.getParticipantAddress());    // 주소
        setCellValue(row, colIndex++, data.getParticipantAntecedents()); // 경력
        setCellValue(row, colIndex++, data.getParticipantSpecific());   // 특정계층
        setCellValue(row, colIndex++, data.getParticipantPlacement());  // 알선요청
        setCellValue(row, colIndex++, data.getParticipantJobSkill());   // 취업역량
        setCellValue(row, colIndex++, data.getParticipantLastCons());   // 최근상담일
        setCellValue(row, colIndex++, data.getParticipantProgress());   // 진행단계
        setCellValue(row, colIndex++, data.getParticipantInItCons());   // 초기상담일
        setCellValue(row, colIndex++, data.getParticipantJobEX());      // 구직만료일
        setCellValue(row, colIndex++, data.getParticipantEXPDate());    // 기간만료일
        setCellValue(row, colIndex++, data.getParticipantIAPDate());    // IAP수료일
        setCellValue(row, colIndex++, data.isParticipantISIAP3Month()); // IAP3개월여부
        setCellValue(row, colIndex++, data.getParticipantIAP3Month()); // IAP3개월일자
        setCellValue(row, colIndex++, data.isParticipantISIAP5Month()); // IAP5개월여부
        setCellValue(row, colIndex++, data.getParticipantIAP5Month()); // IAP5개월일자
        setCellValue(row, colIndex++, data.getParticipantAllowanceDate()); // 수당지급일
        setCellValue(row, colIndex++, data.getParticipantJobWant());    // 희망직무
        setCellValue(row, colIndex++, data.getParticipantSalWant());    // 희망급여
        setCellValue(row, colIndex++, data.getParticipantStartDate());  // 취창업일
        setCellValue(row, colIndex++, data.getParticipantProcDate());   // 취창업처리일
        setCellValue(row, colIndex++, data.getParticipantEmpType());    // 취업유형
        setCellValue(row, colIndex++, data.getParticipantLoyer());      // 취업처
        setCellValue(row, colIndex++, data.getParticipantSalary());     // 임금
        setCellValue(row, colIndex++, data.getParticipantJobRole());    // 직무
        setCellValue(row, colIndex++, data.getParticipantIncentive());  // 취업인센티브_구분
        setCellValue(row, colIndex++, data.getParticipantJobcat());     // 일경험분류
        setCellValue(row, colIndex++, data.getParticipantMemo());       // 메모
        setCellValue(row, colIndex++, data.getParticipantOthers());     // 기타
        setCellValue(row, colIndex++, data.isParticipantClose());       // 마감
        setCellValue(row, colIndex++, data.getParticipantQuit());       // 퇴사일
        setCellValue(row, colIndex++, data.getParticipantBranch());     // 지점
        setCellValue(row, colIndex++, data.getParticipantEmploymentService()); // 간접고용서비스
        setCellValue(row, colIndex++, data.getParticipantManagerChangeDate()); // 전담자_변경일
        setCellValue(row, colIndex++, data.getParticipantInitialManagerAccount()); // 초기전담자_계정
        setCellValue(row, colIndex, data.getParticipantModifyDate());   // 참여자_수정일
    }

    /**
     * 텍스트 전용 셀 값 설정 (null을 빈 문자열로 처리)
     */
    private void setStringCellValue(Row row, int columnIndex, Object value) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellValue(value == null ? "" : String.valueOf(value));
    }

    /**
     * 시트 헤더 행 생성
     */
    private void createSheetHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    /**
     * 희망직무 시트 행 1건 기록 (스트리밍)
     */
    private void writeWishJobRow(Row row, ParticipantDTO data) {
        int col = 0;
        setStringCellValue(row, col++, data.getParticipantJobNo());
        setStringCellValue(row, col++, data.getParticipantUserName());
        setStringCellValue(row, col++, data.getParticipantPartic());
        setStringCellValue(row, col++, data.getExcelCategoryLarge());
        setStringCellValue(row, col++, data.getExcelCategoryMid());
        setStringCellValue(row, col, data.getExcelWishJob());
    }

    /**
     * 자격증 시트 행 1건 기록 (스트리밍)
     */
    private void writeCertificateRow(Row row, ParticipantDTO data) {
        int col = 0;
        setStringCellValue(row, col++, data.getParticipantJobNo());
        setStringCellValue(row, col++, data.getParticipantUserName());
        setStringCellValue(row, col++, data.getParticipantPartic());
        setStringCellValue(row, col, data.getExcelCertificateName());
    }

    /**
     * 직업훈련 시트 행 1건 기록 (스트리밍)
     */
    private void writeTrainingRow(Row row, ParticipantDTO data) {
        int col = 0;
        setStringCellValue(row, col++, data.getParticipantJobNo());
        setStringCellValue(row, col++, data.getParticipantUserName());
        setStringCellValue(row, col++, data.getParticipantPartic());
        setStringCellValue(row, col, data.getExcelTrainingName());
    }
}
