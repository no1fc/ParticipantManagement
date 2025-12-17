package com.jobmoa.app.CounselMain.view.login;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import com.jobmoa.app.CounselMain.view.function.MemberRoleCheck;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private InfoBean infoBean;

    @GetMapping("/login.do")
    public String loginController(HttpSession session) throws Exception {
        log.info("-----------------------------------");
        //Session에 저장되어 있는 Login DATA
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");

        String page = "views/login";
        //Session이 비어있지 않다면 dashboard.login 페이지로 이동
        if (loginBean != null) {
            log.info("dashboard.login page 이동");
            page = "redirect:dashboard.login";
        }

        log.info("login DATA : [{}]", loginBean);
        log.info("login Controller page : [{}]", page);
        log.info("-----------------------------------");
        return page;
    }

    @PostMapping("/login.do")
    public String loginController(Model model, HttpSession session, MemberDTO memberDTO, LoginBean loginBean, MemberRoleCheck memberRoleCheck){
        log.info("-----------------------------------");
        log.info("Start loginController");
        String url = "login.do";
        String icon = "error";
        String title = "로그인 실패";
        String message = "";

        memberDTO.setMemberCondition("loginSelect");
        memberDTO = memberService.selectOne(memberDTO);
        log.info("loginDTO : [{}]",memberDTO);

        // 사용자가 입력한 Data 가 Null 이 아니고
        // 검색된 Data 가 Null 이 아니면 Session 에 저장
        if(memberDTO != null){
            if(memberDTO.getMemberUserID() != null){
                log.info("loginController login Success user ID : [{}]",memberDTO.getMemberUserID());

                String role = memberDTO.getMemberRole();
                boolean isManager = memberDTO.isMemberISManager();
                //로그인 정보를 Bean 객체에 담고
                loginBean.setMemberUserID(memberDTO.getMemberUserID());
                loginBean.setMemberUserName(memberDTO.getMemberUserName());
                loginBean.setMemberBranch(memberDTO.getMemberBranch());
                loginBean.setMemberRole(role);
                loginBean.setMemberUniqueNumber(memberDTO.getMemberUniqueNumber());
                boolean branchRole = memberRoleCheck.checkBranchRole(role);
                boolean praRole = role.equals("PRA");

                //Session에 저장해 사용
                session.setAttribute("JOBMOA_LOGIN_DATA", loginBean);
                session.setAttribute("IS_BRANCH_MANAGER", branchRole);
                session.setAttribute("IS_MANAGER", isManager);
                session.setAttribute("IS_PRA_MANAGER",praRole);

                //Session 시간 6시간 지정
                session.setMaxInactiveInterval(21600);
                log.info("Session MaxInactiveInterval : [{}]",session.getMaxInactiveInterval());
                session.setAttribute("SESSION_TIME",System.currentTimeMillis());

                url = "dashboard.login";
                icon = "success";
                title = "로그인 성공";
            }
        }

        //info.jsp 페이지로 넘어갈때 활용
        //로그인 성공 : dashboard.login 페이지로 이동
        //로그인 실패 : login.do 페이지로 이동
        //SweetAlert 사용중 아이콘 선택
        //성공 : success
        //실패 : error
        InfoBean.info(model,url,icon,title,message);
        log.info("로그인 여부 : [{}]",title);

        //info 페이지로 이동
        log.info("-----------------------------------");
        return "views/info";
    }

    @GetMapping("/logout.do")
    public String logoutController(HttpSession session){
        log.info("-----------------------------------");
        //모든 세션 정보 제거
        log.info("session remove : [{}]", session.getAttribute("JOBMOA_LOGIN_DATA"));
        session.invalidate();
        log.info("-----------------------------------");
        return "redirect:login.do";
    }
}
