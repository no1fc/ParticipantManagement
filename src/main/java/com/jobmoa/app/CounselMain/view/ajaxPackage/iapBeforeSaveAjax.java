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

/**
 * IAP(개인활동계획) 상태 사전 저장을 위한 비동기 REST 컨트롤러.
 * <p>
 * 참여자의 IAP 상태를 업데이트하며, 세션에서 사용자 권한(관리자, 지점관리자)을
 * 확인하여 DTO에 설정한다.
 * </p>
 */
@Slf4j
@RestController
public class iapBeforeSaveAjax {

    @Autowired
    private ParticipantServiceImpl participantService;

    /**
     * IAP 상태를 사전 업데이트한다.
     * <p>세션에서 로그인 사용자의 아이디와 관리자 권한을 확인한 후 IAP 상태를 업데이트한다.</p>
     *
     * @param participantDTO IAP 상태 정보가 담긴 DTO
     * @param session        HTTP 세션 (로그인 정보 및 권한 확인용)
     * @return 업데이트 성공 여부
     */
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
