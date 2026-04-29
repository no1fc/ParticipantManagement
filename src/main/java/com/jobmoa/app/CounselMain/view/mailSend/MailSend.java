package com.jobmoa.app.CounselMain.view.mailSend;

import com.jobmoa.app.CounselMain.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MailSend {

    @Autowired
    private RedisService redisService;

    /*@GetMapping("/mailSend.login")
    public String mailSendPage(){
        log.info("mailSendPage");
        return "mailSend/mailSend";
    }

    @PostMapping("/mailSend.login")
    public String mailSendAPI(){
        log.info("mailSendAPI");
        return "mailSend/mailSend";
    }*/

}
