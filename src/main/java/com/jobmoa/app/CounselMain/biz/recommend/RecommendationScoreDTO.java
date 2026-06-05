package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias({"certNo", "gujinNo"})
    private String certNo;

    private Number score;

    private String reason;

    /** 추천점수를 Integer로 반환 (Gemini가 float/int 어느 형식으로 반환해도 호환) */
    public Integer getScore() {
        return score != null ? score.intValue() : null;
    }
}
