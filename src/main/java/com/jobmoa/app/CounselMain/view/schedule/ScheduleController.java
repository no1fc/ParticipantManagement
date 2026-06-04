package com.jobmoa.app.CounselMain.view.schedule;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 상담 일정 페이지 컨트롤러.
 * 상담사 개인 일정 페이지와 관리자 일정 통합 관리 페이지를 렌더링한다.
 */
@Slf4j
@Controller
public class ScheduleController {

    /**
     * 상담사 개인 일정 관리 페이지를 표시한다.
     *
     * @param session HTTP 세션 (로그인 정보 조회용)
     * @param model   뷰에 전달할 모델 객체
     * @return JSP 뷰 이름 ("views/schedule/schedulePage") 또는 로그인 페이지로 리다이렉트
     */
    @GetMapping("schedule.login")
    public String schedulePage(HttpSession session, Model model) {
        log.info("GET /schedule.login");
        LoginBean login = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (login == null) {
            return "redirect:/login.do";
        }

        model.addAttribute("counselorId", login.getMemberUserID());
        model.addAttribute("counselorName", login.getMemberUserName());
        return "views/schedule/schedulePage";
    }

    /**
     * 관리자 일정 통합 관리 페이지를 표시한다.
     * 지점 내 전체 상담사의 일정을 통합 조회할 수 있는 관리 화면을 렌더링한다.
     *
     * @param session HTTP 세션 (로그인 및 지점 정보 조회용)
     * @param model   뷰에 전달할 모델 객체
     * @return JSP 뷰 이름 ("views/schedule/scheduleManagerPage") 또는 로그인 페이지로 리다이렉트
     */
    @GetMapping("scheduleManager.login")
    public String scheduleManagerPage(HttpSession session, Model model) {
        log.info("GET /scheduleManager.login");
        LoginBean login = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (login == null) {
            return "redirect:/login.do";
        }

        model.addAttribute("counselorId", login.getMemberUserID());
        model.addAttribute("counselorName", login.getMemberUserName());
        model.addAttribute("branch", login.getMemberBranch());
        return "views/schedule/scheduleManagerPage";
    }
}
