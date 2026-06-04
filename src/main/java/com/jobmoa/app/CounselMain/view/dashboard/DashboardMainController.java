package com.jobmoa.app.CounselMain.view.dashboard;


import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Calendar;
import java.util.List;


/**
 * 메인 대시보드 페이지 컨트롤러.
 * <p>
 * 전담자의 개인 대시보드(성공금, 인센티브, 참여자 현황, 성과 점수, KPI),
 * 성공금 상세 페이지, 성과 점수 상세 페이지, 지점별 평균 점수 페이지를 제공한다.
 * </p>
 */
@Slf4j
@Controller
public class DashboardMainController {
    @Autowired
    private DashboardServiceImpl dashboardService;

    @Autowired
    private ChangeJson changeJson;


    //내 성과에 표기될 문자를 입력
    private static final String[] DASHBOARD_TEXT = {"잡모아 평균","지점 평균","전담자"};

    /**
     * 대시보드 조회 기간을 설정한다.
     * <p>
     * 배정인원은 해당 년도 기준(1월~12월), 실적 데이터는 전년 11월~당해 10월 기준으로 설정한다.
     * 년도가 미지정이면 현재 년도를 기본값으로 사용한다.
     * </p>
     *
     * @param dashboardDTO 기간을 설정할 대시보드 DTO
     * @throws NullPointerException DTO가 {@code null}인 경우
     */
    private static void failDate(DashboardDTO dashboardDTO) throws NullPointerException{

        // NullPointException 방지를 위해 오류를 반환
        if(dashboardDTO == null){
            throw new NullPointerException("DashboardDTO is Null");
        }

        // DashBoardDTO dashBoardYear null 이라면 현재 년도를 불러오고 null이 아니라면 그대로 사용
        String dashBoardYear = dashboardDTO.getDashBoardYear() == null
                ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR))
                : dashboardDTO.getDashBoardYear();

        //배정인원 년도 기준 데이터 조회용
        String startDate = dashBoardYear + "-01-01";
        String endDate = dashBoardYear + "-12-31";
        dashboardDTO.setDashBoardPASD(startDate);
        dashboardDTO.setDashBoardPAED(endDate);

        //데이터 일정용 작년 11월 ~ 이번년도 10월까지 데이터
        // ex) 2024-11-01
        String dashBoardStartDate = (Integer.parseInt(dashBoardYear)-1) + "-11-01";
        // ex) 2025-10-31
        String dashBoardEndDate = dashBoardYear + "-10-31";

        dashboardDTO.setDashBoardStartDate(dashBoardStartDate);
        dashboardDTO.setDashBoardEndDate(dashBoardEndDate);

    }


    /**
     * 전담자 메인 대시보드 페이지로 이동한다.
     * <p>
     * 세션에서 로그인 정보를 추출하고, 성공금, 인센티브, 일일 업무, 참여자 현황,
     * 성과 점수, KPI 등 종합 대시보드 데이터를 조회하여 모델에 추가한다.
     * </p>
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param session      HTTP 세션 (로그인 정보 추출용)
     * @param dashboardDTO 검색 조건 (년도 등)
     * @return {@code "views/DashBoardPage"} JSP 뷰
     */
    @GetMapping("/dashboard.login")
    public String dashboardMain(Model model, HttpSession session, DashboardDTO dashboardDTO) {

//        long beforeTime = System.currentTimeMillis();

        log.info("Start dashboardMain Controller(GetMapping)");

        try {
            // 세션 정보 추출
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            String userID = loginBean.getMemberUserID();
            String branch = loginBean.getMemberBranch();
            String dashBoardYear = dashboardDTO.getDashBoardYear() == null
                    ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR))
                    : dashboardDTO.getDashBoardYear();

            log.info("DashBoard login ID : [{}] / Branch : [{}] / YEAR : [{}]", userID, branch, dashBoardYear);

            // DTO 설정
            setupDashboardDTO(dashboardDTO, userID, branch, dashBoardYear);

            // 데이터 조회 및 처리
            processDashboardData(model, dashboardDTO, dashBoardYear);

        } catch (Exception e) {
            log.error("Dashboard 데이터 로딩 실패", e);
            model.addAttribute("error", "데이터 로딩 중 오류가 발생했습니다.");
        }

//        long afterTime = System.currentTimeMillis();
//

        return "views/DashBoardPage";
    }

    /**
     * 대시보드 DTO에 사용자 ID, 지점, 년도 및 조회 기간을 설정한다.
     *
     * @param dto    설정할 대시보드 DTO
     * @param userID 로그인 사용자 ID
     * @param branch 소속 지점명
     * @param year   조회 년도
     */
    private void setupDashboardDTO(DashboardDTO dto, String userID, String branch, String year) {
        dto.setDashboardUserID(userID);
        dto.setDashboardBranch(branch);
        dto.setDashBoardYear(year);

        failDate(dto);

//        //배정인원 년도 기준 데이터 조회용
//        String startDate = year + "-01-01";
//        String endDate = year + "-12-31";
//        dto.setDashBoardPASD(startDate);
//        dto.setDashBoardPAED(endDate);
//
//        //데이터 일정용 작년 11월 ~ 이번년도 10월까지 데이터
//        String dashBoardStartDate = (Integer.parseInt(year)-1) + "-11-01";
//        String dashBoardEndDate = year + "-10-31";
//        dto.setDashBoardStartDate(dashBoardStartDate);
//        dto.setDashBoardEndDate(dashBoardEndDate);
    }

    /**
     * 대시보드에 표시할 전체 데이터를 조회하고 모델에 추가한다.
     * <p>
     * 지점/사용자 정보, 성공금, 일일 대시보드, 참여자 현황,
     * 성과 점수, KPI, 취업자 인원 비율 등을 포함한다.
     * </p>
     *
     * @param model          뷰에 전달할 데이터 모델
     * @param dashboardDTO   검색 조건이 설정된 대시보드 DTO
     * @param dashBoardYear  조회 년도
     * @throws Exception 데이터 조회 실패 시
     */
    private void processDashboardData(Model model, DashboardDTO dashboardDTO, String dashBoardYear)
            throws Exception {

        // 기본 데이터
        model.addAttribute("dashBoardYear", dashBoardYear);
        model.addAttribute("dashBoardDataTitle", changeJson.toJson(DASHBOARD_TEXT));

        // 1. 지점 및 사용자 정보
        dashboardDTO.setDashboardCondition("selectBranchAndUser");
        DashboardDTO branchAndUser = dashboardService.selectOne(dashboardDTO);

        // 2. 성공금 정보
        dashboardDTO.setDashboardCondition("selectSuccessMoney");
        DashboardDTO successMoney = dashboardService.selectOne(dashboardDTO);

        if (branchAndUser != null && successMoney != null) {
            processSuccessMoneyData(model, branchAndUser, successMoney);
        }

        // 3. 일일 대시보드 (금일 업무)
        dashboardDTO.setDashboardCondition("selectDailyDashboard");
        DashboardDTO dailyParticipant = dashboardService.selectOne(dashboardDTO);
        model.addAttribute("dailyDashboard", dailyParticipant);

        // 4. 참여자 현황 - 기존 changeJson 활용
        processParticipantData(model, dashboardDTO);

        // 5. 성과 점수 현황 - 기존 changeJson 활용
        processScoreData(model, dashboardDTO);

        // 6. KPI 현황
        dashboardDTO.setDashboardCondition("myKPIDashboard");
        DashboardDTO myKPI = dashboardService.selectOne(dashboardDTO);
        model.addAttribute("myKPI", myKPI);

        // 6-1. 취업자 인원 및 비율
        dashboardDTO.setDashBoardStartDate((Integer.parseInt(dashBoardYear)-1) + "-11-01");
        dashboardDTO.setDashBoardEndDate(dashBoardYear + "-12-31");
        DashboardDTO employmentRate = dashboardService.selectOne(dashboardDTO);
        if (employmentRate != null) {
            int totalEmployed = employmentRate.getTotalEmployed() == 0 ? 0 : (int) employmentRate.getTotalEmployed();
            employmentRate.setDashBoardEmployedCountUser(totalEmployed);
//            employmentRate.setEmploymentRate(employmentRate.getEmploymentRate());
        }
        model.addAttribute("employmentRate", employmentRate);
    }

    /**
     * 성공금 및 인센티브 데이터를 잡모아 평균/지점 평균/전담자 기준으로 계산하여 모델에 추가한다.
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param branchAndUser 지점 수 및 사용자 수 정보
     * @param successMoney  성공금 및 인센티브 데이터
     */
    private void processSuccessMoneyData(Model model, DashboardDTO branchAndUser,
                                         DashboardDTO successMoney) {
        int branchCount = branchAndUser.getDashboardCountBranch();
        int userCount = branchAndUser.getDashboardCountUser();

        // 성공금 데이터
        int[] successMoneyArray = {
                successMoney.getDashBoardSuccessMoneyTotal() / branchCount,
                successMoney.getDashBoardSuccessMoneyBranch() / userCount,
                successMoney.getDashBoardSuccessMoneyUser()
        };

        // 인센티브 데이터
        int[] incentiveArray = {
                successMoney.getDashBoardSuccessMoneyTotalIncentive() / branchCount,
                successMoney.getDashBoardSuccessMoneyBranchIncentive() / userCount,
                successMoney.getDashBoardSuccessMoneyUserIncentive()
        };

        model.addAttribute("dashBoardSuccessMoney", changeJson.arrayToJson(successMoneyArray));
        model.addAttribute("dashBoardSuccessMoneyIncentive", changeJson.arrayToJson(incentiveArray));
    }

    /**
     * 참여자 현황 데이터(전체/미취업사후/현재/현재년도)를 JSON으로 변환하여 모델에 추가한다.
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param dashboardDTO 검색 조건이 설정된 대시보드 DTO
     */
    private void processParticipantData(Model model, DashboardDTO dashboardDTO) {
        // 전체 참여자
        dashboardDTO.setDashboardCondition("selectTotalParticipant");
        List<DashboardDTO> totalParticipant = dashboardService.selectAll(dashboardDTO);

        String totalParticipantJsonData = changeJson.convertListToJsonArray(totalParticipant,
                item -> {
                    DashboardDTO dto = (DashboardDTO) item;
                    return "{\"year\":\"" + dto.getDashBoardParticipatedYear() + "\"," +
                            "\"data\":\"" + dto.getDashBoardParticipatedCountOne() + "\"}";
                });


        // 전체 참여자 ('미취업사후관리', '미취업사후종료')
        dashboardDTO.setDashboardCondition("selectTotalUnemployedParticipant");
        List<DashboardDTO> totalUnemployedParticipant = dashboardService.selectAll(dashboardDTO);

        String totalUnemployedParticipantJsonData = changeJson.convertListToJsonArray(totalUnemployedParticipant,
                item -> {
                    DashboardDTO dto = (DashboardDTO) item;
                    return "{\"year\":\"" + dto.getDashBoardParticipatedYear() + "\"," +
                            "\"data\":\"" + dto.getDashBoardParticipatedCountOne() + "\"}";
                });

        // 현재 참여자
        dashboardDTO.setDashboardCondition("selectCurrentParticipant");
        List<DashboardDTO> currentParticipant = dashboardService.selectAll(dashboardDTO);

        String currentParticipantJsonData = changeJson.convertListToJsonArray(currentParticipant,
                item -> {
                    DashboardDTO dto = (DashboardDTO) item;
                    return "{\"year\":\"" + dto.getDashBoardParticipatedYear() + "\"," +
                            "\"data\":\"" + dto.getDashBoardParticipatedCountOne() + "\"}";
                });

        // 현재 년도 참여자
        dashboardDTO.setDashboardCondition("selectNowParticipant");
        DashboardDTO nowParticipant = dashboardService.selectOne(dashboardDTO);

        String nowParticipantJsonData = "[]";
        if (nowParticipant != null) {
            nowParticipantJsonData = "[" +
                    "{\"year\":\"\",\"data\":\"" + nowParticipant.getDashBoardParticipatedCountOne() + "\"}," +
                    "{\"year\":\"\",\"data\":\"" + nowParticipant.getDashBoardParticipatedCountTwo() + "\"}" +
                    "]";
        }

        model.addAttribute("totalParticipantJsonData", totalParticipantJsonData);
        model.addAttribute("totalUnemployedParticipantJsonData", totalUnemployedParticipantJsonData);
        model.addAttribute("currentParticipantJsonData", currentParticipantJsonData);
        model.addAttribute("nowParticipantJsonData", nowParticipantJsonData);

        log.info("totalParticipantJsonData : [{}]", totalParticipantJsonData);
        log.info("totalUnemployedParticipantJsonData : [{}]", totalUnemployedParticipantJsonData);
        log.info("currentParticipantJsonData : [{}]", currentParticipantJsonData);
        log.info("nowParticipantJsonData : [{}]", nowParticipantJsonData);
    }

    /**
     * 성과 점수 및 순위 데이터를 JSON으로 변환하여 모델에 추가한다.
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param dashboardDTO 검색 조건이 설정된 대시보드 DTO
     */
    private void processScoreData(Model model, DashboardDTO dashboardDTO) {
        dashboardDTO.setDashboardCondition("selectRankAndScore");
        List<DashboardDTO> testDatas = dashboardService.selectAll(dashboardDTO);

/*        String scoreJson = changeJson.convertListToJsonArray(testDatas, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            return "{\"myRanking\":\"" + dto.getMyRanking() + "\"," +
                    "\"myTotalRanking\":\"" + dto.getMyTotalRanking() + "\"," +
                    "\"myScore\":\"" + dto.getMyScore() + "\"," +
                    "\"data\":[\"" + dto.getTotalBranchScoreAVG() + "\",\"" +
                    dto.getMyBranchScoreAVG() + "\",\"" + dto.getMyScore() + "\"]," +
                    "\"totalTopScore\":\"" + dto.getTotalTopScore() + "\"," +
                    "\"pointsToNextGrade\":\"" + dto.getPointsToNextGrade() + "\"," +
                    "\"nextGrade\":\"" + dto.getNextGrade() + "\"" +
                    "}";
        });*/
        String scoreJson = changeJson.convertListToJsonArray(testDatas, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            return "{\"myRanking\":\"" + dto.getMyRanking() + "\"," +
                    "\"myTotalRanking\":\"" + dto.getMyTotalRanking() + "\"," +
                    "\"myScore\":\"" + dto.getMyScore() + "\"," +
                    "\"data\":[\"" + dto.getMyScore() + "\"]," +
                    "\"nextGrade\":\"" + dto.getNextGrade() + "\"" +
                    "}";
        });

        log.info("scoreJson : [{}]", scoreJson);
        model.addAttribute("scoreJson", scoreJson);
    }


    /**
     * 성공금 상세 페이지로 이동한다.
     * <p>월별 성공금 및 인센티브 데이터를 JSON으로 변환하여 차트에 제공한다.</p>
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param session      HTTP 세션 (로그인 정보 추출용)
     * @param dashboardDTO 검색 조건 (년도 등)
     * @return {@code "views/DashBoardSuccessMoneyPage"} JSP 뷰, 에러 시 {@code "views/info"}
     */
    @GetMapping("/successMoney.login")
    public String successMoney(Model model, HttpSession session, DashboardDTO dashboardDTO) {
        //session 값에 저장된 login Data를 Bean class에 저장
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        //login ID, 지점 변수를 생성
        String userID = loginBean.getMemberUserID();
        String branch = loginBean.getMemberBranch();
        //생성된 ID, 지점 변수를 DashboardDTO에 저장
        dashboardDTO.setDashboardUserID(userID);
        dashboardDTO.setDashBoardUserBranch(branch);

        try{
            failDate(dashboardDTO);
        }
        catch (NullPointerException e){
            log.error("Method successMoney url[/successMoney.login] DashboardDTO is Null ", e);
            String url="dashboard.login";
            String icon="back";
            String title="오류가 발생했습니다.";
            String message="시스템 관리자에게 문의해주세요.\n 오류 내역 : Method successMoney url[/successMoney.login] DashboardDTO is Null";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }
        //dashboard selectAll을 진행
        //selectSuccessMoneyDetails condition을 추가
        dashboardDTO.setDashboardCondition("selectSuccessMoneyDetails");
        List<DashboardDTO> datas = dashboardService.selectAll(dashboardDTO);
        if(datas == null || datas.size() == 0){
            log.error("Method successMoney url[/successMoney.login] datas is null or datas size is 0 ");
            String url="dashboard.login";
            String icon="back";
            String title="발생한 성공금이 없습니다.";
            String message="성공금 추가 후 진행해주세요.";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }

        String successMoneyJson = changeJson.convertListToJsonArray(datas, item -> {
            DashboardDTO dto = (DashboardDTO) item;  // 객체 캐스팅
            return "{\"date\":\"" + dto.getDashBoardDate() + "\","
                    + "\"data\":\"" + dto.getDashBoardSuccessMoney() + "\"}";
        });

        String incentiveJson = changeJson.convertListToJsonArray(datas, item -> {
            DashboardDTO dto = (DashboardDTO) item;  // 객체 캐스팅
            return "{\"date\":\"" + dto.getDashBoardDate() + "\","
                    + "\"data\":\"" + dto.getDashBoardIncentive() + "\"}";
        });

        log.info("successMoney successMoneyJson : [{}]", successMoneyJson);
        log.info("successMoney incentiveJson : [{}]", incentiveJson);
        log.info("successMoney datas : [{}]", datas);
        model.addAttribute("successMoneyDetails", datas);
        model.addAttribute("successMoneyJson", successMoneyJson);
        model.addAttribute("incentiveJson", incentiveJson);

        return "views/DashBoardSuccessMoneyPage";
    }

    /**
     * 개인 성과 점수 상세 페이지로 이동한다.
     * <p>
     * 관리자가 아닌 경우 자신의 지점/아이디로 제한하여 조회하며,
     * 취업/알선취업/조기취업/고용유지/나은일자리별 점수와 인원수를 제공한다.
     * </p>
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param session      HTTP 세션 (로그인 정보 및 권한 확인용)
     * @param dashboardDTO 검색 조건 (년도 등)
     * @return {@code "views/DashBoardScoreAndSituation"} JSP 뷰, 에러 시 {@code "views/info"}
     */
    @GetMapping("scoreDashboard.login")
    public String scoreDashboard(Model model, HttpSession session, DashboardDTO dashboardDTO){
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
//        boolean isBranchManager = (Boolean)session.getAttribute("IS_BRANCH_MANAGER");
        boolean isManager = (Boolean)session.getAttribute("IS_MANAGER");
        String userID = loginBean.getMemberUserID();
        String branch = loginBean.getMemberBranch();

        //관리자가 아니라면 지점, 사용자아이디를 추가하고 진행한다.
        if(!isManager){
            dashboardDTO.setDashboardUserID(userID);
            dashboardDTO.setDashBoardUserBranch(branch);
        }
//        //지점 관리자라면 아이디는 추가하지 않고 진행한다.
//        else if(isBranchManager){
//            dashboardDTO.setDashBoardUserBranch(branch);
//        }
        //상세보기를 클릭하면 DB에 사용자의 각 평가 현황별 % 점수를 반환해준다.

        try{
            failDate(dashboardDTO);
        }
        catch (NullPointerException e){
            log.error("Method successMoney url[/scoreDashboard.login] DashboardDTO is Null ", e);
            String url="dashboard.login";
            String icon="back";
            String title="오류가 발생했습니다.";
            String message="시스템 관리자에게 문의해주세요.\n 오류 내역 : url[/scoreDashboard.login] DashboardDTO is Null";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }


        //고용유지 점수 데이터를 추가하기 위해 boolean 값 전달
        dashboardDTO.setDashboardExcludeRetention(true);
        dashboardDTO.setDashboardCondition("selectScoreAndAvg");
        List<DashboardDTO> datas = dashboardService.selectAll(dashboardDTO);
        // 반환된 data를 가지고 json 형식으로 그래프를 그릴 수 있도록 반환한다.
        String responseJson = changeJson.convertListToJsonArray(datas,item ->{
            DashboardDTO dto = (DashboardDTO)item;
            // JSON 포맷 문자열 분리 (가독성 및 유지보수 용이성 확보)
            String jsonFormat = "{" +
                    "\"completedCount\":{\"name\":\"종료자수\",\"data\":\"%d\"}," +
                    "\"myScore\": {" +
                    "    \"name\": \"개인 점수\"," +
                    "    \"data\": [%.2f,%.2f,%.2f,%.2f,%.2f,%.2f]," +
                    "    \"oneData\": [%.2f,%.2f,%.2f,%.2f,%.2f]" +
                    "  }," +
                    "  \"myCount\": {" +
                    "    \"name\": \"점수 분포\"," +
                    "    \"data\": [%d,%d,%d,%d,%d]," +
                    "    \"avgData\": [%.2f,%.2f,%.2f,%.2f,%.2f]" +
                    "  }" +
                    "}";

            return String.format(jsonFormat,
                    dto.getTotalCompleted(),
                    // myScore.data (6개)
                    dto.getTotalScore(),dto.getEmploymentLastScore(),dto.getPlacementLastScore(),dto.getEarlyEmploymentLastScore(),dto.getRetentionLastScore(),dto.getBetterJobLastScore(),
                    // myScore.oneData (5개)
                    dto.getEmploymentOneScore(),dto.getPlacementOneScore(),dto.getEarlyEmploymentOneScore(),dto.getRetentionOneScore(),dto.getBetterJobOneScore(),
                    // myCount.data (5개)
                    dto.getTotalEmployed(),dto.getReferredEmploymentCount(),dto.getEarlyEmploymentCount(),dto.getRetentionCount(),dto.getBetterJobCount(),
                    // myCount.avgData (5개)
                    dto.getEmploymentRate(),dto.getPlacementRate(),dto.getEarlyEmploymentRate(),dto.getRetentionRate(),dto.getBetterJobRate()
            );
        });

        log.info("scoreDashboard responseJson : [{}]",responseJson);

        model.addAttribute("scoreAndAvg",responseJson);

        return "views/DashBoardScoreAndSituation";
    }

    /**
     * 지점별 평균 점수 대시보드 페이지로 이동한다.
     * <p>각 지점의 평균 성과 점수를 그래프로 표시하기 위한 데이터를 제공한다.</p>
     *
     * @param model        뷰에 전달할 데이터 모델
     * @param dashboardDTO 검색 조건 (년도 등)
     * @return {@code "views/DashBoardBranchScoreAndSituation"} JSP 뷰, 에러 시 {@code "views/info"}
     */
    @GetMapping("scoreBranchDashboard.login")
    public String scoreBranchDashboard(Model model, DashboardDTO dashboardDTO){
        //내 지점 평균 그래프 클릭하면 DB에 사용자의 각 평가 현황별 % 점수를 반환
        try{
            failDate(dashboardDTO);
        }
        catch (NullPointerException e){
            log.error("Method successMoney url[/scoreBranchDashboard.login] DashboardDTO is Null ", e);
            String url="dashboard.login";
            String icon="back";
            String title="오류가 발생했습니다.";
            String message="시스템 관리자에게 문의해주세요.\n 오류 내역 : url[/scoreBranchDashboard.login] DashboardDTO is Null";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }
        //처음 접속하면 1년 미만 근무자로 검색해서 가져올 예정이기 때문에 false를 지정
        dashboardDTO.setDashboardFlagCondition(false);
        dashboardDTO.setDashboardCondition("selectBranchAvg");
        List<DashboardDTO> datas = dashboardService.selectAll(dashboardDTO);
        // 반환된 data를 가지고 json 형식으로 그래프를 그릴 수 있도록 반환한다.
        String responseJson = changeJson.convertListToJsonArray(datas,item ->{
            DashboardDTO dto = (DashboardDTO)item;
            return String.format("{\"name\":\"%s\", \"data\":\"%.2f\"}",
                    dto.getDashboardBranch(),dto.getTotalBranchScoreAVG()
            );
        });

        log.info("scoreBranchDashboard responseJson : [{}]",responseJson);

        model.addAttribute("branchAvg",responseJson);
        model.addAttribute("dashBoardStartDate",dashboardDTO.getDashBoardStartDate());
        model.addAttribute("dashBoardEndDate",dashboardDTO.getDashBoardEndDate());

        return "views/DashBoardBranchScoreAndSituation";
    }
}
