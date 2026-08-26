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
 * <p>실적기간(전년 11/1 ~ 당해 10/31) 중 연계일이 있는 참여자를 기준으로
 * 전체/지점별/상담사별 연계 참여자 수와 연계 건수(전체·종료자)를 조회하여 페이지에 제공한다.</p>
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

        // 5. 상담사별
        dto.setCondition("selectLinkageByCounselor");
        List<LinkageDashboardDTO> counselorList = linkageDashboardService.selectAll(dto);
        model.addAttribute("linkageByCounselor", buildRowsJson(counselorList, true));

        model.addAttribute("linkageStartDate", startDate);
        model.addAttribute("linkageEndDate", endDate);
        model.addAttribute("linkageIsManager", isManager);

        return "views/DashBoardLinkagePage";
    }

    /**
     * 전체 합계 DTO를 JSON 객체 문자열로 변환한다.
     *
     * @param totals 전체 합계 DTO(없으면 0 처리)
     * @return {@code {"linkageParticipantCount":..,...}} 형식 JSON
     */
    private String buildTotalsJson(LinkageDashboardDTO totals) {
        int lp = totals != null ? totals.getLinkageParticipantCount() : 0;
        int le = totals != null ? totals.getLinkageEventCount() : 0;
        int tp = totals != null ? totals.getTerminatedParticipantCount() : 0;
        int te = totals != null ? totals.getTerminatedEventCount() : 0;
        return String.format(
                "{\"linkageParticipantCount\":%d,\"linkageEventCount\":%d," +
                        "\"terminatedParticipantCount\":%d,\"terminatedEventCount\":%d}",
                lp, le, tp, te);
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
                            "\"linkageParticipantCount\":%d,\"linkageEventCount\":%d," +
                                    "\"terminatedParticipantCount\":%d,\"terminatedEventCount\":%d}",
                            d.getLinkageParticipantCount(), d.getLinkageEventCount(),
                            d.getTerminatedParticipantCount(), d.getTerminatedEventCount());
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
