package com.jobmoa.app.CounselMain.view.participant.recommendAjax;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.recommend.ParticipantJobRecommendServiceImpl;
import com.jobmoa.app.CounselMain.biz.recommend.ProcessRecommendResultDTO;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendConcurrencyManager;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendNotificationDTO;
import com.jobmoa.app.CounselMain.biz.recommend.JobPostingCopyDTO;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendParticipantResponseDTO;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendRequestDTO;
import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI 채용공고 추천 REST API 컨트롤러.
 * <p>Gemini AI를 활용하여 참여자에게 적합한 채용공고를 추천한다.
 * 추천 결과 조회, AI 추천 실행 및 저장, 채용공고 상세 조회 기능을 제공한다.
 * 상담사당 동시 AI 추천 요청은 최대 5건으로 제한되며,
 * 추천 완료 시 WebSocket을 통해 실시간 알림을 전송한다.</p>
 *
 * @see com.jobmoa.app.CounselMain.biz.recommend.ParticipantJobRecommendServiceImpl
 * @see com.jobmoa.app.CounselMain.biz.recommend.RecommendConcurrencyManager
 */
@Slf4j
@RestController
@RequestMapping("/api/recommend")
public class recommendAjaxController {

    @Autowired
    private ParticipantJobRecommendServiceImpl recommendService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private RecommendConcurrencyManager concurrencyManager;

    /**
     * 참여자의 AI 추천 결과를 조회한다.
     * <p>구직번호를 기반으로 기존에 저장된 추천 결과를 반환한다.</p>
     * @param request 구직번호를 포함한 추천 요청 DTO
     * @return 추천 결과 데이터를 담은 JSON 응답
     */
    @PostMapping(value = "/detail",
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<RecommendParticipantResponseDTO> recommend(@RequestBody RecommendRequestDTO request) {
        try {

            long startTime = System.currentTimeMillis();
            log.info("recommend start time: {} ms", startTime);

            if (request == null) {
                RecommendParticipantResponseDTO err = new RecommendParticipantResponseDTO();
                err.setSuccess(false);
                err.setMessage("요청 데이터가 유효하지 않습니다.");
                return ResponseEntity.badRequest().body(err);
            }

            int jobSeekerNo = request.getJobSeekerNo();
            if (jobSeekerNo == 0) {
                RecommendParticipantResponseDTO err = new RecommendParticipantResponseDTO();
                err.setSuccess(false);
                err.setMessage("구직번호가 필요합니다.");
                return ResponseEntity.badRequest().body(err);
            }

            RecommendParticipantResponseDTO response = recommendService.getRecommendDetailResponse(jobSeekerNo);

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            log.info("recommend response time: {} ms", responseTime);
            log.info("recommend end time: {} ms", endTime);

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            log.error("Invalid jobSeekerNo format", e);
            RecommendParticipantResponseDTO err = new RecommendParticipantResponseDTO();
            err.setSuccess(false);
            err.setMessage("유효하지 않은 구직번호 형식입니다.");
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            log.error("Error recommending job", e);
            RecommendParticipantResponseDTO err = new RecommendParticipantResponseDTO();
            err.setSuccess(false);
            err.setMessage("추천 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(err);
        }
    }

    /**
     * AI 채용공고 추천을 실행하고 결과를 저장한다.
     * <p>상담사당 동시 요청을 최대 5건으로 제한하며, 추천 완료 후 WebSocket을 통해
     * 실시간 알림을 전송한다. forceRefresh 옵션으로 기존 추천 결과를 갱신할 수 있다.</p>
     * @param request 구직번호와 강제 갱신 여부를 포함한 추천 요청 DTO
     * @param session HTTP 세션 (상담사 ID 추출 및 동시 요청 제한용)
     * @return AI 추천 처리 결과(저장 건수, 참여자명, 활성 추천 수 등)를 담은 JSON 응답
     */
    @PostMapping(value = "/saveRecommendAI",
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<ProcessRecommendResultDTO> saveRecommendAI(
            @RequestBody RecommendRequestDTO request, HttpSession session) {

        int jobSeekerNo = 0;
        ProcessRecommendResultDTO result = null;

        // 세션에서 상담사 ID 추출
        LoginBean loginData = (session != null)
                ? (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA") : null;
        String memberUserID = (loginData != null) ? loginData.getMemberUserID() : null;
        boolean acquired = false;

        try {

            long startTime = System.currentTimeMillis();
            log.info("saveRecommendAI start time: {} ms", startTime);

            if (request == null) {
                ProcessRecommendResultDTO err = new ProcessRecommendResultDTO();
                err.setSuccess(false);
                err.setMessage("요청 데이터가 유효하지 않습니다.");
                return ResponseEntity.badRequest().body(err);
            }

            jobSeekerNo = request.getJobSeekerNo();
            boolean forceRefresh = request.getForceRefresh();
            if (jobSeekerNo == 0) {
                ProcessRecommendResultDTO err = new ProcessRecommendResultDTO();
                err.setSuccess(false);
                err.setMessage("구직번호가 필요합니다.");
                return ResponseEntity.badRequest().body(err);
            }

            // 동시 요청 제한 체크 (상담사당 최대 5건)
            if (memberUserID != null && !concurrencyManager.tryAcquire(memberUserID)) {
                ProcessRecommendResultDTO err = new ProcessRecommendResultDTO();
                err.setSuccess(false);
                err.setMessage("동시 AI 추천 요청이 최대 5건을 초과했습니다. 진행 중인 추천이 완료된 후 다시 시도해주세요.");
                err.setActiveRecommendCount(concurrencyManager.getActiveCount(memberUserID));
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err);
            }
            acquired = (memberUserID != null);

            result = recommendService.processAndSaveRecommend(jobSeekerNo, forceRefresh);

            // 응답에 현재 활성 추천 수 설정
            if (memberUserID != null) {
                result.setActiveRecommendCount(concurrencyManager.getActiveCount(memberUserID));
            }

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            log.info("saveRecommendAI response time: {} ms", responseTime);
            log.info("saveRecommendAI end time: {} ms", endTime);

            return ResponseEntity.ok(result);

        } catch (NumberFormatException e) {
            log.error("Invalid jobSeekerNo format", e);
            result = new ProcessRecommendResultDTO();
            result.setSuccess(false);
            result.setMessage("유효하지 않은 구직번호 형식입니다.");
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            log.error("Error recommending job", e);
            result = new ProcessRecommendResultDTO();
            result.setSuccess(false);
            result.setMessage("추천 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(result);
        } finally {
            // 슬롯 해제 (알림 전송 전에 해제하여 정확한 카운트 반영)
            if (acquired) {
                concurrencyManager.release(memberUserID);
            }
            // WebSocket 알림 전송
            sendRecommendNotification(session, jobSeekerNo, result);
        }
    }

    /**
     * AI 추천 완료 후 WebSocket 알림 전송
     */
    private void sendRecommendNotification(HttpSession session, int jobSeekerNo,
                                           ProcessRecommendResultDTO result) {
        try {
            if (result == null || session == null) return;

            LoginBean loginData = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            if (loginData == null || loginData.getMemberUserID() == null) return;

            RecommendNotificationDTO notification = new RecommendNotificationDTO();
            notification.setSuccess(result.isSuccess());
            notification.setMessage(result.getMessage());
            notification.setJobSeekerNo(jobSeekerNo);
            notification.setParticipantName(result.getParticipantName());
            notification.setSavedCount(result.getSavedCount());
            notification.setReused(result.getReused());
            notification.setLastRecommendedAt(result.getLastRecommendedAt());
            notification.setActiveRecommendCount(concurrencyManager.getActiveCount(loginData.getMemberUserID()));

            String destination = "/topic/recommend-complete/" + loginData.getMemberUserID();
            webSocketService.sendObject(destination, notification);
            log.info("[WebSocket] 추천 알림 전송 user={}, jobSeekerNo={}", loginData.getMemberUserID(), jobSeekerNo);
        } catch (Exception e) {
            log.warn("[WebSocket] 추천 알림 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 응답 시간 체크용 메서드 (현재 미구현).
     */
    private void checkResponseTime() {
    }

    /**
     * 채용공고 상세 정보를 조회한다.
     * @param request 구인인증번호(wantedAuthNo)를 포함한 요청 Map
     * @return 채용공고 상세 데이터를 담은 JSON 응답
     */
    @PostMapping(value = "/jobPostingDetail",
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> getJobPostingDetail(@RequestBody java.util.Map<String, String> request) {
        String wantedAuthNo = request.get("wantedAuthNo");
        if (wantedAuthNo == null || wantedAuthNo.isBlank()) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("success", false, "message", "구인인증번호가 필요합니다."));
        }

        JobPostingCopyDTO detail = recommendService.getJobPostingDetail(wantedAuthNo);
        if (detail == null) {
            return ResponseEntity.ok().body(
                    java.util.Map.of("success", false, "message", "해당 공고를 찾을 수 없습니다."));
        }

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("data", detail);
        return ResponseEntity.ok(result);
    }
}
