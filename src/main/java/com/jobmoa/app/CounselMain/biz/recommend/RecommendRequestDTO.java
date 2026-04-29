package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.Data;

@Data
public class RecommendRequestDTO {
    private int jobSeekerNo;
    private Boolean forceRefresh = false;
}
