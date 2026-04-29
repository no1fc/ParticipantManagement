package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

import java.util.List;

@Data
public class RecommendParticipantResponseDTO {
    private boolean success;
    private ParticipantDetailResponseDTO participant;
    private List<ParticipantJobRecommendDTO> recommendList;
    private ParticipantJobRecommendDTO bestRecommend;
    private Object shareSummary;
    private String message;
    private String lastRecommendedAt;
    private Boolean cooldownActive;
}
