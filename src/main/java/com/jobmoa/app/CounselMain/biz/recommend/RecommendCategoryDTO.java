package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * 추천용 참여자 희망직무 카테고리 DTO. 참여자의 희망직무 대분류/중분류 및 희망순위 정보를 담는다.
 */
@Data
public class RecommendCategoryDTO {
    private int jobSeekerNo;
    private String categoryMain; // 카테고리대분류;
    private String categoryMiddle;//카테고리중분류;
    private String infoJob; //희망직무;
    private String infoRank; //희망순위;
    private String recommendedKeywords; //추천키워드;
}
