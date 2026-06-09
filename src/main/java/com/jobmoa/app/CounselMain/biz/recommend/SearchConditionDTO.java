package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * AI 생성 검색 조건 DTO. Gemini API가 참여자 정보를 분석하여 생성한 키워드, 직종코드, 지역 등의 검색 조건을 담는다.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchConditionDTO {

    private List<String> keywords; // 키워드

    @JsonProperty("jobs_code")
    private List<String> jobsCode; // 직종분류코드

    private String jobCategory; // 직무분류

    private Integer maxCount; // 최대 검색 결과 수

    private Boolean parseError = false; // 오류 메시지

    private Boolean isAddress = false;

    private List<String> largescaleUnits; // 광역자치 단위 (시, 도)

    private List<String> localUnits; // 기초자치 단위 (구, 시, 군)
}
