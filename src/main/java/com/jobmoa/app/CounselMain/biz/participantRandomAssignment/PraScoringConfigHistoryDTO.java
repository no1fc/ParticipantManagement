package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

@Data
public class PraScoringConfigHistoryDTO {
    private String branch;
    private String writerId;
    private double weightLoad;
    private double weightFair;
    private double weightStreak;
    private double weightPace;
    private int gapThreshold;
    private int dailyLimit;
    private String limitG1;
    private String limitG2;
    private String limitG3;
}
