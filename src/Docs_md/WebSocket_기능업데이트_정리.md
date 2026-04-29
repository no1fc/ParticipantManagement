# 실시간 알림 기능 구현 작업 계획서

## 📋 프로젝트 개요

**프로젝트명**: 실시간 메일 요청 알림 시스템  
**기술 스택**: Spring MVC, WebSocket, STOMP, SockJS  
**목표**: 사용자에게 메일 요청이 올 때 페이지에서 실시간 알림 제공  
**예상 기간**: 2-3주

---

## 📅 작업 일정 및 단계별 Task

### Phase 1: 환경 설정 및 기본 구조 (3-4일)

#### Day 1: 프로젝트 설정
- [ ] **Task 1.1**: pom.xml 의존성 추가
```
- spring-websocket (5.3.30)
  - spring-messaging (5.3.30)
  - jackson-databind (2.15.2)
  - redis 관련 의존성 (선택사항)
```

- 예상 시간: 1시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 1.2**: WebSocket 설정 클래스 생성
```
경로: src/main/java/com/jobmoa/app/config/WebSocketConfig.java
  - MessageBroker 설정
  - STOMP Endpoint 등록
  - CORS 설정
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 높음

#### Day 2: Spring 설정 파일 업데이트
- [ ] **Task 1.3**: applicationContext.xml 수정
```
- WebSocket 관련 Bean 등록
  - Component Scan 경로 추가 (com.jobmoa.app.notification)
  - MessageBroker 설정 통합
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 1.4**: ds-servlet.xml 수정 (필요시)
```
- WebSocket HandlerMapping 추가
  - Interceptor 설정
```

- 예상 시간: 1시간
- 담당자: Backend 개발자
- 우선순위: 중간

#### Day 3-4: 데이터베이스 설계 및 구축
- [ ] **Task 1.5**: 알림 테이블 설계 (영구 저장)
```sql
-- notifications 테이블
  CREATE TABLE notifications (
      notification_id VARCHAR(50) PRIMARY KEY,
      user_id VARCHAR(50) NOT NULL,
      type VARCHAR(30) NOT NULL,
      title VARCHAR(200) NOT NULL,
      message TEXT,
      url VARCHAR(500),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      read_at TIMESTAMP NULL,
      is_read CHAR(1) DEFAULT 'N',
      deleted_at TIMESTAMP NULL,
      FOREIGN KEY (user_id) REFERENCES members(user_id)
  );
  
  CREATE INDEX idx_notifications_user_id ON notifications(user_id);
  CREATE INDEX idx_notifications_created_at ON notifications(created_at);
```

- 예상 시간: 3시간
- 담당자: Backend 개발자 + DBA
- 우선순위: 높음

- [ ] **Task 1.6**: 알림 설정 테이블 설계
```sql
-- notification_settings 테이블
  CREATE TABLE notification_settings (
      setting_id INT AUTO_INCREMENT PRIMARY KEY,
      user_id VARCHAR(50) NOT NULL,
      notification_type VARCHAR(30) NOT NULL,
      is_enabled CHAR(1) DEFAULT 'Y',
      push_enabled CHAR(1) DEFAULT 'Y',
      email_enabled CHAR(1) DEFAULT 'N',
      sound_enabled CHAR(1) DEFAULT 'Y',
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      UNIQUE KEY unique_user_type (user_id, notification_type),
      FOREIGN KEY (user_id) REFERENCES members(user_id)
  );
```

- 예상 시간: 2시간
- 담당자: Backend 개발자 + DBA
- 우선순위: 중간

---

### Phase 2: Backend 핵심 기능 개발 (5-6일)

#### Day 5-6: DTO 및 DAO 레이어 구현
- [ ] **Task 2.1**: NotificationDTO 클래스 생성
```
경로: src/main/java/com/jobmoa/app/notification/dto/NotificationDTO.java
  - 모든 필드 정의 (Lombok 활용)
  - Validation 어노테이션 추가
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 2.2**: NotificationSettingDTO 클래스 생성
```
경로: src/main/java/com/jobmoa/app/notification/dto/NotificationSettingDTO.java
```

- 예상 시간: 1시간
- 담당자: Backend 개발자
- 우선순위: 중간

- [ ] **Task 2.3**: NotificationDAO 인터페이스 및 구현체 생성
```
경로: 
  - src/main/java/com/jobmoa/app/notification/dao/NotificationDAO.java
  - src/main/java/com/jobmoa/app/notification/dao/NotificationDAOImpl.java
  
  주요 메서드:
  - insertNotification()
  - selectNotificationsByUserId()
  - updateNotificationAsRead()
  - deleteNotification()
  - selectUnreadCount()
```

- 예상 시간: 4시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 2.4**: MyBatis Mapper XML 생성
```
경로: src/main/resources/mappings/Notification-mapping.xml
  - SQL 쿼리 작성
  - ResultMap 정의
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 높음

#### Day 7-8: Service 레이어 구현
- [ ] **Task 2.5**: NotificationService 인터페이스 생성
```
경로: src/main/java/com/jobmoa/app/notification/service/NotificationService.java
```

- 예상 시간: 1시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 2.6**: NotificationServiceImpl 구현
```
경로: src/main/java/com/jobmoa/app/notification/service/NotificationServiceImpl.java
  
  주요 기능:
  - sendNotificationToUser() - 특정 사용자에게 알림
  - sendNotificationToAll() - 전체 브로드캐스트
  - saveNotification() - DB 저장
  - getNotifications() - 알림 목록 조회
  - markAsRead() - 읽음 처리
  - getUnreadCount() - 안읽은 알림 개수
```

- 예상 시간: 6시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 2.7**: NotificationSettingService 구현
```
경로: 
  - src/main/java/com/jobmoa/app/notification/service/NotificationSettingService.java
  - src/main/java/com/jobmoa/app/notification/service/NotificationSettingServiceImpl.java
  
  주요 기능:
  - getUserSettings()
  - updateSettings()
  - isNotificationEnabled()
```

- 예상 시간: 4시간
- 담당자: Backend 개발자
- 우선순위: 중간

#### Day 9-10: Controller 및 API 구현
- [ ] **Task 2.8**: NotificationController 생성
```
경로: src/main/java/com/jobmoa/app/notification/controller/NotificationController.java
  
  REST API 엔드포인트:
  - POST /api/notification/send - 알림 전송 (테스트용)
  - GET /api/notification/list - 알림 목록 조회
  - PUT /api/notification/{id}/read - 읽음 처리
  - DELETE /api/notification/{id} - 알림 삭제
  - GET /api/notification/unread-count - 안읽은 알림 개수
```

- 예상 시간: 5시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 2.9**: NotificationSettingController 생성
```
경로: src/main/java/com/jobmoa/app/notification/controller/NotificationSettingController.java
  
  REST API 엔드포인트:
  - GET /api/notification/settings - 설정 조회
  - PUT /api/notification/settings - 설정 변경
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 중간

---

### Phase 3: 보안 및 인증 처리 (2-3일)

#### Day 11-12: WebSocket 보안 구현
- [ ] **Task 3.1**: WebSocket Interceptor 구현
```
경로: src/main/java/com/jobmoa/app/config/WebSocketAuthInterceptor.java
  
  기능:
  - 세션 검증
  - 사용자 인증 확인
  - CSRF 토큰 검증
```

- 예상 시간: 4시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 3.2**: Spring Security 통합 (사용 중인 경우)
```
- WebSocket Security Config 설정
  - 인증/인가 규칙 정의
  - CORS 정책 설정
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 3.3**: 사용자별 WebSocket 세션 관리
```
경로: src/main/java/com/jobmoa/app/notification/websocket/WebSocketSessionManager.java
  
  기능:
  - 사용자 연결/해제 추적
  - 활성 세션 관리
  - 재연결 처리
```

- 예상 시간: 4시간
- 담당자: Backend 개발자
- 우선순위: 중간

#### Day 13: CSRF 및 XSS 방어
- [ ] **Task 3.4**: CSRF 토큰 처리
```
- WebSocket HandShake에서 CSRF 토큰 검증
  - JSP에서 CSRF 토큰 포함
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 높음

- [ ] **Task 3.5**: XSS 방어 처리
```
- 알림 메시지 입력 검증
  - HTML 이스케이프 처리
  - CSP(Content Security Policy) 헤더 설정
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 높음

---

### Phase 4: Frontend 개발 (4-5일)

#### Day 14-15: JavaScript 핵심 로직 구현
- [ ] **Task 4.1**: notificationWebSocket.js 생성
```
경로: src/main/webapp/js/notificationWebSocket.js
  
  주요 함수:
  - connectWebSocket()
  - disconnectWebSocket()
  - showNotification()
  - displayInPageNotification()
  - updateNotificationBadge()
  - playNotificationSound()
  - requestNotificationPermission()
```

- 예상 시간: 6시간
- 담당자: Frontend 개발자
- 우선순위: 높음

- [ ] **Task 4.2**: notificationUI.js 생성
```
경로: src/main/webapp/js/notificationUI.js
  
  주요 기능:
  - 알림 목록 렌더링
  - 알림 클릭 이벤트 처리
  - 읽음 처리 UI 업데이트
  - 알림 삭제 처리
  - 무한 스크롤 또는 페이지네이션
```

- 예상 시간: 5시간
- 담당자: Frontend 개발자
- 우선순위: 높음

#### Day 16-17: CSS 스타일링
- [ ] **Task 4.3**: notification.css 생성
```
경로: src/main/webapp/css/notification.css
  
  스타일 요소:
  - 알림 벨 아이콘 디자인
  - 알림 배지 스타일
  - 알림 드롭다운 메뉴
  - 알림 아이템 카드 디자인
  - 읽음/안읽음 상태 표시
  - 애니메이션 효과 (fade-in, slide-in)
  - 반응형 디자인
```

- 예상 시간: 6시간
- 담당자: Frontend 개발자
- 우선순위: 중간

- [ ] **Task 4.4**: 알림 사운드 파일 추가
```
경로: src/main/webapp/sounds/notification.mp3
  - 알림 사운드 파일 준비
  - 적절한 볼륨 및 길이 조정
```

- 예상 시간: 1시간
- 담당자: Frontend 개발자
- 우선순위: 낮음

#### Day 18: JSP 페이지 통합
- [ ] **Task 4.5**: gnb.tag 또는 공통 헤더 수정
```
경로: src/main/webapp/WEB-INF/tags/gnb.tag
  
  추가 요소:
  - SockJS/STOMP 라이브러리 CDN
  - 알림 벨 아이콘
  - 알림 드롭다운 메뉴
  - 알림 컨테이너
  - currentUserId hidden input
```

- 예상 시간: 3시간
- 담당자: Frontend 개발자
- 우선순위: 높음

- [ ] **Task 4.6**: 알림 설정 페이지 생성
```
경로: src/main/webapp/WEB-INF/views/notificationSettings.jsp
  
  기능:
  - 알림 타입별 ON/OFF 토글
  - 사운드 설정
  - 이메일 알림 설정 (선택)
  - 설정 저장 버튼
```

- 예상 시간: 4시간
- 담당자: Frontend 개발자
- 우선순위: 중간

---

### Phase 5: 브라우저 호환성 및 테스트 (3-4일)

#### Day 19-20: 브라우저 호환성 테스트
- [ ] **Task 5.1**: Chrome 테스트
```
테스트 항목:
  - WebSocket 연결
  - 알림 수신
  - 브라우저 알림 권한
  - UI 렌더링
  - 성능 모니터링
```

- 예상 시간: 2시간
- 담당자: QA 팀
- 우선순위: 높음

- [ ] **Task 5.2**: Firefox 테스트
    - 예상 시간: 2시간
    - 담당자: QA 팀
    - 우선순위: 높음

- [ ] **Task 5.3**: Safari 테스트
    - 예상 시간: 2시간
    - 담당자: QA 팀
    - 우선순위: 중간

- [ ] **Task 5.4**: Edge 테스트
    - 예상 시간: 1시간
    - 담당자: QA 팀
    - 우선순위: 중간

- [ ] **Task 5.5**: 모바일 브라우저 테스트
```
- iOS Safari
  - Android Chrome
  - 반응형 디자인 확인
```

- 예상 시간: 3시간
- 담당자: QA 팀
- 우선순위: 중간

#### Day 21: 폴백 메커니즘 구현
- [ ] **Task 5.6**: SockJS 폴백 테스트
```
- WebSocket 미지원 환경에서 테스트
  - Long Polling 동작 확인
  - 성능 측정
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 중간

- [ ] **Task 5.7**: 오류 처리 및 재연결 로직
```
- 네트워크 끊김 시나리오
  - 서버 재시작 시나리오
  - 자동 재연결 테스트
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 높음

#### Day 22: 통합 테스트
- [ ] **Task 5.8**: 기능 통합 테스트
```
테스트 시나리오:
  1. 메일 요청 발생 → 알림 수신
  2. 알림 클릭 → 페이지 이동
  3. 알림 읽음 처리
  4. 알림 설정 변경
  5. 다중 사용자 동시 접속
```

- 예상 시간: 4시간
- 담당자: QA 팀
- 우선순위: 높음

- [ ] **Task 5.9**: 부하 테스트
```
- 동시 접속자 100명 시뮬레이션
  - 초당 알림 전송 횟수 측정
  - 메모리 사용량 모니터링
```

- 예상 시간: 3시간
- 담당자: Backend 개발자 + QA 팀
- 우선순위: 중간

---

### Phase 6: 성능 최적화 (2-3일)

#### Day 23: Redis 통합 (선택사항)
- [ ] **Task 6.1**: Redis 의존성 추가 및 설정
```
pom.xml:
  - spring-data-redis
  - jedis or lettuce
  
  application.properties:
  - Redis 연결 정보
  - 세션 저장소 설정
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 중간

- [ ] **Task 6.2**: Redis Session 관리
```
- WebSocket 세션을 Redis에 저장
  - 다중 서버 환경 대응
  - 세션 공유 로직 구현
```

- 예상 시간: 4시간
- 담당자: Backend 개발자
- 우선순위: 중간

- [ ] **Task 6.3**: Redis Pub/Sub 활용
```
- 서버 간 알림 전파
  - 분산 환경에서의 브로드캐스트
```

- 예상 시간: 4시간
- 담당자: Backend 개발자
- 우선순위: 낮음

#### Day 24-25: 성능 튜닝
- [ ] **Task 6.4**: 데이터베이스 쿼리 최적화
```
- 인덱스 최적화
  - N+1 문제 해결
  - 페이징 성능 개선
  - 캐싱 전략 수립
```

- 예상 시간: 4시간
- 담당자: Backend 개발자 + DBA
- 우선순위: 중간

- [ ] **Task 6.5**: JavaScript 번들링 및 압축
```
- Minification 적용
  - Gzip 압축
  - CDN 캐싱 전략
```

- 예상 시간: 2시간
- 담당자: Frontend 개발자
- 우선순위: 낮음

- [ ] **Task 6.6**: 메시지 크기 최적화
```
- JSON 페이로드 최소화
  - 불필요한 데이터 제거
  - 압축 프로토콜 적용
```

- 예상 시간: 2시간
- 담당자: Backend 개발자
- 우선순위: 중간

---

### Phase 7: 문서화 및 배포 준비 (2일)

#### Day 26: 문서 작성
- [ ] **Task 7.1**: API 문서 작성
```
- REST API 엔드포인트 명세
  - WebSocket 이벤트 명세
  - 요청/응답 예시
  - 에러 코드 정의
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 중간

- [ ] **Task 7.2**: 사용자 가이드 작성
```
- 알림 기능 사용법
  - 설정 방법
  - 문제 해결 가이드
```

- 예상 시간: 2시간
- 담당자: 기획자 또는 개발자
- 우선순위: 낮음

- [ ] **Task 7.3**: 개발자 문서 작성
```
- 아키텍처 다이어그램
  - 데이터베이스 스키마
  - 설치 및 설정 가이드
  - 트러블슈팅 가이드
```

- 예상 시간: 3시간
- 담당자: Backend 개발자
- 우선순위: 중간

#### Day 27: 배포 준비
- [ ] **Task 7.4**: 환경 설정 파일 정리
```
- 개발/스테이징/운영 환경별 설정 분리
  - 환경 변수 설정
  - 보안 정보 외부화
```

- 예상 시간: 2시간
- 담당자: DevOps 또는 Backend 개발자
- 우선순위: 높음

- [ ] **Task 7.5**: 배포 스크립트 작성
```
- 빌드 스크립트
  - 배포 자동화 스크립트
  - 롤백 절차
```

- 예상 시간: 2시간
- 담당자: DevOps
- 우선순위: 중간

- [ ] **Task 7.6**: 모니터링 설정
```
- 로그 수집 설정
  - 성능 모니터링 도구 연동
  - 알림 임계치 설정
```

- 예상 시간: 2시간
- 담당자: DevOps
- 우선순위: 중간

---

## 🎯 우선순위별 작업 분류

### P0 (최우선 - 필수)
- WebSocket 설정 및 기본 구조
- 알림 테이블 설계 및 생성
- NotificationService 핵심 로직
- NotificationController REST API
- Frontend WebSocket 연결
- 보안 및 인증 처리

### P1 (높음 - 중요)
- 알림 영구 저장 기능
- 알림 설정 기능
- 브라우저 호환성 테스트
- 통합 테스트

### P2 (중간 - 선택적)
- Redis 통합
- 성능 최적화
- UI/UX 개선
- 모바일 최적화

### P3 (낮음 - 부가 기능)
- 알림 사운드
- 이메일 알림 연동
- 고급 알림 필터링
- 알림 통계 대시보드

---

## 📊 리소스 및 역할 분담

### Backend 개발자 (1-2명)
- WebSocket 설정
- Service/DAO 레이어
- REST API 개발
- 보안 구현
- 성능 최적화

### Frontend 개발자 (1명)
- JavaScript 로직
- UI/UX 구현
- CSS 스타일링
- 브라우저 호환성

### DBA (0.5명)
- 테이블 설계
- 쿼리 최적화
- 인덱스 관리

### QA (1명)
- 기능 테스트
- 브라우저 테스트
- 부하 테스트
- 버그 리포팅

### DevOps (0.5명)
- 배포 자동화
- 모니터링 설정
- 인프라 관리

---

## 🔍 리스크 및 대응 방안

### 리스크 1: 브라우저 호환성 문제
**대응 방안**:
- SockJS 폴백 메커니즘 활용
- 초기 단계에서 다양한 브라우저 테스트
- Progressive Enhancement 전략

### 리스크 2: 성능 저하
**대응 방안**:
- Redis 캐싱 도입
- 메시지 큐 시스템 고려
- 데이터베이스 쿼리 최적화
- 연결 수 제한 및 Rate Limiting

### 리스크 3: 보안 취약점
**대응 방안**:
- WebSocket 인증/인가 철저히 구현
- CSRF/XSS 방어 메커니즘
- 정기적인 보안 감사
- 입력 검증 강화

### 리스크 4: 일정 지연
**대응 방안**:
- MVP 기능 우선 개발
- 선택적 기능은 Phase 2로 이관
- 주간 진행 상황 점검
- 이슈 발생 시 즉시 에스컬레이션

---

## ✅ 체크리스트

### 개발 전 준비사항
- [ ] 개발 환경 구축 완료
- [ ] 요구사항 명세서 검토
- [ ] 기술 스택 확정
- [ ] 팀원 역할 분담
- [ ] 개발 일정 합의

### 개발 중 체크사항
- [ ] 코드 리뷰 진행
- [ ] 단위 테스트 작성
- [ ] Git 브랜치 전략 준수
- [ ] 주간 진행 상황 보고
- [ ] 이슈 트래킹

### 배포 전 체크사항
- [ ] 전체 기능 테스트 완료
- [ ] 보안 취약점 점검
- [ ] 성능 테스트 통과
- [ ] 문서화 완료
- [ ] 운영 환경 설정 완료
- [ ] 롤백 계획 수립
- [ ] 모니터링 설정 완료

---

## 📞 연락처 및 문의

- **프로젝트 관리자**: [이름] - [이메일]
- **기술 리더**: [이름] - [이메일]
- **Git Repository**: [저장소 URL]
- **이슈 트래커**: [이슈 트래커 URL]
- **문서 위키**: [위키 URL]

---

## 📝 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|-----------|
| 1.0  | 2025-11-10 | AI Assistant | 초안 작성 |

---

**마지막 업데이트**: 2025-11-10  
**문서 상태**: 초안  
**승인 필요**: 프로젝트 관리자, 기술 리더