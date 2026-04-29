package com.jobmoa.app.CounselMain.view.participant.recommendAjax;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.recommend.ParticipantJobRecommendServiceImpl;
import com.jobmoa.app.CounselMain.biz.recommend.ProcessRecommendResultDTO;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendConcurrencyManager;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendNotificationDTO;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendParticipantResponseDTO;
import com.jobmoa.app.CounselMain.biz.recommend.RecommendRequestDTO;
import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 응답시간 체크
    private void checkResponseTime() {



    }
}
