package com.jobmoa.app.CounselMain.view.hr;

import com.jobmoa.app.CounselMain.biz.hr.HrAccountDTO;
import com.jobmoa.app.CounselMain.biz.hr.HrAccountService;
import com.jobmoa.app.CounselMain.biz.hr.HrDashboardService;
import com.jobmoa.app.CounselMain.biz.hr.HrDepartmentDTO;
import com.jobmoa.app.CounselMain.biz.hr.HrDepartmentService;
import com.jobmoa.app.CounselMain.biz.hr.HrSiteAccessDTO;
import com.jobmoa.app.CounselMain.biz.hr.HrSiteAccessService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * HR(입퇴사자관리) REST API 컨트롤러.
 * <p>부서/조직, 계정, 사이트 접속·권한 CRUD를 제공한다. 모든 API는 시스템 관리자 권한 검증 후 처리된다(데모).</p>
 *
 * @see HrPageController
 * @see AdminAccessSupport
 */
@Slf4j
@RestController
@RequestMapping("/hr/api")
public class HrApiController {

    @Autowired
    private HrDepartmentService hrDepartmentService;

    @Autowired
    private HrAccountService hrAccountService;

    @Autowired
    private HrSiteAccessService hrSiteAccessService;

    @Autowired
    private HrDashboardService hrDashboardService;

    /** HR 로그인({@code HR_LOGIN_DATA}) 여부를 확인한다. 미인증이면 401, 인증이면 null.
     *  ({@link HrApiInterceptor}가 1차 차단하며 이 검증은 방어적 이중 확인이다.) */
    private ResponseEntity<Map<String, Object>> checkManager(HttpSession session) {
        if (!HrAccessSupport.isAuthed(session)) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        return null;
    }

    private Map<String, Object> result(boolean success, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        return map;
    }

    // ===== 인원현황 대시보드 (읽기전용) =====
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(hrDashboardService.getDashboard());
    }

    // ===== 부서/조직 관리 =====
    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments(HrDepartmentDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(hrDepartmentService.getDepartmentList(dto));
    }

    @GetMapping("/departments/{deptCode}")
    public ResponseEntity<?> getDepartment(@PathVariable String deptCode, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        HrDepartmentDTO dto = new HrDepartmentDTO();
        dto.setDeptCode(deptCode);
        return ResponseEntity.ok(hrDepartmentService.getDepartmentOne(dto));
    }

    @PostMapping("/departments")
    public ResponseEntity<?> addDepartment(@RequestBody HrDepartmentDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        if (dto.getDeptCode() == null || dto.getDeptCode().isBlank()) {
            return ResponseEntity.ok(result(false, "부서코드는 필수입니다."));
        }
        if (hrDepartmentService.isDeptCodeExists(dto)) {
            return ResponseEntity.ok(result(false, "이미 존재하는 부서코드입니다."));
        }
        boolean success = hrDepartmentService.addDepartment(dto);
        return ResponseEntity.ok(result(success, success ? "부서가 등록되었습니다." : "등록에 실패했습니다."));
    }

    @PutMapping("/departments/{deptCode}")
    public ResponseEntity<?> updateDepartment(@PathVariable String deptCode, @RequestBody HrDepartmentDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        dto.setDeptCode(deptCode);
        boolean success = hrDepartmentService.modifyDepartment(dto);
        return ResponseEntity.ok(result(success, success ? "부서 정보가 수정되었습니다." : "수정에 실패했습니다."));
    }

    @DeleteMapping("/departments/{deptCode}")
    public ResponseEntity<?> deleteDepartment(@PathVariable String deptCode, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        HrDepartmentDTO dto = new HrDepartmentDTO();
        dto.setDeptCode(deptCode);
        boolean success = hrDepartmentService.removeDepartment(dto);
        return ResponseEntity.ok(result(success, success ? "부서가 비활성화되었습니다." : "비활성화에 실패했습니다."));
    }

    // ===== 계정 관리 =====
    @GetMapping("/accounts")
    public ResponseEntity<?> getAccounts(HrAccountDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(hrAccountService.getAccountList(dto));
    }

    @GetMapping("/accounts/{userId}")
    public ResponseEntity<?> getAccount(@PathVariable String userId, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        HrAccountDTO dto = new HrAccountDTO();
        dto.setUserId(userId);
        return ResponseEntity.ok(hrAccountService.getAccountOne(dto));
    }

    @PutMapping("/accounts/{userId}/status")
    public ResponseEntity<?> changeAccountStatus(@PathVariable String userId, @RequestBody HrAccountDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        dto.setUserId(userId);
        boolean success = hrAccountService.changeStatus(dto);
        return ResponseEntity.ok(result(success, success ? "계정 상태가 변경되었습니다." : "변경에 실패했습니다."));
    }

    @PostMapping("/accounts/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable String userId, @RequestBody HrAccountDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.ok(result(false, "임시 비밀번호를 입력하세요."));
        }
        dto.setUserId(userId);
        boolean success = hrAccountService.resetPassword(dto);
        return ResponseEntity.ok(result(success, success ? "비밀번호가 초기화되었습니다." : "초기화에 실패했습니다."));
    }

    @PostMapping("/accounts/{userId}/unlock")
    public ResponseEntity<?> unlockAccount(@PathVariable String userId, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        HrAccountDTO dto = new HrAccountDTO();
        dto.setUserId(userId);
        boolean success = hrAccountService.unlock(dto);
        return ResponseEntity.ok(result(success, success ? "잠금이 해제되었습니다." : "해제에 실패했습니다."));
    }

    // ===== 사이트 접속·권한 관리 =====
    @GetMapping("/site-access")
    public ResponseEntity<?> getSiteAccess(HrSiteAccessDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(hrSiteAccessService.getSiteAccessList(dto));
    }

    @GetMapping("/site-access/{accessPk}")
    public ResponseEntity<?> getSiteAccessOne(@PathVariable int accessPk, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        HrSiteAccessDTO dto = new HrSiteAccessDTO();
        dto.setAccessPk(accessPk);
        return ResponseEntity.ok(hrSiteAccessService.getSiteAccessOne(dto));
    }

    @PostMapping("/site-access")
    public ResponseEntity<?> addSiteAccess(@RequestBody HrSiteAccessDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        if (dto.getUserId() == null || dto.getUserId().isBlank() || dto.getSiteCode() == null || dto.getSiteCode().isBlank()) {
            return ResponseEntity.ok(result(false, "직원아이디와 사이트는 필수입니다."));
        }
        if (hrSiteAccessService.isSiteAccessExists(dto)) {
            return ResponseEntity.ok(result(false, "이미 부여된 (직원·사이트·부서) 접속입니다."));
        }
        boolean success = hrSiteAccessService.addSiteAccess(dto);
        return ResponseEntity.ok(result(success, success ? "사이트 접속이 부여되었습니다." : "부여에 실패했습니다."));
    }

    @PutMapping("/site-access/{accessPk}")
    public ResponseEntity<?> updateSiteAccess(@PathVariable int accessPk, @RequestBody HrSiteAccessDTO dto, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        dto.setAccessPk(accessPk);
        boolean success = hrSiteAccessService.modifySiteAccess(dto);
        return ResponseEntity.ok(result(success, success ? "접속 정보가 수정되었습니다." : "수정에 실패했습니다."));
    }

    @DeleteMapping("/site-access/{accessPk}")
    public ResponseEntity<?> deleteSiteAccess(@PathVariable int accessPk, HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        HrSiteAccessDTO dto = new HrSiteAccessDTO();
        dto.setAccessPk(accessPk);
        boolean success = hrSiteAccessService.removeSiteAccess(dto);
        return ResponseEntity.ok(result(success, success ? "사이트 접속이 회수되었습니다." : "회수에 실패했습니다."));
    }

    /** 사이트 드롭다운 목록 (J_사이트). */
    @GetMapping("/sites")
    public ResponseEntity<?> getSites(HttpSession session) {
        ResponseEntity<Map<String, Object>> denied = checkManager(session);
        if (denied != null) return denied;
        return ResponseEntity.ok(hrSiteAccessService.getSiteList());
    }
}
