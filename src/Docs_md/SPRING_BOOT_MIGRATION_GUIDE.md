# Spring Boot WebSocket 마이그레이션 완료

## 변경 사항 요약

### 1. pom.xml 수정
- ✅ `spring-websocket`, `spring-messaging` → `spring-boot-starter-websocket`으로 교체
- ✅ `tomcat-embed-jasper` 추가 (JSP 지원)

### 2. 새 파일 생성
- ✅ `TestProjectApplication.java` - Spring Boot 메인 클래스 생성
- ✅ DataSource 자동 설정 제외 (커스텀 DataSource 사용)

### 3. 설정 파일 수정
- ✅ `WebSocketConfig.java` 간소화 (Spring Boot 자동 설정 사용)
- ✅ `WebMvcConfig.java` - `@EnableWebMvc` 주석 처리
- ✅ `WebAppInitializer.java` → `WebAppInitializer.java.bak`으로 백업

### 4. application.properties 추가
```properties
server.port=8088
spring.mvc.view.prefix=/WEB-INF/
spring.mvc.view.suffix=.jsp
```

## 다음 단계

### 1. Maven 의존성 다시 로드
IntelliJ IDEA에서:
- 우클릭 pom.xml → Maven → Reload Project
- 또는 Maven 탭에서 새로고침 아이콘 클릭

### 2. 서버 실행 방법

#### 방법 A: IntelliJ에서 실행
1. `TestProjectApplication.java` 열기
2. `main` 메서드 옆 실행 버튼 클릭
3. 또는 우클릭 → Run 'TestProjectApplication'

#### 방법 B: Maven으로 실행
```bash
cd C:\JobmoaIntelliJFolder\JobmoaProject
mvn spring-boot:run
```

### 3. WebSocket 테스트
1. 브라우저에서 접속:
   ```
   http://localhost:8088/webSocketTestPage.jsp
   ```

2. 개발자 도구 콘솔 확인:
   - ✅ "Connected:" 메시지 확인
   - ✅ 404 에러 없음

3. 메시지 전송 테스트:
   - 텍스트 입력
   - "메세지 전송" 버튼 클릭
   - 응답 확인

### 4. WebSocket Info 엔드포인트 테스트
```
http://localhost:8088/ws/info
```
- 기대 결과: JSON 응답 (404 아님!)

## 예상 서버 로그

정상 시작 시 다음과 같은 로그 확인:
```
INFO: Starting TestProjectApplication
INFO: Mapped "{[/ws]}" onto
INFO: Mapped URL path [/topic/**]
INFO: WebSocketHandlerMapping
INFO: SockJsService
INFO: Started TestProjectApplication in X seconds
```

## 주요 변경점

### Before (일반 Spring)
- WebAppInitializer로 수동 설정
- DispatcherServlet 수동 매핑
- WebSocket 설정 복잡

### After (Spring Boot)
- TestProjectApplication - 자동 설정
- Spring Boot 자동 구성 활용
- WebSocket 간단한 설정만으로 작동

## 문제 해결

### 문제 1: 서버 시작 안 됨
- Maven 의존성 재로드 확인
- JDK 17 사용 확인

### 문제 2: JSP 404 에러
- `tomcat-embed-jasper` 의존성 확인
- `application.properties`의 JSP 설정 확인

### 문제 3: WebSocket 여전히 404
- 서버 로그에서 "Mapped /ws" 확인
- LoginInterceptor에서 `/ws/**` 제외 확인

## 롤백 방법

만약 문제가 발생하면:
```bash
cd C:\JobmoaIntelliJFolder\JobmoaProject\src\main\java\com\jobmoa\app\config
mv WebAppInitializer.java.bak WebAppInitializer.java
```

그리고 pom.xml과 WebMvcConfig를 git에서 되돌리기

## 성공 확인

✅ 서버가 8088 포트에서 시작됨
✅ WebSocket 연결 성공 (콘솔에 "Connected:" 표시)
✅ 메시지 전송/수신 정상 작동
✅ 기존 페이지들 정상 작동 (*.do, *.login, *.api)
