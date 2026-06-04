package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

import java.util.List;

/**
 * 참여자 추천 종합 응답 DTO. 참여자 상세 정보, 추천 목록, 베스트 추천, 쿨다운 상태 등을 포함하여 프론트엔드에 반환한다.
 */
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
