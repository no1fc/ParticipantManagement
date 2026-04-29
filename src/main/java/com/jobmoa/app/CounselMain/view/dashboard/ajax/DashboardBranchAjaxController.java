package com.jobmoa.app.CounselMain.view.dashboard.ajax;

import com.google.gson.JsonObject;
import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import com.jobmoa.app.CounselMain.biz.report.ReportDTO;
import com.jobmoa.app.CounselMain.biz.report.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DashboardBranchAjaxController {

    @Autowired
    ReportService reportService;

    @Autowired
    MemberService memberService;


    // dailyWorkSave mapping.xml SQL 문법
    @PostMapping(value = "/dashboard/branchReportUpdate.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public ResponseEntity<JsonObject> dashboardReportUpdate(HttpSession session, @RequestBody(required = false) ReportDTO reportDTO){
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");

        JsonObject jsonObject = new JsonObject();

        if (reportDTO == null) {
            jsonObject.addProperty("flag", "false");
            jsonObject.addProperty("message", "전달된 데이터가 없습니다.");
            return ResponseEntity.status(400).body(jsonObject);
        }

        log.info("dashboardReportUpdate reportDTO : [{}]", reportDTO);

        if (loginBean == null) {
            jsonObject.addProperty("flag", "false");
            jsonObject.addProperty("message", "로그인 후 이용해 주세요.");
            return ResponseEntity.status(500).body(jsonObject);
        }


        try {
            /**
             * dailyWorkSave 진행할때
             * assignmentStatusUpdate 같이 업데이트를 진행하니
             * 추후 변경할 일 있으면 service도 변경해야함
             */
            String branch = loginBean.getMemberBranch();
            reportDTO.setBranch(branch);
            log.info("일일보고 저장 지점 : {}", branch);
            log.info("일일보고 저장일 : {}", reportDTO.getDailyUpdateStatusDate());

            reportDTO.setReportCondition("dailyWorkSave");
            boolean result = reportService.update(reportDTO);

            jsonObject.addProperty("flag", result);
            jsonObject.addProperty("message", result ? "일일보고 완료" : "일일보고 실패");
            return ResponseEntity.status(result ? 200 : 500).body(jsonObject);
        }
        catch (NullPointerException e) {
            log.error("NullPointerException: ", e);
            jsonObject.addProperty("flag", false);
            jsonObject.addProperty("message", "빈칸이 발생하여 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(jsonObject);
        }
        catch (Exception e) {
            log.error("Error updating branch report", e);
            jsonObject.addProperty("flag", false);
            jsonObject.addProperty("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(500).body(jsonObject);
        }
    }




    @PostMapping(value = "/dashboard/memberOrderUpdate.login",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public ResponseEntity<JsonObject> memberOrderUpdate(HttpSession session, @RequestBody(required = false) MemberDTO memberDTO) {
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");

        JsonObject jsonObject = new JsonObject();

        if (loginBean==null){
            jsonObject.addProperty("flag", false);
            jsonObject.addProperty("message", "로그인 정보가 없습니다. 로그인 후 다시 시도해주세요.");
            return ResponseEntity.status(401).body(jsonObject);
        }

        try {

            memberDTO.setMemberCondition("memberOrderUpdate");
            boolean flag = memberService.update(memberDTO);

            jsonObject.addProperty("flag", flag);
            if(flag){
                jsonObject.addProperty("message", "순서가 저장되었습니다.");
            }else{
                jsonObject.addProperty("message", "순서 저장중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }

            return ResponseEntity.status(200).body(jsonObject);
        }
        catch (Exception e) {
            log.error("Error updating member order", e);
            jsonObject.addProperty("flag", false);
            jsonObject.addProperty("message", "순서 저장중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(500).body(jsonObject);
        }
    }
}
