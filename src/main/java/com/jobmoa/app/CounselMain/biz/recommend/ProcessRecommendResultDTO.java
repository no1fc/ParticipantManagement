package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * AI 추천 처리 결과 DTO. 추천 실행 후 성공 여부, 저장 건수, 캐시 재사용 여부 등 처리 결과를 담아 반환한다.
 */
@Data
public class ProcessRecommendResultDTO {

    private boolean success;
    private String message;
    private Boolean reused;
    private SearchConditionDTO searchCondition;
    private Integer savedCount;
    private String lastRecommendedAt;
    private int activeRecommendCount;
    private String participantName;
    private int jobSeekerNo;
}
