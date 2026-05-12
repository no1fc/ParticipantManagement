package com.jobmoa.app.CounselMain.view.schedule;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.schedule.ScheduleDTO;
import com.jobmoa.app.CounselMain.biz.schedule.ScheduleService;
import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
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
@RequestMapping("/api/schedule")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebSocketService webSocketService;

    private LoginBean getLogin(HttpSession session) {
        return (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
    }

    private ResponseEntity<Map<String, Object>> unauthorized() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "로그인이 필요합니다.");
        return ResponseEntity.status(401).body(result);
    }

    // ===== 일정 목록 조회 =====
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getScheduleList(ScheduleDTO dto, HttpSession session) {
        log.info("GET /api/schedule/list");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        dto.setCounselorId(login.getMemberUserID());
        List<ScheduleDTO> list = scheduleService.getScheduleList(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // ===== 일정 상세 조회 =====
    @GetMapping("/detail/{scheduleId}")
    public ResponseEntity<Map<String, Object>> getScheduleDetail(@PathVariable int scheduleId, HttpSession session) {
        log.info("GET /api/schedule/detail/{}", scheduleId);
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(scheduleId);
        ScheduleDTO detail = scheduleService.getScheduleDetail(dto);

        Map<String, Object> result = new HashMap<>();
        if (detail == null) {
            result.put("success", false);
            result.put("message", "일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }

        result.put("success", true);
        result.put("data", detail);
        return ResponseEntity.ok(result);
    }

    // ===== 일정 등록 =====
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSchedule(@RequestBody ScheduleDTO dto, HttpSession session) {
        log.info("POST /api/schedule/save");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        // 필수값 검증
        if (dto.getScheduleDate() == null || dto.getScheduleDate().isEmpty()
                || dto.getStartTime() == null || dto.getStartTime().isEmpty()
                || dto.getScheduleType() == null || dto.getScheduleType().isEmpty()) {
            result.put("success", false);
            result.put("message", "필수 항목을 입력해주세요. (날짜, 시작시간, 일정유형)");
            return ResponseEntity.badRequest().body(result);
        }

        dto.setCounselorId(login.getMemberUserID());
        int inserted = scheduleService.insertSchedule(dto);

        if (inserted == -1) {
            result.put("success", false);
            result.put("message", "해당 시간에 이미 일정이 존재합니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", true);
        result.put("message", "일정이 등록되었습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 일정 수정 =====
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateSchedule(@RequestBody ScheduleDTO dto, HttpSession session) {
        log.info("PUT /api/schedule/update");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        // 소유권 확인: 본인 일정이거나 관리자
        ScheduleDTO existing = scheduleService.getScheduleDetail(dto);
        if (existing == null) {
            result.put("success", false);
            result.put("message", "일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }

        boolean isOwner = login.getMemberUserID().equals(existing.getCounselorId());
        Boolean isManager = (Boolean) session.getAttribute("IS_MANAGER");
        Boolean isBranchManager = (Boolean) session.getAttribute("IS_BRANCH_MANAGER");

        if (!isOwner && !Boolean.TRUE.equals(isManager) && !Boolean.TRUE.equals(isBranchManager)) {
            result.put("success", false);
            result.put("message", "본인의 일정만 수정할 수 있습니다.");
            return ResponseEntity.status(403).body(result);
        }

        int updated = scheduleService.updateSchedule(dto);

        if (updated == -1) {
            result.put("success", false);
            result.put("message", "해당 시간에 이미 일정이 존재합니다.");
            return ResponseEntity.status(409).body(result);
        }

        // 관리자가 타인 일정 수정 시 WebSocket 알림 전송
        if (!isOwner) {
            sendScheduleModifiedNotification(login.getMemberUserName(), existing.getCounselorId(), "수정");
        }

        result.put("success", true);
        result.put("message", "일정이 수정되었습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 일정 삭제 =====
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Map<String, Object>> deleteSchedule(@PathVariable int scheduleId, HttpSession session) {
        log.info("DELETE /api/schedule/delete/{}", scheduleId);
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(scheduleId);

        // 소유권 확인: 본인 일정이거나 관리자
        ScheduleDTO existing = scheduleService.getScheduleDetail(dto);
        if (existing == null) {
            result.put("success", false);
            result.put("message", "일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }

        boolean isOwner = login.getMemberUserID().equals(existing.getCounselorId());
        Boolean isManager = (Boolean) session.getAttribute("IS_MANAGER");
        Boolean isBranchManager = (Boolean) session.getAttribute("IS_BRANCH_MANAGER");

        if (!isOwner && !Boolean.TRUE.equals(isManager) && !Boolean.TRUE.equals(isBranchManager)) {
            result.put("success", false);
            result.put("message", "본인의 일정만 삭제할 수 있습니다.");
            return ResponseEntity.status(403).body(result);
        }

        boolean deleted = scheduleService.deleteSchedule(dto);

        // 관리자가 타인 일정 삭제 시 WebSocket 알림 전송
        if (deleted && !isOwner) {
            sendScheduleModifiedNotification(login.getMemberUserName(), existing.getCounselorId(), "삭제");
        }

        result.put("success", deleted);
        result.put("message", deleted ? "일정이 삭제되었습니다." : "삭제에 실패했습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 드래그 앤 드롭 일정 변경 =====
    @PutMapping("/drag-update")
    public ResponseEntity<Map<String, Object>> dragUpdateSchedule(@RequestBody ScheduleDTO dto, HttpSession session) {
        log.info("PUT /api/schedule/drag-update");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        Map<String, Object> result = new HashMap<>();

        // 본인 일정만 드래그 가능 (관리자도 불가)
        ScheduleDTO existing = scheduleService.getScheduleDetail(dto);
        if (existing == null) {
            result.put("success", false);
            result.put("message", "일정을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(result);
        }

        if (!login.getMemberUserID().equals(existing.getCounselorId())) {
            result.put("success", false);
            result.put("message", "본인의 일정만 변경할 수 있습니다.");
            return ResponseEntity.status(403).body(result);
        }

        int updated = scheduleService.updateScheduleDrag(dto);

        if (updated == -1) {
            result.put("success", false);
            result.put("message", "해당 시간에 이미 일정이 존재합니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", true);
        result.put("message", "일정이 변경되었습니다.");
        return ResponseEntity.ok(result);
    }

    // ===== 참여자 검색 =====
    @GetMapping("/participants")
    public ResponseEntity<Map<String, Object>> searchParticipants(@RequestParam(defaultValue = "") String keyword, HttpSession session) {
        log.info("GET /api/schedule/participants?keyword={}", keyword);
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setKeyword(keyword);
        dto.setCounselorId(login.getMemberUserID());
        List<ScheduleDTO> list = scheduleService.searchParticipant(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // ===== 오늘의 일정 (대시보드 위젯) =====
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodaySchedule(HttpSession session) {
        log.info("GET /api/schedule/today");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setCounselorId(login.getMemberUserID());
        List<ScheduleDTO> list = scheduleService.getTodaySchedule(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        result.put("count", list.size());
        return ResponseEntity.ok(result);
    }

    // ===== 2단계: 관리자 지점 일정 통합 조회 API =====

    @GetMapping("/branch-list")
    public ResponseEntity<Map<String, Object>> getBranchScheduleList(ScheduleDTO dto, HttpSession session) {
        log.info("GET /api/schedule/branch-list");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        if (!isManagerOrBranchManager(session)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(result);
        }

        dto.setBranch(login.getMemberBranch());
        List<ScheduleDTO> list = scheduleService.getBranchScheduleList(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getScheduleStats(HttpSession session) {
        log.info("GET /api/schedule/stats");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        if (!isManagerOrBranchManager(session)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(result);
        }

        ScheduleDTO dto = new ScheduleDTO();
        dto.setBranch(login.getMemberBranch());
        ScheduleDTO stats = scheduleService.getScheduleStats(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/counselors")
    public ResponseEntity<Map<String, Object>> getCounselors(HttpSession session) {
        log.info("GET /api/schedule/counselors");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        if (!isManagerOrBranchManager(session)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(result);
        }

        ScheduleDTO dto = new ScheduleDTO();
        dto.setBranch(login.getMemberBranch());
        List<ScheduleDTO> list = scheduleService.getCounselorsByBranch(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // ===== 내부 유틸 메서드 =====

    private boolean isManagerOrBranchManager(HttpSession session) {
        Boolean isManager = (Boolean) session.getAttribute("IS_MANAGER");
        Boolean isBranchManager = (Boolean) session.getAttribute("IS_BRANCH_MANAGER");
        return Boolean.TRUE.equals(isManager) || Boolean.TRUE.equals(isBranchManager);
    }

    private void sendScheduleModifiedNotification(String managerName, String targetCounselorId, String action) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SCHEDULE_MODIFIED");
        notification.put("title", "일정 변경 알림");
        notification.put("message", managerName + "님이 회원님의 일정을 " + action + "했습니다.");
        webSocketService.sendObject("/topic/schedule-modified/" + targetCounselorId, notification);
        log.info("일정 변경 알림 전송: 대상={}, 액션={}", targetCounselorId, action);
    }
}
