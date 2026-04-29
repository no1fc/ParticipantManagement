package com.jobmoa.app.CounselMain.biz.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrangementDTO {

    private String arrangementBranch; // 지점
    private String arrangementStartDate; // 알선 현황 조회 시작 날짜
    private String arrangementPerformanceStartDate; // 알선 현황 조회 실적 시작 날짜
    private String arrangementEndDate; // 알선 현황 조회 끝 날짜
    private String arrangementPreviousMonthStartDate; // 알선 현황 조회 전월 시작 날짜
    private String arrangementPreviousMonthEndDate; // 알선 현황 조회 전월 끝 날짜
    private String arrangementYear; // 알선 현황 조회 년도
    private String arrangementMonth; // 알선 현황 조회 월

    // CARD DATA
    private int arrangementTotal; // 총 알선 건수
    private String arrangementFirstRankBranch; // 1위 알선 지점
    private String arrangementSecondRankBranch; // 2위 알선 지점
    private int arrangementFirstRankCount; // 1위 알선 건수
    private int arrangementSecondRankCount; // 2위 알선 건수
    private float arrangementFirstRankScorePercent; // 1위 알선 점수 비율
    private float arrangementSecondRankScorePercent; // 2위 알선 점수 비율
    private float arrangementPreviousMonthAchievementRate; //전월 대비 달성률

    // CHART DATA
    private int arrangementCount; // 알선 건수
    private float arrangementRankScorePercent; // 알선 점수 비율
    private int arrangementRank; // 알선 순위

    private String arrangementCondition;

}
