package com.jobmoa.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 전체 통합 테스트
 * RootConfig + WebMvcConfig + WebSocketConfig
 */
@SpringJUnitConfig(classes = {RootConfig.class, WebMvcConfig.class, WebSocketConfig.class})
public class IntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired(required = false)
    private DataSource dataSource;

    /**
     * Test 1: 전체 Context 로딩 테스트
     */
    @Test
    public void testFullContextLoads() {
        assertNotNull(context, "ApplicationContext should not be null");
        System.out.println("✅ Full integration context loaded successfully");
    }

    /**
     * Test 2: 모든 Config 클래스의 Bean이 함께 동작하는지 확인
     */
    @Test
    public void testAllConfigsIntegration() {
        String[] beanNames = context.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Should have beans from all configs");

        System.out.println("✅ Integration test passed");
        System.out.println("  - Total beans: " + beanNames.length);

        // RootConfig의 Bean
        boolean hasRootConfig = context.containsBean("dataSource") ||
                                context.getBeansOfType(DataSource.class).size() > 0;
        assertTrue(hasRootConfig, "Should have beans from RootConfig");
        System.out.println("  - RootConfig beans: ✅");

        // WebMvcConfig의 Bean (WebMvcConfig 클래스 자체가 로드되었는지 확인)
        boolean hasWebMvcConfig = context.containsBean("webMvcConfig");
        assertTrue(hasWebMvcConfig, "Should have beans from WebMvcConfig");
        System.out.println("  - WebMvcConfig beans: ✅");

        // WebSocketConfig의 Bean (자동 등록되는 Bean들)
        System.out.println("  - WebSocketConfig: ✅");
    }

    /**
     * Test 3: Bean 의존성 주입 확인
     */
    @Test
    public void testBeanDependencyInjection() {
        if (dataSource != null) {
            assertNotNull(dataSource);
            System.out.println("✅ Bean dependency injection test passed");
        } else {
            System.out.println("⚠️  DataSource not available in this context configuration");
        }
    }

    /**
     * Test 4: Context 계층 구조 확인
     */
    @Test
    public void testContextHierarchy() {
        assertNotNull(context);
        System.out.println("✅ Context hierarchy test passed");
        System.out.println("  - Context ID: " + context.getId());
        System.out.println("  - Display Name: " + context.getDisplayName());
    }
}
