package com.jobmoa.app.CounselMain.view.dashboard.ajax;

import com.google.gson.JsonObject;
import com.jobmoa.app.CounselMain.biz.dashboard.ArrangementDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.ArrangementServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@RestController
public class DashboardArrangementAjaxController {

    @Autowired
    private ArrangementServiceImpl arrangementService;

    @Autowired
    private ChangeJson changeJson;

    /**
     * 날짜 계산 및 포맷팅을 처리하는 공통 메서드
     * @param today 기준 날짜
     * @param year 설정 연도
     * @param month 설정 월
     * @param adjuster 날짜 조정 정책 (월초/월말 등)
     * @param monthOffset 월 가감 (0: 현재, -1: 이전달 등)
     * @param yearOffset 연 가감 (0: 현재, -1: 작년 등)
     * @return yyyy-MM-dd 형식의 문자열
     */
    private String getFormattedDate(LocalDate today, int year, int month,
                                    java.time.temporal.TemporalAdjuster adjuster,
                                    int monthOffset, int yearOffset) {
        return today.withYear(year)
                .withMonth(month)
                .plusMonths(monthOffset)
                .plusYears(yearOffset)
                .with(adjuster)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String firstDayOfMonthDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.firstDayOfMonth(), 0, 0);
    }

    private String lastDayOfMonthDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.lastDayOfMonth(), 0, 0);
    }

    private String firstDayPreviousMonthOfMonthDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.firstDayOfMonth(), -1, 0);
    }

    private String lastDayPreviousMonthOfMonthDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.lastDayOfMonth(), -1, 0);
    }

    private String firstYearDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.firstDayOfMonth(), 0, -1);
    }

    private String lastYearDate(LocalDate today, int year, int month) {
        // 기존 로직 유지: year, month 설정 후 해당 월의 마지막 날
        return getFormattedDate(today, year, month, TemporalAdjusters.lastDayOfMonth(), 0, 0);
    }

    private String firstDayPreviousYearDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.firstDayOfMonth(), 0, -2);
    }

    private String lastDayPreviousYearDate(LocalDate today, int year, int month) {
        return getFormattedDate(today, year, month, TemporalAdjusters.lastDayOfMonth(), 0, -1);
    }

    @GetMapping(value = "/ajax/branchArrangement.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public ResponseEntity<JsonObject> branchReportUpdate(@ModelAttribute ArrangementDTO arrangementDTO) {

        JsonObject jsonObject = new JsonObject();
        LocalDate today = LocalDate.now();

        String year = arrangementDTO.getArrangementYear();
        String month = arrangementDTO.getArrangementMonth();

        int arrangementYear = Integer.parseInt(year);
        int arrangementMonth = Integer.parseInt(month);

        arrangementData(arrangementDTO, today, arrangementYear, arrangementMonth);

        try{
            arrangementCardData(arrangementDTO, jsonObject);

            arrangementChartData(arrangementDTO, jsonObject);

            jsonObject.addProperty("flag", true);
            return ResponseEntity.status(200).body(jsonObject);
        }
        catch (Exception e){
            log.error("Error updating branch report: {}", e.getMessage());
            jsonObject.addProperty("flag", false);
            return ResponseEntity.status(500).body(jsonObject);
        }

    }

    private void arrangementData(ArrangementDTO arrangementDTO, LocalDate today, int year, int month){
        if(month == 0){
            month = 11;
            arrangementDTO.setArrangementStartDate(firstYearDate(today, year, month));
            arrangementDTO.setArrangementPreviousMonthStartDate(firstDayPreviousYearDate(today, year, month));

            month = 10;
            arrangementDTO.setArrangementEndDate(lastYearDate(today, year, month));
            arrangementDTO.setArrangementPreviousMonthEndDate(lastDayPreviousYearDate(today, year, month));

            // 실적 기간 시작일 등록
            arrangementDTO.setArrangementPerformanceStartDate(firstYearDate(today, year, month));
        }
        else {
            arrangementDTO.setArrangementStartDate(firstDayOfMonthDate(today, year, month));
            arrangementDTO.setArrangementEndDate(lastDayOfMonthDate(today, year, month));

            arrangementDTO.setArrangementPreviousMonthStartDate(firstDayPreviousMonthOfMonthDate(today, year, month));
            arrangementDTO.setArrangementPreviousMonthEndDate(lastDayPreviousMonthOfMonthDate(today, year, month));

            // 실적 기간 시작일 등록
            arrangementDTO.setArrangementPerformanceStartDate(firstYearDate(today, year, month));
        }

//        log.info("arrangementDTO.arrangementStartDate : {}", arrangementDTO.getArrangementStartDate());
//        log.info("arrangementDTO.arrangementEndDate : {}", arrangementDTO.getArrangementEndDate());
//        log.info("arrangementDTO.arrangementPreviousMonthStartDate : {}", arrangementDTO.getArrangementPreviousMonthStartDate());
//        log.info("arrangementDTO.arrangementPreviousMonthEndDate : {}", arrangementDTO.getArrangementPreviousMonthEndDate());
//        log.info("arrangementDTO.arrangementPerformanceStartDate : {}", arrangementDTO.getArrangementPerformanceStartDate());

    }

    private void arrangementCardData(ArrangementDTO arrangementDTO, JsonObject jsonObject){
        arrangementDTO.setArrangementCondition("arrangementCardData");
        ArrangementDTO arrangementCardData = arrangementService.selectOne(arrangementDTO);
        String arrangementCardJsonData = String.format(
                "{"+
                        "\"totalArrangement\":%d," +
                        "\"firstRank\":{" +
                        "\"branch\":\"%s\"," +
                        "\"arrangementCount\":%d," +
                        "\"arrangementRankScorePercent\":%.2f" +
                        "}," +
                        "\"secondRank\":{" +
                        "\"branch\":\"%s\"," +
                        "\"arrangementCount\":%d," +
                        "\"arrangementRankScorePercent\":%.2f" +
                        "}," +
                        "\"previousMonthAchievementRate\":%.2f" +
                        "}",
                arrangementCardData.getArrangementTotal(),
                arrangementCardData.getArrangementFirstRankBranch(),
                arrangementCardData.getArrangementFirstRankCount(),
                arrangementCardData.getArrangementFirstRankScorePercent(),
                arrangementCardData.getArrangementSecondRankBranch(),
                arrangementCardData.getArrangementSecondRankCount(),
                arrangementCardData.getArrangementSecondRankScorePercent(),
                arrangementCardData.getArrangementPreviousMonthAchievementRate()
        );

        jsonObject.addProperty("arrangementCardData", arrangementCardJsonData);

    }

    private void arrangementChartData(ArrangementDTO arrangementDTO, JsonObject jsonObject){
        arrangementDTO.setArrangementCondition("arrangementChartData");
        List<ArrangementDTO> arrangementChartData = arrangementService.selectAll(arrangementDTO);

        String arrangementChartJson = changeJson.convertListToJsonArray(arrangementChartData, item -> {
            ArrangementDTO data = (ArrangementDTO) item;
            return String.format(
                    "{\"branch\":\"%s\"," +
                            "\"arrangementCount\":%d," +
                            "\"arrangementRank\":%d," +
                            "\"arrangementRankScorePercent\":%.2f" +
                            "}",
                    data.getArrangementBranch(),
                    data.getArrangementCount(),
                    data.getArrangementRank(),
                    data.getArrangementRankScorePercent()
            );
        });

        jsonObject.addProperty("arrangementChartData", arrangementChartJson);

    }


}
