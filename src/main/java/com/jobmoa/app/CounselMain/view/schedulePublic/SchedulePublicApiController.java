package com.jobmoa.app.CounselMain.view.schedulePublic;

import com.jobmoa.app.CounselMain.biz.schedule.ScheduleDTO;
import com.jobmoa.app.CounselMain.biz.schedule.ScheduleService;
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
@RequestMapping("/schedulePublic/api")
public class SchedulePublicApiController {

    @Autowired
    private ScheduleService scheduleService;

    private static final String SESSION_KEY = "SCHEDULE_PUBLIC_AUTH";

    // ===== 인증 =====
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> request, HttpSession session) {
        log.info("POST /schedulePublic/api/verify");
        Map<String, Object> result = new HashMap<>();

        String uniqueNumber = request.get("uniqueNumber");
        String branch = request.get("branch");

        if (uniqueNumber == null || uniqueNumber.isEmpty() || branch == null || branch.isEmpty()) {
            result.put("success", false);
            result.put("message", "지점과 접근코드를 입력해주세요.");
            return ResponseEntity.badRequest().body(result);
        }

        ScheduleDTO dto = new ScheduleDTO();
        dto.setUniqueNumber(uniqueNumber);
        dto.setBranch(branch);

        boolean verified = scheduleService.selectVerifyPublicAccess(dto);
        if (!verified) {
            result.put("success", false);
            result.put("message", "접근코드가 올바르지 않습니다.");
            return ResponseEntity.status(401).body(result);
        }

        session.setAttribute(SESSION_KEY, branch);
        result.put("success", true);
        result.put("message", "인증되었습니다.");
        result.put("branch", branch);
        return ResponseEntity.ok(result);
    }

    // ===== 일정 목록 (참여자명 마스킹) =====
    @GetMapping("/schedule-list")
    public ResponseEntity<Map<String, Object>> getScheduleList(ScheduleDTO dto, HttpSession session) {
        log.info("GET /schedulePublic/api/schedule-list");
        String branch = getAuthBranch(session);
        if (branch == null) return unauthorizedPublic();

        dto.setBranch(branch);
        List<ScheduleDTO> list = scheduleService.getBranchScheduleList(dto);

        // 참여자명 마스킹
        for (ScheduleDTO item : list) {
            item.setParticipantName(maskName(item.getParticipantName()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // ===== 날짜별 시간표 (참여자명 마스킹) =====
    @GetMapping("/day-detail")
    public ResponseEntity<Map<String, Object>> getDayDetail(@RequestParam String date, HttpSession session) {
        log.info("GET /schedulePublic/api/day-detail?date={}", date);
        String branch = getAuthBranch(session);
        if (branch == null) return unauthorizedPublic();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setBranch(branch);
        dto.setSearchStartDate(date);
        dto.setSearchEndDate(date);
        List<ScheduleDTO> list = scheduleService.getBranchScheduleList(dto);

        // 참여자명 마스킹
        for (ScheduleDTO item : list) {
            item.setParticipantName(maskName(item.getParticipantName()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // ===== 상담사 목록 =====
    @GetMapping("/counselors")
    public ResponseEntity<Map<String, Object>> getCounselors(HttpSession session) {
        log.info("GET /schedulePublic/api/counselors");
        String branch = getAuthBranch(session);
        if (branch == null) return unauthorizedPublic();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setBranch(branch);
        List<ScheduleDTO> list = scheduleService.getCounselorsByBranch(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    // ===== 내부 유틸 =====

    private String getAuthBranch(HttpSession session) {
        return (String) session.getAttribute(SESSION_KEY);
    }

    private ResponseEntity<Map<String, Object>> unauthorizedPublic() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "인증이 필요합니다.");
        return ResponseEntity.status(401).body(result);
    }

    private String maskName(String name) {
        if (name == null || name.isEmpty()) return "(미정)";
        if (name.length() <= 1) return name;
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }
}
