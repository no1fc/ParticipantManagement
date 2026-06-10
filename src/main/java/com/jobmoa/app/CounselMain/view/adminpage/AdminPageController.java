package com.jobmoa.app.CounselMain.view.adminpage;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 페이지 컨트롤러.
 * <p>
 * 관리자 대시보드, 사용자 관리, 참여자 관리, 지점 관리, 일일업무보고,
 * 기준금액, 배정 히스토리, 알선 관리, 이력서 요청, 자격증/직업훈련,
 * 엑셀 빌더, 연계 현황 등의 관리자 전용 JSP 페이지를 제공한다.
 * </p>
 *
 * @see AdminAccessSupport
 * @see AdminApiController
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminPageController {

    /**
     * 관리자 운영 현황 대시보드 페이지로 이동한다.
     *
     * @param session HTTP 세션
     * @return 관리자 접근 권한이 없으면 메인으로 리다이렉트, 있으면 {@code "admin/adminManagementDashboard"} JSP 뷰
     */
    @GetMapping
    public String adminDashboard(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminManagementDashboard";
    }

    /**
     * 사용자 관리 페이지로 이동한다. (시스템 관리자 전용)
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminUserManagement"} JSP 뷰
     */
    @GetMapping("/users")
    public String adminUserManagement(HttpSession session) {
        if (!AdminAccessSupport.isManager(session)) return "redirect:/admin";
        return "admin/adminUserManagement";
    }

    /**
     * 참여자 관리 페이지로 이동한다.
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminParticipantManagement"} JSP 뷰
     */
    @GetMapping("/participants")
    public String adminParticipantManagement(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminParticipantManagement";
    }

    /**
     * 지점 관리 페이지로 이동한다. (시스템 관리자 전용)
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminBranchManagement"} JSP 뷰
     */
    @GetMapping("/branches")
    public String adminBranchManagement(HttpSession session) {
        if (!AdminAccessSupport.isManager(session)) return "redirect:/admin";
        return "admin/adminBranchManagement";
    }

    /**
     * 일일업무보고 페이지로 이동한다.
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminDailyReport"} JSP 뷰
     */
    @GetMapping("/daily-reports")
    public String adminDailyReport(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminDailyReport";
    }

    /**
     * 알선 관리 페이지로 이동한다.
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminJobPlacement"} JSP 뷰
     */
    @GetMapping("/job-placements")
    public String adminJobPlacement(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminJobPlacement";
    }

    /**
     * 이력서 요청 관리 페이지로 이동한다.
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminResumeRequest"} JSP 뷰
     */
    @GetMapping("/resume-requests")
    public String adminResumeRequest(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminResumeRequest";
    }

    /**
     * 연계 현황 페이지로 이동한다.
     *
     * @param session HTTP 세션
     * @return {@code "admin/adminLinkageStats"} JSP 뷰
     */
    @GetMapping("/linkage-stats")
    public String adminLinkageStats(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminLinkageStats";
    }

}