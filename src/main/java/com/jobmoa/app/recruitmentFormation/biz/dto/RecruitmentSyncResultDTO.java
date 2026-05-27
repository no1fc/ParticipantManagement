package com.jobmoa.app.recruitmentFormation.biz.dto;

import lombok.Data;

/**
 * 채용공고 페이지 단위 동기화 결과.
 */
@Data
public class RecruitmentSyncResultDTO {
    private int totalCount;
    private int expectedPages;
    private int completedPages;
    private int fetchedCount;
    private int savedCount;
    private boolean allPagesFetched;
    private String failureMessage;
}
