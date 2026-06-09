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

/**
 * 공개 상담 일정 REST API 컨트롤러.
 * 로그인 없이 접근코드 인증을 통해 지점의 상담 일정을 조회할 수 있는 공개 API를 제공한다.
 * 참여자 개인정보 보호를 위해 참여자명을 마스킹 처리하여 반환한다.
 */
@Slf4j
@RestController
@RequestMapping("/schedulePublic/api")
public class SchedulePublicApiController {

    @Autowired
    private ScheduleService scheduleService;

    private static final String SESSION_KEY = "SCHEDULE_PUBLIC_AUTH";

    /**
     * 공개 일정 접근을 위한 접근코드 인증을 수행한다.
     * 인증 성공 시 세션에 지점 정보를 저장하여 이후 API 호출 시 사용한다.
     *
     * @param request 인증 요청 데이터 (uniqueNumber: 접근코드, branch: 지점명)
     * @param session HTTP 세션 (인증 정보 저장용)
     * @return 인증 결과 JSON 응답
     */
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

    /**
     * 인증된 지점의 일정 목록을 조회한다 (참여자명 마스킹 처리).
     *
     * @param dto     일정 검색 조건 DTO
     * @param session HTTP 세션 (인증 지점 정보 조회용)
     * @return 마스킹 처리된 일정 목록 JSON 응답
     */
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

    /**
     * 특정 날짜의 일정 상세 시간표를 조회한다 (참여자명 마스킹 처리).
     *
     * @param date    조회할 날짜 (yyyy-MM-dd 형식)
     * @param session HTTP 세션 (인증 지점 정보 조회용)
     * @return 해당 날짜의 일정 목록 JSON 응답
     */
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

    /**
     * 인증된 지점의 상담사 목록을 조회한다.
     *
     * @param session HTTP 세션 (인증 지점 정보 조회용)
     * @return 상담사 목록 JSON 응답
     */
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

    /**
     * 세션에서 인증된 지점명을 조회한다.
     *
     * @param session HTTP 세션
     * @return 인증된 지점명 (인증되지 않은 경우 null)
     */
    private String getAuthBranch(HttpSession session) {
        return (String) session.getAttribute(SESSION_KEY);
    }

    /**
     * 공개 API 인증 실패 시 401 응답을 반환한다.
     *
     * @return 401 상태 코드와 인증 필요 메시지를 담은 응답
     */
    private ResponseEntity<Map<String, Object>> unauthorizedPublic() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "인증이 필요합니다.");
        return ResponseEntity.status(401).body(result);
    }

    /**
     * 참여자명을 마스킹 처리한다.
     * 첫 글자만 노출하고 나머지는 '*'로 대체한다.
     *
     * @param name 원본 이름
     * @return 마스킹된 이름 (예: "홍**")
     */
    private String maskName(String name) {
        if (name == null || name.isEmpty()) return "(미정)";
        if (name.length() <= 1) return name;
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }
}
