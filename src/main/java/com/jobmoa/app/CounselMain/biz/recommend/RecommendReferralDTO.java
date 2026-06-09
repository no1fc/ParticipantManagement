package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * 추천 알선 정보 DTO. 참여자의 알선 상세 정보와 추천사를 담으며, AI 추천 시 참고 데이터로 사용된다.
 */
@Data
public class RecommendReferralDTO {
    private int jobSeekerNo; // 구직자번호
    private String infoAlsonDetail; // 알선상세정보
    private String infoAdditionalInfo; // 추천사
}
