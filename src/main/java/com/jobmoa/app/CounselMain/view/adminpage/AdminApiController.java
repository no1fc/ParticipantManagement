package com.jobmoa.app.CounselMain.view.adminpage;

import com.jobmoa.app.CounselMain.biz.adminpage.AdminDTO;
import com.jobmoa.app.CounselMain.biz.adminpage.AdminService;
import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private AdminService adminService;

    private ResponseEntity<Map<String, Object>> checkAccess(HttpSession session) {
        LoginBean login = AdminAccessSupport.getLoginBean(session);
        if (login == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        if (!AdminAccessSupport.hasAdminAccess(session)) return ResponseEntity.status(403).body(Map.of("message", "관리자 권한이 필요합니다."));
        return null;
    }

    private ResponseEntity<Map<String, Object>> checkManagerOnly(HttpSession session) {
        LoginBean login = AdminAccessSupport.getLoginBean(session);
        if (login == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        if (!AdminAccessSupport.isManager(session)) return ResponseEntity.status(403).body(Map.of("message", "시스템 관리자 권한이 필요합니다."));
        return null;
    }

    // ===== 대시보드 KPI =====
    @GetMapping("/kpi")
    public ResponseEntity<?> getKpi(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/kpi");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getDashboardData(dto));
    }

    // ===== 사용자 관리 (시스템 관리자 전용) =====
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/users");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getUserList(dto));
    }

    @GetMapping("/users/{userNo}")
    public ResponseEntity<?> getUser(@PathVariable int userNo, HttpSession session) {
        log.info("GET /admin/api/users/{}", userNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        return ResponseEntity.ok(adminService.getUserOne(dto));
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/users");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자가 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userNo}")
    public ResponseEntity<?> updateUser(@PathVariable int userNo, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/users/{}", userNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        dto.setMemberNo(userNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자 정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{userNo}")
    public ResponseEntity<?> deleteUser(@PathVariable int userNo, HttpSession session) {
        log.info("DELETE /admin/api/users/{}", userNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자가 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userNo}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable int userNo, HttpSession session) {
        log.info("PUT /admin/api/users/{}/reset-password", userNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.resetPassword(dto);
        result.put("success", success);
        result.put("message", success ? "비밀번호가 초기화되었습니다." : "초기화에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/next-no")
    public ResponseEntity<?> getNextMemberNo(HttpSession session) {
        log.info("GET /admin/api/users/next-no");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(Map.of("nextNo", adminService.getNextMemberNo()));
    }

    @GetMapping("/users/check-id")
    public ResponseEntity<?> checkUserId(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/users/check-id");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        boolean exists = adminService.checkUserIdExists(dto);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ===== 지점 관리 (시스템 관리자 ���용) =====
    @GetMapping("/branches")
    public ResponseEntity<?> getBranches(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/branches");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getBranchList(dto));
    }

    @GetMapping("/branches/{branchNo}")
    public ResponseEntity<?> getBranch(@PathVariable int branchNo, HttpSession session) {
        log.info("GET /admin/api/branches/{}", branchNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setBranchNo(branchNo);
        return ResponseEntity.ok(adminService.getBranchOne(dto));
    }

    @PostMapping("/branches")
    public ResponseEntity<?> addBranch(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/branches");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addBranch(dto);
        result.put("success", success);
        result.put("message", success ? "지점이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/branches/{branchNo}")
    public ResponseEntity<?> updateBranch(@PathVariable int branchNo, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/branches/{}", branchNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        dto.setBranchNo(branchNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyBranch(dto);
        result.put("success", success);
        result.put("message", success ? "지점 정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/branches/{branchNo}")
    public ResponseEntity<?> deleteBranch(@PathVariable int branchNo, HttpSession session) {
        log.info("DELETE /admin/api/branches/{}", branchNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setBranchNo(branchNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeBranch(dto);
        result.put("success", success);
        result.put("message", success ? "지점�� 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 참여자 관리 =====
    @GetMapping("/participants")
    public ResponseEntity<?> getParticipants(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/participants");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getParticipantList(dto));
    }

    @GetMapping("/participants/{jobNo}")
    public ResponseEntity<?> getParticipant(@PathVariable int jobNo, HttpSession session) {
        log.info("GET /admin/api/participants/{}", jobNo);
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setJobNo(jobNo);
        return ResponseEntity.ok(adminService.getParticipantOne(dto));
    }

    @DeleteMapping("/participants/{jobNo}")
    public ResponseEntity<?> deleteParticipant(@PathVariable int jobNo, HttpSession session) {
        log.info("DELETE /admin/api/participants/{}", jobNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setJobNo(jobNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeParticipant(dto);
        result.put("success", success);
        result.put("message", success ? "참여자가 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 일일업무보고 =====
    @GetMapping("/daily-reports")
    public ResponseEntity<?> getDailyReports(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/daily-reports");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getDailyReportList(dto));
    }

    // ===== 기준금액 (시스템 관리자 전용) =====
    @GetMapping("/standard-amounts")
    public ResponseEntity<?> getStandardAmounts(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/standard-amounts");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getStandardAmountList(dto));
    }

    @GetMapping("/standard-amounts/{pk}")
    public ResponseEntity<?> getStandardAmount(@PathVariable int pk, HttpSession session) {
        log.info("GET /admin/api/standard-amounts/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        return ResponseEntity.ok(adminService.getStandardAmountOne(dto));
    }

    @PostMapping("/standard-amounts")
    public ResponseEntity<?> addStandardAmount(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/standard-amounts");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addStandardAmount(dto);
        result.put("success", success);
        result.put("message", success ? "기준금액이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/standard-amounts/{pk}")
    public ResponseEntity<?> updateStandardAmount(@PathVariable int pk, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/standard-amounts/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyStandardAmount(dto);
        result.put("success", success);
        result.put("message", success ? "기준금액이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/standard-amounts/{pk}")
    public ResponseEntity<?> deleteStandardAmount(@PathVariable int pk, HttpSession session) {
        log.info("DELETE /admin/api/standard-amounts/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeStandardAmount(dto);
        result.put("success", success);
        result.put("message", success ? "기준금액이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 나은기준임금 (시스템 관리자 전용) =====
    @GetMapping("/better-wages")
    public ResponseEntity<?> getBetterWages(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/better-wages");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getBetterWageList(dto));
    }

    @GetMapping("/better-wages/{pk}")
    public ResponseEntity<?> getBetterWage(@PathVariable int pk, HttpSession session) {
        log.info("GET /admin/api/better-wages/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        return ResponseEntity.ok(adminService.getBetterWageOne(dto));
    }

    @PostMapping("/better-wages")
    public ResponseEntity<?> addBetterWage(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/better-wages");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addBetterWage(dto);
        result.put("success", success);
        result.put("message", success ? "나은기준임금이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/better-wages/{pk}")
    public ResponseEntity<?> updateBetterWage(@PathVariable int pk, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/better-wages/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyBetterWage(dto);
        result.put("success", success);
        result.put("message", success ? "나은기준임금이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/better-wages/{pk}")
    public ResponseEntity<?> deleteBetterWage(@PathVariable int pk, HttpSession session) {
        log.info("DELETE /admin/api/better-wages/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeBetterWage(dto);
        result.put("success", success);
        result.put("message", success ? "나은기준임금이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 배정 히스토리 =====
    @GetMapping("/assignment-csv-history")
    public ResponseEntity<?> getCsvHistory(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/assignment-csv-history");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getCsvHistoryList(dto));
    }

    @GetMapping("/assignment-formula-history")
    public ResponseEntity<?> getFormulaHistory(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/assignment-formula-history");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getFormulaHistoryList(dto));
    }

    // ===== 알선 관리 =====
    @GetMapping("/job-placements")
    public ResponseEntity<?> getJobPlacements(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/job-placements");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getJobPlacementList(dto));
    }

    @GetMapping("/job-placements/{regNo}")
    public ResponseEntity<?> getJobPlacement(@PathVariable int regNo, HttpSession session) {
        log.info("GET /admin/api/job-placements/{}", regNo);
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setRegistrationNo(regNo);
        return ResponseEntity.ok(adminService.getJobPlacementOne(dto));
    }

    @PostMapping("/job-placements")
    public ResponseEntity<?> addJobPlacement(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/job-placements");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/job-placements/{regNo}")
    public ResponseEntity<?> updateJobPlacement(@PathVariable int regNo, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/job-placements/{}", regNo);
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        dto.setRegistrationNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/job-placements/{regNo}")
    public ResponseEntity<?> deleteJobPlacement(@PathVariable int regNo, HttpSession session) {
        log.info("DELETE /admin/api/job-placements/{}", regNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setRegistrationNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 이력서 요청 =====
    @GetMapping("/resume-requests")
    public ResponseEntity<?> getResumeRequests(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/resume-requests");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getResumeRequestList(dto));
    }

    @GetMapping("/resume-requests/{regNo}")
    public ResponseEntity<?> getResumeRequest(@PathVariable int regNo, HttpSession session) {
        log.info("GET /admin/api/resume-requests/{}", regNo);
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setResumeRegNo(regNo);
        return ResponseEntity.ok(adminService.getResumeRequestOne(dto));
    }

    @PutMapping("/resume-requests/{regNo}/status")
    public ResponseEntity<?> updateResumeStatus(@PathVariable int regNo, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/resume-requests/{}/status", regNo);
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        dto.setResumeRegNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.updateResumeStatus(dto);
        result.put("success", success);
        result.put("message", success ? "상태가 변경되었습니다." : "변경에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 자격증 (시스템 관리자 전용) =====
    @GetMapping("/certificates")
    public ResponseEntity<?> getCertificates(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/certificates");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getCertificateList(dto));
    }

    @PostMapping("/certificates")
    public ResponseEntity<?> addCertificate(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/certificates");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addCertificate(dto);
        result.put("success", success);
        result.put("message", success ? "자격증이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/certificates/{id}")
    public ResponseEntity<?> updateCertificate(@PathVariable int id, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/certificates/{}", id);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        dto.setCertificateNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyCertificate(dto);
        result.put("success", success);
        result.put("message", success ? "자격증이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/certificates/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable int id, HttpSession session) {
        log.info("DELETE /admin/api/certificates/{}", id);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setCertificateNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeCertificate(dto);
        result.put("success", success);
        result.put("message", success ? "자격증이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 직업훈련 (시스템 관리자 전용) =====
    @GetMapping("/trainings")
    public ResponseEntity<?> getTrainings(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/trainings");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getTrainingList(dto));
    }

    @PostMapping("/trainings")
    public ResponseEntity<?> addTraining(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/trainings");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addTraining(dto);
        result.put("success", success);
        result.put("message", success ? "직업훈련이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/trainings/{id}")
    public ResponseEntity<?> updateTraining(@PathVariable int id, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/trainings/{}", id);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        dto.setTrainingNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyTraining(dto);
        result.put("success", success);
        result.put("message", success ? "직업훈련이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/trainings/{id}")
    public ResponseEntity<?> deleteTraining(@PathVariable int id, HttpSession session) {
        log.info("DELETE /admin/api/trainings/{}", id);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setTrainingNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeTraining(dto);
        result.put("success", success);
        result.put("message", success ? "직업훈련이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 상담사 목록 (공통 API) =====
    @GetMapping("/counselors")
    public ResponseEntity<?> getCounselors(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/counselors");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getCounselorList(dto));
    }

    // ===== 상담사별 통계 =====
    @GetMapping("/placement-stats")
    public ResponseEntity<?> getPlacementStats(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/placement-stats");
        ResponseEntity<Map<String, Object>> denied = checkAccess(session);
        if (denied != null) return denied;
        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getPlacementStatsByCounselor(dto));
    }
}