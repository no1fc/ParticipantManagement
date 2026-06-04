package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * AI 추천 점수 DTO. Gemini API 응답에서 파싱된 구인인증번호별 추천 점수와 추천 사유를 담는다.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationScoreDTO {

    @JsonProperty("구인인증번호")
    private String certNo;

    private Integer score;

    private String reason;
}
