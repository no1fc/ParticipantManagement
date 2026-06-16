package com.jobmoa.app.CounselMain.biz.schedule;

import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 상담 일정 알림 스케줄러.
 *
 * <p>매 분(cron: {@code 0 * * * * *})마다 실행되어 알림 대상 일정을 조회하고,
 * WebSocket을 통해 해당 상담사에게 실시간 알림을 전송한다.
 */
@Component
@Slf4j
public class ScheduleAlertScheduler {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 알림 대상 상담 일정을 조회하여 WebSocket으로 상담사에게 전송한다.
     *
     * <p>매 분 실행되며, {@code /topic/schedule-alert/{상담사ID}} 토픽으로
     * 일정 유형, 참여자명, 시작 시각 등의 알림 메시지를 발송한다.
     * (클라이언트 구독 경로 {@code gnb-notification.js}와 일치)
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkScheduleAlerts() {
        try {
            List<ScheduleDTO> targets = scheduleService.getAlertTargets();
            for (ScheduleDTO target : targets) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "SCHEDULE_ALERT");
                notification.put("title", "상담 일정 알림");
                notification.put("message", target.getAlertMinutes() + "분 후 "
                        + target.getParticipantName() + "님과 "
                        + target.getScheduleType() + " 일정이 있습니다.");
                notification.put("scheduleId", target.getScheduleId());
                notification.put("startTime", target.getStartTime());

                webSocketService.sendObject(
                        "/topic/schedule-alert/" + target.getCounselorId(),
                        notification
                );
                log.info("일정 알림 전송: 상담사={}, 일정ID={}", target.getCounselorId(), target.getScheduleId());
            }
        } catch (Exception e) {
            log.error("일정 알림 스케줄러 오류", e);
        }
    }
}
