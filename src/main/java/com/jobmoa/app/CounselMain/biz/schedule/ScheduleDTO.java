package com.jobmoa.app.CounselMain.biz.schedule;

import lombok.Data;

@Data
public class ScheduleDTO {
    // 테이블 매핑
    private Integer scheduleId;
    private Integer participantJobNo;
    private String counselorId;
    private String scheduleDate;
    private String startTime;
    private String endTime;
    private String scheduleType;
    private String memo;
    private Integer alertMinutes;
    private String regDate;
    private String modDate;
    private String deleteYn;

    // JOIN 결과
    private String participantName;
    private String participantStage;

    // 상담사 정보 (관리자 조회용)
    private String counselorName;

    // 검색/필터
    private String searchStartDate;
    private String searchEndDate;
    private String keyword;
    private String condition;

    // 드래그 앤 드롭용
    private String newDate;
    private String newStartTime;
    private String newEndTime;
}
