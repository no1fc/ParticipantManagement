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

/**
 * 상담 일정 관리 REST API 컨트롤러.
 * 상담사의 일정 CRUD, 드래그 앤 드롭 일정 변경, 참여자 검색,
 * 오늘의 일정 조회, 관리자 지점 일정 통합 조회 등의 기능을 제공한다.
 * 관리자가 타인의 일정을 수정/삭제할 경우 WebSocket 알림을 전송한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/schedule")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 세션에서 로그인 정보를 조회한다.
     *
     * @param session HTTP 세션
     * @return 로그인 정보 객체 (없으면 null)
     */
    private LoginBean getLogin(HttpSession session) {
        return (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
    }

    /**
     * 인증되지 않은 요청에 대한 401 응답을 반환한다.
     *
     * @return 401 상태 코드와 오류 메시지를 담은 응답
     */
    private ResponseEntity<Map<String, Object>> unauthorized() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "로그인이 필요합니다.");
        return ResponseEntity.status(401).body(result);
    }

    /**
     * 로그인한 상담사의 일정 목록을 조회한다.
     *
     * @param dto     일정 검색 조건 DTO
     * @param session HTTP 세션
     * @return 일정 목록 JSON 응답
     */
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

    /**
     * 일정 상세 정보를 조회한다.
     *
     * @param scheduleId 일정 ID
     * @param session    HTTP 세션
     * @return 일정 상세 데이터 또는 404 응답
     */
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

    /**
     * 새 일정을 등록한다.
     * 날짜, 시작시간, 일정유형은 필수이며, 동일 일자에 중복 상담이 있을 경우 409 응답을 반환한다.
     *
     * @param dto     등록할 일정 데이터
     * @param session HTTP 세션
     * @return 등록 결과 JSON 응답
     */
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
            result.put("message", "해당 일자에 이미 등록된 상담이 있습니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", true);
        result.put("message", "일정이 등록되었습니다.");
        return ResponseEntity.ok(result);
    }

    /**
     * 일정을 수정한다.
     * 본인 일정이거나 관리자 권한이 있어야 수정 가능하며,
     * 관리자가 타인 일정 수정 시 WebSocket 알림을 전송한다.
     *
     * @param dto     수정할 일정 데이터
     * @param session HTTP 세션
     * @return 수정 결과 JSON 응답
     */
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

        // 중복체크용 counselorId 설정
        dto.setCounselorId(existing.getCounselorId());

        int updated = scheduleService.updateSchedule(dto);

        if (updated == -1) {
            result.put("success", false);
            result.put("message", "해당 일자에 이미 등록된 상담이 있습니다.");
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

    /**
     * 일정을 삭제한다.
     * 본인 일정이거나 관리자 권한이 있어야 삭제 가능하며,
     * 관리자가 타인 일정 삭제 시 WebSocket 알림을 전송한다.
     *
     * @param scheduleId 삭제할 일정 ID
     * @param session    HTTP 세션
     * @return 삭제 결과 JSON 응답
     */
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

    /**
     * 드래그 앤 드롭으로 일정 날짜/시간을 변경한다.
     * 본인 일정만 드래그 가능하며, 관리자도 타인 일정 드래그는 불가하다.
     *
     * @param dto     변경할 일정 데이터 (날짜, 시간 등)
     * @param session HTTP 세션
     * @return 변경 결과 JSON 응답
     */
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

        // 중복체크용 counselorId 설정
        dto.setCounselorId(existing.getCounselorId());

        int updated = scheduleService.updateScheduleDrag(dto);

        if (updated == -1) {
            result.put("success", false);
            result.put("message", "해당 일자에 이미 등록된 상담이 있습니다.");
            return ResponseEntity.status(409).body(result);
        }

        result.put("success", true);
        result.put("message", "일정이 변경되었습니다.");
        return ResponseEntity.ok(result);
    }

    /**
     * 일정 등록 시 참여자를 키워드로 검색한다.
     *
     * @param keyword 검색 키워드 (참여자명 등)
     * @param session HTTP 세션
     * @return 검색된 참여자 목록 JSON 응답
     */
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

    /**
     * 오늘의 일정을 조회한다 (대시보드 위젯용).
     *
     * @param session HTTP 세션
     * @return 오늘의 일정 목록 및 건수 JSON 응답
     */
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

    /**
     * 관리자 지점 내 전체 상담사의 일정 목록을 통합 조회한다.
     *
     * @param dto     일정 검색 조건 DTO
     * @param session HTTP 세션
     * @return 지점 일정 목록 JSON 응답
     */
    @GetMapping("/branch-list")
    public ResponseEntity<Map<String, Object>> getBranchScheduleList(ScheduleDTO dto, HttpSession session) {
        log.info("GET /api/schedule/branch-list");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        dto.setBranch(login.getMemberBranch());
        List<ScheduleDTO> list = scheduleService.getBranchScheduleList(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    /**
     * 지점 일정 통계 정보를 조회한다.
     *
     * @param session HTTP 세션
     * @return 일정 통계 데이터 JSON 응답
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getScheduleStats(HttpSession session) {
        log.info("GET /api/schedule/stats");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setBranch(login.getMemberBranch());
        ScheduleDTO stats = scheduleService.getScheduleStats(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        return ResponseEntity.ok(result);
    }

    /**
     * 지점 소속 상담사 목록을 조회한다.
     *
     * @param session HTTP 세션
     * @return 상담사 목록 JSON 응답
     */
    @GetMapping("/counselors")
    public ResponseEntity<Map<String, Object>> getCounselors(HttpSession session) {
        log.info("GET /api/schedule/counselors");
        LoginBean login = getLogin(session);
        if (login == null) return unauthorized();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setBranch(login.getMemberBranch());
        List<ScheduleDTO> list = scheduleService.getCounselorsByBranch(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    /**
     * 세션의 사용자가 총관리자 또는 지점장인지 확인한다.
     *
     * @param session HTTP 세션
     * @return 관리자 또는 지점장이면 true
     */
    private boolean isManagerOrBranchManager(HttpSession session) {
        Boolean isManager = (Boolean) session.getAttribute("IS_MANAGER");
        Boolean isBranchManager = (Boolean) session.getAttribute("IS_BRANCH_MANAGER");
        return Boolean.TRUE.equals(isManager) || Boolean.TRUE.equals(isBranchManager);
    }

    /**
     * 관리자가 타인의 일정을 변경했을 때 대상 상담사에게 WebSocket 알림을 전송한다.
     *
     * @param managerName        변경을 수행한 관리자 이름
     * @param targetCounselorId  알림 대상 상담사 ID
     * @param action             수행된 동작 ("수정" 또는 "삭제")
     */
    private void sendScheduleModifiedNotification(String managerName, String targetCounselorId, String action) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SCHEDULE_MODIFIED");
        notification.put("title", "일정 변경 알림");
        notification.put("message", managerName + "님이 회원님의 일정을 " + action + "했습니다.");
        webSocketService.sendObject("/topic/schedule-modified/" + targetCounselorId, notification);
        log.info("일정 변경 알림 전송: 대상={}, 액션={}", targetCounselorId, action);
    }
}
