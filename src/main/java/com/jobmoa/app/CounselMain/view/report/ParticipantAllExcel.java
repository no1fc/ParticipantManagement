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
import java.util.List;

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
        // 1. 캐싱된 템플릿을 메모리에서 로드
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(templateBytes2))) {
            if (templateBytes2 == null) {
                throw new IllegalStateException("템플릿 파일이 로드되지 않았습니다.");
            }
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 사용
            List<ParticipantDTO> datas = null;
            try{
                datas = participantService.selectAll(participantDTO);
            } catch (Exception e){
                log.error("참여자 데이터 조회 실패: {}", e.getMessage());
                datas = List.of(); // 빈 리스트 할당
            }

            if (datas == null) {
                log.warn("조회된 참여자 데이터가 없습니다. 빈 목록으로 처리합니다.");
                datas = List.of(); // null 대신 빈 리스트 사용
            }

            createRow(sheet,1,datas);

            // 희망직무 시트 생성
            List<ParticipantDTO> wishJobDatas = selectExcelSheetData(participantDTO, "participantExcelWishJob");
            Sheet wishJobSheet = workbook.createSheet("희망직무");
            createSheetHeader(wishJobSheet, new String[]{"구직번호", "상담사명", "참여자명", "카테고리_대", "카테고리_중", "희망직무"});
            createWishJobRows(wishJobSheet, wishJobDatas);

            // 자격증 시트 생성
            List<ParticipantDTO> certifDatas = selectExcelSheetData(participantDTO, "participantExcelCertificate");
            Sheet certifSheet = workbook.createSheet("자격증");
            createSheetHeader(certifSheet, new String[]{"구직번호", "상담사명", "참여자명", "자격증명"});
            createCertificateRows(certifSheet, certifDatas);

            // 직업훈련 시트 생성
            List<ParticipantDTO> trainingDatas = selectExcelSheetData(participantDTO, "participantExcelTraining");
            Sheet trainingSheet = workbook.createSheet("직업훈련");
            createSheetHeader(trainingSheet, new String[]{"구직번호", "상담사명", "참여자명", "직업훈련명"});
            createTrainingRows(trainingSheet, trainingDatas);

            // 수식이 포함된 셀들의 계산을 실행
            // 1. 워크북의 CreationHelper를 얻어옴
            // 2. FormulaEvaluator 생성
            // 3. evaluateAll()로 모든 수식을 재계산
            workbook.getCreationHelper()
                    .createFormulaEvaluator()
                    .evaluateAll();

            /*
             * 수식 계산 방법은 크게 두 가지가 있습니다:
             * 1. evaluateAll(): 워크북의 모든 수식을 한 번에 계산
             * 2. evaluate(Cell): 특정 셀의 수식만 계산
             *
             * evaluateAll()은 다음과 같은 경우에 유용합니다:
             * - 여러 시트에 걸쳐 수식이 있는 경우
             * - 수식들이 서로 연관되어 있는 경우
             * - 수식이 많은 경우 한 번에 처리 가능
             */

            //지점 전체 참여자 다운 요청이면 앞 부분을 지점으로 변경
            String title = participantDTO.getParticipantBranch();
            if(!branchManagementPageFlag){
                title = participantDTO.getParticipantUserid();
            }

            // 4. 파일 다운로드 설정
            String fileName = URLEncoder.encode(title + "_전체참여자_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 참여자 기본정보 데이터를 시트의 지정된 시작 행부터 채워 넣는다.
     *
     * @param sheet    대상 시트
     * @param startRow 데이터 시작 행 번호
     * @param datas    참여자 데이터 목록
     */
    private void createRow(Sheet sheet, int startRow, List<ParticipantDTO> datas) {
        // 2. startRow 데이터 시작 위치 설정 (템플릿에 따라 조정)
        // 3. 데이터 채우기

        // null 체크 추가
        if (datas == null || datas.isEmpty()) {
            log.warn("처리할 데이터가 없습니다.");
            return; // 데이터가 없으면 메소드 종료
        }

        for (ParticipantDTO data : datas) {
            //row 값 가져오기
            Row row = setRowValue(sheet,startRow);

            //행을 초기화 하여 값을 출력
            int colIndex = 0;
            setProgress(row,colIndex,data);
            startRow++;
        }
    }

    /**
     * 시트에서 지정된 행을 반환하며, 존재하지 않으면 새로 생성한다.
     *
     * @param sheet    대상 시트
     * @param startRow 행 번호
     * @return 해당 행 객체
     */
    private Row setRowValue(Sheet sheet, int startRow) {
        Row row = sheet.getRow(startRow); // 기존 행 가져오기
        if (row == null) {
            row = sheet.createRow(startRow); // 행이 없으면 새로 생성
        }
        return row;
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
     * 엑셀 시트 데이터 조회 helper
     * participantCondition을 바꿔서 기존 selectAll을 재사용한다.
     */
    private List<ParticipantDTO> selectExcelSheetData(ParticipantDTO participantDTO, String condition) {
        participantDTO.setParticipantCondition(condition);
        try {
            List<ParticipantDTO> datas = participantService.selectAll(participantDTO);
            return datas == null ? List.of() : datas;
        } catch (Exception e) {
            log.error("엑셀 시트 데이터 조회 실패 condition: {}", condition, e);
            return List.of();
        }
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
     * 희망직무 시트 행 생성
     */
    private void createWishJobRows(Sheet sheet, List<ParticipantDTO> datas) {
        if (datas == null || datas.isEmpty()) return;
        int rowIndex = 1;
        for (ParticipantDTO data : datas) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;
            setStringCellValue(row, col++, data.getParticipantJobNo());
            setStringCellValue(row, col++, data.getParticipantUserName());
            setStringCellValue(row, col++, data.getParticipantPartic());
            setStringCellValue(row, col++, data.getExcelCategoryLarge());
            setStringCellValue(row, col++, data.getExcelCategoryMid());
            setStringCellValue(row, col, data.getExcelWishJob());
        }
    }

    /**
     * 자격증 시트 행 생성
     */
    private void createCertificateRows(Sheet sheet, List<ParticipantDTO> datas) {
        if (datas == null || datas.isEmpty()) return;
        int rowIndex = 1;
        for (ParticipantDTO data : datas) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;
            setStringCellValue(row, col++, data.getParticipantJobNo());
            setStringCellValue(row, col++, data.getParticipantUserName());
            setStringCellValue(row, col++, data.getParticipantPartic());
            setStringCellValue(row, col, data.getExcelCertificateName());
        }
    }

    /**
     * 직업훈련 시트 행 생성
     */
    private void createTrainingRows(Sheet sheet, List<ParticipantDTO> datas) {
        if (datas == null || datas.isEmpty()) return;
        int rowIndex = 1;
        for (ParticipantDTO data : datas) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;
            setStringCellValue(row, col++, data.getParticipantJobNo());
            setStringCellValue(row, col++, data.getParticipantUserName());
            setStringCellValue(row, col++, data.getParticipantPartic());
            setStringCellValue(row, col, data.getExcelTrainingName());
        }
    }
}
