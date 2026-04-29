package com.jobmoa.app.CounselMain.view.ajaxPackage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicDTO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ParticipantDeleteAjax {

    @Autowired
    private ParticipantServiceImpl participantService;

    @PostMapping("/participantDelete.login")
    public List<Integer> ParticipantDelete(@RequestBody BasicDTO basicDTO, ParticipantDTO participantDTO, HttpSession session) {
        log.info("-----------------------------------");
        log.info("Start ParticipantDelete Ajax");
        //로그인 정보를 확인
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        //로그인 정보에서 아이디 확인
        String loginId = loginBean.getMemberUserID();

        //로그인 정보에서 지점 관리자 권한을 확인
        boolean branchAdminBoolean = (Boolean)session.getAttribute("IS_BRANCH_MANAGER");
        //로그인 정보에서 관리자 권한을 확인
        boolean adminBoolean = (Boolean)session.getAttribute("IS_MANAGER");

        //지점 관리자 이거나 관리자이면 adminFlag True
        boolean adminFlag = branchAdminBoolean || adminBoolean;

        //구직번호 배열이 넘어오면 해당 배열을 기준으로 삭제를 진행
        int[] jobNos = basicDTO.getBasicJobNos();
        log.info("Participant jobNos : [{}]",jobNos);
        log.info("Participant loginId : [{}]",loginId);
        // 삭제전 아이디를 추가한다.
        participantDTO.setParticipantUserid(loginId);

        List<Integer> result = new ArrayList<Integer>();
        // 여러 구직번호가 넘어와 삭제할 예정이니 반복문(forEach)를 사용하여 삭제를 진행
        for(int jobNo : jobNos) {
            // condition 추가
            participantDTO.setParticipantCondition("participantDelete");
            if(adminFlag){
                participantDTO.setParticipantCondition("participantDeleteAdmin");
            }


            //각 정보에 구직번호를 추가하고  기본정보 삭제 탭으로 전달한다.
            participantDTO.setParticipantJobNo(jobNo);
            //참여자 삭제를 확인한다.
            //참여자를 삭제하지 못했다면 false 를 반환받고 반환 받은 jobNo를 배열에 담아 둔다.
            if(!participantService.delete(participantDTO)){
                log.error("Participant Delete Fail jobNo : [{}]",jobNo);
                result.add(jobNo);
            }
        }

        //담긴 배열을 전달해준다.
        return result;
    }
}
