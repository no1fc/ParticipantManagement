package com.jobmoa.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {
    "com.jobmoa.app.CounselMain.biz.common",
    "com.jobmoa.app.CounselMain.biz.interceptor",
    "com.jobmoa.app.CounselMain.biz.login",
    "com.jobmoa.app.CounselMain.biz.participant",
    "com.jobmoa.app.CounselMain.biz.particcertif",
    "com.jobmoa.app.CounselMain.biz.participantBasic",
    "com.jobmoa.app.CounselMain.biz.participantCounsel",
    "com.jobmoa.app.CounselMain.biz.participantEmployment",
    "com.jobmoa.app.CounselMain.biz.participantEducation",
    "com.jobmoa.app.CounselMain.biz.dashboard",
    "com.jobmoa.app.CounselMain.biz.report",
    "com.jobmoa.app.CounselMain.biz.participantRandomAssignment",
    "com.jobmoa.app.CounselMain.biz.mailSend",
    "com.jobmoa.app.CounselMain.biz.redis",
    "com.jobmoa.app.CounselMain.biz.adminpage",
    "com.jobmoa.app.CounselMain.biz.recommend",
    "com.jobmoa.app.jobPlacement.biz.jobPlacement",
    "com.jobmoa.app.recruitmentFormation.biz",
})
@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class RootConfig {

    // Database Configuration
    @Value("${jdbc.driver}")
    private String jdbcDriver;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.username}")
    private String jdbcUsername;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Value("${jdbc.maxTotal}")
    private int jdbcMaxTotal;

    @Value("${jdbc.maxIdle}")
    private int jdbcMaxIdle;

    @Value("${jdbc.minIdle}")
    private int jdbcMinIdle;

    @Value("${jdbc.initialSize}")
    private int jdbcInitialSize;

    // MyBatis Configuration
    @Value("${mybatis.config-location}")
    private String mybatisConfigLocation;

    // Redis Configuration
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${redis.timeout}")
    private int redisTimeout;

    @Value("${redis.pool.maxTotal}")
    private int redisMaxTotal;

    @Value("${redis.pool.maxIdle}")
    private int redisMaxIdle;

    @Value("${redis.pool.minIdle}")
    private int redisMinIdle;

    // Mail Configuration
    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private int mailPort;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.defaultEncoding}")
    private String mailDefaultEncoding;

    @Value("${mail.smtp.auth}")
    private boolean mailSmtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean mailSmtpStarttlsEnable;

    @Value("${mail.smtp.ssl.enable}")
    private boolean mailSmtpSslEnable;

    @Value("${mail.smtp.ssl.trust}")
    private String mailSmtpSslTrust;

    @Value("${mail.smtp.socketFactory.class}")
    private String mailSmtpSocketFactoryClass;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * DataSource Bean 설정
     * Apache Commons DBCP2를 사용한 데이터베이스 커넥션 풀
     */
    @Bean(name = "ds", destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        dataSource.setMaxTotal(jdbcMaxTotal);
        dataSource.setMaxIdle(jdbcMaxIdle);
        dataSource.setMinIdle(jdbcMinIdle);
        dataSource.setInitialSize(jdbcInitialSize);
        return dataSource;
    }

    /**
     * ObjectMapper Bean
     * JSON 직렬화/역직렬화에 사용
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * JdbcTemplate Bean
     * JDBC 작업을 위한 템플릿
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * TransactionManager Bean
     * 트랜잭션 관리를 위한 매니저
     */
    @Bean(name = "txManager")
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * SqlSessionFactory Bean
     * MyBatis 설정 및 매퍼 등록
     */
    @Bean(name = "sqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // MyBatis Config 파일 설정
        sessionFactory.setConfigLocation(
            new PathMatchingResourcePatternResolver().getResource(mybatisConfigLocation)
        );

        return sessionFactory.getObject();
    }

    /**
     * SqlSessionTemplate Bean
     * MyBatis 작업을 위한 템플릿
     */
    @Bean(name = "mybatis")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * JavaMailSender Bean
     * 이메일 발송을 위한 설정
     */
    @Bean(name = "mailSendBean")
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        mailSender.setDefaultEncoding(mailDefaultEncoding);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
        props.put("mail.smtps.ssl.checkserveridentity", "true");
        props.put("mail.smtps.ssl.trust", mailSmtpSslTrust);
        props.put("mail.smtp.socketFactory.class", mailSmtpSocketFactoryClass);

        return mailSender;
    }

    /**
     * JedisPoolConfig Bean
     * Redis 연결 풀 설정
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisMaxTotal);
        config.setMaxIdle(redisMaxIdle);
        config.setMinIdle(redisMinIdle);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        return config;
    }

    /**
     * JedisConnectionFactory Bean
     * Redis 연결 팩토리
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory factory = new JedisConnectionFactory(jedisPoolConfig);
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setPassword(redisPassword);
        factory.setTimeout(redisTimeout);
        factory.setUsePool(true);
        return factory;
    }

    /**
     * RedisTemplate Bean
     * Redis 작업을 위한 템플릿 (직렬화 설정 포함)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key Serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value Serializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    /**
     * Transaction Advice 설정
     * CounselMain 패키지용 트랜잭션 어드바이저
     */
    @Bean
    public Advisor txAdviceCounselMain(PlatformTransactionManager transactionManager) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.jobmoa.app.CounselMain.biz..*.*Impl.*(..))");

        Properties attributes = new Properties();
        attributes.setProperty("select*", "PROPAGATION_REQUIRED,readOnly");
        attributes.setProperty("*", "PROPAGATION_REQUIRED");

        TransactionInterceptor interceptor = new TransactionInterceptor(transactionManager, attributes);

        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    /**
     * Transaction Advice 설정
     * jobPlacement 패키지용 트랜잭션 어드바이저
     */
    @Bean
    public Advisor txAdviceJobPlacement(PlatformTransactionManager transactionManager) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.jobmoa.app.jobPlacement.biz.*.*Impl.*(..))");

        Properties attributes = new Properties();
        attributes.setProperty("select*", "PROPAGATION_REQUIRED,readOnly");
        attributes.setProperty("*", "PROPAGATION_REQUIRED");

        TransactionInterceptor interceptor = new TransactionInterceptor(transactionManager, attributes);

        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    /**
     * RestTemplate Bean 설정
     * REST API 호출을 위한 RestTemplate Bean을 생성합니다.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Google Gen AI Client Bean
     * Gemini API 호출을 위한 SDK 클라이언트
     * RestTemplate 방식이 아닌 SDK 방식 사용
     */
    @Bean
    public Client geminiClient() {
        return new Client.Builder().apiKey(geminiApiKey).build();
    }
}
