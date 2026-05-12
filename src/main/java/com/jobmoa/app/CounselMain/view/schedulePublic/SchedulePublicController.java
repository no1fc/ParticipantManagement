package com.jobmoa.app.CounselMain.view.schedulePublic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/schedulePublic")
public class SchedulePublicController {

    @GetMapping({"", "/", "/login"})
    public String publicSchedulePage() {
        log.info("GET /schedulePublic");
        return "views/schedule/schedulePublicPage";
    }
}
