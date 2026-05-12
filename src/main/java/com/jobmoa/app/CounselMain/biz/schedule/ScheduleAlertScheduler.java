package com.jobmoa.app.CounselMain.biz.schedule;

import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ScheduleAlertScheduler {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebSocketService webSocketService;

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

                webSocketService.sendObjectToUser(
                        "/topic/notification",
                        notification,
                        target.getCounselorId()
                );
                log.info("일정 알림 전송: 상담사={}, 일정ID={}", target.getCounselorId(), target.getScheduleId());
            }
        } catch (Exception e) {
            log.error("일정 알림 스케줄러 오류: {}", e.getMessage());
        }
    }
}
