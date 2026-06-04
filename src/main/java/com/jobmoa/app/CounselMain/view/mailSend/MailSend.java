package com.jobmoa.app.CounselMain.view.mailSend;

import com.jobmoa.app.CounselMain.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메일 발송 페이지 컨트롤러.
 * <p>현재 메일 발송 페이지 관련 엔드포인트는 비활성화 상태이며,
 * 실제 메일 발송 기능은 {@link AsyncMailSend}에서 비동기 API로 제공된다.</p>
 *
 * @see AsyncMailSend
 */
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
