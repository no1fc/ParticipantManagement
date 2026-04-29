package com.jobmoa.app.CounselMain.biz.report;

import lombok.Data;

import java.util.List;

@Data
public class ReportDTO {

    // DB 변수(컬럼)
    private String counselorName;        // 이름
    private String userID;               //전담자 아이디
    private int registYear;              // 등록년도
    private String branch;               // 지점
    private int cancelCount;             // 취소자
    private int totalParticipants;       // 전체참여자
    private int currentProgress;         // 현진행자
    private int thirdStageCount;         // 3단계진행자
    private int completedCount;          // 종료자
    private int discontinuedCount;       // 중단자
    private int nonApprovedEmployment;   // 성과 불인정 취업
    private int approvedEmployment;      // 성과 인정 취업
    private int totalEmployment;         // 전체취업자
    private int referralEmployment;      // 알선취업
    private int betterJobCount;          // 나은일자리
    private int earlyEmployment;         // 조기취업자
    private int employmentRetention;     // 고용유지
    private int iapEstablishMonth;       // IAP수립1달
    private int incentiveOccurred;       // 인센발생
    private int incentiveNotOccurred;    // 인센미발생
    private int type1; // Ⅰ유형
    private int type2; // Ⅱ유형

    // 비율 관련 필드 (소수점 처리를 위해 double 타입 사용)
    private double employmentRate;           // 취업률
    private double evaluationEmploymentRate;      // 평가취업률
    private double referralEmploymentRate;        // 알선취업률
    private double betterJobRate;                 // 나은일자리비율
    private double retentionRate;                 // 고용유지율
    private double earlyEmploymentRate;           // 조기취업자비율
    private double incentiveOccurrenceRate;       // 취업인센티브발생율

    // DB 외 변수
    private String[] userIds; //사용자 계정
    private int todayPersonnelOneType; // 금일 배정 인원 1유형
    private int todayPersonnelTwoType; // 금일 배정 인원 2유형

    private int memberTodayEmployment;// 금일 누적 실적 일반 취업
    private int memberTodayPlacement;// 금일 누적 실적 알선 취업
    private int memberToWeekEmployment;// 주간 누적 실적 일반 취업
    private int memberToWeekPlacement;// 주간 누적 실적 알선 취업
    private int memberToMonthEmployment;// 월간 누적 실적 일반 취업
    private int memberToMonthPlacement;// 월간 누적 실적 알선 취업
    private int memberToYearEmployment;// 연간 누적 실적 일반 취업
    private int memberToYearPlacement;// 연간 누적 실적 알선 취업
    private String endUpdateStatus; // 실적 마지막 다운로드일
    private String dailyUpdateStatusDate; // 일일보고 저장일
    private boolean memberDailyWorkDiaryIssuance; // 일일보고 발급 여부


    //누적실적 데이터를 위한 DB 변수(컬럼)
    private List<Integer> todayPersonnelOneTypes; // 금일 배정 인원 1유형
    private List<Integer> todayPersonnelTwoTypes; // 금일 배정 인원 2유형

    private List<Integer> memberTodayEmployments;// 금일 누적 실적 일반 취업
    private List<Integer> memberTodayPlacements;// 금일 누적 실적 알선 취업
    private List<Integer> memberToWeekEmployments;// 주간 누적 실적 일반 취업
    private List<Integer> memberToWeekPlacements;// 주간 누적 실적 알선 취업
    private List<Integer> memberToMonthEmployments;// 월간 누적 실적 일반 취업
    private List<Integer> memberToMonthPlacements;// 월간 누적 실적 알선 취업
    private List<Integer> memberToYearEmployments;// 연간 누적 실적 일반 취업
    private List<Integer> memberToYearPlacements;// 연간 누적 실적 알선 취업

//    private String[] userIds; //사용자 계정

//    private int memberType1; // Ⅰ유형
    private int branchType1; // 지점Ⅰ유형
//    private int memberType2; // Ⅱ유형
    private int branchType2; // 지점Ⅱ유형
    
    private String startDate; //시작년도
    private String endDate; //끝년도
    private String year; // 등록년도
    private String reportCondition; //개발자 구분

}
