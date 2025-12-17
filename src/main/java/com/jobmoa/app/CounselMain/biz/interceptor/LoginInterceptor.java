package com.jobmoa.app.CounselMain.biz.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Slf4j // 로깅 기능을 제공하는 Lombok 애노테이션
public class LoginInterceptor implements HandlerInterceptor { // 스프링 MVC의 HandlerInterceptor 구현 클래스

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
            "/ws-notification/**"  // WebSocket 알림 엔드포인트 추가
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청이 컨트롤러에 도달하기 전에 실행되는 메서드

        log.info("-----------------------------------"); // 로그 구분 라인
        log.info("---Start LoginInterceptor---"); // 인터셉터 시작 로그 출력

        boolean flag = true;

        log.info("---Check Page---");
        String requestURI = request.getRequestURI();

        // 제외 패턴 확인
        for (String pattern : excludePatterns) {
            if (pathMatcher.match(pattern, requestURI)) {
                log.info("---Skip Login Check for [{}]---", requestURI);
                return true; // 인터셉터 처리 생략
            }
        }


        HttpSession session = request.getSession(false);
        // 현재 HTTP 요청에 연관된 세션을 가져옴.
        // `false` 옵션: 기존 세션이 없으면 새로운 세션을 생성하지 않고 null을 반환함.
        if (session == null || session.getAttribute("JOBMOA_LOGIN_DATA") == null) {
            // 세션이 없거나, 특정 로그인 데이터("JOBMOA_LOGIN_DATA") 속성이 없는 경우
            log.info("---Login Fail: Redirecting to login page---");
            // 로그인을 실패했다고 로그 출력

            response.sendRedirect("/login.do");
            // 로그인 페이지("/login.do")로 리다이렉트

            flag = false;
            // 컨트롤러 실행을 중단하고 요청 처리를 여기서 종료
        }
        else if (session.getAttribute("JOBMOA_LOGIN_DATA") != null) {
            session.setMaxInactiveInterval(60 * 60);
        }

        log.info("---Login Success---");
        // 세션 정보가 있고, 로그인 데이터가 포함되어 있으므로 로그인을 성공했다고 출력

        log.info("-----------------------------------"); // 로그 구분 라인

        return flag; // 정상적으로 요청을 처리하도록 컨트롤러 실행을 허용
    }

}
