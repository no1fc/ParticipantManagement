package com.jobmoa.app.CounselMain.view.dashboard;

import com.jobmoa.app.CounselMain.biz.dashboard.ArrangementDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.ArrangementServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Controller
public class DashboardArrangementController {

    @Autowired
    private ArrangementServiceImpl arrangementService;

    @Autowired
    private ChangeJson changeJson;

    private String firstDayOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    private String lastDayOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String firstDayPreviousMonthOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.with(TemporalAdjusters.firstDayOfMonth());
        firstDay = firstDay.minusMonths(1);
        return firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    private String lastDayPreviousMonthOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        lastDay = lastDay.minusMonths(1);
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String startArrangementPerformanceStartDate(){
        LocalDate today = LocalDate.now();
        String previousYear = today.getYear() - 1 + "";
        LocalDate lastDay = LocalDate.parse(previousYear + "-11-01");
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @GetMapping("/arrangementDashboard.login")
    public String arrangementDashboard(Model model, ArrangementDTO arrangementDTO){

        arrangementDTO.setArrangementStartDate(firstDayOfMonthDate());
        arrangementDTO.setArrangementEndDate(lastDayOfMonthDate());

        arrangementDTO.setArrangementPreviousMonthStartDate(firstDayPreviousMonthOfMonthDate());
        arrangementDTO.setArrangementPreviousMonthEndDate(lastDayPreviousMonthOfMonthDate());

        // 실적 기간 시작일 등록
        arrangementDTO.setArrangementPerformanceStartDate(startArrangementPerformanceStartDate());

//        log.info("arrangementDTO.arrangementStartDate : {}", arrangementDTO.getArrangementStartDate());
//        log.info("arrangementDTO.arrangementEndDate : {}", arrangementDTO.getArrangementEndDate());
//        log.info("arrangementDTO.arrangementPreviousMonthStartDate : {}", arrangementDTO.getArrangementPreviousMonthStartDate());
//        log.info("arrangementDTO.arrangementPreviousMonthEndDate : {}", arrangementDTO.getArrangementPreviousMonthEndDate());
//        log.info("arrangementDTO.arrangementPerformanceStartDate : {}", arrangementDTO.getArrangementPerformanceStartDate());


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
        model.addAttribute("arrangementCardData", arrangementCardJsonData);


        arrangementDTO.setArrangementCondition("arrangementChartData");
        List<ArrangementDTO> arrangementChartData = arrangementService.selectAll(arrangementDTO);

        String arrangementChartJson = changeJson.convertListToJsonArray(arrangementChartData, item -> {
            ArrangementDTO data = (ArrangementDTO) item;
            return String.format(
                    "{\"branch\":\"%s\"" +
                            ",\"arrangementCount\":%d" +
                            ",\"arrangementRank\":%d" +
                            ",\"arrangementRankScorePercent\":%.2f" +
                            "}",
                    data.getArrangementBranch(),
                    data.getArrangementCount(),
                    data.getArrangementRank(),
                    data.getArrangementRankScorePercent()
            );
        });
        model.addAttribute("arrangementChartData", arrangementChartJson);

//        log.info("arrangementChartJson : [{}]",arrangementChartJson);
//        log.info("arrangementCardData : [{}]",arrangementCardData);

        return "views/DashBoardArrangementPage";
    }

}
