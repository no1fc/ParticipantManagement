package com.jobmoa.app.CounselMain.view.ajaxPackage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/resumeRequest")
public class ResumeRequestAsyncController {

    @Autowired
    private ParticipantService participantService;

    @PostMapping(value = "/statusUpdate.login", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> resumeRequestStatusUpdate(@RequestBody ParticipantDTO participantDTO, HttpSession session) {
        log.info("ResumeRequestAsyncController resumeRequestStatusUpdate Start");
        log.info("ResumeRequestAsyncController resumeRequestStatusUpdate participantDTO : [{}]", participantDTO);

        // 세션 확인
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (loginBean == null) {
            String responseJson = "{\"statusData\":\"error\",\"message\":\"로그인이 필요합니다.\"}";
            return ResponseEntity.status(401).body(responseJson);
        }

        // 사용자 권한 정보 설정
        participantDTO.setParticipantUserid(loginBean.getMemberUserID());
        participantDTO.setParticipantBranch(loginBean.getMemberBranch());
//        participantDTO.setParticipantBranchManagement(loginBean.getMemberRole());
//        participantDTO.setParticipantManagement(loginBean.isMemberManagement());

        participantDTO.setParticipantCondition("resumeRequestStatusUpdate");

        String responseJson = "{\"statusData\":\"%s\",\"message\":\"%s\"}";

        try {
            boolean flag = participantService.update(participantDTO);
            if (!flag) {
                responseJson = String.format(responseJson, "error", "상태 변경 중 오류가 발생했습니다.");
                return ResponseEntity.status(400).body(responseJson);
            }

            responseJson = String.format(responseJson, "success", "상태가 성공적으로 변경되었습니다.");
            log.info("ResumeRequestAsyncController resumeRequestStatusUpdate responseJson : [{}]", responseJson);
            log.info("ResumeRequestAsyncController resumeRequestStatusUpdate End");
            return ResponseEntity.status(200).body(responseJson);
        } catch (Exception e) {
            log.error("ResumeRequestAsyncController resumeRequestStatusUpdate Exception Error : {}", e.getMessage());
            responseJson = String.format(responseJson, "error", "상태 변경 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(responseJson);
        }
    }
}