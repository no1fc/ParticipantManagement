package com.jobmoa.app.CounselMain.biz.participantEducation;

import lombok.Data;

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
