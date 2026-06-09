package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

/**
 * 참여자 배정 산정식 설정 히스토리 데이터 전송 객체.
 * 배정 가중치(업무량, 공정성, 연속, 속도), 격차 임계값, 일일 제한 등 산정식 구성 이력을 전달한다.
 */
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
