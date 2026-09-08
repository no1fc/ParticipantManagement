package com.jobmoa.app.CounselMain.view.dashboard;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.LinkageDashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.LinkageDashboardServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

/**
 * 연계 실적 대시보드 페이지 컨트롤러.
 * <p>전체/지점별/상담사별 연계 건수를 두 기준(실적 기간 전체=연계일 기준,
 * 종료일 기준=실제종료일이 실적기간 내)으로 조회하여 페이지에 제공한다.
 * 실적기간은 전년 11/1 ~ 당해 10/31.</p>
 * <p>권한 스코프: 관리자(IS_MANAGER)는 전체, 일반 상담사는 본인(전담자_계정) 데이터만 조회한다.</p>
 */
@Slf4j
@Controller
public class LinkageDashboardController {

    @Autowired
    private LinkageDashboardServiceImpl linkageDashboardService;

    @Autowired
    private ChangeJson changeJson;

    /**
     * 연계 실적 대시보드 페이지로 이동한다.
     *
     * @param model   뷰에 전달할 데이터 모델
     * @param session HTTP 세션 (로그인 정보 및 권한 확인용)
     * @return {@code "views/DashBoardLinkagePage"} JSP 뷰
     */
    @GetMapping("/linkageDashboard.login")
    public String linkageDashboard(Model model, HttpSession session) {
        LinkageDashboardDTO dto = new LinkageDashboardDTO();

        // 1. 실적기간 설정 (전년 11/1 ~ 당해 10/31)
        LocalDate today = LocalDate.now();
        String startDate = (today.getYear() - 1) + "-11-01";
        String endDate = today.getYear() + "-10-31";
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        // 2. 권한 스코프: 관리자가 아니면 본인 전담자_계정으로 한정
        boolean isManager = Boolean.TRUE.equals(session.getAttribute("IS_MANAGER"));
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (!isManager && loginBean != null) {
            dto.setScopeAccount(loginBean.getMemberUserID());
        }

        // 3. 전체 합계 (단건)
        dto.setCondition("selectLinkageTotals");
        LinkageDashboardDTO totals = linkageDashboardService.selectOne(dto);
        model.addAttribute("linkageTotals", buildTotalsJson(totals));

        // 4. 지점별
        dto.setCondition("selectLinkageByBranch");
        List<LinkageDashboardDTO> branchList = linkageDashboardService.selectAll(dto);
        model.addAttribute("linkageByBranch", buildRowsJson(branchList, false));

        // 5. 상담사별 — 지점 한정 직급(파트장·팀장·총괄·차장)이면 소속 지점으로, 아니면 현행 스코프 유지
        dto.setCondition("selectLinkageByCounselor");
        String branchScope = LinkageScopeSupport.resolveCounselorBranchScope(loginBean);
        String savedAccount = dto.getScopeAccount();
        if (branchScope != null) {
            dto.setScopeBranch(branchScope);
            dto.setScopeAccount(null); // 지점 스코프가 우선(계정 한정 해제)
        }
        List<LinkageDashboardDTO> counselorList = linkageDashboardService.selectAll(dto);
        model.addAttribute("linkageByCounselor", buildRowsJson(counselorList, true));
        dto.setScopeBranch(null);       // 이후 차트 쿼리에 영향 없도록 원복
        dto.setScopeAccount(savedAccount);

        // 6. 지점별 × 실적인정/미인정 2분류 (스택 차트용, 5종 외 전부를 미인정으로 포함)
        dto.setCondition("selectLinkageByBranchCategory");
        List<LinkageDashboardDTO> branchCategoryList = linkageDashboardService.selectAll(dto);
        model.addAttribute("linkageByBranchCategory", buildBranchCategoryRowsJson(branchCategoryList));

        model.addAttribute("linkageStartDate", startDate);
        model.addAttribute("linkageEndDate", endDate);
        model.addAttribute("linkageIsManager", isManager);

        // 일반(상담·PRA)에게는 연계 현황 차트·지점별 상세를 숨긴다(본인 KPI·상담사별 상세만 노출).
        boolean hideBranchViews = loginBean != null
                && "NORMAL".equals(loginBean.getPermissionGroup());
        model.addAttribute("linkageHideBranchViews", hideBranchViews);

        return "views/DashBoardLinkagePage";
    }

    /**
     * 전체 합계 DTO를 JSON 객체 문자열로 변환한다.
     *
     * @param totals 전체 합계 DTO(없으면 0 처리)
     * @return {@code {"linkageParticipantCount":..,...}} 형식 JSON
     */
    private String buildTotalsJson(LinkageDashboardDTO totals) {
        int full = totals != null ? totals.getFullPeriodEventCount() : 0;
        int term = totals != null ? totals.getTerminatedEventCount() : 0;
        return String.format(
                "{\"fullPeriodEventCount\":%d,\"terminatedEventCount\":%d}",
                full, term);
    }

    /**
     * 지점별/상담사별 목록을 JSON 배열 문자열로 변환한다.
     *
     * @param rows             집계 행 목록
     * @param includeCounselor 상담사 라벨(이름/계정) 포함 여부
     * @return JSON 배열 문자열
     */
    private String buildRowsJson(List<LinkageDashboardDTO> rows, boolean includeCounselor) {
        return changeJson.convertListToJsonArray(rows, item -> {
            LinkageDashboardDTO d = (LinkageDashboardDTO) item;
            String label = includeCounselor
                    ? String.format("\"branch\":\"%s\",\"counselorName\":\"%s\",\"counselorAccount\":\"%s\",",
                        nullSafe(d.getBranch()), nullSafe(d.getCounselorName()), nullSafe(d.getCounselorAccount()))
                    : String.format("\"branch\":\"%s\",", nullSafe(d.getBranch()));
            return "{" + label +
                    String.format(
                            "\"rank1TerminatedEventCount\":%d,\"fullPeriodEventCount\":%d,\"terminatedEventCount\":%d}",
                            d.getRank1TerminatedEventCount(), d.getFullPeriodEventCount(), d.getTerminatedEventCount());
        });
    }

    /**
     * 지점별 × 실적인정/미인정 2분류 목록을 JSON 배열 문자열로 변환한다(스택 차트용).
     *
     * @param rows 지점×분류 집계 행 목록(실적인정 5종 vs 미인정 그 외 전부)
     * @return {@code [{"branch":"..","linkageCategory":"..","fullPeriodEventCount":..,"terminatedEventCount":..}]} 형식 JSON 배열
     */
    private String buildBranchCategoryRowsJson(List<LinkageDashboardDTO> rows) {
        return changeJson.convertListToJsonArray(rows, item -> {
            LinkageDashboardDTO d = (LinkageDashboardDTO) item;
            return String.format(
                    "{\"branch\":\"%s\",\"linkageCategory\":\"%s\",\"fullPeriodEventCount\":%d,\"terminatedEventCount\":%d}",
                    nullSafe(d.getBranch()), nullSafe(d.getLinkageCategory()),
                    d.getFullPeriodEventCount(), d.getTerminatedEventCount());
        });
    }

    /**
     * null 문자열을 빈 문자열로 변환하고 JSON 특수문자를 이스케이프한다.
     *
     * @param value 원본 문자열
     * @return 이스케이프된 안전 문자열
     */
    private String nullSafe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
