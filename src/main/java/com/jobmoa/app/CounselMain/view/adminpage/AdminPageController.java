package com.jobmoa.app.CounselMain.view.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String adminDashboard() {
        return "admin/adminTotalDashboard";
    }

    @GetMapping("/users")
    public String adminUserManagement() {
        return "admin/adminUserManagement";
    }

    @GetMapping("/participants")
    public String adminParticipantManagement() {
        return "admin/adminParticipantManagement";
    }

    @GetMapping("/branches")
    public String adminBranchManagement() {
        return "admin/adminBranchManagement";
    }

    @GetMapping("/daily-reports")
    public String adminDailyReport() {
        return "admin/adminDailyReport";
    }

    @GetMapping("/standard-amounts")
    public String adminStandardAmount() {
        return "admin/adminStandardAmount";
    }

    @GetMapping("/assignment-history")
    public String adminAssignmentHistory() {
        return "admin/adminAssignmentHistory";
    }

    @GetMapping("/job-placements")
    public String adminJobPlacement() {
        return "admin/adminJobPlacement";
    }

    @GetMapping("/resume-requests")
    public String adminResumeRequest() {
        return "admin/adminResumeRequest";
    }

    @GetMapping("/certificate-training")
    public String adminCertificateTraining() {
        return "admin/adminCertificateTraining";
    }
}