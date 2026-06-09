package com.jobmoa.app.CounselMain.biz.participantEducation;

import lombok.Data;

/**
 * 참여자 직업훈련 정보 데이터 전송 객체.
 * 참여자가 이수한 직업훈련의 등록, 수정, 삭제 및 조회 시 사용된다.
 */
@Data
public class EducationDTO {
    private int educationNo; //직업훈련 PK 번호
    private int[] educationNos; //여러 직업훈련 PK 번호
    private int educationJobNo; //참여자 PK 번호
    private String education; //직업훈련 명칭
    private String[] educations; //여러 직업훈련 명칭

    //DB 외 정보
    private String educationCondition;
}
