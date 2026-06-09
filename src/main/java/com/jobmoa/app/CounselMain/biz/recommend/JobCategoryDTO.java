package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

/**
 * 직무 카테고리 DTO. 대분류/중분류/소분류 직종 카테고리 정보를 담으며, 참여자 희망직무 매핑 시 사용된다.
 */
@Data
public class JobCategoryDTO {
    private int pk;
    private String categoryId;        // 카테고리ID
    private String largeCategoryId;   // 대분류카테고리ID
    private String midCategoryId;     // 중분류카테고리ID
    private String categoryName;      // 카테고리명칭
    private int categoryType;         // 카테고리분류 (1=대, 2=중, 3=소)
}
