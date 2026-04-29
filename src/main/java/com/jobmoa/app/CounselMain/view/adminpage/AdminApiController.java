package com.jobmoa.app.CounselMain.view.adminpage;

import com.jobmoa.app.CounselMain.biz.adminpage.AdminDTO;
import com.jobmoa.app.CounselMain.biz.adminpage.AdminService;
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

    // ===== 대시보드 KPI =====
    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Object>> getKpi() {
        log.info("GET /admin/api/kpi");
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    // ===== 사용자 관리 =====
    @GetMapping("/users")
    public ResponseEntity<List<AdminDTO>> getUsers(AdminDTO dto) {
        log.info("GET /admin/api/users");
        return ResponseEntity.ok(adminService.getUserList(dto));
    }

    @GetMapping("/users/{userNo}")
    public ResponseEntity<AdminDTO> getUser(@PathVariable int userNo) {
        log.info("GET /admin/api/users/{}", userNo);
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        return ResponseEntity.ok(adminService.getUserOne(dto));
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/users");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자가 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userNo}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable int userNo, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/users/{}", userNo);
        dto.setMemberNo(userNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자 정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{userNo}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable int userNo) {
        log.info("DELETE /admin/api/users/{}", userNo);
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자가 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userNo}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable int userNo) {
        log.info("PUT /admin/api/users/{}/reset-password", userNo);
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.resetPassword(dto);
        result.put("success", success);
        result.put("message", success ? "비밀번호가 초기화되었습니다." : "초기화에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 지점 관리 =====
    @GetMapping("/branches")
    public ResponseEntity<List<AdminDTO>> getBranches(AdminDTO dto) {
        log.info("GET /admin/api/branches");
        return ResponseEntity.ok(adminService.getBranchList(dto));
    }

    @GetMapping("/branches/{branchNo}")
    public ResponseEntity<AdminDTO> getBranch(@PathVariable int branchNo) {
        log.info("GET /admin/api/branches/{}", branchNo);
        AdminDTO dto = new AdminDTO();
        dto.setBranchNo(branchNo);
        return ResponseEntity.ok(adminService.getBranchOne(dto));
    }

    @PostMapping("/branches")
    public ResponseEntity<Map<String, Object>> addBranch(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/branches");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addBranch(dto);
        result.put("success", success);
        result.put("message", success ? "지점이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/branches/{branchNo}")
    public ResponseEntity<Map<String, Object>> updateBranch(@PathVariable int branchNo, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/branches/{}", branchNo);
        dto.setBranchNo(branchNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyBranch(dto);
        result.put("success", success);
        result.put("message", success ? "지점 정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/branches/{branchNo}")
    public ResponseEntity<Map<String, Object>> deleteBranch(@PathVariable int branchNo) {
        log.info("DELETE /admin/api/branches/{}", branchNo);
        AdminDTO dto = new AdminDTO();
        dto.setBranchNo(branchNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeBranch(dto);
        result.put("success", success);
        result.put("message", success ? "지점이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 참여자 관리 =====
    @GetMapping("/participants")
    public ResponseEntity<List<AdminDTO>> getParticipants(AdminDTO dto) {
        log.info("GET /admin/api/participants");
        return ResponseEntity.ok(adminService.getParticipantList(dto));
    }

    @GetMapping("/participants/{jobNo}")
    public ResponseEntity<AdminDTO> getParticipant(@PathVariable int jobNo) {
        log.info("GET /admin/api/participants/{}", jobNo);
        AdminDTO dto = new AdminDTO();
        dto.setJobNo(jobNo);
        return ResponseEntity.ok(adminService.getParticipantOne(dto));
    }

    @DeleteMapping("/participants/{jobNo}")
    public ResponseEntity<Map<String, Object>> deleteParticipant(@PathVariable int jobNo) {
        log.info("DELETE /admin/api/participants/{}", jobNo);
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
    public ResponseEntity<List<AdminDTO>> getDailyReports(AdminDTO dto) {
        log.info("GET /admin/api/daily-reports");
        return ResponseEntity.ok(adminService.getDailyReportList(dto));
    }

    // ===== 기준금액 =====
    @GetMapping("/standard-amounts")
    public ResponseEntity<List<AdminDTO>> getStandardAmounts(AdminDTO dto) {
        log.info("GET /admin/api/standard-amounts");
        return ResponseEntity.ok(adminService.getStandardAmountList(dto));
    }

    @GetMapping("/standard-amounts/{pk}")
    public ResponseEntity<AdminDTO> getStandardAmount(@PathVariable int pk) {
        log.info("GET /admin/api/standard-amounts/{}", pk);
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        return ResponseEntity.ok(adminService.getStandardAmountOne(dto));
    }

    @PostMapping("/standard-amounts")
    public ResponseEntity<Map<String, Object>> addStandardAmount(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/standard-amounts");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addStandardAmount(dto);
        result.put("success", success);
        result.put("message", success ? "기준금액이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/standard-amounts/{pk}")
    public ResponseEntity<Map<String, Object>> updateStandardAmount(@PathVariable int pk, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/standard-amounts/{}", pk);
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyStandardAmount(dto);
        result.put("success", success);
        result.put("message", success ? "기준금액이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/standard-amounts/{pk}")
    public ResponseEntity<Map<String, Object>> deleteStandardAmount(@PathVariable int pk) {
        log.info("DELETE /admin/api/standard-amounts/{}", pk);
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeStandardAmount(dto);
        result.put("success", success);
        result.put("message", success ? "기준금액이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 나은기준임금 =====
    @GetMapping("/better-wages")
    public ResponseEntity<List<AdminDTO>> getBetterWages(AdminDTO dto) {
        log.info("GET /admin/api/better-wages");
        return ResponseEntity.ok(adminService.getBetterWageList(dto));
    }

    @GetMapping("/better-wages/{pk}")
    public ResponseEntity<AdminDTO> getBetterWage(@PathVariable int pk) {
        log.info("GET /admin/api/better-wages/{}", pk);
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        return ResponseEntity.ok(adminService.getBetterWageOne(dto));
    }

    @PostMapping("/better-wages")
    public ResponseEntity<Map<String, Object>> addBetterWage(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/better-wages");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addBetterWage(dto);
        result.put("success", success);
        result.put("message", success ? "나은기준임금이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/better-wages/{pk}")
    public ResponseEntity<Map<String, Object>> updateBetterWage(@PathVariable int pk, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/better-wages/{}", pk);
        dto.setPk(pk);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyBetterWage(dto);
        result.put("success", success);
        result.put("message", success ? "나은기준임금이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/better-wages/{pk}")
    public ResponseEntity<Map<String, Object>> deleteBetterWage(@PathVariable int pk) {
        log.info("DELETE /admin/api/better-wages/{}", pk);
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
    public ResponseEntity<List<AdminDTO>> getCsvHistory(AdminDTO dto) {
        log.info("GET /admin/api/assignment-csv-history");
        return ResponseEntity.ok(adminService.getCsvHistoryList(dto));
    }

    @GetMapping("/assignment-formula-history")
    public ResponseEntity<List<AdminDTO>> getFormulaHistory(AdminDTO dto) {
        log.info("GET /admin/api/assignment-formula-history");
        return ResponseEntity.ok(adminService.getFormulaHistoryList(dto));
    }

    // ===== 알선 관리 =====
    @GetMapping("/job-placements")
    public ResponseEntity<List<AdminDTO>> getJobPlacements(AdminDTO dto) {
        log.info("GET /admin/api/job-placements");
        return ResponseEntity.ok(adminService.getJobPlacementList(dto));
    }

    @GetMapping("/job-placements/{regNo}")
    public ResponseEntity<AdminDTO> getJobPlacement(@PathVariable int regNo) {
        log.info("GET /admin/api/job-placements/{}", regNo);
        AdminDTO dto = new AdminDTO();
        dto.setRegistrationNo(regNo);
        return ResponseEntity.ok(adminService.getJobPlacementOne(dto));
    }

    @PostMapping("/job-placements")
    public ResponseEntity<Map<String, Object>> addJobPlacement(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/job-placements");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/job-placements/{regNo}")
    public ResponseEntity<Map<String, Object>> updateJobPlacement(@PathVariable int regNo, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/job-placements/{}", regNo);
        dto.setRegistrationNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/job-placements/{regNo}")
    public ResponseEntity<Map<String, Object>> deleteJobPlacement(@PathVariable int regNo) {
        log.info("DELETE /admin/api/job-placements/{}", regNo);
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
    public ResponseEntity<List<AdminDTO>> getResumeRequests(AdminDTO dto) {
        log.info("GET /admin/api/resume-requests");
        return ResponseEntity.ok(adminService.getResumeRequestList(dto));
    }

    @GetMapping("/resume-requests/{regNo}")
    public ResponseEntity<AdminDTO> getResumeRequest(@PathVariable int regNo) {
        log.info("GET /admin/api/resume-requests/{}", regNo);
        AdminDTO dto = new AdminDTO();
        dto.setResumeRegNo(regNo);
        return ResponseEntity.ok(adminService.getResumeRequestOne(dto));
    }

    @PutMapping("/resume-requests/{regNo}/status")
    public ResponseEntity<Map<String, Object>> updateResumeStatus(@PathVariable int regNo, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/resume-requests/{}/status", regNo);
        dto.setResumeRegNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.updateResumeStatus(dto);
        result.put("success", success);
        result.put("message", success ? "상태가 변경되었습니다." : "변경에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 자격증 =====
    @GetMapping("/certificates")
    public ResponseEntity<List<AdminDTO>> getCertificates(AdminDTO dto) {
        log.info("GET /admin/api/certificates");
        return ResponseEntity.ok(adminService.getCertificateList(dto));
    }

    @PostMapping("/certificates")
    public ResponseEntity<Map<String, Object>> addCertificate(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/certificates");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addCertificate(dto);
        result.put("success", success);
        result.put("message", success ? "자격증이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/certificates/{id}")
    public ResponseEntity<Map<String, Object>> updateCertificate(@PathVariable int id, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/certificates/{}", id);
        dto.setCertificateNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyCertificate(dto);
        result.put("success", success);
        result.put("message", success ? "자격증이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/certificates/{id}")
    public ResponseEntity<Map<String, Object>> deleteCertificate(@PathVariable int id) {
        log.info("DELETE /admin/api/certificates/{}", id);
        AdminDTO dto = new AdminDTO();
        dto.setCertificateNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeCertificate(dto);
        result.put("success", success);
        result.put("message", success ? "자격증이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 직업훈련 =====
    @GetMapping("/trainings")
    public ResponseEntity<List<AdminDTO>> getTrainings(AdminDTO dto) {
        log.info("GET /admin/api/trainings");
        return ResponseEntity.ok(adminService.getTrainingList(dto));
    }

    @PostMapping("/trainings")
    public ResponseEntity<Map<String, Object>> addTraining(@RequestBody AdminDTO dto) {
        log.info("POST /admin/api/trainings");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addTraining(dto);
        result.put("success", success);
        result.put("message", success ? "직업훈련이 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/trainings/{id}")
    public ResponseEntity<Map<String, Object>> updateTraining(@PathVariable int id, @RequestBody AdminDTO dto) {
        log.info("PUT /admin/api/trainings/{}", id);
        dto.setTrainingNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyTraining(dto);
        result.put("success", success);
        result.put("message", success ? "직업훈련이 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/trainings/{id}")
    public ResponseEntity<Map<String, Object>> deleteTraining(@PathVariable int id) {
        log.info("DELETE /admin/api/trainings/{}", id);
        AdminDTO dto = new AdminDTO();
        dto.setTrainingNo(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeTraining(dto);
        result.put("success", success);
        result.put("message", success ? "직업훈련이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }
}