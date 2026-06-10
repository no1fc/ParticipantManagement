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

/**
 * 관리자 REST API 컨트롤러.
 * <p>
 * 대시보드 KPI, 사용자 관리, 지점 관리, 참여자 관리, 일일업무보고, 기준금액,
 * 나은기준임금, 배정 히스토리, 알선 관리, 이력서 요청, 자격증, 직업훈련,
 * 상담사 목록, 연계 현황, 운영 현황 대시보드 등의 API를 제공한다.
 * </p>
 * <p>모든 API는 권한 검증(관리자 또는 시스템 관리자)을 수행한 후 처리된다.</p>
 *
 * @see AdminAccessSupport
 * @see AdminService
 */
@Slf4j
@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private AdminService adminService;

    /**
     * 시스템 관리자 전용 접근 권한을 확인한다.
     *
     * @param session HTTP 세션
     * @return 권한이 없으면 401/403 응답, 권한이 있으면 {@code null}
     */
    private ResponseEntity<Map<String, Object>> checkManagerOnly(HttpSession session) {
        LoginBean login = AdminAccessSupport.getLoginBean(session);
        if (login == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        if (!AdminAccessSupport.isManager(session)) return ResponseEntity.status(403).body(Map.of("message", "시스템 관리자 권한이 필요합니다."));
        return null;
    }

    /**
     * 대시보드 KPI 데이터를 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return KPI 데이터 목록 (JSON)
     */
    // ===== 대시보드 KPI =====
    @GetMapping("/kpi")
    public ResponseEntity<?> getKpi(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/kpi");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getDashboardData(dto));
    }

    /**
     * 사용자 목록을 조회한다. (시스템 관리자 전용)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 사용자 목록 (JSON)
     */
    // ===== 사용자 관리 (시스템 관리자 전용) =====
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/users");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getUserList(dto));
    }

    /**
     * 특정 사용자의 상세 정보를 조회한다. (시스템 관리자 전용)
     *
     * @param userNo  사용자 번호
     * @param session HTTP 세션
     * @return 사용자 상세 정보 (JSON)
     */
    @GetMapping("/users/{userNo}")
    public ResponseEntity<?> getUser(@PathVariable int userNo, HttpSession session) {
        log.info("GET /admin/api/users/{}", userNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        return ResponseEntity.ok(adminService.getUserOne(dto));
    }

    /**
     * 새로운 사용자를 등록한다. (시스템 관리자 전용)
     *
     * @param dto     등록할 사용자 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
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

    /**
     * 사용자 정보를 수정한다. (시스템 관리자 전용)
     *
     * @param userNo  수정 대상 사용자 번호
     * @param dto     수정할 사용자 정보
     * @param session HTTP 세션
     * @return 수정 결과 (success, message)
     */
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

    /**
     * 사용자를 삭제한다. (시스템 관리자 전용)
     *
     * @param userNo  삭제 대상 사용자 번호
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * 사용자의 비밀번호를 초기화한다. (시스템 관리자 전용)
     *
     * @param userNo  대상 사용자 번호
     * @param session HTTP 세션
     * @return 초기화 결과 (success, message)
     */
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

    /**
     * 사용자를 승인 처리한다. (시스템 관리자 전용)
     *
     * @param userNo  승인 대상 사용자 번호
     * @param session HTTP 세션
     * @return 승인 결과 (success, message)
     */
    @PutMapping("/users/{userNo}/approve")
    public ResponseEntity<?> approveUser(@PathVariable int userNo, HttpSession session) {
        log.info("PUT /admin/api/users/{}/approve", userNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setMemberNo(userNo);
        dto.setUseStatus("사용");
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.approveUser(dto);
        result.put("success", success);
        result.put("message", success ? "사용자가 승인되었습니다." : "승인에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    /**
     * 다음 사용자 번호를 조회한다. (시스템 관리자 전용)
     *
     * @param session HTTP 세션
     * @return 다음 사용자 번호 (nextNo)
     */
    @GetMapping("/users/next-no")
    public ResponseEntity<?> getNextMemberNo(HttpSession session) {
        log.info("GET /admin/api/users/next-no");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(Map.of("nextNo", adminService.getNextMemberNo()));
    }

    /**
     * 사용자 아이디 중복 여부를 확인한다. (시스템 관리자 전용)
     *
     * @param dto     확인할 아이디 정보가 담긴 DTO
     * @param session HTTP 세션
     * @return 중복 여부 (exists)
     */
    @GetMapping("/users/check-id")
    public ResponseEntity<?> checkUserId(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/users/check-id");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        boolean exists = adminService.checkUserIdExists(dto);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * 지점 목록을 조회한다. (시스템 관리자 전용)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 지점 목록 (JSON)
     */
    // ===== 지점 관리 (시스템 관리자 전용) =====
    @GetMapping("/branches")
    public ResponseEntity<?> getBranches(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/branches");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getBranchList(dto));
    }

    /**
     * 특정 지점의 상세 정보를 조회한다. (시스템 관리자 전용)
     *
     * @param branchNo 지점 번호
     * @param session  HTTP 세션
     * @return 지점 상세 정보 (JSON)
     */
    @GetMapping("/branches/{branchNo}")
    public ResponseEntity<?> getBranch(@PathVariable int branchNo, HttpSession session) {
        log.info("GET /admin/api/branches/{}", branchNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setBranchNo(branchNo);
        return ResponseEntity.ok(adminService.getBranchOne(dto));
    }

    /**
     * 새로운 지점을 등록한다. (시스템 관리자 전용)
     *
     * @param dto     등록할 지점 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
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

    /**
     * 지점 정보를 수정한다. (시스템 관리자 전용)
     *
     * @param branchNo 수정 대상 지점 번호
     * @param dto      수정할 지점 정보
     * @param session  HTTP 세션
     * @return 수정 결과 (success, message)
     */
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

    /**
     * 지점을 비활성화한다. (시스템 관리자 전용)
     * <p>소속 사용자가 있는 경우 사용자는 유지되며 지점만 비활성화된다.</p>
     *
     * @param branchNo 비활성화 대상 지점 번호
     * @param session  HTTP 세션
     * @return 비활성화 결과 (success, message, affectedUsers)
     */
    @DeleteMapping("/branches/{branchNo}")
    public ResponseEntity<?> deleteBranch(@PathVariable int branchNo, HttpSession session) {
        log.info("DELETE /admin/api/branches/{}", branchNo);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setBranchNo(branchNo);
        int userCount = adminService.getBranchUserCount(dto);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.removeBranch(dto);
        String message = success ? "지점이 비활성화되었습니다." : "비활성화에 실패했습니다.";
        if (success && userCount > 0) {
            message += " (소속 사용자 " + userCount + "명은 유지됩니다.)";
        }
        result.put("success", success);
        result.put("message", message);
        result.put("affectedUsers", userCount);
        return ResponseEntity.ok(result);
    }

    /**
     * 참여자 목록을 조회한다.
     *
     * @param dto     검색 조건 DTO (지점관리자는 소속 지점으로 제한됨)
     * @param session HTTP 세션
     * @return 참여자 목록 (JSON)
     */
    // ===== 참여자 관리 =====
    @GetMapping("/participants")
    public ResponseEntity<?> getParticipants(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/participants");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getParticipantList(dto));
    }

    /**
     * 특정 참여자의 상세 정보를 조회한다.
     *
     * @param jobNo   구직번호
     * @param session HTTP 세션
     * @return 참여자 상세 정보 (JSON)
     */
    @GetMapping("/participants/{jobNo}")
    public ResponseEntity<?> getParticipant(@PathVariable int jobNo, HttpSession session) {
        log.info("GET /admin/api/participants/{}", jobNo);        AdminDTO dto = new AdminDTO();
        dto.setJobNo(jobNo);
        return ResponseEntity.ok(adminService.getParticipantOne(dto));
    }

    /**
     * 참여자를 삭제한다. (시스템 관리자 전용)
     *
     * @param jobNo   삭제 대상 구직번호
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * 일일업무보고 목록을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 일일업무보고 목록 (JSON)
     */
    // ===== 일일업무보고 =====
    @GetMapping("/daily-reports")
    public ResponseEntity<?> getDailyReports(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/daily-reports");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getDailyReportList(dto));
    }

    /**
     * 기준금액 목록을 조회한다. (시스템 관리자 전용)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 기준금액 목록 (JSON)
     */
    // ===== 기준금액 (시스템 관리자 전용) =====
    @GetMapping("/standard-amounts")
    public ResponseEntity<?> getStandardAmounts(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/standard-amounts");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getStandardAmountList(dto));
    }

    /**
     * 특정 기준금액의 상세 정보를 조회한다. (시스템 관리자 전용)
     *
     * @param pk      기준금액 PK
     * @param session HTTP 세션
     * @return 기준금액 상세 정보 (JSON)
     */
    @GetMapping("/standard-amounts/{pk}")
    public ResponseEntity<?> getStandardAmount(@PathVariable int pk, HttpSession session) {
        log.info("GET /admin/api/standard-amounts/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        return ResponseEntity.ok(adminService.getStandardAmountOne(dto));
    }

    /**
     * 새로운 기준금액을 등록한다. (시스템 관리자 전용)
     *
     * @param dto     등록할 기준금액 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
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

    /**
     * 기준금액 정보를 수정한다. (시스템 관리자 전용)
     *
     * @param pk      수정 대상 기준금액 PK
     * @param dto     수정할 기준금액 정보
     * @param session HTTP 세션
     * @return 수정 결과 (success, message)
     */
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

    /**
     * 기준금액을 삭제한다. (시스템 관리자 전용)
     *
     * @param pk      삭제 대상 기준금액 PK
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * 나은기준임금 목록을 조회한다. (시스템 관리자 전용)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 나은기준임금 목록 (JSON)
     */
    // ===== 나은기준임금 (시스템 관리자 전용) =====
    @GetMapping("/better-wages")
    public ResponseEntity<?> getBetterWages(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/better-wages");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getBetterWageList(dto));
    }

    /**
     * 특정 나은기준임금의 상세 정보를 조회한다. (시스템 관리자 전용)
     *
     * @param pk      나은기준임금 PK
     * @param session HTTP 세션
     * @return 나은기준임금 상세 정보 (JSON)
     */
    @GetMapping("/better-wages/{pk}")
    public ResponseEntity<?> getBetterWage(@PathVariable int pk, HttpSession session) {
        log.info("GET /admin/api/better-wages/{}", pk);
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        AdminDTO dto = new AdminDTO();
        dto.setPk(pk);
        return ResponseEntity.ok(adminService.getBetterWageOne(dto));
    }

    /**
     * 새로운 나은기준임금을 등록한다. (시스템 관리자 전용)
     *
     * @param dto     등록할 나은기준임금 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
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

    /**
     * 나은기준임금 정보를 수정한다. (시스템 관리자 전용)
     *
     * @param pk      수정 대상 나은기준임금 PK
     * @param dto     수정할 나은기준임금 정보
     * @param session HTTP 세션
     * @return 수정 결과 (success, message)
     */
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

    /**
     * 나은기준임금을 삭제한다. (시스템 관리자 전용)
     *
     * @param pk      삭제 대상 나은기준임금 PK
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * CSV 배정 히스토리 목록을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return CSV 배정 히스토리 목록 (JSON)
     */
    // ===== 배정 히스토리 =====
    @GetMapping("/assignment-csv-history")
    public ResponseEntity<?> getCsvHistory(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/assignment-csv-history");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getCsvHistoryList(dto));
    }

    /**
     * 산식 배정 히스토리 목록을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 산식 배정 히스토리 목록 (JSON)
     */
    @GetMapping("/assignment-formula-history")
    public ResponseEntity<?> getFormulaHistory(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/assignment-formula-history");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getFormulaHistoryList(dto));
    }

    /**
     * 알선 목록을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 알선 목록 (JSON)
     */
    // ===== 알선 관리 =====
    @GetMapping("/job-placements")
    public ResponseEntity<?> getJobPlacements(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/job-placements");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getJobPlacementList(dto));
    }

    /**
     * 특정 알선의 상세 정보를 조회한다.
     *
     * @param regNo   알선 등록번호
     * @param session HTTP 세션
     * @return 알선 상세 정보 (JSON)
     */
    @GetMapping("/job-placements/{regNo}")
    public ResponseEntity<?> getJobPlacement(@PathVariable int regNo, HttpSession session) {
        log.info("GET /admin/api/job-placements/{}", regNo);        AdminDTO dto = new AdminDTO();
        dto.setRegistrationNo(regNo);
        return ResponseEntity.ok(adminService.getJobPlacementOne(dto));
    }

    /**
     * 새로운 알선 정보를 등록한다.
     *
     * @param dto     등록할 알선 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
    @PostMapping("/job-placements")
    public ResponseEntity<?> addJobPlacement(@RequestBody AdminDTO dto, HttpSession session) {
        log.info("POST /admin/api/job-placements");        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.addJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 등록되었습니다." : "등록에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    /**
     * 알선 정보를 수정한다.
     *
     * @param regNo   수정 대상 알선 등록번호
     * @param dto     수정할 알선 정보
     * @param session HTTP 세션
     * @return 수정 결과 (success, message)
     */
    @PutMapping("/job-placements/{regNo}")
    public ResponseEntity<?> updateJobPlacement(@PathVariable int regNo, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/job-placements/{}", regNo);        dto.setRegistrationNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.modifyJobPlacement(dto);
        result.put("success", success);
        result.put("message", success ? "알선정보가 수정되었습니다." : "수정에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    /**
     * 알선 정보를 삭제한다. (시스템 관리자 전용)
     *
     * @param regNo   삭제 대상 알선 등록번호
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * 이력서 요청 목록을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 이력서 요청 목록 (JSON)
     */
    // ===== 이력서 요청 =====
    @GetMapping("/resume-requests")
    public ResponseEntity<?> getResumeRequests(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/resume-requests");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getResumeRequestList(dto));
    }

    /**
     * 특정 이력서 요청의 상세 정보를 조회한다.
     *
     * @param regNo   이력서 요청 등록번호
     * @param session HTTP 세션
     * @return 이력서 요청 상세 정보 (JSON)
     */
    @GetMapping("/resume-requests/{regNo}")
    public ResponseEntity<?> getResumeRequest(@PathVariable int regNo, HttpSession session) {
        log.info("GET /admin/api/resume-requests/{}", regNo);        AdminDTO dto = new AdminDTO();
        dto.setResumeRegNo(regNo);
        return ResponseEntity.ok(adminService.getResumeRequestOne(dto));
    }

    /**
     * 이력서 요청의 상태를 변경한다.
     *
     * @param regNo   대상 이력서 요청 등록번호
     * @param dto     변경할 상태 정보
     * @param session HTTP 세션
     * @return 상태 변경 결과 (success, message)
     */
    @PutMapping("/resume-requests/{regNo}/status")
    public ResponseEntity<?> updateResumeStatus(@PathVariable int regNo, @RequestBody AdminDTO dto, HttpSession session) {
        log.info("PUT /admin/api/resume-requests/{}/status", regNo);        dto.setResumeRegNo(regNo);
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.updateResumeStatus(dto);
        result.put("success", success);
        result.put("message", success ? "상태가 변경되었습니다." : "변경에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    /**
     * 자격증 목록을 조회한다. (시스템 관리자 전용)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 자격증 목록 (JSON)
     */
    // ===== 자격증 (시스템 관리자 전용) =====
    @GetMapping("/certificates")
    public ResponseEntity<?> getCertificates(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/certificates");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getCertificateList(dto));
    }

    /**
     * 새로운 자격증을 등록한다. (시스템 관리자 전용)
     *
     * @param dto     등록할 자격증 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
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

    /**
     * 자격증 정보를 수정한다. (시스템 관리자 전용)
     *
     * @param id      수정 대상 자격증 번호
     * @param dto     수정할 자격증 정보
     * @param session HTTP 세션
     * @return 수정 결과 (success, message)
     */
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

    /**
     * 자격증을 삭제한다. (시스템 관리자 전용)
     *
     * @param id      삭제 대상 자격증 번호
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * 직업훈련 목록을 조회한다. (시스템 관리자 전용)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 직업훈련 목록 (JSON)
     */
    // ===== 직업훈련 (시스템 관리자 전용) =====
    @GetMapping("/trainings")
    public ResponseEntity<?> getTrainings(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/trainings");
        ResponseEntity<Map<String, Object>> denied = checkManagerOnly(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(adminService.getTrainingList(dto));
    }

    /**
     * 새로운 직업훈련을 등록한다. (시스템 관리자 전용)
     *
     * @param dto     등록할 직업훈련 정보
     * @param session HTTP 세션
     * @return 등록 결과 (success, message)
     */
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

    /**
     * 직업훈련 정보를 수정한다. (시스템 관리자 전용)
     *
     * @param id      수정 대상 직업훈련 번호
     * @param dto     수정할 직업훈련 정보
     * @param session HTTP 세션
     * @return 수정 결과 (success, message)
     */
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

    /**
     * 직업훈련을 삭제한다. (시스템 관리자 전용)
     *
     * @param id      삭제 대상 직업훈련 번호
     * @param session HTTP 세션
     * @return 삭제 결과 (success, message)
     */
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

    /**
     * 상담사 목록을 조회한다. (공통 API)
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 상담사 목록 (JSON)
     */
    // ===== 상담사 목록 (공통 API) =====
    @GetMapping("/counselors")
    public ResponseEntity<?> getCounselors(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/counselors");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getCounselorList(dto));
    }

    /**
     * 상담사별 알선 통계를 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 상담사별 알선 통계 (JSON)
     */
    // ===== 상담사별 통계 =====
    @GetMapping("/placement-stats")
    public ResponseEntity<?> getPlacementStats(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/placement-stats");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getPlacementStatsByCounselor(dto));
    }

    /**
     * 연계 현황 통계를 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 연계 현황 통계 (JSON)
     */
    // ===== 연계 현황 =====
    @GetMapping("/linkage-stats")
    public ResponseEntity<?> getLinkageStats(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/linkage-stats");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getLinkageStats(dto));
    }

    /**
     * 상담사별 연계 현황을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 상담사별 연계 현황 (JSON)
     */
    @GetMapping("/linkage-stats/by-counselor")
    public ResponseEntity<?> getLinkageByCounselor(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/linkage-stats/by-counselor");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getLinkageByCounselor(dto));
    }

    /**
     * 유형별 연계 현황을 조회한다.
     *
     * @param dto     검색 조건 DTO
     * @param session HTTP 세션
     * @return 유형별 연계 현황 (JSON)
     */
    @GetMapping("/linkage-stats/by-type")
    public ResponseEntity<?> getLinkageByType(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/linkage-stats/by-type");        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getLinkageByType(dto));
    }

    /**
     * 지점별 운영 현황 대시보드 데이터를 조회한다.
     *
     * @param dto     검색 조건 DTO (검색년도 포함)
     * @param session HTTP 세션
     * @return 운영 현황 데이터 목록 (JSON)
     */
    // ===== 운영 현황 대시보드 =====
    @GetMapping("/management-dashboard")
    public ResponseEntity<?> getManagementDashboardData(AdminDTO dto, HttpSession session) {
        log.info("GET /admin/api/management-dashboard year={}", dto.getSearchYear());        AdminAccessSupport.enforceBranchScope(session, dto);
        return ResponseEntity.ok(adminService.getManagementDashboardData(dto));
    }

    /**
     * 지점별 운영 현황 데이터를 엑셀 파일로 다운로드한다.
     * <p>Apache POI를 사용하여 XLSX 형식의 엑셀 파일을 생성하며, 합계 행을 포함한다.</p>
     *
     * @param dto      검색 조건 DTO (검색년도 포함)
     * @param session  HTTP 세션
     * @param response HTTP 응답 (엑셀 파일 스트림 출력용)
     */
    @GetMapping("/management-dashboard/excel")
    public void downloadManagementDashboardExcel(AdminDTO dto, HttpSession session, jakarta.servlet.http.HttpServletResponse response) {
        log.info("GET /admin/api/management-dashboard/excel year={}", dto.getSearchYear());
        if (!AdminAccessSupport.hasAdminAccess(session)) {
            response.setStatus(403);
            return;
        }
        AdminAccessSupport.enforceBranchScope(session, dto);
        List<AdminDTO> dataList = adminService.getManagementDashboardData(dto);

        try {
            String year = dto.getSearchYear() != null ? dto.getSearchYear() : String.valueOf(java.time.Year.now().getValue());
            String fileName = java.net.URLEncoder.encode("지점별_운영현황_" + year + "년.xlsx", java.nio.charset.StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("운영현황");

            // 헤더 스타일
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            // 헤더 행
            String[] headers = {"지점명", "배정인원", "자체모집인원수", "참여자수", "취소인원", "상담사수", "상담사 1명당 초기상담 인원"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행
            int totalAssigned = 0, totalSelfRecruit = 0, totalActive = 0, totalCanceled = 0;
            double totalCounselor = 0;
            int rowNum = 1;
            for (AdminDTO item : dataList) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getBranchName());
                row.createCell(1).setCellValue(item.getAssignedCount());
                row.createCell(2).setCellValue(item.getSelfRecruitCount());
                row.createCell(3).setCellValue(item.getActiveParticipantCount());
                row.createCell(4).setCellValue(item.getCanceledCount());
                row.createCell(5).setCellValue(item.getCounselorWeighted());
                row.createCell(6).setCellValue(item.getCounselorLoad());

                totalAssigned += item.getAssignedCount();
                totalSelfRecruit += item.getSelfRecruitCount();
                totalActive += item.getActiveParticipantCount();
                totalCanceled += item.getCanceledCount();
                totalCounselor += item.getCounselorWeighted();
            }

            // 합계 행
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowNum);
            org.apache.poi.ss.usermodel.CellStyle totalStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);

            org.apache.poi.ss.usermodel.Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("전체 합계");
            totalLabelCell.setCellStyle(totalStyle);
            totalRow.createCell(1).setCellValue(totalAssigned);
            totalRow.createCell(2).setCellValue(totalSelfRecruit);
            totalRow.createCell(3).setCellValue(totalActive);
            totalRow.createCell(4).setCellValue(totalCanceled);
            totalRow.createCell(5).setCellValue(Math.round(totalCounselor * 100.0) / 100.0);
            totalRow.createCell(6).setCellValue(totalCounselor > 0 ? Math.round((totalActive / totalCounselor) * 10.0) / 10.0 : 0);

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            log.error("운영 현황 엑셀 다운로드 실패", e);
            response.setStatus(500);
        }
    }
}