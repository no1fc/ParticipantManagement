package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.Data;

import java.util.List;

@Data
public class PraHistoryRequestDTO {
    private List<ParticipantRandomAssignmentDTO> assignments;
    private List<PraDailyReportDTO> dailyReports;
    private PraScoringConfigHistoryDTO scoringConfig;
    private List<PraCsvHistoryDTO> csvHistories;
}
