package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * AI 추천 최적 선발 결과 데이터 전송 객체.
 * Gemini AI가 산출한 최적 구직번호와 추천 점수 목록을 전달한다.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestSelectionResultDTO {

    private String bestGujinNo;

    private List<RecommendationScoreDTO> scores;
}
