package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.Data;

@Data
public class DashboardDTO {
    private String dashboardUserID; //전담자 아이디
    private String dashboardBranch; //지점
    private int dashboardTotalCount; //참여자 수
    private int dashboardByYearCount1; //년도별 참여자 1
    private int dashboardByYearCount2; //년도별 참여자 2
    private int dashboardByYearCount3; //년도별 참여자 3

    //DB 외 정보
    private String dashboardCondition; // 개발자 구분
    private boolean dashboardFlagCondition; // 개발자 구분 boolean

    //지점 전체 개수 및 인원
    private int dashboardCountBranch;
    private int dashboardCountUser;
    private int dashboardCloseCount;

    //Query 조건용 변수 선언
    private String dashBoardYear;
//    private String dashBoardEndDate;

    //DashBoard에 전달해줄 변수 선언
    // 취업자 수
    private int dashBoardEmployedCountTotal;
    private int dashBoardEmployedCountBranch;
    private int dashBoardEmployedCountUser;

    // 알선 취업자 수
    private int dashBoardReferredEmployedCountTotal;
    private int dashBoardReferredEmployedCountBranch;
    private int dashBoardReferredEmployedCountUser;

    // 조기 취업자 수
    private int dashBoardEarlyEmployedCountTotal;
    private int dashBoardEarlyEmployedCountBranch;
    private int dashBoardEarlyEmployedCountUser;

    // 나은 일자리 수
    private int dashBoardBetterJobCountTotal;
    private int dashBoardBetterJobCountBranch;
    private int dashBoardBetterJobCountUser;

    // 고용유지 6개월
    private int dashBoardSixMonthRetentionCountTotal;
    private int dashBoardSixMonthRetentionCountBranch;
    private int dashBoardSixMonthRetentionCountUser;

    //고용유지 12개월
    private int dashBoardTwelveMonthRetentionCountTotal;
    private int dashBoardTwelveMonthRetentionCountBranch;
    private int dashBoardTwelveMonthRetentionCountUser;

    //성공금 금액 전달용 변수 전체,지점,개인 성공금
    private int dashBoardSuccessMoneyTotal;
    private int dashBoardSuccessMoneyBranch;
    private int dashBoardSuccessMoneyUser;
    //성공금 인센티브 금액 전달용 변수 전체,지점,개인 인센티브
    private int dashBoardSuccessMoneyTotalIncentive;
    private int dashBoardSuccessMoneyBranchIncentive;
    private int dashBoardSuccessMoneyUserIncentive;

    //참여자 현황 전달용 변수
    private int dashBoardParticipatedYearCount;
    private int dashBoardParticipatedCountOne;
    private int dashBoardParticipatedCountTwo;
    private int dashBoardParticipatedCountTotal;
    private int dashBoardParticipatedYear;
    private int dashBoardParticipatedInitiator;

    //긍일 업무 현황 변수
    private int dashBoardLastCons; // 최근상담일 15일 도래자
    private int dashBoardJobEX; // 구직만료일 15일 도래자
    private int dashBoardEXPDate; // 기간만료일 15일 도래자
    private int dashBoardInItCons; // 초기상담일 미실시자

    //성공금 상세 현황 변수
    private int dashBoardJobNo; // 성공금 상세 현황 구직번호
    private String dashBoardDate; // 성공금 상세 현황 날짜
    private String dashBoardUserName; // 성공금 상세 현황 전담자_이름
    private String dashBoardPartic; // 성공금 상세 현황 참여자_성명
    private int dashBoardSuccessMoney; // 성공금 상세 현황 성공금
    private int dashBoardIncentive; // 성공금 상세 현황 인센티브
    private String dashBoardUserBranch; // dashBoardBranch 오류발생으로 새로운 지점 변수 생성

    //지점관리 대시보드 변수
    private String dashBoardStartDate; // 대시보드 상세 현황 시작 날짜
    private String dashBoardEndDate; // 대시보드 상세 현황 끝 날짜
    private String dashBoardPASD; //(Participant Assignment Start Date) 참여자 배정 시작일
    private String dashBoardPAED; //(Participant Assignment End Date) 참여자 배정 종료일
    private int lastYearMoney; // 작년 성공금
    private int currentYearMoney; // 이번년도 성공금
    private int falseCaseNum; // 미해당 건수
    private int trueCaseNum; // 해당 건수
    private int assignedParticipants; // 배정인원
    private int noServiceCount;          // 서비스미제공 건수
    private int lessThanOneMonthCount;   // 1개월미만퇴사 건수
    private int dispatchCompanyCount;    // 파견업체 건수
    private int iapSevenDaysCount;       // IAP7일이내취업 건수
    private int underThirtyHoursCount;   // 주30시간미만 건수
    private int underMinWageCount;       // 최저임금미만 건수
    private int etcCount;                // 기타(해외취업포함) 건수
    private String businessUnit; // 사업부서

    //점수현황 데이터 변수
    // 기본 통계
    private long totalCompleted;      // 종료자 수
    private long totalEmployed;       // 취업자 수
    private long referredEmploymentCount;  // 알선취업자 수
    private long earlyEmploymentCount;     // 조기취업자 수
    private long retentionCount;           // 고용유지자 수
    private long betterJobCount;           // 나은일자리 취업자 수
    private long specialGroupCount;        // 특정계층 취업자 수
    private long totalEmployedSpecialGroupCount; // 특정계층 + 취업자 포함 취업자 수

    // 취업 관련 지표
    private double employmentRate;    // 취업률
    private double employmentScore;   // 취업자 점수
    private double employmentOneScore;   //취업자 1인당 점수
    private double employmentStandardScore;   //표준 취업자 점수
    private double employmentLastScore;   //가중취업자점수
    private double employmentTopScore; // 취업자 점수 상위 20%
    // 알선취업 관련 지표
    private double placementRate;     // 알선취업률
    private double placementScore;    // 알선취업자 점수
    private double placementOneScore; // 알선취업자 1인당 점수
    private double placementStandardScore; // 표준 알선취업자 점수
    private double placementLastScore; // 가중알선취업자점수
    private double placementTopScore; // 알선취업자점수 상위 20%
    // 조기취업 관련 지표
    private double earlyEmploymentRate;  // 조기취업률
    private double earlyEmploymentScore; // 조기취업자 점수
    private double earlyEmploymentOneScore; // 조기취업자 1인당 점수
    private double earlyEmploymentStandardScore; // 표준 조기취업자 점수
    private double earlyEmploymentLastScore; // 가중조기취업자점수
    private double earlyEmploymentTopScore; // 조기취업자점수 상위 20%
    // 고용유지 관련 지표
    private double retentionRate;     // 고용유지율
    private double retentionScore;    // 고용유지자 점수
    private double retentionOneScore;    // 고용유지자 1인당 점수
    private double retentionStandardScore;    // 표준 고용유지자 점수
    private double retentionLastScore;    // 가중고용취업자점수
    private double retentionTopScore;    // 고용취업자점수 상위 20%
    // 나은일자리 관련 지표
    private double betterJobRate;     // 나은일자리 취업률
    private double betterJobScore;    // 나은일자리 취업자 점수
    private double betterJobOneScore;    // 나은일자리 취업자 1인당 점수
    private double betterJobStandardScore;    // 표준 나은일자리 점수
    private double betterJobLastScore;    // 가중나은취업자점수
    private double betterJobTopScore;    // 나은취업자점수 상위 20%
    // 최종 점수
    private double totalScore;        // 총점
    private double totalStandardScore;        // 표준 총점
    //내 점수, 전체지점합계, 전체지점평균, 내지점합계, 내지점평균
    private double myScore; //내 점수
    private double totalBranchScore; //전체지점합계
    private double totalBranchScoreAVG; //전체지점평균(총 평균)
    private double myBranchScore; //내지점합계
    private double myBranchScoreAVG; //내지점평균
    private double totalTopScore; //전체 최고 점수
    private double pointsToNextGrade; //다음 등급 까지 남은 점수
    private String nextGrade; //다음 등급
    private String myRanking; //내등급
    private String myBranchRanking; //지점내순위
    private String myTotalRanking; //전체내순위

    private boolean dashboardExcludeRetention; // 고용유지 포함 여부
    private boolean dashboardBranchAndPeople; // 지점, 개인별 조회 여부

    //메인 대시보드 내 실적 현황 25% 평균 현황 (상위 15%, 하위 15% 제외)
    private double avgEmploymentRateMiddle; // 취업률
    private double avgPlacementRateMiddle; // 알선취업률
    private double avgEarlyEmploymentRateMiddle; // 조기취업률
    private double avgBetterJobRateMiddle; // 나은일자리 취업률

    //메인 대시보드 관리자 권한, 지점 관리자 권한 확인용 변수
    private boolean isManagement;
    private boolean isBranchManagement;

    //필터 관련 설정
    private String sortType;
    private String sortColumn;
}
