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

    public static void main(String[] args) {
        SpringApplication.run(TestProjectApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestProjectApplication.class);
    }
}
