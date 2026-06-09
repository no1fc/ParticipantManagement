package com.jobmoa.app.CounselMain.view.report;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardService;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberServiceImpl;
import com.jobmoa.app.CounselMain.biz.report.ReportDTO;
import com.jobmoa.app.CounselMain.biz.report.ReportServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 일일업무보고서 컨트롤러.
 * 상담사별 일일업무보고 페이지 표시 및 Excel 템플릿 기반의 일일보고서 다운로드 기능을 제공한다.
 * 서버 시작 시 Excel 템플릿 파일을 메모리에 캐싱하여 반복 요청 시 성능을 최적화한다.
 */
@Slf4j
@Controller
public class DailyWorkReport3 {

    @Autowired
    private ResourceLoader resourceLoader;  // Spring의 ResourceLoader 주입

    @Autowired
    private ReportServiceImpl reportService;

    @Autowired
    private DashboardService dashboardService;

    private byte[] templateBytes;  // 템플릿 파일을 캐싱할 바이트 배열

    /**
     * 서버 시작 시 Excel 템플릿 파일(template.xlsx)을 메모리에 캐싱한다.
     * 매 요청마다 파일을 읽지 않고 캐싱된 바이트 배열을 사용하여 성능을 향상시킨다.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing Excel template...");
        try (InputStream is = resourceLoader.getResource("classpath:excelTemplates/template.xlsx").getInputStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            templateBytes = baos.toByteArray();
            log.info("Excel template loaded into memory successfully, size: {} bytes", templateBytes.length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Excel template during initialization", e);
        }
    }

    /**
     * 일일업무보고 페이지를 표시한다.
     * 지점 내 상담사별 보고 데이터와 취업률/알선취업률/총점 대시보드 점수를 조회하여 뷰에 전달한다.
     *
     * @param model        뷰에 전달할 모델 객체
     * @param reportDTO    보고서 조회 조건 DTO
     * @param dashboardDTO 대시보드 점수 조회 조건 DTO
     * @param session      HTTP 세션 (로그인 정보 조회용)
     * @return JSP 뷰 이름 ("views/DailyWorkReportPage")
     */
    @GetMapping("report.login")
    public String reportPage(Model model, ReportDTO reportDTO, DashboardDTO dashboardDTO, HttpSession session) {
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");

        String branch = loginBean.getMemberBranch();

        //일일업무보고 상담사별 조회
        reportDTO.setReportCondition("dailyReportSelect");
        reportDTO.setBranch(branch);
        List<ReportDTO> reportDTOList = reportService.selectAll(reportDTO);

        log.info("reportDTOList : [{}]",reportDTOList);
        model.addAttribute("users", reportDTOList);

        /*<!-- todo 수정해야함 -->
            DashboardDTO에서 취업률, 알선취업률, 총점 3가지 데이터를 조회
            조회 하고 각 데이터를 View로 전달
            조회 전달전 NullPointException 처리
            조회 후 Null 값 및 빈값에 대한 처리
        */
        dashboardDTO.setDashboardCondition("selectReportDashboard");

        // 연도 설정
        // 연도 정보가 없을 경우 현재 연도를 기본값으로 설정
        int year = LocalDate.now().getYear();
        String startDate = String.format("%d-11-01", (year-1));
        String endDate = String.format("%d-10-31", year);

        dashboardDTO.setDashboardBranch(branch);
        dashboardDTO.setDashBoardStartDate(startDate);
        dashboardDTO.setDashBoardEndDate(endDate);

        DashboardDTO score = dashboardService.selectOne(dashboardDTO);
        if(score == null){
            score = dashboardDTO;
            score.setEmploymentRate(0);
            score.setPlacementRate(0);
            score.setTotalScore(0);
        }
        model.addAttribute("rateScore", score);


        return "views/DailyWorkReportPage";
    }

    /**
     * 일일업무보고서 Excel 파일을 다운로드한다.
     * 선택된 상담사 ID 목록을 기반으로 보고서 데이터를 조회하여 Excel 파일로 생성한다.
     *
     * @param response  HTTP 응답 (Excel 파일 출력용)
     * @param reportDTO 보고서 조회 조건 (상담사 ID 목록, 연도, 지점 등)
     * @return 오류 발생 시 보고서 페이지로 리다이렉트, 성공 시 null
     */
    @PostMapping("downloadExcel.login")
    public String downloadExcel(HttpServletResponse response, ReportDTO reportDTO){
        log.info("createExcel reportDTO UserIds : [{}]", (Object) reportDTO.getUserIds());
        try{
            //엑셀 파일 다운로드 시작
            createExcel(response,reportDTO);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return "redirect:report.login";
        }

        return null;
    }

    /**
     * Excel 템플릿을 기반으로 일일보고서 데이터를 채워 넣고 파일을 응답에 출력한다.
     * 배정 인원, 누적 실적, 민간위탁기관 평가 실적, 참여자 진행현황 등 여러 섹션을 구성한다.
     *
     * @param response  HTTP 응답 (Excel 파일 출력용)
     * @param reportDTO 보고서 조회 조건 DTO
     */
    private void createExcel(HttpServletResponse response, ReportDTO reportDTO){
        // 1. 캐싱된 템플릿을 메모리에서 로드
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(templateBytes))) {
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 사용
            String yearStr = reportDTO.getYear();


            // 연도 정보가 없을 경우 현재 연도를 기본값으로 설정
            int year;
            if (yearStr == null || yearStr.trim().isEmpty()) {
                year = LocalDate.now().getYear();
                reportDTO.setYear(String.valueOf(year));
            } else {
                year = Integer.parseInt(yearStr);
            }

            String branch = reportDTO.getBranch() == null ?"":reportDTO.getBranch();
            //데이터 조회 시작일
            reportDTO.setStartDate((year-1)+"-11-01");
            // 조회 마지막 년도를 일일보고 일자로 변경
            reportDTO.setEndDate(reportDTO.getDailyUpdateStatusDate());
            //데이터 조회 종료일
//            reportDTO.setEndDate("2025-10-31");

            //상단 일일보고 제목을 지정
            setCellValue(setRowValue(sheet,0), 0, branch+"지점 국민취업지원제도 업무진행현황 일일보고");
            log.info("createExcel branch : [{}]",branch);

            setCellValue(setRowValue(sheet,4), 0, year+"년 총 서비스 대상 인원(전산 배정 인원)");
            //배정 인원
            reportDTO.setReportCondition("reportSelectAssignOne");
            ReportDTO assignOne = reportService.selectOne(reportDTO);
            if(assignOne == null){
                assignOne = reportDTO;
            }
            setCellValue(setRowValue(sheet,8),2,assignOne.getType1());
            setCellValue(setRowValue(sheet,8),4,assignOne.getType2());
            log.info("createExcel assignOne : [{}],[{}]",assignOne.getType1(), assignOne.getType2());

            //금일 배정 인원
            reportDTO.setReportCondition("dailyBranchReportSelect");
            log.info("createExcel dailyUpdateStatusDate : [{}]",reportDTO.getDailyUpdateStatusDate());
            ReportDTO dailyAssignOne = reportService.selectOne(reportDTO);
            if(dailyAssignOne == null){
                dailyAssignOne = reportDTO;
            }
            setCellValue(setRowValue(sheet,8),6,dailyAssignOne.getBranchType1());
            setCellValue(setRowValue(sheet,8),8,dailyAssignOne.getBranchType2());
            log.info("createExcel dailyAssignOne : [{}],[{}]",dailyAssignOne.getBranchType1(), dailyAssignOne.getBranchType2());

            //누적 실적
            reportDTO.setReportCondition("reportSelectPerformanceAll");
            List<ReportDTO> PerformanceAll = reportService.selectAll(reportDTO);
            createRow(sheet,12,PerformanceAll,reportDTO.getReportCondition());

            //민간위탁기관 평가 실적
            //제목을 지정
            setCellValue(setRowValue(sheet,24), 1, year+"년 민간위탁기관 평가 실적");
            //요청 년도 참여자 생성
            reportDTO.setReportCondition("reportSelectStatusAll");
            List<ReportDTO> datas = reportService.selectAll(reportDTO);
            log.info("createExcel datas : [{}]",datas);
            createRow(sheet,27,datas,reportDTO.getReportCondition());


            //요청 년도의 참여자 진행현황을 출력
            //제목을 지정
            setCellValue(setRowValue(sheet,37), 1, year+"년 참여자 진행현황 (국민취업지원제도)");
            //요청 년도 참여자 생성
            reportDTO.setReportCondition("reportSelectProgressAll");
            datas = reportService.selectAll(reportDTO);
            log.info("createExcel datas : [{}]",datas);
            createRow(sheet,40,datas,reportDTO.getReportCondition());

            //요청 이전 년도의 참여자 진행현황을 출력
            //제목을 지정
            setCellValue(setRowValue(sheet,50), 1, (year-1)+"년 참여자 진행현황 (국민취업지원제도)");
            //이전 년도 생성해서 추가
            reportDTO.setYear((year-1)+"");
            //요청 년도 참여자 생성
            reportDTO.setReportCondition("reportSelectProgressAll");
            datas = reportService.selectAll(reportDTO);
            log.info("createExcel datas : [{}]",datas);
            createRow(sheet,53,datas,reportDTO.getReportCondition());

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

            // 4. 파일 다운로드 설정
            String fileName = URLEncoder.encode(branch + "_일일보고서_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 조건에 따라 시트의 지정된 시작 행부터 데이터 행을 생성한다.
     *
     * @param sheet    대상 시트
     * @param startRow 데이터 시작 행 번호
     * @param datas    출력할 데이터 목록
     * @param condition 데이터 유형 구분 (reportSelectStatusAll, reportSelectProgressAll, reportSelectPerformanceAll)
     */
    private void createRow(Sheet sheet, int startRow, List<ReportDTO> datas, String condition) {
        log.info("createRow Start... : [{}]",condition);
        // 2. startRow 데이터 시작 위치 설정 (템플릿에 따라 조정)
        // 3. 데이터 채우기
        if(condition.equals("reportSelectStatusAll")){
            for (ReportDTO data : datas) {
                //row 값 가져오기
                Row row = setRowValue(sheet,startRow);

                //행을 초기화 하여 값을 출력
                int colIndex = 0;
                setStatus(row,colIndex,data);
                startRow++;
            }
        }
        else if(condition.equals("reportSelectProgressAll")){
            for (ReportDTO data : datas) {
                //row 값 가져오기
                Row row = setRowValue(sheet,startRow);

                //행을 초기화 하여 값을 출력
                int colIndex = 0;
                setProgress(row,colIndex,data);
                startRow++;
            }
        }
        else if(condition.equals("reportSelectPerformanceAll")){
            for (ReportDTO data : datas) {
                //row 값 가져오기
                Row row = setRowValue(sheet,startRow);

                //행을 초기화 하여 값을 출력
                int colIndex = 0;
                setPerformance(row,colIndex,data);
                log.info("setPerformance data : [{}]",data);
                startRow++;
            }
        }
        log.info("createRow End... : [{}]",condition);

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
     * 셀 값을 설정하며 기존 스타일을 유지한다.
     *
     * @param row         대상 행
     * @param columnIndex 열 인덱스
     * @param value       설정할 값 (String, Integer, Double)
     */
    private void setCellValue(Row row, int columnIndex, Object value) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex); // 셀이 없으면 생성
        }
        // 기존 스타일 유지
        CellStyle existingStyle = cell.getCellStyle();
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        // 스타일 재적용
        if (existingStyle != null) {
            cell.setCellStyle(existingStyle);
        }
    }

    /**
     * 참여자 진행현황 데이터를 행에 설정한다.
     *
     * @param row      대상 행
     * @param colIndex 시작 열 인덱스
     * @param data     진행현황 데이터
     */
    private void setProgress(Row row, int colIndex, ReportDTO data){
        setCellValue(row, colIndex++, data.getCounselorName());        // 이름
        setCellValue(row, colIndex++, data.getCancelCount());          // 취소자
        setCellValue(row, colIndex++, data.getTotalParticipants());    // 참여자 합계
        setCellValue(row, colIndex++, data.getCurrentProgress());      // 현진행자
        setCellValue(row, colIndex++, data.getTotalEmployment());     // 취업자로 변경
        setCellValue(row, colIndex++, data.getCompletedCount());      // 기간만료
        setCellValue(row, colIndex++, data.getDiscontinuedCount());   // 중단
        setCellValue(row, colIndex++, data.getNonApprovedEmployment()); // 성과 불인정 취업
        setCellValue(row, colIndex++, data.getApprovedEmployment());   // 성과 인정 취업
        setCellValue(row, colIndex++, data.getReferralEmployment());   // 알선취업
        setCellValue(row, colIndex++, data.getEvaluationEmploymentRate()); // 평가 취업률
        setCellValue(row, colIndex++, data.getReferralEmploymentRate());   // 알선취업률
        setCellValue(row, colIndex++, data.getBetterJobRate());           // 229만원 이상 취업률
        setCellValue(row, colIndex++, data.getRetentionRate());           // 고용유지율
        setCellValue(row, colIndex++, data.getEarlyEmploymentRate());     // 조기취업실적
        setCellValue(row, colIndex++, data.getIncentiveOccurrenceRate()); // 취업인센티브 발생률
        setCellValue(row, colIndex, data.getIncentiveNotOccurred());    // 취업인센티브 미발생자
    }
    /**
     * 민간위탁기관 평가 실적 데이터를 행에 설정한다.
     *
     * @param row      대상 행
     * @param colIndex 시작 열 인덱스
     * @param data     평가 실적 데이터
     */
    private void setStatus(Row row, int colIndex, ReportDTO data){
        setCellValue(row, colIndex++, data.getCounselorName());        // 이름
        setCellValue(row, colIndex++, data.getCompletedCount());          // 종료참여자
        setCellValue(row, colIndex++, data.getTotalEmployment());         // 취업자
        setCellValue(row, colIndex++, data.getReferralEmployment());      // 알선취업
        setCellValue(row, colIndex++, data.getApprovedEmployment());      // 성과 인정 취업
        setCellValue(row, colIndex++, data.getNonApprovedEmployment());   // 성과 불인정 취업
        setCellValue(row, colIndex++, data.getEmploymentRetention());     // 고용유지
        setCellValue(row, colIndex++, data.getBetterJobCount());          // 229만원 이상 취업자
        setCellValue(row, colIndex++, data.getEmploymentRate());          // 취업률
        setCellValue(row, colIndex++, data.getEvaluationEmploymentRate()); // 평가 취업률
        setCellValue(row, colIndex++, data.getReferralEmploymentRate());   // 알선취업률
        setCellValue(row, colIndex++, data.getBetterJobRate());           // 229만원 이상 취업률
        setCellValue(row, colIndex++, data.getRetentionRate());           // 고용유지율
        colIndex+=2;//위치를 맞추기 위해 한번더 +2을 실행
        setCellValue(row, colIndex++, data.getIncentiveOccurrenceRate()); // 취업인센티브 발생률
        setCellValue(row, colIndex, data.getIncentiveNotOccurred());      // 취업인센티브 미발생자
    }
    /**
     * 누적 실적 데이터를 행에 설정한다.
     * 금일/금주/금월/금년별 일반 취업 및 알선 취업 실적을 출력한다.
     *
     * @param row      대상 행
     * @param colIndex 시작 열 인덱스
     * @param data     누적 실적 데이터
     */
    private void setPerformance(Row row, int colIndex, ReportDTO data){
        setCellValue(row, colIndex++, data.getCounselorName());        // 이름
        setCellValue(row, colIndex, data.getMemberTodayEmployment());    // 금일 일반 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberTodayPlacement());     // 금일 알선 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberToWeekEmployment());   // 금주 일반 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberToWeekPlacement());    // 금주 알선 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberToMonthEmployment());  // 금월 일반 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberToMonthPlacement());   // 금월 알선 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberToYearEmployment());   // 금년 일반 취업
        colIndex+=2;
        setCellValue(row, colIndex, data.getMemberToYearPlacement());    // 금년 알선 취업
    }
}