package com.jobmoa.app.CounselMain.biz.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 로그인 인증 인터셉터.
 *
 * <p>모든 HTTP 요청에 대해 세션의 {@code JOBMOA_LOGIN_DATA} 속성을 확인하여
 * 인증되지 않은 요청을 로그인 페이지({@code /})로 리다이렉트한다.
 *
 * <p>인증이 성공하면 세션 만료 시간을 갱신한다.
 * 정적 리소스, WebSocket, API, 공개 페이지 등은 {@code excludePatterns}에 의해 인증 검사를 건너뛴다.
 *
 * @see com.jobmoa.app.config.WebMvcConfig#addInterceptors(InterceptorRegistry)
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 제외할 패턴 목록
    private final List<String> excludePatterns = Arrays.asList(
            "/api/**",           // 모든 API 경로
            "*.api",           // 모든 API 경로
            "/**/*.api",           // 모든 API 경로
            "/**/*.do",          // 모든 .do 파일
            "/login.do",         // 로그인 페이지 (명시적으로 포함)
            "/jobPlacement/**",  // 기업회원 페이지
//            "/mailSend/**"  // 메일전달용
            "/ws/**", // WebSocket 엔드포인트 추가
            "/ws-notification/**",  // WebSocket 알림 엔드포인트 추가
            "/error",            // Spring Boot 기본 에러 페이지
            "/error/**",         // 에러 하위 경로들
            "/css/**",           // CSS 리소스 제외
            "/js/**",            // JS 리소스 제외
            "/images/**",        // 이미지 리소스 제외
            "/img/**",           // img 리소스 제외
            "/assets/**",        // 기타 자산 제외
            "/resources/**",     // 정적 리소스 제외
            "/favicon.ico",      // 파비콘 제외
            "/actuator/**"       // Spring Boot Actuator 제외
    );

    /**
     * 컨트롤러 실행 전 로그인 여부를 확인한다.
     *
     * <p>요청 URI가 제외 패턴에 해당하면 인증 없이 통과시키고,
     * 세션에 로그인 정보가 없으면 루트({@code /})로 리다이렉트한다.
     * 인증된 요청은 세션 유효 시간을 갱신한 뒤 컨트롤러로 진행한다.
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler  실행 대상 핸들러
     * @return 인증 성공 시 {@code true}, 미인증 시 {@code false} (리다이렉트 처리)
     * @throws Exception 처리 중 발생한 예외
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청이 컨트롤러에 도달하기 전에 실행되는 메서드

        String requestURI = request.getRequestURI();
        log.debug("LoginInterceptor - Request URI: [{}]", requestURI);

        // 제외 패턴 확인
        for (String pattern : excludePatterns) {
            if (pathMatcher.match(pattern, requestURI)) {
                log.debug("Skip Login Check for [{}]", requestURI);
                return true; // 인터셉터 처리 생략
            }
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JOBMOA_LOGIN_DATA") == null) {
            log.debug("Login Fail: Redirecting to login page - URI: [{}]", requestURI);
            response.sendRedirect("/");
            return false;
        }
        else if (session.getAttribute("JOBMOA_LOGIN_DATA") != null) {
            session.setMaxInactiveInterval(60 * 60 * 60);
        }

        log.debug("Login Success - URI: [{}]", requestURI);

        return true; // 정상적으로 요청을 처리하도록 컨트롤러 실행을 허용
    }

}
