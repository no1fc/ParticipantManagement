package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

import java.util.List;

/**
 * 추천 대상 참여자 DTO. AI 추천 시 참여자의 기본 정보, 희망직무 카테고리, 알선 정보를 포함하여 Gemini API에 전달한다.
 */
@Data
public class RecommendParticipantDTO {
    private int jobSeekerNo; // 구직번호;
    private String summaryGender; // 성별;
    private String infoName; // 참여자명;
    private String infoStage; // 진행단계;
    private boolean infoFocus; // 집중알선여부;
    private String infoEducation; //학력;
    private String infoMajor; //전공;
    private String infoAddress; // 주소
    private String infoCareer; // 경력
    private String infoDesiredSalary; // 희망급여
    private String specialClass; // 특정계층
    private String employmentCapacity; // 취업역량
    private String categoryMajor; //직무 카테고리 대분류;
    private String categoryMiddle; //직무 카테고리 중분류;
    private List<RecommendCategoryDTO> categoryDTO;
    private RecommendReferralDTO referralDTO;
}
