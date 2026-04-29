package com.jobmoa.app.CounselMain.view.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Slf4j
@Controller
public class InfoBean {
    public static void info(Model model, String url, String icon, String title, String message){
        log.info("infoModel url : [{}]", url);
        log.info("infoModel icon : [{}]", icon);
        log.info("infoModel title : [{}]", title);
        log.info("infoModel message : [{}]", message);
        model.addAttribute("url", url);
        model.addAttribute("icon", icon);
        model.addAttribute("title", title);
        model.addAttribute("message", message);
    }
}
