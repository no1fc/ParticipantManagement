package com.jobmoa.app.CounselMain.view.dashboard;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.LinkageDashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.LinkageDashboardServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 연계 실적 대시보드 Excel 다운로드 컨트롤러.
 * <p>연계 실적 대시보드 화면({@code /linkageDashboard.login})의 지점별·상담사별 집계표를
 * {@code 지점별}/{@code 상담사별} 두 시트로 분리한 Excel 파일로 내려받는다.</p>
 * <p>실적기간·권한 스코프(관리자=전체, 일반 상담사=본인)는 {@link LinkageDashboardController}와
 * 동일하게 적용하여 화면과 동일한 데이터를 보장한다.</p>
 */
@Slf4j
@Controller
public class LinkageDashboardExcelController {

    /** 컨소시엄 지점(고정 5개) — 지점 마스터에 플래그가 없어 화면 JS와 동일하게 코드 상수로 관리. */
    private static final List<String> CONSORTIUM_BRANCHES =
            Arrays.asList("의정부", "북부", "광명", "성남", "인천");

    @Autowired
    private LinkageDashboardServiceImpl linkageDashboardService;

    /**
     * 연계 실적 대시보드 집계표를 Excel 파일로 다운로드한다.
     *
     * @param response HTTP 응답 (Excel 파일 출력용)
     * @param session  HTTP 세션 (로그인 정보 및 권한 확인용)
     */
    @GetMapping("/linkageDashboardExcel.login")
    public void exportLinkageDashboard(HttpServletResponse response, HttpSession session) {
        try {
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            if (loginBean == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 정보가 없습니다.");
                return;
            }

            // 1. 실적기간 설정 (전년 11/1 ~ 당해 10/31) — 화면 컨트롤러와 동일
            LocalDate today = LocalDate.now();
            String startDate = (today.getYear() - 1) + "-11-01";
            String endDate = today.getYear() + "-10-31";

            LinkageDashboardDTO dto = new LinkageDashboardDTO();
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);

            // 2. 권한 스코프: 관리자가 아니면 본인 전담자_계정으로 한정(IDOR 방지)
            boolean isManager = Boolean.TRUE.equals(session.getAttribute("IS_MANAGER"));
            if (!isManager) {
                dto.setScopeAccount(loginBean.getMemberUserID());
            }

            // 3. 데이터 조회 (기존 condition 그대로 재사용)
            dto.setCondition("selectLinkageTotals");
            LinkageDashboardDTO totals = linkageDashboardService.selectOne(dto);

            dto.setCondition("selectLinkageByBranch");
            List<LinkageDashboardDTO> branchList = linkageDashboardService.selectAll(dto);

            dto.setCondition("selectLinkageByCounselor");
            List<LinkageDashboardDTO> counselorList = linkageDashboardService.selectAll(dto);

            int fullTotal = totals != null ? totals.getFullPeriodEventCount() : 0;
            int termTotal = totals != null ? totals.getTerminatedEventCount() : 0;

            // 4. Excel 생성 (소량 집계 데이터 → XSSFWorkbook)
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                CellStyle headerStyle = buildHeaderStyle(workbook);
                writeBranchSheet(workbook, headerStyle, branchList, startDate, endDate, fullTotal, termTotal);
                writeCounselorSheet(workbook, headerStyle, counselorList, startDate, endDate, fullTotal, termTotal);

                String fileName = URLEncoder.encode(
                        "연계실적_대시보드_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                workbook.write(response.getOutputStream());
            }

        } catch (Exception e) {
            log.error("연계 실적 대시보드 Excel 다운로드 오류", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excel 생성 중 오류가 발생했습니다.");
            } catch (Exception ignored) {}
        }
    }

    /**
     * 지점별 상세 시트를 작성한다.
     * <p>컬럼 값 매핑은 화면과 동일: 실적 확정 = terminatedEventCount, 종료 미확정자 포함 = fullPeriodEventCount.</p>
     */
    private void writeBranchSheet(XSSFWorkbook workbook, CellStyle headerStyle,
                                  List<LinkageDashboardDTO> rows,
                                  String startDate, String endDate, int fullTotal, int termTotal) {
        Sheet sheet = workbook.createSheet("지점별");
        int rowNum = writeSummary(sheet, "연계 실적 대시보드 - 지점별 상세",
                startDate, endDate, fullTotal, termTotal);

        Row header = sheet.createRow(rowNum++);
        writeHeaderCells(header, headerStyle,
                "순위", "지점", "연계 건수(실적 확정)", "연계 건수(종료 미확정자 포함)");

        int rank = 1;
        if (rows != null) {
            for (LinkageDashboardDTO d : rows) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rank++);
                row.createCell(1).setCellValue(branchLabel(d.getBranch()));
                row.createCell(2).setCellValue(d.getTerminatedEventCount());
                row.createCell(3).setCellValue(d.getFullPeriodEventCount());
            }
        }
        autoSize(sheet, 4);
    }

    /**
     * 상담사별 상세 시트를 작성한다.
     * <p>상담사 라벨은 이름(없으면 계정)을 사용한다.</p>
     */
    private void writeCounselorSheet(XSSFWorkbook workbook, CellStyle headerStyle,
                                     List<LinkageDashboardDTO> rows,
                                     String startDate, String endDate, int fullTotal, int termTotal) {
        Sheet sheet = workbook.createSheet("상담사별");
        int rowNum = writeSummary(sheet, "연계 실적 대시보드 - 상담사별 상세",
                startDate, endDate, fullTotal, termTotal);

        Row header = sheet.createRow(rowNum++);
        writeHeaderCells(header, headerStyle,
                "순위", "지점", "상담사", "연계 건수(실적 확정)", "연계 건수(종료 미확정자 포함)");

        int rank = 1;
        if (rows != null) {
            for (LinkageDashboardDTO d : rows) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rank++);
                row.createCell(1).setCellValue(branchLabel(d.getBranch()));
                row.createCell(2).setCellValue(counselorLabel(d));
                row.createCell(3).setCellValue(d.getTerminatedEventCount());
                row.createCell(4).setCellValue(d.getFullPeriodEventCount());
            }
        }
        autoSize(sheet, 5);
    }

    /**
     * 시트 상단에 제목·실적기간·KPI 요약을 기록하고, 다음 기록 시작 행 번호를 반환한다.
     *
     * @return 요약 뒤(빈 행 포함)의 다음 행 인덱스
     */
    private int writeSummary(Sheet sheet, String title,
                             String startDate, String endDate, int fullTotal, int termTotal) {
        sheet.createRow(0).createCell(0).setCellValue(title);
        sheet.createRow(1).createCell(0).setCellValue("실적기간: " + startDate + " ~ " + endDate);
        sheet.createRow(2).createCell(0).setCellValue(
                "연계 건수(실적 기간 전체): " + fullTotal + " / 연계 건수(종료일 기준): " + termTotal);
        // 3행은 빈 행, 4행부터 헤더
        return 4;
    }

    /** 헤더 행에 볼드 스타일을 적용해 셀 값을 채운다. */
    private void writeHeaderCells(Row header, CellStyle headerStyle, String... labels) {
        for (int i = 0; i < labels.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(labels[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /** 볼드 헤더 셀 스타일을 생성한다. */
    private CellStyle buildHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /** 컬럼 폭 자동 조정. */
    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 지점 라벨: 컨소시엄 지점이면 {@code "(컨소시엄)"} 접미사를 붙인다(화면 JS와 동일).
     */
    private String branchLabel(String branch) {
        if (branch == null) {
            return "";
        }
        return CONSORTIUM_BRANCHES.contains(branch) ? branch + "(컨소시엄)" : branch;
    }

    /** 상담사 라벨: 이름 우선, 없으면 계정. */
    private String counselorLabel(LinkageDashboardDTO d) {
        String name = d.getCounselorName();
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return d.getCounselorAccount() != null ? d.getCounselorAccount() : "";
    }
}
