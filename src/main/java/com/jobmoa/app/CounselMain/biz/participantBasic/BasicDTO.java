package com.jobmoa.app.CounselMain.biz.participantBasic;

import lombok.Data;

@Data
public class BasicDTO {
    //DB 변수
    private int basicJobNo; // PK(구직번호)
    private String basicBranch; //지점
    private String basicRegDate; // 등록일
    private String basicUserid; // 전담자_계정
    private String basicPartic; // 참여자
    private String basicDob; // 생년월일
    private String basicGender; // 성별
    private String basicRecruit; // 모집경로
    private String basicPartType; // 참여유형
    private String basicSchool; // 학교명
    private String basicSpecialty; // 전공
    private String basicAddress; // 주소(시,구)
    private String basicAntecedents; // 경력
    private String basicSpecific; // 특정계층
    private String basicEducation; // 학력
    private boolean basicClose; // 마감여부

    //join 변수
    private String basicUsername; //전담자 이름

    //다중 삭제 구직번호 변수
    private int[] basicJobNos;

    //DB 외 변수
    private String basicCondition; //개발자 구분

    //권한 관리
    private boolean basicBranchManagement;
    private boolean basicManagement;

}
