package com.jobmoa.app.CounselMain.view.ajaxPackage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicDTO;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ParticipantCloseAjax {

    @Autowired
    private BasicServiceImpl basicService;

    @PostMapping("/ParticipantClose.login")
    public boolean ParticipantClose(@RequestBody BasicDTO basicDTO, HttpSession session){
        log.info("-----------------------------------");
        log.info("Start ParticipantClose Ajax");
        //마감여부
        boolean flag = basicDTO.isBasicClose();

        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        String userID = loginBean.getMemberUserID();
        log.info("Participant loginId : [{}]",userID);
        //사용자 아이디를 추가하여 basic service 로 전달한다.
        basicDTO.setBasicUserid(userID);
        //update를 위한 condition 추가
        basicDTO.setBasicCondition("basicCloseUpdate");

        //마감 여부 update를 성공했다면
        if (basicService.update(basicDTO)){
            log.info("Participant Close Success");
            //기본정보 selectOne을 진행해 마감여부를 불러온다.
            //selectOne을 위해 condition을 추가한다.
            basicDTO.setBasicCondition("basicSelectClose");
            //basicDTO 를 재활용해 null 여부를 확인하고 DTO가 null 이면 에러를 반환를 발생 시킨다.
            basicDTO = basicService.selectOne(basicDTO);
            if(basicDTO == null){
                new RuntimeException("basicDTO is null");
            }

            return basicDTO.isBasicClose();
        }

        log.info("-----------------------------------");
        return flag;
    }
}
