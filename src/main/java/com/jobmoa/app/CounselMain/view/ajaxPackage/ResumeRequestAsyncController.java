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

/**
 * 이력서 요청 상태 변경을 위한 비동기 REST 컨트롤러.
 * <p>이력서 요청의 처리 상태를 업데이트하는 API를 제공한다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/resumeRequest")
public class ResumeRequestAsyncController {

    @Autowired
    private ParticipantService participantService;

    /**
     * 이력서 요청의 상태를 변경한다.
     * <p>로그인 세션을 확인하고, 사용자 정보를 설정한 후 상태를 업데이트한다.</p>
     *
     * @param participantDTO 상태 변경 정보가 담긴 DTO
     * @param session        HTTP 세션 (로그인 확인 및 사용자 정보 설정용)
     * @return 처리 결과 JSON ({@code statusData}, {@code message})
     */
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