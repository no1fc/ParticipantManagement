package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

@Data
public class ParticipantJobRecommendDTO {
    private int pk; // PK of the recommendation record
    private int jobSeekerNo; // FK of the 구직번호
    private String participantName; // 참여자명
    private String progressStage; // 진행단계
    private String education; // 학력
    private String major; // 전공
    private String categoryMajor; // 카테고리대분류
    private String categoryMiddle; // 카테고리중분류
    private String desiredJob; // 희망직무
    private String referralDetail; // 알선상세정보
    private String generatedSearchCondition; // 생성된검색조건
    private String recommendedJobCertNo; // 추천채용정보_구인인증번호
    private String recommendedJobUrl; // 추천채용정보_URL
    private String recommendedJobCompany; // 추천채용정보_기업명
    private String recommendedJobTitle; // 추천채용정보_제목
    private String recommendedJobIndustry; // 추천채용정보_업종
    private boolean bestJobInfo; // 베스트채용정보
    private Integer recommendationScore; // 추천점수
    private String recommendationReason; // 추천사유
    private String createdAt; // 저장일시
    private String updatedAt; // 수정일시
}
