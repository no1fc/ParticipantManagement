package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.Data;

@Data
public class AdminDTO {
    // 공통 검색/조건 파라미터
    private String adminCondition;
    private String searchBranch;
    private String searchName;
    private String searchUserId;
    private String searchRole;
    private String searchStartDate;
    private String searchEndDate;
    private int pk;

    // J_참여자관리_로그인정보 (사용자 관리)
    private int memberNo;
    private String branch;
    private String memberName;
    private String userId;
    private String password;
    private String role;
    private boolean isAdmin;
    private String regDate;
    private String uniqueNo;
    private String email;
    private String phoneNumber;
    private String useStatus;
    private String hireDate;
    private String lastAssignDate;
    private String continuousPeriod;
    private int viewOrder;
    private boolean performanceWrite;
    private boolean dailyReportIssue;
    private double assignWeight;

    // J_참여자관리_지점 (지점 관리)
    private int branchNo;
    private String branchName;
    private int branchStaff;
    private int type1;
    private int type2;
    private String department;
    private int branchOrder;

    // J_참여자관리 (참여자 - 관리자 조회용)
    private int jobNo;
    private String participantName;
    private String birthDate;
    private String gender;
    private String counselorAccount;
    private String progressStage;
    private String participantRegDate;
    private boolean closed;
    private String recruitPath;
    private String participationType;
    private String career;
    private String education;
    private String specialClass;
    private String employmentCapacity;
    private String desiredJob;
    private String desiredSalary;
    private String memo;
    private String searchJobNo;
    private String searchStatus;
    private String searchClosed;

    // J_참여자관리_일일업무보고 (일일업무보고)
    private String reportUserId;
    private String reportBranch;
    private int dailyGeneralEmp;
    private int dailyPlacementEmp;
    private int weekGeneralEmp;
    private int weekPlacementEmp;
    private int monthGeneralEmp;
    private int monthPlacementEmp;
    private int yearGeneralEmp;
    private int yearPlacementEmp;
    private String reportDate;
    private String reportRegDate;

    // J_참여자관리_기준금액 (기준금액)
    private String division;
    private String year;
    private int minWage;
    private int maxWage;
    private int successFee;

    // J_참여자관리_나은기준임금 (나은기준임금)
    private String betterYear;
    private String betterYearDate;
    private int betterWage;
    private String betterRegDate;

    // J_참여자관리_배정CSV_히스토리 (CSV 히스토리)
    private int rowNumber;
    private String csvBranch;
    private String csvWriterId;
    private String counselorId;
    private String csvParticipant;
    private String csvParticipationType;
    private String csvGender;
    private String csvBirthDate;
    private String csvRecruitPath;
    private String csvCareer;
    private String csvEducation;
    private String csvSpecificClass;
    private String csvProgressStage;
    private String csvTravelCounselor;
    private String csvRegDate;

    // J_참여자관리_배정산정식_히스토리 (산정식 히스토리)
    private String formulaBranch;
    private String formulaWriterId;
    private double weightLoad;
    private double weightProcess;
    private double weightContinuous;
    private double weightPace;
    private int gapThreshold;
    private int dailyLimit;
    private String g1Limit;
    private String g2Limit;
    private String g3Limit;
    private String formulaRegDate;

    // J_참여자관리_알선상세정보 (알선 관리)
    private int registrationNo;
    private int placementJobNo;
    private String detailInfo;
    private String recommendation;
    private String placementRegDate;
    private String placementUpdateDate;

    // J_참여자관리_이력서요청양식 (이력서 요청)
    private int resumeRegNo;
    private int resumeJobNo;
    private String companyName;
    private String managerName;
    private String managerEmail;
    private String emergencyContact;
    private String otherRequests;
    private String requestStatus;
    private String resumeRegDate;
    private String resumeUpdateDate;

    // J_참여자관리_자격증 (자격증)
    private int certificateNo;
    private int certJobNo;
    private String certificateName;

    // J_참여자관리_직업훈련 (직업훈련)
    private int trainingNo;
    private int trainJobNo;
    private String trainingName;

    // 대시보드 KPI
    private int totalParticipants;
    private int monthlyNewAssignments;
    private double employmentRate;
    private int fiscal_employed;
    private int fiscal_completed;
    private double goalAchievementRate;
    private int branchParticipantCount;
    private String branchLabel;
    private int employmentCount;
    private int placementCount;
}