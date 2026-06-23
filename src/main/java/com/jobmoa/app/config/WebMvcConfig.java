package com.jobmoa.app.config;

import com.jobmoa.app.CounselMain.biz.interceptor.LoginInterceptor;
import com.jobmoa.app.CounselMain.view.adminpage.AdminApiInterceptor;
import com.jobmoa.app.CounselMain.view.adminpage.AdminPageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Spring MVC 웹 계층 설정.
 *
 * <p>컨트롤러(view) 패키지의 컴포넌트 스캔, 인터셉터, 메시지 컨버터,
 * 정적 리소스 핸들러를 등록한다.
 * <ul>
 *   <li>{@link com.jobmoa.app.CounselMain.biz.interceptor.LoginInterceptor} — 로그인 인증 인터셉터</li>
 *   <li>Jackson {@code MappingJackson2HttpMessageConverter} — JSON 직렬화</li>
 *   <li>CSS/JS/이미지 정적 리소스 캐시 (1일 + must-revalidate)</li>
 * </ul>
 *
 * @see RootConfig 서비스(biz) 계층 및 인프라 빈 설정
 */
@Configuration
// @EnableWebMvc  // Spring Boot 사용 시 주석 처리 (자동 설정 사용)
@ComponentScan(basePackages = {
    "com.jobmoa.app.CounselMain.view.login",
    "com.jobmoa.app.CounselMain.view.participant",
    "com.jobmoa.app.CounselMain.view.dashboard",
    "com.jobmoa.app.CounselMain.view.ajaxPackage",
    "com.jobmoa.app.CounselMain.view.management",
    "com.jobmoa.app.CounselMain.view.function",
    "com.jobmoa.app.CounselMain.view.report",
    "com.jobmoa.app.CounselMain.view.branchManagement",
    "com.jobmoa.app.CounselMain.view.participantRandomAssignment",
    "com.jobmoa.app.CounselMain.view.mailSend",
    "com.jobmoa.app.CounselMain.view.mypage",
    "com.jobmoa.app.CounselMain.view.adminpage",
    "com.jobmoa.app.CounselMain.view.linkagePopup",
    "com.jobmoa.app.CounselMain.view.schedule",
    "com.jobmoa.app.CounselMain.view.schedulePublic",
    "com.jobmoa.app.jobPlacement.view.jobPlacement",
    "com.jobmoa.app.jobPlacement.view.async",
    "com.jobmoa.app.jobPlacement.view.webSocket",
    "com.jobmoa.app.recruitmentFormation.view.jobinfo"
})
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * ViewResolver 설정
     * Spring Boot에서는 application.properties로 설정하므로 주석 처리
     * (spring.mvc.view.prefix, spring.mvc.view.suffix 사용)
     */
    // @Bean
    // public InternalResourceViewResolver viewResolver() {
    //     InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    //     viewResolver.setPrefix("/WEB-INF/");
    //     viewResolver.setSuffix(".jsp");
    //     viewResolver.setOrder(1);
    //     return viewResolver;
    // }

    /**
     * Interceptor 설정
     * 모든 요청에 대해 로그인 체크 인터셉터 적용
     * WebSocket, 에러 페이지, 정적 리소스 경로는 제외
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 관리자 REST API 공통 권한 인터셉터 (로그인 인터셉터보다 먼저 실행되어 미인증 시 401 JSON 응답)
        registry.addInterceptor(new AdminApiInterceptor())
                .addPathPatterns("/admin/api/**");

        // 관리자 페이지 공통 권한 인터셉터 (/admin/** 페이지, API 경로는 위 인터셉터가 담당하므로 제외)
        // 컨트롤러별 hasAdminAccess 가드 누락 시에도 URL 직접입력 우회를 구조적으로 차단
        registry.addInterceptor(new AdminPageInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/api/**");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/ws/**",              // WebSocket
                        "/ws-notification/**", // WebSocket 알림
                        "/error",              // Spring Boot 기본 에러 페이지
                        "/error/**",           // 에러 하위 경로
                        "/css/**",             // CSS
                        "/js/**",              // JavaScript
                        "/images/**",          // 이미지
                        "/img/**",             // img
                        "/assets/**",          // 기타 자산
                        "/resources/**",       // 정적 리소스
                        "/favicon.ico",        // 파비콘
                        "/actuator/**",        // Actuator
                        "/jobinfo/**",         // 채용공고 확인용(고용24)
                        "/recruitmentInformation/**", // 채용공고 검색 API (공개)
                        "/jobPlacement/**", // 참여자 정보 페이지
                        "/schedulePublic/**",  // 공개 일정 조회 (독립 인증)
                        "/register.do",        // 셀프 회원가입
                        "/register.api"        // 회원가입 API
                );
    }

    /**
     * HTTP Message Converter 설정
     * Spring Boot가 자동으로 RequestMappingHandlerAdapter를 생성하므로
     * MessageConverter만 등록
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    /**
     * 정적 리소스 핸들러 설정
     * WebSocket 등 특수 경로 제외
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        // 정적 리소스 캐시: 1일 + must-revalidate (ETag/Last-Modified로 재검증)
        // 버전 없는 파일(adminlte.min.css, adminlte.js 등)도 안전하게 캐시됨
        CacheControl resourceCache = CacheControl.maxAge(1, TimeUnit.DAYS)
                .mustRevalidate();

        registry.addResourceHandler("/css/**")
                .addResourceLocations("/css/")
                .setCacheControl(resourceCache);
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/js/")
                .setCacheControl(resourceCache);
        registry.addResourceHandler("/img/**")
                .addResourceLocations("/img/")
                .setCacheControl(resourceCache);
    }
}
