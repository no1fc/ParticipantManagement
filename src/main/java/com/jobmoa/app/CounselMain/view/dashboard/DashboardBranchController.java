package com.jobmoa.app.CounselMain.view.dashboard;

import com.jobmoa.app.CounselMain.biz.dashboard.DashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

/**
 * 지점별 종합 관리 대시보드 페이지 컨트롤러.
 * <p>
 * 지점별 성공금 현황(당해/전년), 인센티브 해당/미해당 현황,
 * 인센티브 미해당 사유별 현황 데이터를 JSON으로 변환하여 대시보드 페이지에 제공한다.
 * </p>
 */
@Slf4j
@Controller
public class DashboardBranchController {

    @Autowired
    private DashboardServiceImpl dashboardService;

    @Autowired
    private ChangeJson changeJson;

    private static final LocalDate DATE = LocalDate.now();

    /**
     * 지점별 종합 관리 대시보드 페이지로 이동한다.
     * <p>
     * 지점별 성공금 현황, 인센티브 해당/미해당 현황, 인센티브 미해당 사유별 현황을
     * JSON으로 변환하여 모델에 추가한다. 기간이 미지정이면 올해 전체를 기본값으로 설정한다.
     * </p>
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param session      HTTP 세션
     * @param dashboardDTO 검색 조건 (시작일, 종료일)
     * @return {@code "views/DashBoardTotalManagement"} JSP 뷰
     */
    @GetMapping("/branchDashboard.login")
    public String dashboardBranchSuccessMoney(Model model, HttpSession session, DashboardDTO dashboardDTO){
        String startDate = dashboardDTO.getDashBoardStartDate();
        String endDate = dashboardDTO.getDashBoardEndDate();

        if (startDate == null || endDate == null) {
            startDate = DATE.withDayOfYear(1).toString();
            endDate = DATE.withDayOfYear(DATE.lengthOfYear()).toString();
            // 시작일이 없다면 무조건 올해 1월로 설정
            dashboardDTO.setDashBoardStartDate(startDate);
            // 마지막일이 없다면 올해 12월 말로 설정
            dashboardDTO.setDashBoardEndDate(endDate);
        }

        // 지점별 성공금 현황 json 변환 시작
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 json 제작 시작");
        dashboardDTO.setDashboardCondition("selectBranchManagementMoney");
        List<DashboardDTO> managementMoney = dashboardService.selectAll(dashboardDTO);
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 DashboardDTO managementMoney 문제 없음");

        String jsonResult1 = changeJson.convertListToJsonArray(managementMoney, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            String branch = dto.getDashboardBranch() != null ?
                    escapeJsonString(dto.getDashboardBranch()) : "";

            return String.format(
                    "{\"thisSuccess\":{\"branch\":\"%s\",\"data\":%s}," +
                            "\"previousSuccess\":{\"branch\":\"%s\",\"data\":%s}}",
                    branch,
                    dto.getCurrentYearMoney() != 0 ? dto.getCurrentYearMoney() : 0,
                    branch,
                    dto.getLastYearMoney() != 0 ? dto.getLastYearMoney() : 0
            );
        });
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 json 제작 끝 : [{}]",jsonResult1);
        // 지점별 성공금 현황 json 변환 끝

        // 인센 현황 json 변환 시작
        log.info("dashboardBranchSuccessMoney 인센 현황 json 제작 시작");
        dashboardDTO.setDashboardCondition("selectBranchInventiveStatus");
        List<DashboardDTO> inventiveStatus = dashboardService.selectAll(dashboardDTO);
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 DashboardDTO inventiveStatus 문제 없음");

        String jsonResult2 = changeJson.convertListToJsonArray(inventiveStatus, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            String branch = dto.getDashboardBranch() != null ?
                    escapeJsonString(dto.getDashboardBranch()) : "전체 지점";

            return String.format(
                    "{\"branch\":\"%s\"," +
                            "\"trueCase\":%d," +
                            "\"falseCase\":%d," +
                            "\"noService\":%d}",
                    branch,
                    dto.getTrueCaseNum(),
                    dto.getFalseCaseNum(),
                    dto.getNoServiceCount()
            );
        });
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 json 제작 끝 : [{}]",jsonResult2);
        // 인센 현황 json 변환 끝

        // 인센 미해당 현황 json 변환 시작
        log.info("dashboardBranchSuccessMoney 인센 미해당 현황 json 제작 시작");
        dashboardDTO.setDashboardCondition("selectBranchInventiveFalseStatus");
        List<DashboardDTO> inventiveFalseStatus = dashboardService.selectAll(dashboardDTO);
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 DashboardDTO inventiveFalseStatus 문제 없음");

        String jsonResult3 = changeJson.convertListToJsonArray(inventiveFalseStatus, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            String branch = dto.getDashboardBranch() != null ?
                    escapeJsonString(dto.getDashboardBranch()) : "전체 지점";

            return String.format(
                    "{\"branch\":\"%s\"," +
                            "\"noService\":%d," +
                            "\"lessThanOneMonth\":%d," +
                            "\"dispatchCompany\":%d," +
                            "\"iapSevenDays\":%d," +
                            "\"underThirtyHours\":%d," +
                            "\"underMinWage\":%d," +
                            "\"etc\":%d}",
                    branch,
                    dto.getNoServiceCount(),
                    dto.getLessThanOneMonthCount(),
                    dto.getDispatchCompanyCount(),
                    dto.getIapSevenDaysCount(),
                    dto.getUnderThirtyHoursCount(),
                    dto.getUnderMinWageCount(),
                    dto.getEtcCount()
            );
        });
        log.info("dashboardBranchSuccessMoney 지점별 성공금 현황 json 제작 끝 : [{}]",jsonResult3);
        // 인센 미해당 현황 json 변환 끝

        model.addAttribute("jsonResult1", jsonResult1);
        model.addAttribute("jsonResult2", jsonResult2);
        model.addAttribute("jsonResult3", jsonResult3);
        model.addAttribute("dashBoardStartDate", startDate);
        model.addAttribute("dashBoardEndDate", endDate);

        return "views/DashBoardTotalManagement";
    }

    /**
     * JSON 문자열에 포함될 수 없는 특수문자를 이스케이프 처리한다.
     *
     * @param input 이스케이프 처리할 문자열
     * @return 이스케이프 처리된 문자열, {@code null}이면 빈 문자열
     */
    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
