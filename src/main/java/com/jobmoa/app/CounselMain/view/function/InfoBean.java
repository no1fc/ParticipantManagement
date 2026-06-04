package com.jobmoa.app.CounselMain.view.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

/**
 * 알림 페이지(info.jsp) 전달용 유틸리티 클래스.
 * <p>SweetAlert2 알림을 표시한 후 지정된 URL로 이동하기 위한
 * Model 속성(url, icon, title, message)을 설정한다.</p>
 */
@Slf4j
@Controller
public class InfoBean {
    /**
     * 알림 페이지에 표시할 정보를 Model에 설정한다.
     * @param model Spring MVC Model 객체
     * @param url 알림 확인 후 이동할 URL
     * @param icon SweetAlert2 아이콘 유형 (success, error, warning, info 등)
     * @param title 알림 제목
     * @param message 알림 본문 메시지
     */
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
