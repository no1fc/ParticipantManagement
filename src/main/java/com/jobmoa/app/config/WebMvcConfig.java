package com.jobmoa.app.config;

import com.jobmoa.app.CounselMain.biz.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

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
    "com.jobmoa.app.CounselMain.view.chatBot",
    "com.jobmoa.app.CounselMain.view.branchManagement",
    "com.jobmoa.app.CounselMain.view.participantRandomAssignment",
    "com.jobmoa.app.CounselMain.view.mailSend",
    "com.jobmoa.app.CounselMain.view.mypage",
    "com.jobmoa.app.jobPlacement.view.jobPlacement",
    "com.jobmoa.app.jobPlacement.view.async",
    "com.jobmoa.app.jobPlacement.view.webSocket"
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
     * WebSocket 경로는 제외
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/ws/**", "/ws-notification/**");  // WebSocket 경로 제외
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
        // 필요시 정적 리소스 추가
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
}
