package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

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
