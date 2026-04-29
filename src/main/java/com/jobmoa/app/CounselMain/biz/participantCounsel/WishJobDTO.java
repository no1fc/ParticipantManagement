package com.jobmoa.app.CounselMain.biz.participantCounsel;

import lombok.Data;

@Data
public class WishJobDTO {
    private int pk;
    private int counselJobNo;
    private int wishRank;
    private String jobWant;
    private String categoryLarge;
    private String categoryMid;
    private String categorySub;
}
