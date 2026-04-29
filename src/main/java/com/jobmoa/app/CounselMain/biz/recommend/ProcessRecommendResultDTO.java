package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

@Data
public class ProcessRecommendResultDTO {

    private boolean success;
    private String message;
    private Boolean reused;
    private SearchConditionDTO searchCondition;
    private Integer savedCount;
    private String lastRecommendedAt;
    private int activeRecommendCount;
    private String participantName;
    private int jobSeekerNo;
}
