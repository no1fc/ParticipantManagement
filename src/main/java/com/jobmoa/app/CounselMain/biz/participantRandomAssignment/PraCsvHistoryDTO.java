package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

/**
 * 참여자 배정 CSV 업로드 히스토리 데이터 전송 객체.
 * CSV 파일을 통해 일괄 등록된 참여자 배정 이력의 각 행 데이터를 전달한다.
 */
@Data
public class PraCsvHistoryDTO {
    private int rowNumber;
    private String branch;
    private String writerId;
    private String counselorID;
    private String participant;
    private String participationType;
    private String gender;
    private String birthDate;
    private String recruitmentPath;
    private String hasCareer;
    private String education;
    private String specificClass;
    private String progressStage;
    private String travelCounselor;
}
