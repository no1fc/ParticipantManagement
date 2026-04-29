package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

@Data
public class PraDailyReportDTO {
    private String branch;
    private String counselorID;
    private int assignmentType1;// 1유형 배정
    private int assignmentType2;// 2유형 배정
    private int todayEmployment;// 금일 누적 실적 일반 취업
    private int todayPlacement;// 금일 누적 실적 알선 취업
    private int toWeekEmployment;// 주간 누적 실적 일반 취업
    private int toWeekPlacement;// 주간 누적 실적 알선 취업
    private int toMonthEmployment;// 월간 누적 실적 일반 취업
    private int toMonthPlacement;// 월간 누적 실적 알선 취업
    private int toYearEmployment;// 연간 누적 실적 일반 취업
    private int toYearPlacement;// 연간 누적 실적 알선 취업
}
