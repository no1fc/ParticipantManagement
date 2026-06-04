package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 참여자 상세 정보 응답 DTO. AI 추천 화면에서 참여자의 기본 정보, 카테고리 목록, 알선 정보를 포함하여 반환한다.
 */
@Data
public class ParticipantDetailResponseDTO {

    private Integer infoGujikNo;
    private String summaryGender;
    private String infoName;
    private String infoStage;
    private boolean infoFocus;
    private String infoEducation;
    private String infoMajor;
    private List<RecommendCategoryDTO> categoryList;

    @JsonProperty("Referral")
    private RecommendReferralDTO referral;
}
