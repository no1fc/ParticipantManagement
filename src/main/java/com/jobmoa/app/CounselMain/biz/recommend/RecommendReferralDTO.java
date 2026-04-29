package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

@Data
public class RecommendReferralDTO {
    private int jobSeekerNo; // 구직자번호
    private String infoAlsonDetail; // 알선상세정보
    private String infoAdditionalInfo; // 추천사
}
