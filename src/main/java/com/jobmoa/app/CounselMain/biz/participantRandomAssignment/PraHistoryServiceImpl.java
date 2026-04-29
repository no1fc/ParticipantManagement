package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PraHistoryServiceImpl implements PraHistoryService {

    @Autowired
    private PraHistoryDAO praHistoryDAO;

    @Autowired
    private ParticipantRandomAssignmentService praService;

    @Override
    public boolean insertHistory(PraHistoryRequestDTO request, String branch, String writerId) {
        boolean flag = true;

        if (request == null) {
            return false;
        }

        List<ParticipantRandomAssignmentDTO> assignments = request.getAssignments();
        if (assignments != null) {
            for (ParticipantRandomAssignmentDTO dto : assignments) {
                dto.setCondition("praInsert");
                dto.setBranch(branch);
                flag = praService.insert(dto) && flag;
            }
        }

        List<PraDailyReportDTO> dailyReports = request.getDailyReports();
        if (dailyReports != null) {
            for (PraDailyReportDTO dto : dailyReports) {
                dto.setBranch(branch);

                PraDailyReportDTO existingReport = praHistoryDAO.getPraDailyReport(dto);
                if (existingReport != null) {
                    dto.setTodayEmployment(existingReport.getTodayEmployment());// 금일 누적 실적 일반 취업
                    dto.setTodayPlacement(existingReport.getTodayPlacement());// 금일 누적 실적 알선 취업
                    dto.setToWeekEmployment(existingReport.getToWeekEmployment());// 주간 누적 실적 일반 취업
                    dto.setToWeekPlacement(existingReport.getToWeekPlacement());// 주간 누적 실적 알선 취업
                    dto.setToMonthEmployment(existingReport.getToMonthEmployment());// 월간 누적 실적 일반 취업
                    dto.setToMonthPlacement(existingReport.getToMonthPlacement());// 월간 누적 실적 알선 취업
                    dto.setToYearEmployment(existingReport.getToYearEmployment());// 연간 누적 실적 일반 취업
                    dto.setToYearPlacement(existingReport.getToYearPlacement());// 연간 누적 실적 알선 취업
                }
                log.info("DTO SELECT : [{}]", dto);

                flag = praHistoryDAO.upsertDailyReport(dto) && flag;
                log.info("DTO INSERT : [{}]", dto);
                log.info("DTO INSERT Flag : [{}]", flag);
            }
        }

        PraScoringConfigHistoryDTO scoringConfig = request.getScoringConfig();
        if (scoringConfig != null) {
            scoringConfig.setBranch(branch);
            scoringConfig.setWriterId(writerId);
            flag = praHistoryDAO.insertScoringConfig(scoringConfig) && flag;
        }

        List<PraCsvHistoryDTO> csvHistories = request.getCsvHistories();
        if (csvHistories != null) {
            for (PraCsvHistoryDTO dto : csvHistories) {
                dto.setBranch(branch);
                dto.setWriterId(writerId);
                flag = praHistoryDAO.insertCsvHistory(dto) && flag;
            }
        }

        return flag;
    }
}
