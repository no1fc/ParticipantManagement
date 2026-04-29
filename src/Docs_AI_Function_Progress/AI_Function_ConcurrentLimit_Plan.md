# AI_Function_ConcurrentLimit_Plan.md — AI 추천 동시 요청 제한 기능

> **작성일:** 2026-04-27  
> **상태:** :white_check_mark: 구현 완료  
> **선행 조건:** AI 추천 저장 기능 구현 완료  
> **목표:** 상담사당 AI 추천 동시 요청을 최대 5건으로 제한하여 서버 부하 관리

---

## 1. 기능 개요

### 1-1. 배경

현재 AI 추천 기능은 상담사가 여러 참여자에 대해 동시에 AI 추천을 요청할 수 있다.  
각 요청은 Gemini API를 2단계로 호출하므로 처리 시간이 길고(최대 180초), 동시 요청이 많아지면 서버 리소스(Gemini API 호출, DB 연결, 메모리)가 과부하될 수 있다.

**상담사별 동시 요청 수를 최대 5건으로 제한**하여 안정적인 서비스 운영을 보장한다.

### 1-2. 기능 요약

| 항목 | 내용 |
|------|------|
| 제한 단위 | 상담사(memberUserID)별 개별 적용 |
| 최대 동시 요청 | 5건 |
| 제한 초과 시 | HTTP 429 응답 + 안내 메시지 |
| 슬롯 해제 시점 | AI 추천 처리 완료 후 (성공/실패 모두) |
| 슬롯 표시 | 추천 모달에 남은 슬롯 수 표시 |
| WebSocket 연동 | 알림에 `activeRecommendCount` 포함 → 실시간 슬롯 업데이트 |

---

## 2. 기술 설계

### 2-1. RecommendConcurrencyManager

**파일:** `src/main/java/com/jobmoa/app/CounselMain/biz/recommend/RecommendConcurrencyManager.java`

- `@Component` Spring Bean (싱글톤)
- `ConcurrentHashMap<String, AtomicInteger>` — 상담사별 활성 요청 수 추적
- CAS(Compare-And-Swap) 루프로 원자적 슬롯 획득/해제

| 메서드 | 역할 |
|--------|------|
| `tryAcquire(userId)` | 슬롯 획득 시도 (5건 미만이면 true) |
| `release(userId)` | 슬롯 해제 (처리 완료 시) |
| `getActiveCount(userId)` | 현재 활성 요청 수 |
| `getRemainingSlots(userId)` | 남은 슬롯 수 (5 - active) |

### 2-2. Controller 흐름

```
saveRecommendAI() 요청 수신
    ↓
세션에서 memberUserID 추출
    ↓
concurrencyManager.tryAcquire(memberUserID)
    ├─ 실패 → HTTP 429 + 안내 메시지 반환
    └─ 성공 → acquired = true
        ↓
    processAndSaveRecommend() 실행 (try 블록)
        ↓
    finally:
        concurrencyManager.release(memberUserID)  ← 슬롯 해제
        sendRecommendNotification()               ← WebSocket 알림 (해제 후 카운트 반영)
```

### 2-3. DTO 변경

| DTO | 추가 필드 | 용도 |
|-----|-----------|------|
| `ProcessRecommendResultDTO` | `int activeRecommendCount` | HTTP 응답에 현재 활성 수 포함 |
| `RecommendNotificationDTO` | `int activeRecommendCount` | WebSocket 알림에 현재 활성 수 포함 |

### 2-4. Frontend 변경

- **recommend-modal_0.0.3.js:**
  - AJAX 에러 핸들러에 HTTP 429 처리 추가
  - `updateSlotDisplay(activeCount)` 함수 추가
  - `handleRecommendNotification()`에서 `activeRecommendCount` 처리
- **recommendModal.jsp:**
  - AI 추천 버튼 옆에 슬롯 정보 표시 (`남은 동시 추천 슬롯: N/5`)

---

## 3. 변경 대상 파일

| 구분 | 파일 | 변경 내용 |
|------|------|-----------|
| **신규** | `biz/recommend/RecommendConcurrencyManager.java` | 동시성 관리 컴포넌트 |
| 수정 | `biz/recommend/ProcessRecommendResultDTO.java` | `activeRecommendCount` 필드 추가 |
| 수정 | `biz/recommend/RecommendNotificationDTO.java` | `activeRecommendCount` 필드 추가 |
| 수정 | `view/.../recommendAjaxController.java` | 동시성 제한 로직 적용 |
| 수정 | `js/recommend-modal_0.0.3.js` | HTTP 429 처리 + 슬롯 표시 |
| 수정 | `views/includeModal/recommendModal.jsp` | 슬롯 정보 UI |
| 수정 | `css/.../recommend-modal_0.0.1.css` | 슬롯 정보 스타일 |

---

## 4. 설계 결정 사항

### 4-1. 인메모리 vs DB 기반

**인메모리(ConcurrentHashMap) 선택 이유:**
- AI 추천은 단일 서버에서 실행 (다중 서버 미사용)
- 서버 재시작 시 모든 진행 중 요청이 타임아웃되므로 상태 초기화가 적절
- DB 기반보다 성능 오버헤드 없음 (CAS 연산만 수행)

### 4-2. 슬롯 해제 타이밍

`finally` 블록에서 `release()` → `sendRecommendNotification()` 순서로 실행:
- 알림 전송 시점에 정확한 활성 수 반영 (이미 해제된 상태)
- 프론트엔드에서 남은 슬롯을 올바르게 표시

---

## 5. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-27 | v1.0 | 최초 작성 및 구현 완료 | SD |
