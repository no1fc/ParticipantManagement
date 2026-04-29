package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

@Data
public class RecommendCategoryDTO {
    private int jobSeekerNo;
    private String categoryMain; // 카테고리대분류;
    private String categoryMiddle;//카테고리중분류;
    private String infoJob; //희망직무;
    private String infoRank; //희망순위;
}
