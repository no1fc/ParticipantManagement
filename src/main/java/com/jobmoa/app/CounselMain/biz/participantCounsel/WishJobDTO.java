package com.jobmoa.app.CounselMain.biz.participantCounsel;

import lombok.Data;

/**
 * 참여자 희망직무 데이터 전송 객체.
 * 참여자별 다중 희망직무(순위, 직무명, 대/중/소분류)를 J_참여자관리_희망직무 테이블과 매핑하여 관리한다.
 */
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
