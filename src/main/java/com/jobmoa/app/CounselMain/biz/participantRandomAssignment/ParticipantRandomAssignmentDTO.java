package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

import java.util.List;

@Data
public class ParticipantRandomAssignmentDTO {
    private int pkNumber; // PK 번호
    private String counselor; // 상담사
    private String counselorID; // 상담사 ID
    private String counselorEmploymentDate; // 상담사입사일
    private String branch; // 지점
    private String participant; // 참여자
    private String birthDate; // 생년월일
    private String gender; // 성별
    private String recruitmentPath; // 모집경로
    private String participationType; // 참여유형
    private String progressStage; // 진행단계
    private String specificClass; // 특정계층
    private int career; // 경력
    private int hasCareer; // 경력유무
    private String education; // 학력
    private List<String> counselors;
    private List<String> participants;
    private List<String> birthDates;
    private List<String> genders;
    private List<String> recruitmentPaths;
    private List<String> participationTypes;
    private List<String> progressStages;

    //조회할 데이터 DTO
    private int yearDate;
    private int assignmentTotal;
    private int assignmentType1;
    private int assignmentType2;
    private int assignmentGenderMan;
    private int assignmentGenderWoman;
    private int assignmentYear;
    private int assignmentYouth;
    private int assignmentMiddleAged;
    private int assignmentSpecialGroup;
    private int assignmentCurrent;
    private int assignmentMax;
    private int assignmentMin;
    private int assignmentEducationHigh; // 고졸
    private int assignmentEducationCollege; // 대졸
    private int assignmentAllocationDay; // 당일 배정
    private int assignmentAllocationWeek; // 주간 배정
    private int assignmentAllocationTwoWeek; // 2주간 배정
    private int assignmentAllocationMonth; // 이번달 배정
    private float assignmentHeadcountWeight; // 인원 가중치

    //개발자 구분
    private String condition;
}
