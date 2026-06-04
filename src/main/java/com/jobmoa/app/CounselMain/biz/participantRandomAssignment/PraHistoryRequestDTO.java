package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

import java.util.List;

/**
 * 참여자 배정 히스토리 일괄 저장 요청 객체.
 * 배정 결과, 일일업무보고, 산정식 설정, CSV 히스토리를 한 번에 묶어 전달한다.
 */
@Data
public class PraHistoryRequestDTO {
    private List<ParticipantRandomAssignmentDTO> assignments;
    private List<PraDailyReportDTO> dailyReports;
    private PraScoringConfigHistoryDTO scoringConfig;
    private List<PraCsvHistoryDTO> csvHistories;
}
