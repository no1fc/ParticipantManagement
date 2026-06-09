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

/**
 * 참여자 마감 처리를 위한 비동기 REST 컨트롤러.
 * <p>참여자의 마감 여부를 업데이트하고 변경된 마감 상태를 반환한다.</p>
 */
@Slf4j
@RestController
public class ParticipantCloseAjax {

    @Autowired
    private BasicServiceImpl basicService;

    /**
     * 참여자의 마감 여부를 변경한다.
     * <p>마감 상태를 업데이트한 후 변경된 마감 여부를 DB에서 다시 조회하여 반환한다.</p>
     *
     * @param basicDTO 마감 여부 및 구직번호 정보가 담긴 DTO
     * @param session  HTTP 세션 (로그인 사용자 아이디 확인용)
     * @return 변경된 마감 여부 ({@code true}: 마감, {@code false}: 미마감)
     */
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
