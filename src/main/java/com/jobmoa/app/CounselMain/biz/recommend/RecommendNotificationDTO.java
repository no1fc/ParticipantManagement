package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

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
