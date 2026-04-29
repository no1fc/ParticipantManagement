package com.jobmoa.app.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RootConfig 통합 테스트
 * Context 로딩 및 Bean 생성 검증
 */
@SpringJUnitConfig(classes = RootConfig.class)
public class RootConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Test 1: Spring Context 로딩 테스트
     */
    @Test
    public void testContextLoads() {
        assertNotNull(context, "ApplicationContext should not be null");
        System.out.println("✅ Context loaded successfully");
    }

    /**
     * Test 2: 모든 주요 Bean이 생성되었는지 확인
     */
    @Test
    public void testAllBeansCreated() {
        assertNotNull(dataSource, "DataSource bean should not be null");
        assertNotNull(jdbcTemplate, "JdbcTemplate bean should not be null");
        assertNotNull(transactionManager, "TransactionManager bean should not be null");
        assertNotNull(sqlSessionFactory, "SqlSessionFactory bean should not be null");
        assertNotNull(redisTemplate, "RedisTemplate bean should not be null");
        assertNotNull(mailSender, "JavaMailSender bean should not be null");

        System.out.println("✅ All beans created successfully:");
        System.out.println("  - DataSource: " + dataSource.getClass().getName());
        System.out.println("  - JdbcTemplate: " + jdbcTemplate.getClass().getName());
        System.out.println("  - TransactionManager: " + transactionManager.getClass().getName());
        System.out.println("  - SqlSessionFactory: " + sqlSessionFactory.getClass().getName());
        System.out.println("  - RedisTemplate: " + redisTemplate.getClass().getName());
        System.out.println("  - JavaMailSender: " + mailSender.getClass().getName());
    }

    /**
     * Test 3: DataSource 연결 테스트
     */
    @Test
    public void testDataSourceConnection() {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(1), "Connection should be valid");

            System.out.println("✅ DataSource connection test passed");
            System.out.println("  - Database: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("  - Version: " + connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            fail("DataSource connection failed: " + e.getMessage());
        }
    }

    /**
     * Test 4: JdbcTemplate 동작 테스트
     */
    @Test
    public void testJdbcTemplate() {
        try {
            // 간단한 쿼리 실행
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            assertNotNull(result);
            assertEquals(1, result);

            System.out.println("✅ JdbcTemplate test passed");
        } catch (Exception e) {
            fail("JdbcTemplate test failed: " + e.getMessage());
        }
    }

    /**
     * Test 5: SqlSessionFactory 동작 테스트
     */
    @Test
    public void testSqlSessionFactory() {
        try {
            assertNotNull(sqlSessionFactory.openSession(), "SqlSession should not be null");
            System.out.println("✅ SqlSessionFactory test passed");
        } catch (Exception e) {
            fail("SqlSessionFactory test failed: " + e.getMessage());
        }
    }

    /**
     * Test 6: TransactionManager Bean 확인
     */
    @Test
    public void testTransactionManager() {
        assertNotNull(transactionManager);
        assertEquals("org.springframework.jdbc.datasource.DataSourceTransactionManager",
                transactionManager.getClass().getName());
        System.out.println("✅ TransactionManager test passed");
    }

    /**
     * Test 7: Component Scan 확인
     */
    @Test
    public void testComponentScan() {
        // Component Scan으로 등록된 Bean들이 있는지 확인
        String[] beanNames = context.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Should have beans from component scan");

        System.out.println("✅ Component scan test passed");
        System.out.println("  - Total beans: " + beanNames.length);

        // biz 패키지의 Bean들 확인 (샘플)
        long bizBeanCount = java.util.Arrays.stream(beanNames)
                .filter(name -> {
                    try {
                        return context.getBean(name).getClass().getPackage().getName().contains(".biz");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
        System.out.println("  - Biz layer beans: " + bizBeanCount);
    }

    /**
     * Test 8: Redis 연결 테스트 (선택적)
     * Redis 서버가 실행 중이지 않으면 실패할 수 있음
     */
    @Test
    public void testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("test-key", "test-value");
            Object value = redisTemplate.opsForValue().get("test-key");
            assertEquals("test-value", value);
            redisTemplate.delete("test-key");

            System.out.println("✅ Redis connection test passed");
        } catch (Exception e) {
            System.out.println("⚠️  Redis connection test skipped (Redis server not available): " + e.getMessage());
            // Redis 서버가 없을 수 있으므로 실패하지 않음
        }
    }

    /**
     * Test 9: Mail Sender Bean 확인
     */
    @Test
    public void testMailSender() {
        assertNotNull(mailSender);
        assertEquals("org.springframework.mail.javamail.JavaMailSenderImpl",
                mailSender.getClass().getName());
        System.out.println("✅ JavaMailSender test passed");
    }
}
