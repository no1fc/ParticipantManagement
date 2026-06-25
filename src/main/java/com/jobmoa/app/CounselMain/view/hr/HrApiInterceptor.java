package com.jobmoa.app.CounselMain.view.hr;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * HR REST API({@code /hr/api/**}) 공통 인증 인터셉터. 잡모아 메인 {@code AdminApiInterceptor}의 HR 버전이다.
 *
 * <p>미인증({@code HR_LOGIN_DATA} 없음) 시 401 JSON을 반환한다. 페이지 이동이 아닌 API 경로에만 등록한다.</p>
 *
 * @see HrPageInterceptor
 * @see com.jobmoa.app.config.WebMvcConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Slf4j
public class HrApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (!HrAccessSupport.isAuthed(session)) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
            return false;
        }
        return true;
    }
}
