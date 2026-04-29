package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

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
