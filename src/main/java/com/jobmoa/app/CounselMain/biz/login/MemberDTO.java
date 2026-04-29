package com.jobmoa.app.CounselMain.biz.login;

import lombok.Data;

import java.util.List;

@Data
public class MemberDTO {
    // DB 변수(컬럼)
    private int memberLoginPK; //전담자 PK 번호
    private String memberBranch; //전담자 지점
    private String memberUserName; //전담자 이름
    private String memberRole; //전담자 권한
    private String memberPhoneNumber; //전담자 전화번호
    private String memberUserID; //전담자 아이디
    private String memberUserPW; //전담자 비밀번호
    private String memberUserChangePW; //전담자 변경 비밀번호
    private String memberUserChangePWOK; //전담자 변경 비밀번호 확인
    private boolean memberISManager; //전담자 관리자 권한
    private String memberRegDate; //전담자 동록일
    private String memberUniqueNumber; //전담자 고유번호
    private int memberTodayEmployment;// 금일 누적 실적 일반 취업
    private int memberTodayPlacement;// 금일 누적 실적 알선 취업
    private int memberToWeekEmployment;// 주간 누적 실적 일반 취업
    private int memberToWeekPlacement;// 주간 누적 실적 알선 취업
    private int memberToMonthEmployment;// 월간 누적 실적 일반 취업
    private int memberToMonthPlacement;// 월간 누적 실적 알선 취업
    private int memberToYearEmployment;// 연간 누적 실적 일반 취업
    private int memberToYearPlacement;// 연간 누적 실적 알선 취업
    private String endUpdateStatus; // 실적 마지막 저장일
    private String memberJoinedDate; //전담자 입사일
    private String memberAssignedDate; //전담자 발령일
    private String memberContinuous; //근속구분
    
    private boolean memberPasswordChange; // 패스워드 변경 여부

    // DB 외 변수
    private String[] userIds; // 사용자 계정
    private Integer[] userOderNumber; // 사용자 순서
    private List<Boolean> memberDailyWorkDiaryIssuances; // 일일업무일지 발급 여부

    private String memberCondition; // 개발자 구분
}
