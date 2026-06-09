package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * AI 추천 알림 DTO. WebSocket을 통해 추천 처리 결과를 실시간으로 클라이언트에 전달할 때 사용된다.
 */
@Data
public class RecommendNotificationDTO {

    private boolean success;
    private String message;
    private int jobSeekerNo;
    private String participantName;
    private Integer savedCount;
    private Boolean reused;
    private String lastRecommendedAt;
    private int activeRecommendCount;
}
