package com.jobmoa.app.CounselMain.view.adminpage;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String adminDashboard(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminTotalDashboard";
    }

    @GetMapping("/users")
    public String adminUserManagement(HttpSession session) {
        if (!AdminAccessSupport.isManager(session)) return "redirect:/admin";
        return "admin/adminUserManagement";
    }

    @GetMapping("/participants")
    public String adminParticipantManagement(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminParticipantManagement";
    }

    @GetMapping("/branches")
    public String adminBranchManagement(HttpSession session) {
        if (!AdminAccessSupport.isManager(session)) return "redirect:/admin";
        return "admin/adminBranchManagement";
    }

    @GetMapping("/daily-reports")
    public String adminDailyReport(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminDailyReport";
    }

    @GetMapping("/standard-amounts")
    public String adminStandardAmount(HttpSession session) {
        if (!AdminAccessSupport.isManager(session)) return "redirect:/admin";
        return "admin/adminStandardAmount";
    }

    @GetMapping("/assignment-history")
    public String adminAssignmentHistory(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminAssignmentHistory";
    }

    @GetMapping("/job-placements")
    public String adminJobPlacement(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminJobPlacement";
    }

    @GetMapping("/resume-requests")
    public String adminResumeRequest(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminResumeRequest";
    }

    @GetMapping("/certificate-training")
    public String adminCertificateTraining(HttpSession session) {
        if (!AdminAccessSupport.isManager(session)) return "redirect:/admin";
        return "admin/adminCertificateTraining";
    }

    @GetMapping("/excel-builder")
    public String adminExcelBuilder(HttpSession session) {
        if (!AdminAccessSupport.hasAdminAccess(session)) return "redirect:/";
        return "admin/adminExcelBuilder";
    }
}