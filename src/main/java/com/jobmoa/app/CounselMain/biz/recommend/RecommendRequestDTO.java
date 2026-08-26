package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * AI 추천 요청 DTO. 클라이언트에서 추천을 요청할 때 구직번호와 강제 갱신 여부를 전달한다.
 */
@Data
public class RecommendRequestDTO {
    private int jobSeekerNo;
    private Boolean forceRefresh = false;

    /** 상담사가 추천 모달에서 지정한 원하는 광역 지역(예: 서울, 경기). 임시 입력값으로 DB에 저장하지 않는다. */
    private String desiredLargeRegion;

    /** 상담사가 추천 모달에서 지정한 원하는 기초 지역(예: 강남구, 수원시). 선택 입력. */
    private String desiredLocalRegion;
}
