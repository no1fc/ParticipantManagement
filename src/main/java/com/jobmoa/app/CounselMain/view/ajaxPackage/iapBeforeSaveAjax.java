package com.jobmoa.app.CounselMain.view.ajaxPackage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class iapBeforeSaveAjax {

    @Autowired
    private ParticipantServiceImpl participantService;

    @PostMapping("/iapBeforeSaveAjax.login")
    public boolean iapBeforeSaveAjax(@RequestBody ParticipantDTO participantDTO, HttpSession session){
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        String loginId = loginBean.getMemberUserID();
        boolean branchAdminFlag = (Boolean)session.getAttribute("IS_BRANCH_MANAGER");
        boolean adminFlag = (Boolean)session.getAttribute("IS_MANAGER");

        //권한 확인을 위해 등록 및 아이디 확인
        participantDTO.setParticipantUserid(loginId);
        participantDTO.setParticipantBranchManagement(branchAdminFlag);
        participantDTO.setParticipantManagement(adminFlag);
        
        //지점관리자가 아니거나 관라지 계정이 아니라면
        //DTO에 아이디를 추가하고 업데이트를 진행한다.
//        boolean flag = false;
        participantDTO.setParticipantCondition("iapStatusUpdate");

        return participantService.update(participantDTO);
    }

}
