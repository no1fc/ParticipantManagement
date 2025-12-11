package com.jobmoa.app.CounselMain.view.ajaxPackage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardDTO;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardServiceImpl;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DashboardAjaxController {

    @Autowired
    private DashboardServiceImpl dashboardService;

    @Autowired
    private ParticipantServiceImpl participantService;

    @Autowired
    private ChangeJson changeJson;

    @PostMapping(value = "/dashBoardSuccess.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public String dashBoardAjaxSuccess(@RequestBody DashboardDTO dashboardDTO){
        String branch = dashboardDTO.getDashboardBranch();
        String startDate = dashboardDTO.getDashBoardStartDate();
        String endDate = dashboardDTO.getDashBoardEndDate();
        log.info("dashBoardAjaxSuccess branch : [{}]",branch);
        log.info("dashBoardAjaxSuccess startDate : [{}]",startDate);
        log.info("dashBoardAjaxSuccess endDate : [{}]",endDate);

        if(branch == null){
            return "fail: 지점 확인이 불가능합니다.";
        }
        else if(startDate == null && endDate == null){
            return "fail: 시작 날짜 혹은 마지막 날짜가 없습니다.";
        }
        dashboardDTO.setDashboardCondition("selectCounselSuccessMoney");
        List<DashboardDTO> branchData = dashboardService.selectAll(dashboardDTO);
        log.info("branchData branchData : [{}]",branchData);

//        String branchDataChangeJson = changeJson.convertListToJsonArray(branchData, item -> {
//            DashboardDTO dto = (DashboardDTO) item;
//                  return "{\"consultants\":\"" + dto.getDashBoardUserName() + "\","
//                          + "\"amounts\":\"" + dto.getDashBoardSuccessMoney() + "\"}";
//        });

        String branchDataChangeJson = changeJson.convertListToJsonArray(branchData, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            return String.format(
                    "{\"consultants\":\"%s\",\"amounts\":%s}",
                    dto.getDashBoardUserName(),
                    dto.getDashBoardSuccessMoney()
            );
        });

        log.info("branchDataChangeJson branchDataChangeJson : [{}]",branchDataChangeJson);

        return branchDataChangeJson;
    }

    @PostMapping(value = "/dashBoardInventive.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public String dashBoardAjaxInventive(@RequestBody DashboardDTO dashboardDTO){
        String startDate = dashboardDTO.getDashBoardStartDate();
        String endDate = dashboardDTO.getDashBoardEndDate();
        log.info("dashBoardAjaxInventive startDate : [{}]",startDate);
        log.info("dashBoardAjaxInventive endDate : [{}]",endDate);

        if(startDate == null && endDate == null){
            return "fail: 시작 날짜 혹은 마지막 날짜가 없습니다.";
        }
        dashboardDTO.setDashboardCondition("selectAjaxBranchInventiveFalseStatus");
        List<DashboardDTO> branchData = dashboardService.selectAll(dashboardDTO);
        log.info("dashBoardAjaxInventive branchData : [{}]",branchData);

        String branchDataChangeJson = changeJson.convertListToJsonArray(branchData, item -> {
            DashboardDTO dto = (DashboardDTO) item;
            return String.format(
                    "{\"branch\":\"%s\"," +
                            "\"noService\":%d," +
                            "\"lessThanOneMonth\":%d," +
                            "\"dispatchCompany\":%d," +
                            "\"iapSevenDays\":%d," +
                            "\"underThirtyHours\":%d," +
                            "\"underMinWage\":%d," +
                            "\"etc\":%d}",
                    dto.getDashboardBranch(),
                    dto.getNoServiceCount(),
                    dto.getLessThanOneMonthCount(),
                    dto.getDispatchCompanyCount(),
                    dto.getIapSevenDaysCount(),
                    dto.getUnderThirtyHoursCount(),
                    dto.getUnderMinWageCount(),
                    dto.getEtcCount()
            );
        });

        log.info("dashBoardAjaxInventive branchDataChangeJson : [{}]",branchDataChangeJson);

        return branchDataChangeJson;
    }

    //총점, 취업자, 알선취업자, 고용유지, 조기취업자, 나은일자리 비동기 조회
    @PostMapping(value = "dashBoardAjaxBranchScore.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public String consolScore(@RequestBody DashboardDTO dashboardDTO, HttpSession session){
//        Code 실행 시간을 확인하기 위해 작성
        long beforeTime = System.currentTimeMillis();

        log.info("consolScore Start Ajax");
        log.info("consolScore Session select Start");
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        boolean isManager = (boolean)session.getAttribute("IS_MANAGER");
        boolean isBranchManager = (boolean)session.getAttribute("IS_BRANCH_MANAGER");
        log.error("consolScore session.getAttribute(\"IS_MANAGER\") : [{}]",isManager);
        log.error("consolScore session.getAttribute(\"IS_BRANCH_MANAGER\") : [{}]",isBranchManager);
        String sessionBranch = loginBean.getMemberBranch();
        log.info("consolScore Session select End");
        String branch = dashboardDTO.getDashboardBranch();

        boolean branchFlag = branch.equals(sessionBranch);
//        log.info("consolScore branchFlag : [{}]",branch);
        if(!branchFlag && !isManager){
                log.info("consolScore Fail:");
                log.info("consolScore branch != sessionBranch : [{}]", branchFlag);
                return "Fail: 다른지점은 확인 할 수 없습니다.";
        }

        log.info("consolScore branchUserScore ChangeJson Start");
        boolean conditionFlag = Boolean.parseBoolean(dashboardDTO.getDashboardCondition());
        log.info("consolScore 고용유지 포함 여부 [{}]",dashboardDTO.isDashboardExcludeRetention());
        String selectCondition = (!conditionFlag) ? "selectBranchConsolScore" : "selectBranchConsolScorePerformance";

        if(isManager || isBranchManager){
            selectCondition+="Manager";
            //잡모아 실적
            log.info("consolScore selectBranchConsolScore (잡모아 실적 지점 관리자, 관리자 포함) 고용 점수 미포함 여부: [{}]", !conditionFlag);
        }
//        else{
//            //고용부 실적
//            log.info("consolScore selectBranchConsolScorePerformance (고용부 실적) : [{}]", true);
//        }
        dashboardDTO.setDashboardCondition(selectCondition);

        List<DashboardDTO> dashboardDatas = dashboardService.selectAll(dashboardDTO);
        String branchUserScore = changeJson.convertListToJsonArray(dashboardDatas , item ->{
            DashboardDTO dto = (DashboardDTO) item;
            return String.format(
                    "{\"username\":\"%s\"," +
                            "\"branch\":\"%s\"," +
                            "\"userID\":\"%s\"," +
//                            "\"score\":[%.2f,%.2f,%.2f,%.2f,%.2f,%.2f]," +
                            "\"totalScore\":%.2f," +
                            "\"employmentScore\":%.2f," +
                            "\"placementScore\":%.2f," +
                            "\"earlyEmploymentScore\":%.2f," +
                            "\"retentionScore\":%.2f," +
                            "\"betterJobScore\":%.2f," +
                            "\"myBranchScore\":%.2f}",
                    dto.getDashBoardUserName() == null ? "" : dto.getDashBoardUserName(),
                    dto.getDashboardBranch() == null ? "" : dto.getDashboardBranch(),
                    dto.getDashboardUserID() == null ? "" : dto.getDashboardUserID(),
                    dto.getTotalScore() == 0 ? 0 : dto.getTotalScore(),
                    dto.getEmploymentLastScore() == 0 ? 0 : dto.getEmploymentLastScore(),
                    dto.getPlacementLastScore() == 0 ? 0 : dto.getPlacementLastScore(),
                    dto.getEarlyEmploymentLastScore() == 0 ? 0 : dto.getEarlyEmploymentLastScore(),
                    dto.getRetentionLastScore() == 0 ? 0 : dto.getRetentionLastScore(),
                    dto.getBetterJobLastScore() == 0 ? 0 : dto.getBetterJobLastScore(),
                    dto.getMyBranchScoreAVG() == 0 ? 0 : dto.getMyBranchScoreAVG()
            );
        });
        log.info("consolScore branchUserScore ChangeJson End");

        log.info("consolScore branchScore ChangeJson Start");
        if(!conditionFlag){
            //잡모아 실적
            dashboardDTO.setDashboardCondition("selectTopConsolScore");
            log.info("consolScore selectTopConsolScore (잡모아 실적) : [{}]", false);
        }
        else{
            //고용부 실적
            dashboardDTO.setDashboardCondition("selectTopConsolScorePerformance");
            log.info("consolScore selectTopConsolScorePerformance (고용부 실적) : [{}]", true);
        }
        dashboardDatas = dashboardService.selectAll(dashboardDTO);
        String branchScore = changeJson.convertListToJsonArray(dashboardDatas , item ->{
            DashboardDTO dto = (DashboardDTO) item;
            return String.format(
                    "{\"totalStandardScore\":%.2f," +
                            "\"employmentTopScore\":%.2f," +
                            "\"placementTopScore\":%.2f," +
                            "\"earlyEmploymentTopScore\":%.2f," +
                            "\"retentionTopScore\":%.2f," +
                            "\"betterJobTopScore\":%.2f}",
                    dto.getTotalStandardScore() == 0 ? 0 : dto.getTotalStandardScore(),
                    dto.getEmploymentTopScore() == 0 ? 0 : dto.getEmploymentTopScore(),
                    dto.getPlacementTopScore() == 0 ? 0 : dto.getPlacementTopScore(),
                    dto.getEarlyEmploymentTopScore() == 0 ? 0 : dto.getEarlyEmploymentTopScore(),
                    dto.getRetentionTopScore() == 0 ? 0 : dto.getRetentionTopScore(),
                    dto.getBetterJobTopScore() == 0 ? 0 : dto.getBetterJobTopScore()
            );
        });
        log.info("consolScore branchScore ChangeJson End");

//        log.info("consolScore [{}]",branchUserScore);
//        log.info("consolScore [{}]",branchScore);

//      Code 실행 시간 확인을 위해 작성
        long afterTime = System.currentTimeMillis();

        log.info("End dashboardMain Controller(GetMapping) / beforeTime : [{}], afterTime : [{}]", beforeTime, afterTime);
        log.info("End dashboardMain Controller(GetMapping) / Check Time : [{}]", (afterTime - beforeTime) / 1000);
        return String.format(
                "{\"branchUserScore\":%s,\"branchScore\":%s}",
                branchUserScore,branchScore
        );
    }

    @PostMapping(value = "noServiceAjax.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public List<ParticipantDTO> noServiceSearchAjax(@RequestBody ParticipantDTO participantDTO, HttpSession session){
        log.info("noServiceSearchAjax Start noService.login");
        // 세션에 저장되어 있는 로그인 정보를 확인한다.
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        // 세션에 저장된 관리자 여부를 가져온다.
        boolean isManager = (boolean)session.getAttribute("IS_MANAGER");
        // 세션에 저장된 지점관리자 여부를 가져온다.
        boolean isBranchManager = (boolean)session.getAttribute("IS_BRANCH_MANAGER");
        String branch = participantDTO.getParticipantBranch();
        // 세션 로그인 정보에 있는 지점을 가져온다.
        String sessionBranch = loginBean.getMemberBranch();
        // 관리자 여부를 확인하고 관리자가 아니라면 null을 반환하고
        if(!isManager){
            log.info("noServiceSearchAjax Fail: isManager == false");
            return null;
        }
        //지점이 틀리고 지점 관리자가 아니라면 null을 반환한다.
        else if(!branch.equals(sessionBranch) && !isBranchManager){
            log.info("noServiceSearchAjax Fail: isBranchManager == false");
            return null;
        }
        participantDTO.setParticipantCondition("selectNoService");
        List<ParticipantDTO> datas = participantService.selectAll(participantDTO);
        return datas;
    }

    @PostMapping(value = "scoreBranchPerformanceGraphAjax.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public String scoreBranchPerformanceGraphAjax(@RequestBody DashboardDTO dashboardDTO){

        boolean conditionFlag = dashboardDTO.isDashboardFlagCondition();//Boolean.parseBoolean(dashboardDTO.getDashboardCondition());
        log.info("scoreBranchPerformanceGraphAjax 고용유지 포함 여부 [{}]",dashboardDTO.isDashboardExcludeRetention());
        log.info("scoreBranchPerformanceGraphAjax dashboardDTO.getDashBoardStartDate() : [{}]",dashboardDTO.getDashBoardStartDate());
        log.info("scoreBranchPerformanceGraphAjax dashboardDTO.getDashBoardEndDate() : [{}]",dashboardDTO.getDashBoardEndDate());

        dashboardDTO.setDashboardCondition("selectBranchAvg");
        log.info("scoreBranchPerformanceGraphAjax (1년 미만 상담사) : [{}]", conditionFlag);

        List<DashboardDTO> datas = dashboardService.selectAll(dashboardDTO);
        if(datas.isEmpty() || datas.size() == 0){
            return null;
        }
        // 반환된 data를 가지고 json 형식으로 그래프를 그릴 수 있도록 반환한다.
        String responseJson = changeJson.convertListToJsonArray(datas,item ->{
            DashboardDTO dto = (DashboardDTO)item;
            return String.format("{\"name\":\"%s\", \"data\":\"%.2f\"}",
                    dto.getDashboardBranch(),dto.getTotalBranchScoreAVG()
            );
        });

        log.info("scoreBranchDashboard responseJson : [{}]",responseJson);


        return responseJson;
    }

    @PostMapping(value ="scoreBranchPerformanceTableAjax.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public List<DashboardDTO> scoreBranchPerformanceTableAjax(@RequestBody DashboardDTO dashboardDTO){
        // TODO 전달된 날짜 기준 등록 후 시작일 종료일 처리
        boolean conditionFlag = dashboardDTO.isDashboardFlagCondition();
        log.info("scoreBranchPerformanceTableAjax 고용유지 포함 여부 [{}]",dashboardDTO.isDashboardExcludeRetention());
        log.info("scoreBranchPerformanceTableAjax dashboardDTO.getDashBoardStartDate() : [{}]",dashboardDTO.getDashBoardStartDate());
        log.info("scoreBranchPerformanceTableAjax dashboardDTO.getDashBoardEndDate() : [{}]",dashboardDTO.getDashBoardEndDate());

        log.info("scoreBranchPerformanceTableAjax dashboardDTO.isDashboardBranchAndPeople() : [{}]",dashboardDTO.isDashboardBranchAndPeople());

        // 정렬 사용을 위해 null 값일때 기본값을 설정
        String sortType = dashboardDTO.getSortType() == null ? "DESC" : dashboardDTO.getSortType();
        String sortColumn = dashboardDTO.getSortColumn() == null ? "totalScore" : dashboardDTO.getSortColumn();
        dashboardDTO.setSortType(sortType);
        dashboardDTO.setSortColumn(sortColumn);

        String condition = "selectBranchTable";
        if(!dashboardDTO.isDashboardBranchAndPeople()){
            condition = "selectPeopleTable";
        }

        dashboardDTO.setDashboardCondition(condition);
        log.info("scoreBranchPerformanceTableAjax (1년 미만 상담사) : [{}]", conditionFlag);

        List<DashboardDTO> datas = dashboardService.selectAll(dashboardDTO);
        if(datas.isEmpty() || datas.size() == 0){
            return null;
        }
//        log.info("scoreBranchPerformanceTableAjax datas : [{}]",datas);
        return datas;
    }

}
