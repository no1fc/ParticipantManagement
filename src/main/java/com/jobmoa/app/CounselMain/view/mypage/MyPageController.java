package com.jobmoa.app.CounselMain.view.mypage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MyPageController {

    @GetMapping("/mypage.login")
    public String myPage(HttpSession session){
        log.info("MyPageController get Mapping mypage.login");
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        if(loginBean == null){
            log.info("loginBean is null");
            return "redirect:/";
        }
        String memberID = loginBean.getMemberUserID();
        log.info("MyPageController memberID : [{}]",memberID);


        return "views/myPage";
    }
}
