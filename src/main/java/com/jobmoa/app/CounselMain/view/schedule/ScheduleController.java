package com.jobmoa.app.CounselMain.view.schedule;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ScheduleController {

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
}
