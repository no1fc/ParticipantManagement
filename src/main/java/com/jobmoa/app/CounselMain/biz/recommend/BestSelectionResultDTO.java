package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestSelectionResultDTO {

    private String bestGujinNo;

    private List<RecommendationScoreDTO> scores;
}
