package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationScoreDTO {

    @JsonProperty("구인인증번호")
    private String certNo;

    private Integer score;

    private String reason;
}
