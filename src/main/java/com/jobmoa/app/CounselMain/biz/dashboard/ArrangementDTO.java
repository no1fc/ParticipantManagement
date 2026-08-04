package com.jobmoa.app.CounselMain.biz.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 알선 현황 대시보드 데이터 전송 객체.
 * 지점별 알선 건수, 순위, 점수 비율, 전월 대비 달성률 등 알선 현황 카드 및 차트 데이터를 전달한다.
 */
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

    // CARD DATA (스칼라 집계)
    private int arrangementTotal; // 총 알선 건수
    private float arrangementPreviousMonthAchievementRate; //전월 대비 달성률

    // CHART / RANK-BRANCH DATA
    // ※ 공동 순위 지원: 카드의 1·2위 지점도 arrangementRankBranches 다중 행으로 조회되어
    //   지점(arrangementBranch) 단위 필드로 매핑된다(순위별로 여러 지점이 동일 arrangementRank 를 가질 수 있음).
    private int arrangementCount; // 알선 건수
    private float arrangementRankScorePercent; // 알선 점수 비율
    private int arrangementRank; // 알선 순위 (DENSE_RANK, 동점 시 동일 값)

    private String arrangementCondition;

}
