package com.jobmoa.app.CounselMain.view.mypage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 마이페이지 화면 컨트롤러.
 * <p>로그인된 사용자의 마이페이지 화면을 표시한다.
 * 비동기 데이터 처리는 {@link AsyncMyPage}에서 담당한다.</p>
 *
 * @see AsyncMyPage
 */
@Slf4j
@Controller
public class MyPageController {

    /**
     * 마이페이지 화면을 표시한다.
     * <p>로그인되지 않은 사용자는 메인 페이지로 리다이렉트한다.</p>
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @return 마이페이지 JSP 뷰 이름 (views/myPage) 또는 메인 페이지 리다이렉트
     */
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
