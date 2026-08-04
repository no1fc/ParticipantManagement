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
import java.util.ArrayList;
import java.util.List;

/**
 * 알선 현황 대시보드 페이지 컨트롤러.
 * <p>
 * 당월/전월 기준 알선 카드 데이터(총 알선 건수, 1/2위 지점, 전월 대비 달성률)와
 * 지점별 알선 차트 데이터를 조회하여 대시보드 페이지에 제공한다.
 * </p>
 */
@Slf4j
@Controller
public class DashboardArrangementController {

    @Autowired
    private ArrangementServiceImpl arrangementService;

    @Autowired
    private ChangeJson changeJson;

    /**
     * 이번 달 1일 날짜를 반환한다.
     *
     * @return yyyy-MM-dd 형식의 이번 달 1일 문자열
     */
    private String firstDayOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    /**
     * 이번 달 마지막 날짜를 반환한다.
     *
     * @return yyyy-MM-dd 형식의 이번 달 마지막 날짜 문자열
     */
    private String lastDayOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 전월 1일 날짜를 반환한다.
     *
     * @return yyyy-MM-dd 형식의 전월 1일 문자열
     */
    private String firstDayPreviousMonthOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.with(TemporalAdjusters.firstDayOfMonth());
        firstDay = firstDay.minusMonths(1);
        return firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    /**
     * 전월 마지막 날짜를 반환한다.
     *
     * @return yyyy-MM-dd 형식의 전월 마지막 날짜 문자열
     */
    private String lastDayPreviousMonthOfMonthDate(){
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        lastDay = lastDay.minusMonths(1);
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 알선 실적 기간 시작일(전년도 11월 1일)을 반환한다.
     *
     * @return yyyy-MM-dd 형식의 전년도 11월 1일 문자열
     */
    private String startArrangementPerformanceStartDate(){
        LocalDate today = LocalDate.now();
        String previousYear = today.getYear() - 1 + "";
        LocalDate lastDay = LocalDate.parse(previousYear + "-11-01");
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 알선 현황 대시보드 페이지로 이동한다.
     * <p>
     * 당월/전월 기간을 설정하여 알선 카드 데이터(총건수, 1/2위 지점, 전월 달성률)와
     * 지점별 알선 차트 데이터를 조회하여 모델에 추가한다.
     * </p>
     *
     * @param model          뷰에 전달할 데이터 모델
     * @param arrangementDTO 알선 검색 조건 DTO
     * @return {@code "views/DashBoardArrangementPage"} JSP 뷰
     */
    @GetMapping("/arrangementDashboard.login")
    public String arrangementDashboard(Model model, ArrangementDTO arrangementDTO){

        arrangementDTO.setArrangementStartDate(firstDayOfMonthDate());
        arrangementDTO.setArrangementEndDate(lastDayOfMonthDate());

        arrangementDTO.setArrangementPreviousMonthStartDate(firstDayPreviousMonthOfMonthDate());
        arrangementDTO.setArrangementPreviousMonthEndDate(lastDayPreviousMonthOfMonthDate());

        // 실적 기간 시작일 등록
        arrangementDTO.setArrangementPerformanceStartDate(startArrangementPerformanceStartDate());


        // 카드 데이터(총건수/달성률 + 공동 1·2위 지점 목록)를 JSON으로 구성
        model.addAttribute("arrangementCardData", buildArrangementCardJson(arrangementDTO));


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


        return "views/DashBoardArrangementPage";
    }

    /**
     * 알선 카드 데이터를 JSON 문자열로 구성한다.
     * <p>스칼라 집계(총 알선 건수, 전월 대비 달성률)와 공동 1·2위 지점 목록(동점 시 다중)을
     * {@code firstRank}/{@code secondRank} 배열로 담는다.</p>
     *
     * @param arrangementDTO 조회 기간이 설정된 알선 DTO
     * @return 알선 카드 JSON 문자열
     */
    private String buildArrangementCardJson(ArrangementDTO arrangementDTO) {
        // 1. 스칼라 집계 (총 알선 건수 + 전월 대비 달성률)
        arrangementDTO.setArrangementCondition("arrangementCardData");
        ArrangementDTO cardScalar = arrangementService.selectOne(arrangementDTO);

        // 2. 공동 1·2위 지점 목록 (DENSE_RANK 순위 1/2 지점 전체)
        arrangementDTO.setArrangementCondition("arrangementRankBranches");
        List<ArrangementDTO> rankBranches = arrangementService.selectAll(arrangementDTO);

        List<ArrangementDTO> firstRank = new ArrayList<>();
        List<ArrangementDTO> secondRank = new ArrayList<>();
        if (rankBranches != null) {
            for (ArrangementDTO row : rankBranches) {
                if (row.getArrangementRank() == 1) {
                    firstRank.add(row);
                } else if (row.getArrangementRank() == 2) {
                    secondRank.add(row);
                }
            }
        }

        String firstRankJson = changeJson.convertListToJsonArray(firstRank,
                item -> rankBranchJson((ArrangementDTO) item));
        String secondRankJson = changeJson.convertListToJsonArray(secondRank,
                item -> rankBranchJson((ArrangementDTO) item));

        int total = cardScalar != null ? cardScalar.getArrangementTotal() : 0;
        float rate = cardScalar != null ? cardScalar.getArrangementPreviousMonthAchievementRate() : 0f;

        return String.format(
                "{\"totalArrangement\":%d,\"previousMonthAchievementRate\":%.2f," +
                        "\"firstRank\":%s,\"secondRank\":%s}",
                total, rate, firstRankJson, secondRankJson);
    }

    /**
     * 순위 지점 1건을 JSON 객체 문자열로 변환한다.
     *
     * @param data 순위 지점 행 DTO
     * @return {@code {"branch":..,"arrangementCount":..,"arrangementRankScorePercent":..}}
     */
    private String rankBranchJson(ArrangementDTO data) {
        return String.format(
                "{\"branch\":\"%s\",\"arrangementCount\":%d,\"arrangementRankScorePercent\":%.2f}",
                data.getArrangementBranch(), data.getArrangementCount(), data.getArrangementRankScorePercent());
    }

}
