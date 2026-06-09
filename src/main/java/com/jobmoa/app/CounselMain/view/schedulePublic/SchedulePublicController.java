package com.jobmoa.app.CounselMain.view.schedulePublic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 공개 상담 일정 페이지 컨트롤러.
 * 로그인 없이 접근코드 인증 화면을 통해 지점의 상담 일정을 확인할 수 있는 공개 페이지를 렌더링한다.
 */
@Slf4j
@Controller
@RequestMapping("/schedulePublic")
public class SchedulePublicController {

    /**
     * 공개 상담 일정 인증/조회 페이지를 표시한다.
     *
     * @return JSP 뷰 이름 ("views/schedule/schedulePublicPage")
     */
    @GetMapping({"", "/", "/login"})
    public String publicSchedulePage() {
        log.info("GET /schedulePublic");
        return "views/schedule/schedulePublicPage";
    }
}
