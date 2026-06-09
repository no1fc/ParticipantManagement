package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * AI 추천 요청 DTO. 클라이언트에서 추천을 요청할 때 구직번호와 강제 갱신 여부를 전달한다.
 */
@Data
public class RecommendRequestDTO {
    private int jobSeekerNo;
    private Boolean forceRefresh = false;
}
