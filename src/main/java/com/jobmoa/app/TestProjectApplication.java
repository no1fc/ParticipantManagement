package com.jobmoa.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import com.jobmoa.app.config.RootConfig;
import com.jobmoa.app.config.WebSocketConfig;

/**
 * Spring Boot Application 메인 클래스
 * WAR 배포를 위한 SpringBootServletInitializer 확장
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.jobmoa.app.CounselMain",
        "com.jobmoa.app.jobPlacement",
        "com.jobmoa.app.config"
    },
    exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
    }
)
@Import({RootConfig.class, WebSocketConfig.class})
public class TestProjectApplication extends SpringBootServletInitializer {

    /**
     * 내장 Tomcat으로 애플리케이션을 실행하는 메인 메서드.
     *
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(TestProjectApplication.class, args);
    }

    /**
     * 외부 WAS(Tomcat 등)에 WAR로 배포할 때 사용되는 설정 메서드.
     *
     * @param application SpringApplicationBuilder 인스턴스
     * @return 소스가 등록된 SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestProjectApplication.class);
    }
}
