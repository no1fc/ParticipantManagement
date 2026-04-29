# AI_Function_WebSocket_Notification_Plan.md — WebSocket 기반 AI 추천 완료 알림 기능

> **작성일:** 2026-04-24  
> **상태:** :white_check_mark: 구현 완료  
> **선행 조건:** 기존 WebSocket 인프라 (`WebSocketConfig.java`) 활용  
> **목표:** AI 채용정보 추천 저장 완료 시 WebSocket을 통해 실시간 알림을 전송하고, 버튼 중복 클릭 방지 및 24시간 쿨다운을 적용한다

---

## 1. 기능 개요

### 1-1. 배경

현재 AI 추천 저장(`saveAiRecommend`)은 Gemini API 2회 호출로 인해 **최대 15초 이상** 소요될 수 있다.  
사용자(상담사)가 추천 저장 버튼을 누른 뒤 다른 작업을 하다가도 완료 시점을 놓치지 않도록  
**WebSocket 실시간 알림**을 추가하여 UX를 개선한다.

### 1-2. 기능 요약

| 항목 | 내용 |
|------|------|
| 알림 트리거 | AI 추천 채용정보 저장 완료 시 |
| 알림 대상 | 추천 저장을 요청한 상담사 (세션 기반) |
| 알림 채널 | WebSocket (`/ws-notification` 엔드포인트, STOMP) |
| 알림 내용 | 참여자명, 추천 건수, 완료 시각 |
| UI 표현 | 토스트 알림 (우측 상단, 5초 후 자동 사라짐) |
| 버튼 제어 | 추천 진행 중 버튼 비활성화 (sessionStorage 기반 상태 유지) |
| 24시간 쿨다운 | 24시간 이내 재사용 시 차단 메시지 표시 (forceRefresh=false) |

---

## 2. 기술 설계

### 2-1. 기존 WebSocket 인프라 활용

현재 `WebSocketConfig.java`에 다음이 이미 설정되어 있음:
- 엔드포인트: `/ws`, `/ws-notification` (SockJS fallback 지원)
- 메시지 브로커: `/topic` (구독), `/app` (전송)
- STOMP 프로토콜 사용

### 2-2. 메시지 토픽 설계

```
/topic/recommend-complete/{loginId}
```

- 사용자별 개인 알림을 위해 `loginId`를 토픽에 포함
- 같은 브라우저 세션의 사용자만 수신

### 2-3. 알림 메시지 DTO

```java
public class RecommendNotificationDTO {
    private String participantName;  // 참여자명
    private int savedCount;          // 추천 저장 건수
    private String message;          // 알림 메시지
    private String timestamp;        // 완료 시각
    private boolean success;         // 성공 여부
}
```

### 2-4. 백엔드 구현 포인트

#### 2-4-1. NotificationService 생성

```java
@Service
public class RecommendNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendRecommendComplete(String loginId, 
                                       String participantName, 
                                       int savedCount, 
                                       boolean success) {
        RecommendNotificationDTO notification = new RecommendNotificationDTO();
        notification.setParticipantName(participantName);
        notification.setSavedCount(savedCount);
        notification.setSuccess(success);
        notification.setTimestamp(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        if (success) {
            notification.setMessage(
                participantName + "님의 AI 추천 채용정보 " 
                + savedCount + "건이 저장되었습니다.");
        } else {
            notification.setMessage(
                participantName + "님의 AI 추천 처리 중 오류가 발생했습니다.");
        }

        messagingTemplate.convertAndSend(
            "/topic/recommend-complete/" + loginId, notification);
    }
}
```

#### 2-4-2. Controller/Service 연동

`ParticipantJobRecommendServiceImpl`의 `processAndSaveRecommend()` 완료 후:

```java
// 추천 저장 완료 후 WebSocket 알림 전송
recommendNotificationService.sendRecommendComplete(
    loginId, participantName, savedCount, true);
```

### 2-5. 프론트엔드 구현 포인트

#### 2-5-1. WebSocket 연결 및 구독 (recommend-modal_0.0.3.js)

```javascript
var stompClient = null;

function connectRecommendNotification(loginId) {
    var socket = new SockJS('/ws-notification');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // 디버그 로그 비활성화

    stompClient.connect({}, function(frame) {
        stompClient.subscribe(
            '/topic/recommend-complete/' + loginId,
            function(message) {
                var notification = JSON.parse(message.body);
                showRecommendToast(notification);
            }
        );
    });
}
```

#### 2-5-2. 토스트 알림 UI

```javascript
function showRecommendToast(notification) {
    var toast = document.createElement('div');
    toast.className = 'recommend-toast ' 
        + (notification.success ? 'toast-success' : 'toast-error');
    toast.innerHTML = '<div class="toast-header">'
        + '<strong>AI 추천 완료</strong>'
        + '<span>' + notification.timestamp + '</span>'
        + '</div>'
        + '<div class="toast-body">' + notification.message + '</div>';
    
    document.body.appendChild(toast);
    
    // 5초 후 자동 사라짐
    setTimeout(function() {
        toast.classList.add('toast-fade-out');
        setTimeout(function() { toast.remove(); }, 300);
    }, 5000);
}
```

#### 2-5-3. 토스트 CSS

```css
.recommend-toast {
    position: fixed;
    top: 20px;
    right: 20px;
    min-width: 300px;
    padding: 12px 16px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    z-index: 10000;
    animation: slideIn 0.3s ease-out;
}
.toast-success { background: #d4edda; border-left: 4px solid #28a745; }
.toast-error   { background: #f8d7da; border-left: 4px solid #dc3545; }
.toast-fade-out { opacity: 0; transition: opacity 0.3s; }
@keyframes slideIn {
    from { transform: translateX(100%); opacity: 0; }
    to   { transform: translateX(0);    opacity: 1; }
}
```

---

## 3. 변경 대상 파일

| 파일 | 변경 내용 |
|------|-----------|
| `config/WebSocketConfig.java` | 변경 없음 (기존 설정 활용) |
| `CounselMain/biz/RecommendNotificationService.java` | **신규** — WebSocket 알림 전송 서비스 |
| `CounselMain/biz/RecommendNotificationDTO.java` | **신규** — 알림 메시지 DTO |
| `CounselMain/biz/ParticipantJobRecommendServiceImpl.java` | 추천 저장 완료 후 알림 전송 호출 추가 |
| `webapp/js/recommend-modal_0.0.3.js` | WebSocket 연결, 구독, 토스트 알림 추가 |
| `webapp/css/recommend-modal.css` | 토스트 알림 스타일 추가 |
| `WEB-INF/views/participantMain.jsp` | SockJS/STOMP 라이브러리 로드 확인 |

---

## 4. 개발 일정

| 단계 | 작업 | 예상 기간 | 상태 |
|------|------|-----------|------|
| 1 | DTO 및 NotificationService 생성 | 0.5일 | 🔲 |
| 2 | ServiceImpl에 알림 전송 연동 | 0.5일 | 🔲 |
| 3 | 프론트엔드 WebSocket 연결 및 구독 | 0.5일 | 🔲 |
| 4 | 토스트 UI/CSS 구현 | 0.5일 | 🔲 |
| 5 | 통합 테스트 (정상/오류 케이스) | 0.5일 | 🔲 |
| 6 | 엣지 케이스 및 연결 끊김 처리 | 0.5일 | 🔲 |

**총 예상 소요:** 3일

---

## 5. 테스트 시나리오

| # | 시나리오 | 예상 결과 |
|---|----------|-----------|
| 1 | AI 추천 저장 성공 | 토스트 알림 표시 (초록색, 참여자명 + 건수) |
| 2 | AI 추천 저장 실패 | 토스트 알림 표시 (빨간색, 오류 메시지) |
| 3 | 모달 닫힌 상태에서 완료 | 토스트 알림은 모달과 무관하게 표시 |
| 4 | 다른 페이지에서 완료 | WebSocket 연결이 유지된 페이지에서만 표시 |
| 5 | WebSocket 연결 끊김 후 재연결 | 자동 재연결 시도 (SockJS fallback) |
| 6 | 동시 다수 추천 저장 | 각각 개별 토스트로 표시 |

---

## 6. 리스크 및 대응

| 리스크 | 대응 방안 |
|--------|-----------|
| WebSocket 연결 유지 실패 | SockJS fallback + 재연결 로직 구현 |
| 브라우저 탭 전환 시 알림 미수신 | 토스트는 DOM 기반이므로 탭 활성화 시 표시됨 |
| 동시 접속자 증가 시 부하 | SimpleBroker 사용, 사용자별 개별 토픽으로 격리 |
| 세션 만료 시 알림 전송 실패 | loginId 기반 토픽이므로 세션과 독립적 |

---

## 7. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-24 | v0.1 | 최초 작성 — WebSocket 기반 AI 추천 완료 알림 기능 계획 | SD |
| 2026-04-24 | v1.0 | 구현 완료 — WebSocket 알림 + 버튼 중복 클릭 방지(sessionStorage) + 24시간 쿨다운(forceRefresh=false) 통합 구현. 변경 파일: ProcessRecommendResultDTO, ParticipantJobRecommendServiceImpl, RecommendNotificationDTO(신규), recommendAjaxController, participantMain.jsp, recommendModal.jsp, recommend-modal_0.0.3.js, recommend-modal_0.0.1.css | SD |
