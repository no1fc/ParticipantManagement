# AI_Function_GNB_Notification_Plan.md — GNB 알림 기능 고도화 계획서

> **작성일:** 2026-04-24  
> **상태:** :white_check_mark: 구현 완료 (2026-04-27)  
> **선행 조건:** WebSocket 기반 AI 추천 완료 알림 기능 구현 완료 (`AI_Function_WebSocket_Notification_Plan.md`)  
> **목표:** AI 추천 완료 알림을 GNB(전역 내비게이션 바)에 추가하여 모든 페이지에서 확인 가능하도록 한다

---

## 1. 기능 개요

### 1-1. 배경

현재 AI 추천 완료 알림은 `participantMain.jsp` 페이지의 토스트 알림으로만 동작한다.  
상담사가 추천 저장 후 다른 페이지(대시보드, 참여자 등록 등)로 이동하면 알림을 받을 수 없다.  
**GNB에 알림 벨 아이콘 + 드롭다운**을 추가하여 모든 페이지에서 실시간 알림을 수신할 수 있도록 한다.

### 1-2. 기능 요약

| 항목 | 내용 |
|------|------|
| 알림 위치 | GNB 헤더 우측 (전체화면 토글 옆, 사용자 메뉴 앞) |
| 알림 아이콘 | Bootstrap Icons `bi-bell` / `bi-bell-fill` |
| 읽지 않은 알림 배지 | 빨간 뱃지로 건수 표시 |
| 알림 드롭다운 | 최근 알림 목록 (최대 10건), 클릭 시 해당 참여자 모달로 이동 |
| WebSocket 연결 | 기존 `/ws-notification` 엔드포인트 재사용, 모든 페이지에서 연결 |
| 알림 유형 | AI 추천 완료 (성공/실패), 24시간 쿨다운 안내 |
| 알림 저장 | 세션 기반 (브라우저 `sessionStorage`) — DB 저장은 추후 검토 |

---

## 2. 현재 구조 분석

### 2-1. GNB 파일 구조

- **GNB 태그 파일:** `src/main/webapp/WEB-INF/tags/gnb.tag` (306줄)
- **GNB 사용 페이지:** 30개 JSP (일반 19 + 관리자 11)
- **GNB JS:** `src/main/webapp/js/gnb_0.0.1.js` (세션 시간 표시)
- **GNB CSS:** `src/main/webapp/css/participantCss/custom-modern_0.0.1.css`

### 2-2. GNB 헤더 우측 구조 (gnb.tag Line 26~78)

```
<ul class="navbar-nav ms-auto">
  [1] 전체화면 토글 (bi-arrows-fullscreen)
  [2] 사용자 드롭다운 (로그인 정보)
</ul>
```

**알림 벨 삽입 위치:** [1]과 [2] 사이

### 2-3. 현재 WebSocket 연결 범위

- SockJS/STOMP 라이브러리: `participantMain.jsp`에만 로드
- WebSocket 연결 로직: `recommend-modal_0.0.3.js`에 포함
- `JOBMOA_USER_ID` 변수: `participantMain.jsp`에서만 설정

**문제:** 다른 29개 페이지에서는 WebSocket 연결이 없음

---

## 3. 기술 설계

### 3-1. WebSocket 연결을 GNB 레벨로 이동

현재 `participantMain.jsp`에만 있는 WebSocket 연결을 GNB 공통 영역으로 이동:

1. **SockJS/STOMP CDN** → `gnb.tag`에 추가 (모든 페이지에서 로드)
2. **`JOBMOA_USER_ID`** → `gnb.tag`에서 설정 (세션 데이터 접근)
3. **WebSocket 연결 로직** → 신규 `gnb-notification_0.0.1.js`로 이동
4. **`participantMain.jsp`의 중복 로드** → 제거 (GNB에서 이미 로드)

### 3-2. GNB 알림 벨 HTML 구조

```html
<!-- gnb.tag — 전체화면 토글과 사용자 메뉴 사이에 삽입 -->
<li class="nav-item dropdown" id="gnbNotificationArea">
    <a href="#" class="nav-link" data-bs-toggle="dropdown" aria-expanded="false">
        <i class="bi bi-bell" id="gnbBellIcon"></i>
        <span class="navbar-badge badge bg-danger" id="gnbNotificationBadge" 
              style="display:none;">0</span>
    </a>
    <div class="dropdown-menu dropdown-menu-lg dropdown-menu-end" id="gnbNotificationDropdown">
        <span class="dropdown-item dropdown-header">알림</span>
        <div class="dropdown-divider"></div>
        <div id="gnbNotificationList">
            <!-- 알림 항목이 동적으로 추가됨 -->
        </div>
        <div class="dropdown-divider"></div>
        <a href="#" class="dropdown-item dropdown-footer" onclick="clearAllNotifications()">
            모든 알림 지우기
        </a>
    </div>
</li>
```

### 3-3. 알림 항목 HTML 구조

```html
<!-- 개별 알림 항목 -->
<a href="#" class="dropdown-item gnb-notification-item" 
   data-jobseeker-no="12345" onclick="handleNotificationClick(this)">
    <div class="d-flex align-items-center">
        <i class="bi bi-check-circle-fill text-success me-2"></i>
        <div>
            <p class="mb-0 fw-bold">홍길동님 AI 추천 완료</p>
            <p class="mb-0 text-muted small">5개의 채용정보가 저장되었습니다.</p>
            <span class="text-muted small">14:30:00</span>
        </div>
    </div>
</a>
```

### 3-4. JavaScript 구조 (gnb-notification_0.0.1.js)

| 함수 | 역할 |
|------|------|
| `connectGnbWebSocket()` | WebSocket 연결 및 `/topic/recommend-complete/{userId}` 구독 |
| `handleGnbNotification(notification)` | 알림 수신 → 드롭다운에 항목 추가 + 뱃지 업데이트 + 토스트 표시 |
| `addNotificationItem(notification)` | 드롭다운 리스트에 알림 DOM 추가 (최대 10건 유지) |
| `updateNotificationBadge()` | 읽지 않은 알림 건수 뱃지 업데이트 |
| `handleNotificationClick(element)` | 알림 클릭 → 해당 참여자 추천 모달 오픈 (participantMain 페이지일 경우) |
| `clearAllNotifications()` | 모든 알림 지우기 |
| `saveNotificationsToStorage()` | 알림 목록을 sessionStorage에 저장 (페이지 이동 시 유지) |
| `loadNotificationsFromStorage()` | 페이지 로드 시 sessionStorage에서 알림 목록 복원 |

### 3-5. 알림 데이터 구조 (sessionStorage)

```javascript
// sessionStorage key: 'gnbNotifications_' + JOBMOA_USER_ID
[
    {
        "id": "1714012345678",
        "type": "success",            // success | error | warning
        "jobSeekerNo": 12345,
        "participantName": "홍길동",
        "message": "5개의 채용정보가 추천 저장되었습니다.",
        "timestamp": "14:30:00",
        "read": false
    }
]
```

### 3-6. 기존 recommend-modal_0.0.3.js 연동

GNB에서 WebSocket을 관리하므로 `recommend-modal_0.0.3.js`의 역할 변경:

| 항목 | 현재 | 변경 후 |
|------|------|---------|
| WebSocket 연결 | `recommend-modal_0.0.3.js`에서 직접 연결 | GNB JS에서 연결, 이벤트 위임 |
| 토스트 알림 | `recommend-modal_0.0.3.js`에서 직접 표시 | GNB JS에서 공통 표시 |
| 모달 데이터 새로고침 | `recommend-modal_0.0.3.js`에서 처리 | 커스텀 이벤트로 전달받아 처리 |

**커스텀 이벤트 패턴:**
```javascript
// gnb-notification JS — 알림 수신 시 커스텀 이벤트 발행
document.dispatchEvent(new CustomEvent('recommendComplete', { detail: notification }));

// recommend-modal JS — 이벤트 수신 (participantMain 페이지에서만 동작)
document.addEventListener('recommendComplete', function(e) {
    var notification = e.detail;
    enableAiRecommendButton();
    if (notification.success && notification.savedCount > 0) {
        loadRecommendData(notification.jobSeekerNo);
    }
});
```

---

## 4. 변경 대상 파일

| 구분 | 파일 | 변경 내용 |
|------|------|-----------|
| 수정 | `WEB-INF/tags/gnb.tag` | SockJS/STOMP CDN 추가, `JOBMOA_USER_ID` 변수, 알림 벨 HTML, `gnb-notification_0.0.1.js` 로드 |
| **신규** | `js/gnb-notification_0.0.1.js` | WebSocket 연결, 알림 드롭다운 관리, 토스트, sessionStorage 관리 |
| **신규** | `css/gnb-notification_0.0.1.css` | 알림 벨, 드롭다운, 뱃지, 알림 항목 스타일 |
| 수정 | `js/recommend-modal_0.0.3.js` | WebSocket 직접 연결 제거, 커스텀 이벤트 리스너로 전환 |
| 수정 | `participantMain.jsp` | SockJS/STOMP CDN 중복 제거 (GNB에서 로드) |

---

## 5. 개발 일정

| 단계 | 작업 | 예상 소요 | 상태 |
|------|------|-----------|------|
| 1 | gnb.tag에 알림 벨 HTML + CDN 추가 | 0.5일 | ✅ |
| 2 | gnb-notification_0.0.1.js 생성 (WebSocket + 드롭다운 관리) | 1일 | ✅ |
| 3 | gnb-notification_0.0.1.css 생성 (알림 UI 스타일) | 0.5일 | ✅ |
| 4 | recommend-modal_0.0.3.js 리팩토링 (커스텀 이벤트 전환) | 0.5일 | ✅ |
| 5 | participantMain.jsp 중복 제거 | 0.5일 | ✅ |
| 6 | 다른 페이지에서 알림 수신 테스트 (대시보드 등) | 0.5일 | 🔲 |
| 7 | 알림 클릭 → 참여자 모달 이동 기능 | 0.5일 | 🔲 |
| 8 | 엣지 케이스 처리 및 통합 테스트 | 0.5일 | 🔲 |

**총 예상 소요:** 4.5일

---

## 6. 테스트 시나리오

| # | 시나리오 | 예상 결과 |
|---|----------|-----------|
| 1 | participantMain에서 AI 추천 저장 | GNB 벨에 뱃지 표시 + 드롭다운에 항목 추가 + 토스트 표시 |
| 2 | 대시보드 페이지에서 알림 수신 | GNB 벨에 뱃지 표시 + 드롭다운에 항목 추가 + 토스트 표시 |
| 3 | 알림 드롭다운 열기 | 최근 알림 목록 표시, 읽지 않은 항목 강조 |
| 4 | 알림 클릭 (participantMain 페이지) | 해당 참여자 추천 모달 자동 오픈 |
| 5 | 알림 클릭 (다른 페이지) | participantMain으로 이동 후 해당 참여자 모달 오픈 (또는 안내 메시지) |
| 6 | "모든 알림 지우기" 클릭 | 드롭다운 비움 + 뱃지 숨김 |
| 7 | 페이지 이동 후 복귀 | sessionStorage에서 알림 목록 복원, 뱃지 건수 유지 |
| 8 | 브라우저 새로고침 | WebSocket 재연결, 알림 목록 복원 |
| 9 | 상담사 로그아웃 → 다른 상담사 로그인 | 이전 상담사 알림 미표시 (JOBMOA_USER_ID 기반 분리) |
| 10 | WebSocket 연결 끊김 | 5초 후 자동 재연결, 연결 중 수신 못한 알림은 유실 (세션 기반 한계) |

---

## 7. 리스크 및 대응

| 리스크 | 대응 방안 |
|--------|-----------|
| 30개 JSP 모두에 WebSocket 연결 → 서버 연결 수 증가 | SimpleBroker + 사용자별 토픽으로 부하 격리, 필요 시 idle 타임아웃 적용 |
| 페이지 이동 시 알림 유실 | sessionStorage에 알림 목록 저장 → 페이지 로드 시 복원 |
| GNB 태그 수정 시 전체 페이지 영향 | gnb.tag 변경은 HTML 추가만 수행, 기존 구조 변경 없음 |
| 관리자 페이지(`adminGnb.tag`)는 별도 | 1차는 일반 GNB만 적용, 관리자 GNB는 추후 검토 |
| 알림 클릭 시 다른 페이지에서 참여자 모달 오픈 불가 | 해당 페이지로 리다이렉트 또는 "참여자 조회에서 확인하세요" 안내 메시지 |

---

## 8. 향후 확장 포인트

| 항목 | 설명 | 우선순위 |
|------|------|----------|
| 알림 DB 저장 | 알림 이력 테이블 생성 → 로그인 시 미확인 알림 복원 | 중 |
| 알림 유형 확장 | 참여자 배정, 상담 일정 알림 등 | 중 |
| adminGnb.tag 적용 | 관리자 페이지에도 동일 알림 기능 | 낮음 |
| 브라우저 푸시 알림 | `Notification API` 활용 — 탭 비활성 시에도 알림 | 낮음 |
| 소리 알림 | 알림 수신 시 효과음 재생 옵션 | 낮음 |

---

## 9. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-24 | v0.1 | 최초 작성 — GNB 알림 기능 고도화 상세 계획 | SD |
| 2026-04-27 | v0.2 | 단계 1-5 구현 완료 — GNB 알림 벨, WebSocket 이동, 커스텀 이벤트 전환 | SD |