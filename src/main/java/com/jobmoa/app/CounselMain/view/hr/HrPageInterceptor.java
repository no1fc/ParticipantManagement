package com.jobmoa.app.CounselMain.view.hr;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HR 페이지({@code /hr/**}, 로그인·API 제외) 공통 인증 인터셉터. 잡모아 메인 {@code AdminPageInterceptor}의 HR 버전이다.
 *
 * <p>미인증({@code HR_LOGIN_DATA} 없음) 시 HR 로그인 페이지로 리다이렉트한다. 컨트롤러별 가드가 누락돼도
 * URL 직접입력 우회를 구조적으로 차단한다. JSON 응답이 필요한 {@code /hr/api/**}는 {@link HrApiInterceptor}가 담당한다.</p>
 *
 * @see HrApiInterceptor
 * @see com.jobmoa.app.config.WebMvcConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Slf4j
public class HrPageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (!HrAccessSupport.isAuthed(session)) {
            log.debug("HrPageInterceptor - 미인증 접근 차단: [{}]", request.getRequestURI());
            response.sendRedirect("/hr/login");
            return false;
        }
        return true;
    }
}
