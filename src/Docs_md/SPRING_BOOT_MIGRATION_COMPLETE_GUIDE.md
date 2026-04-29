# Spring MVC → Spring Boot 마이그레이션 완전 가이드

## 📋 목차
1. [마이그레이션 개요](#마이그레이션-개요)
2. [변경 사항 상세](#변경-사항-상세)
3. [Ubuntu 24.04 배포 가이드](#ubuntu-2404-배포-가이드)
4. [트러블슈팅](#트러블슈팅)

---

## 마이그레이션 개요

### 마이그레이션 목적
- **WebSocket 404 오류 해결**
- Spring Boot의 자동 설정 활용
- 배포 및 관리 간소화

### 마이그레이션 범위
- ✅ WebSocket 설정
- ✅ 서블릿 초기화 방식
- ✅ 의존성 관리
- ❌ 비즈니스 로직 (변경 없음)
- ❌ 데이터베이스 설정 (기존 유지)

---

## 변경 사항 상세

### 1. pom.xml 변경

#### 1.1 WebSocket 의존성 변경

**변경 전:**
```xml
<!-- WebSocket -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
    <version>6.2.1</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
    <version>6.2.1</version>
</dependency>
```

**변경 후:**
```xml
<!-- WebSocket - Spring Boot Starter 사용 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

**이유:**
- Spring Boot Starter는 필요한 모든 의존성을 자동으로 포함
- 버전 관리 자동화 (Spring Boot Parent에서 관리)

#### 1.2 JSP 지원 추가

**추가:**
```xml
<!-- Tomcat Jasper for JSP support in Spring Boot -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>
```

**이유:**
- Spring Boot 내장 Tomcat은 기본적으로 JSP를 지원하지 않음
- Jasper 엔진 추가로 JSP 렌더링 가능

#### 1.3 SLF4J 의존성 제거

**제거:**
```xml
<!-- 제거됨 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.9</version>
</dependency>
```

**이유:**
- Spring Boot Starter에 이미 SLF4J 포함
- slf4j-simple과 Logback 충돌 방지

---

### 2. 새로운 파일 생성

#### 2.1 TestProjectApplication.java

**경로:** `src/main/java/com/jobmoa/app/TestProjectApplication.java`

```java
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
```

**주요 기능:**
- `@SpringBootApplication`: Spring Boot 자동 설정 활성화
- `SpringBootServletInitializer`: WAR 파일로 배포 가능
- `exclude`: DataSource 자동 설정 제외 (커스텀 설정 사용)
- `@Import`: 기존 설정 클래스 통합

---

### 3. 설정 파일 변경

#### 3.1 WebSocketConfig.java

**변경 전:**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);

        messageConverters.add(converter);
        return false;
    }

    // 많은 설정 코드...
}
```

**변경 후:**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

**변경 이유:**
- Spring Boot가 기본 MessageConverter 자동 설정
- 코드 간소화로 유지보수성 향상

#### 3.2 WebMvcConfig.java

**변경 사항:**

1. **@EnableWebMvc 주석 처리**
```java
@Configuration
// @EnableWebMvc  // Spring Boot 사용 시 주석 처리 (자동 설정 사용)
@ComponentScan(basePackages = {...})
public class WebMvcConfig implements WebMvcConfigurer {
```

**이유:** Spring Boot 자동 설정과 충돌 방지

2. **ViewResolver Bean 제거**
```java
// 제거됨 - application.properties로 대체
// @Bean
// public InternalResourceViewResolver viewResolver() {...}
```

**이유:** application.properties에서 관리

3. **RequestMappingHandlerAdapter 수정**
```java
// 변경 전
@Bean
public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
    RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
    // ...
    return adapter;
}

// 변경 후
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new MappingJackson2HttpMessageConverter());
}
```

**이유:** Spring Boot Bean 충돌 방지

4. **Interceptor에서 WebSocket 경로 제외**
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/ws/**", "/ws-notification/**");
}
```

**이유:** WebSocket 연결 시 인터셉터 우회

#### 3.3 WebAppInitializer.java

**변경:** `WebAppInitializer.java` → `WebAppInitializer.java.bak` (백업)

**이유:**
- Spring Boot는 자동으로 서블릿 컨테이너 설정
- WebApplicationInitializer 불필요
- 충돌 방지를 위해 비활성화

---

### 4. application.properties 추가 설정

**추가된 설정:**
```properties
# Server Configuration
server.port=8088

# JSP Configuration for Spring Boot
spring.mvc.view.prefix=/WEB-INF/
spring.mvc.view.suffix=.jsp
```

**기존 설정:** 그대로 유지
- 데이터베이스 설정
- Redis 설정
- Mail 설정
- Session 설정

---

## 아키텍처 비교

### Before: 전통적인 Spring MVC

```
[서블릿 컨테이너 (외부 Tomcat)]
    ↓
[WebAppInitializer]
    ↓
[Root Context] ← RootConfig.java
    ↓
[Servlet Context] ← WebMvcConfig.java, WebSocketConfig.java
    ↓
[DispatcherServlet]
    ↓
[Controllers]
```

**특징:**
- 수동 설정 필요
- web.xml 또는 WebApplicationInitializer 필수
- 외부 서블릿 컨테이너 의존
- 복잡한 설정

### After: Spring Boot

```
[TestProjectApplication]
    ↓
[Spring Boot Auto Configuration]
    ↓ (자동)
[Embedded Tomcat] + [Root Context] + [Servlet Context]
    ↓
[Controllers]
```

**특징:**
- 자동 설정
- 내장 서블릿 컨테이너
- 최소한의 설정
- 독립 실행 가능 (JAR/WAR)

---

## 설정 매핑 표

| 항목 | Spring MVC | Spring Boot |
|------|-----------|-------------|
| **서블릿 초기화** | WebAppInitializer | TestProjectApplication |
| **서버 포트** | 외부 Tomcat 설정 | server.port=8088 |
| **JSP 설정** | InternalResourceViewResolver Bean | application.properties |
| **WebSocket** | 수동 설정 + DispatcherServlet 매핑 | @EnableWebSocketMessageBroker |
| **의존성 관리** | 개별 버전 명시 | Spring Boot Parent 관리 |
| **로깅** | slf4j-simple | Logback (자동) |
| **메시지 컨버터** | 수동 Bean 등록 | 자동 설정 |
| **실행 방법** | WAR → 외부 Tomcat | JAR/WAR 독립 실행 |

---

## Ubuntu 24.04 배포 가이드

### 사전 준비

#### 1. 시스템 업데이트
```bash
sudo apt update
sudo apt upgrade -y
```

#### 2. Java 17 설치
```bash
# OpenJDK 17 설치
sudo apt install openjdk-17-jdk -y

# 설치 확인
java -version

# JAVA_HOME 환경 변수 설정
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' | sudo tee -a /etc/profile
echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/profile
source /etc/profile
```

#### 3. Maven 설치 (옵션 - 빌드 서버인 경우)
```bash
sudo apt install maven -y
mvn -version
```

---

### 배포 방법 1: JAR 파일 실행 (권장)

#### Step 1: 프로젝트 빌드 (Windows에서)
```bash
cd C:\JobmoaIntelliJFolder\JobmoaProject
mvn clean package -DskipTests
```

**생성 파일:** `target/TestProject-0.0.1-SNAPSHOT.jar`

#### Step 2: Ubuntu로 파일 전송
```bash
# Windows PowerShell에서
scp target/TestProject-0.0.1-SNAPSHOT.jar ubuntu@your-server-ip:/home/ubuntu/app/

# 또는 Ubuntu에서
scp user@windows-ip:/path/to/TestProject-0.0.1-SNAPSHOT.jar /home/ubuntu/app/
```

#### Step 3: Ubuntu에서 실행
```bash
cd /home/ubuntu/app

# 백그라운드 실행
nohup java -jar TestProject-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

# 또는 프로덕션 설정으로 실행
nohup java -jar TestProject-0.0.1-SNAPSHOT.jar \
  --server.port=8088 \
  --spring.profiles.active=production \
  > app.log 2>&1 &

# 프로세스 확인
ps aux | grep TestProject

# 로그 확인
tail -f app.log
```

#### Step 4: 서비스 등록 (systemd)
```bash
# 서비스 파일 생성
sudo nano /etc/systemd/system/jobmoa.service
```

**파일 내용:**
```ini
[Unit]
Description=Jobmoa Spring Boot Application
After=syslog.target network.target

[Service]
User=ubuntu
Group=ubuntu
Type=simple

# JAR 파일 경로
ExecStart=/usr/bin/java -jar /home/ubuntu/app/TestProject-0.0.1-SNAPSHOT.jar

# 작업 디렉토리
WorkingDirectory=/home/ubuntu/app

# 로그 설정
StandardOutput=journal
StandardError=journal
SyslogIdentifier=jobmoa

# 재시작 정책
Restart=always
RestartSec=10

# 환경 변수 (필요시)
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="SPRING_PROFILES_ACTIVE=production"

[Install]
WantedBy=multi-user.target
```

**서비스 관리:**
```bash
# 서비스 활성화
sudo systemctl daemon-reload
sudo systemctl enable jobmoa.service

# 서비스 시작
sudo systemctl start jobmoa

# 상태 확인
sudo systemctl status jobmoa

# 로그 확인
sudo journalctl -u jobmoa -f

# 서비스 중지
sudo systemctl stop jobmoa

# 서비스 재시작
sudo systemctl restart jobmoa
```

---

### 배포 방법 2: WAR 파일 + 외부 Tomcat

#### Step 1: pom.xml 확인
```xml
<packaging>war</packaging>
```

#### Step 2: 빌드
```bash
mvn clean package -DskipTests
```

**생성 파일:** `target/TestProject-0.0.1-SNAPSHOT.war`

#### Step 3: Tomcat 10 설치 (Ubuntu)
```bash
# Tomcat 10 다운로드
cd /tmp
wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.20/bin/apache-tomcat-10.1.20.tar.gz

# 압축 해제
sudo tar xzf apache-tomcat-10.1.20.tar.gz -C /opt/
sudo mv /opt/apache-tomcat-10.1.20 /opt/tomcat

# 권한 설정
sudo chown -R ubuntu:ubuntu /opt/tomcat
chmod +x /opt/tomcat/bin/*.sh
```

#### Step 4: WAR 파일 배포
```bash
# WAR 파일 복사
cp TestProject-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/ROOT.war

# 또는 컨텍스트 경로 지정
cp TestProject-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/jobmoa.war
```

#### Step 5: Tomcat 시작
```bash
# Tomcat 시작
/opt/tomcat/bin/startup.sh

# 로그 확인
tail -f /opt/tomcat/logs/catalina.out

# Tomcat 중지
/opt/tomcat/bin/shutdown.sh
```

#### Step 6: Tomcat 서비스 등록
```bash
sudo nano /etc/systemd/system/tomcat.service
```

**파일 내용:**
```ini
[Unit]
Description=Apache Tomcat 10
After=network.target

[Service]
Type=forking

User=ubuntu
Group=ubuntu

Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_BASE=/opt/tomcat"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**서비스 관리:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable tomcat
sudo systemctl start tomcat
sudo systemctl status tomcat
```

---

### 배포 방법 3: Docker 배포

#### Step 1: Dockerfile 생성
```dockerfile
# C:\JobmoaIntelliJFolder\JobmoaProject\Dockerfile
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY target/TestProject-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8088

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Step 2: 빌드 및 실행
```bash
# Docker 이미지 빌드
docker build -t jobmoa-app:1.0 .

# 컨테이너 실행
docker run -d \
  --name jobmoa \
  -p 8088:8088 \
  --restart unless-stopped \
  jobmoa-app:1.0

# 로그 확인
docker logs -f jobmoa

# 컨테이너 중지
docker stop jobmoa

# 컨테이너 재시작
docker restart jobmoa
```

#### Step 3: Docker Compose (옵션)

```yaml
# docker-compose.yml
version: '3.8'

services:
  jobmoa-app:
    build: ../..
    container_name: jobmoa
    ports:
      - "8088:8088"
    environment:
      - SPRING_PROFILES_ACTIVE=production
    restart: unless-stopped
    volumes:
      - ./logs:/app/logs
```

```bash
# 실행
docker-compose up -d

# 중지
docker-compose down
```

---

### Nginx 리버스 프록시 설정 (권장)

#### Step 1: Nginx 설치
```bash
sudo apt install nginx -y
```

#### Step 2: 설정 파일 생성
```bash
sudo nano /etc/nginx/sites-available/jobmoa
```

**파일 내용:**
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 또는 IP 주소

    # 최대 업로드 크기
    client_max_body_size 50M;

    # HTTP → HTTPS 리다이렉트 (SSL 설정 후)
    # return 301 https://$server_name$request_uri;

    location / {
        proxy_pass http://localhost:8088;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket 설정
    location /ws {
        proxy_pass http://localhost:8088/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 타임아웃
        proxy_read_timeout 86400;
    }
}
```

#### Step 3: 설정 활성화
```bash
# 심볼릭 링크 생성
sudo ln -s /etc/nginx/sites-available/jobmoa /etc/nginx/sites-enabled/

# 기본 사이트 비활성화 (옵션)
sudo rm /etc/nginx/sites-enabled/default

# 설정 테스트
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx

# Nginx 상태 확인
sudo systemctl status nginx
```

#### Step 4: SSL 설정 (Let's Encrypt)
```bash
# Certbot 설치
sudo apt install certbot python3-certbot-nginx -y

# SSL 인증서 발급
sudo certbot --nginx -d your-domain.com

# 자동 갱신 테스트
sudo certbot renew --dry-run
```

---

### 방화벽 설정

#### UFW (Ubuntu Firewall)
```bash
# UFW 활성화
sudo ufw enable

# SSH 허용 (중요!)
sudo ufw allow 22/tcp

# HTTP/HTTPS 허용
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Spring Boot 직접 접근 허용 (선택)
sudo ufw allow 8088/tcp

# 상태 확인
sudo ufw status verbose
```

---

### 모니터링 설정

#### 1. 로그 확인
```bash
# Spring Boot 로그 (systemd)
sudo journalctl -u jobmoa -f

# Nginx 로그
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# Tomcat 로그 (WAR 배포 시)
tail -f /opt/tomcat/logs/catalina.out
```

#### 2. 헬스 체크 설정

**application.properties에 추가:**
```properties
# Actuator 엔드포인트 활성화
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

**pom.xml에 추가:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**헬스 체크:**
```bash
curl http://localhost:8088/actuator/health
```

#### 3. 자동 재시작 스크립트
```bash
# /home/ubuntu/app/monitor.sh
#!/bin/bash

URL="http://localhost:8088/actuator/health"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $URL)

if [ $RESPONSE -ne 200 ]; then
    echo "Application is down. Restarting..."
    sudo systemctl restart jobmoa
    echo "Application restarted at $(date)" >> /home/ubuntu/app/restart.log
fi
```

**Cron 등록:**
```bash
crontab -e

# 5분마다 체크
*/5 * * * * /home/ubuntu/app/monitor.sh
```

---

### 성능 최적화

#### 1. JVM 메모리 설정
```bash
# systemd 서비스 파일 수정
sudo nano /etc/systemd/system/jobmoa.service
```

```ini
ExecStart=/usr/bin/java \
  -Xms512m \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar /home/ubuntu/app/TestProject-0.0.1-SNAPSHOT.jar
```

#### 2. Tomcat 튜닝 (WAR 배포 시)
```bash
# /opt/tomcat/bin/setenv.sh 생성
nano /opt/tomcat/bin/setenv.sh
```

```bash
#!/bin/bash
export CATALINA_OPTS="$CATALINA_OPTS -Xms512m"
export CATALINA_OPTS="$CATALINA_OPTS -Xmx2g"
export CATALINA_OPTS="$CATALINA_OPTS -XX:+UseG1GC"
export CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=UTF-8"
```

```bash
chmod +x /opt/tomcat/bin/setenv.sh
```

---

### 데이터베이스 연결 확인

#### application.properties (Ubuntu 환경)
```properties
# 환경별 설정 분리
spring.profiles.active=production

# Production 프로파일 (application-production.properties)
jdbc.url=jdbc:sqlserver://211.115.206.107:1433;DatabaseName=db122266;trustServerCertificate=true;
jdbc.username=u122266
jdbc.password=jobmoano1!

# Connection Pool 최적화
jdbc.maxTotal=50
jdbc.maxIdle=20
jdbc.minIdle=10
jdbc.initialSize=10
```

#### 연결 테스트
```bash
# 데이터베이스 연결 테스트
curl http://localhost:8088/actuator/health

# 로그에서 연결 확인
sudo journalctl -u jobmoa | grep -i "database\|sql"
```

---

## 배포 체크리스트

### 배포 전
- [ ] 코드 빌드 성공 확인
- [ ] 단위 테스트 통과 확인
- [ ] application.properties 환경별 설정 확인
- [ ] 데이터베이스 접속 정보 확인
- [ ] Redis 접속 정보 확인
- [ ] 메일 서버 설정 확인

### 배포 중
- [ ] Java 17 설치 확인
- [ ] 방화벽 포트 개방 확인
- [ ] 파일 권한 설정 확인
- [ ] 서비스 등록 및 자동 시작 설정

### 배포 후
- [ ] 애플리케이션 정상 시작 확인
- [ ] 헬스 체크 응답 확인
- [ ] WebSocket 연결 테스트
- [ ] 주요 기능 동작 확인
- [ ] 로그 정상 출력 확인
- [ ] 모니터링 설정 확인

---

## 트러블슈팅

### 1. 포트 이미 사용 중
```bash
# 포트 사용 프로세스 확인
sudo lsof -i :8088

# 프로세스 종료
sudo kill -9 <PID>
```

### 2. 메모리 부족
```bash
# 메모리 확인
free -h

# 스왑 메모리 추가
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 영구 적용
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 3. 데이터베이스 연결 실패
```bash
# 방화벽 확인
sudo ufw status

# 네트워크 연결 테스트
telnet 211.115.206.107 1433

# SQL Server 드라이버 확인
ls -la ~/.m2/repository/com/microsoft/sqlserver/
```

### 4. WebSocket 연결 실패
```bash
# Nginx 설정 확인
sudo nginx -t

# WebSocket 헤더 확인
curl -i -N -H "Connection: Upgrade" \
  -H "Upgrade: websocket" \
  http://localhost:8088/ws/info
```

### 5. JSP 404 에러
```bash
# JSP 파일 경로 확인
ls -la /home/ubuntu/app/WEB-INF/views/

# application.properties 확인
cat /home/ubuntu/app/application.properties | grep view
```

---

## 롤백 계획

### 긴급 롤백 (WebAppInitializer 복구)
```bash
# Windows 개발 환경
cd C:\JobmoaIntelliJFolder\JobmoaProject\src\main\java\com\jobmoa\app\config
move WebAppInitializer.java.bak WebAppInitializer.java

# pom.xml 변경 되돌리기
git checkout pom.xml

# 재빌드
mvn clean package -DskipTests
```

### 점진적 롤백
1. 새 버전과 구 버전 동시 실행 (다른 포트)
2. Nginx에서 트래픽 점진적 전환
3. 모니터링 후 구 버전 종료

---

## 부록

### A. 유용한 명령어 모음
```bash
# 애플리케이션 상태 확인
sudo systemctl status jobmoa

# 실시간 로그 모니터링
sudo journalctl -u jobmoa -f --lines=100

# 메모리 사용량 확인
ps aux | grep java | grep -v grep

# 디스크 사용량 확인
df -h

# 네트워크 연결 확인
netstat -tulpn | grep 8088

# 프로세스 트리
pstree -p $(pgrep -f TestProject)
```

### B. 환경 변수 파일
```bash
# /home/ubuntu/app/env.sh
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export SPRING_PROFILES_ACTIVE=production
export LOG_PATH=/home/ubuntu/app/logs
```

### C. 백업 스크립트
```bash
#!/bin/bash
# /home/ubuntu/app/backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/home/ubuntu/backups"
APP_DIR="/home/ubuntu/app"

mkdir -p $BACKUP_DIR

# JAR 파일 백업
cp $APP_DIR/*.jar $BACKUP_DIR/app_$DATE.jar

# 설정 파일 백업
cp $APP_DIR/application*.properties $BACKUP_DIR/

# 7일 이상 된 백업 삭제
find $BACKUP_DIR -name "app_*.jar" -mtime +7 -delete

echo "Backup completed at $DATE"
```

---

## 연락처 및 지원

- **문제 발생 시:** 로그 파일과 에러 메시지 수집
- **성능 이슈:** JVM 메모리 설정 검토
- **보안 문제:** 방화벽 및 SSL 설정 확인

---

**문서 버전:** 1.0
**작성일:** 2024-12-17
**최종 수정일:** 2024-12-17
