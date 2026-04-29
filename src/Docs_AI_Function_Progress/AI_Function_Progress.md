# AI_Function_Progress.md — 참여자관리 AI 추천 기능 개발 진행 현황

> 기준일: 2026-04-24 (최종 업데이트)  
> 담당자: 주니어 개발자 1인  
> 기준 문서: `src/Docs_md/AI_Function_PRD.md`  
> 현재 브랜치: `Page_Recruitment_information_1.3.3`

---

## 전체 진행 요약

| 단계 | 작업 분류 | 상태 | 비고 |
|------|-----------|------|------|
| 1단계 | 필수 기능 안정화 | :white_check_mark: 완료 | DB/검색/리스트/모달 |
| 2단계 | AI 연동 | :white_check_mark: 완료 | Gemini API 연동, 링크 복사 |
| 3단계 | 카카오톡 공유 | :white_check_mark: 완료 | Kakao JS SDK 방식으로 구현 완료 → `AI_Function_KakaoTalk_Share_Plan.md` 참고 |
| 4단계 | WebSocket 알림 + 버튼 제어 + 24h 쿨다운 | :white_check_mark: 완료 | AI 추천 완료 실시간 알림, 버튼 중복 클릭 방지, 24시간 쿨다운 → `AI_Function_WebSocket_Notification_Plan.md` 참고 |

---

## 주차별 개발 일정 및 진행 현황

### 1~9주차 — AI 추천 채용정보 기능 (완료)

**기간:** 2026-04-03 ~ 2026-06-05  
**상태:** :white_check_mark: 완료

#### 완료된 작업 요약
- DB 설계 및 반영 (J_참여자관리_참여자추천채용정보, J_참여자관리_희망직무 테이블)
- 백엔드: 검색 쿼리, DTO/DAO/Service/Controller, Gemini API 연동
- 프론트엔드: 검색 필터, 추천 모달, 데이터 연동, 링크 복사
- Gemini 2단계 추천 로직 (검색 조건 생성 → 베스트 선별)
- 24시간 캐싱, 에러 처리, 통합 테스트, UX 개선

> 상세 내용은 `AI_Function_1Week_Progress.md` ~ `AI_Function_9Week_Progress.md` 참고

---

### 10주차 이후 — 카카오톡 공유 기능 (완료)

**상태:** :white_check_mark: 완료  
**구현 방식 변경:** REST API + OAuth 2.0 → **Kakao JS SDK (`Kakao.Share.sendCustom`)** 방식으로 전환하여 구현 완료

> **상세 계획:** `AI_Function_KakaoTalk_Share_Plan.md` 참고

#### 완료된 작업 요약
- 카카오 개발자 앱 등록 및 JavaScript 키 설정
- Kakao JS SDK v2.7.4 로드 및 초기화 (`participantMain.jsp`)
- `Kakao.Share.sendCustom()` 커스텀 템플릿(ID: 132481) 기반 공유 구현
- 추천 모달 내 개별 공유 버튼 + 선택 항목 일괄 공유 기능
- 큐 기반 순차 공유 시스템 (브라우저 팝업 정책 대응)
- 공유 진행률 프로그레스 바 UI
- 공유 취소 기능
- 에러 처리 (SDK 미초기화, 공유 항목 미선택 등)

---

### 추가 기능 — WebSocket 알림 + 버튼 제어 + 24시간 쿨다운 (완료)

**상태:** :white_check_mark: 구현 완료

> **상세 계획:** `AI_Function_WebSocket_Notification_Plan.md` 참고

#### 완료된 작업 요약
- **WebSocket 실시간 알림:** 기존 `WebSocketService` + `/ws-notification` 엔드포인트 활용, SockJS/STOMP 클라이언트 추가
- **토스트 알림 UI:** 우측 상단 토스트 (성공/실패/경고), 5초 후 자동 사라짐, slideIn 애니메이션
- **버튼 중복 클릭 방지:** `sessionStorage` 기반 진행 상태 관리, 모달 닫았다 열어도 비활성화 유지, 200초 안전 타임아웃
- **24시간 쿨다운:** `forceRefresh: false`로 변경하여 백엔드 24시간 체크 활성화, `lastRecommendedAt` 타임스탬프 표시
- **Controller WebSocket 연동:** `recommendAjaxController`에서 `WebSocketService.sendObject()` 호출, `LoginBean.memberUserID` 기반 사용자별 토픽
- **24시간 쿨다운 DB 기준 변경:** Java 시간 비교 → MSSQL `DATEDIFF(HOUR, 저장일시, GETDATE())` DB 쿼리(`selectCooldownStatus`)로 교체

---

### 추가 기능 — GNB 알림 기능 고도화 (다음 주 진행 예정)

**상태:** :arrow_forward: 계획 수립 완료, 구현 대기

> **상세 계획:** `AI_Function_GNB_Notification_Plan.md` 참고

#### 주요 작업
- GNB 헤더에 알림 벨 아이콘 + 드롭다운 추가 (30개 JSP 전체 적용)
- WebSocket 연결을 GNB 레벨로 이동 (모든 페이지에서 알림 수신)
- 알림 목록 sessionStorage 기반 페이지 이동 시 유지
- recommend-modal_0.0.3.js 리팩토링 (커스텀 이벤트 패턴)
- 예상 소요: 4.5일

---

## 성공 기준 체크리스트

| # | 성공 기준 | 완료 여부 |
|---|-----------|----------|
| 1 | 집중 알선 참여자를 검색할 수 있다 | :white_check_mark: |
| 2 | 참여자 리스트에서 추천 채용정보 모달을 열 수 있다 | :white_check_mark: |
| 3 | 모달에서 참여자 정보와 추천 채용정보가 정상 표시된다 | :white_check_mark: |
| 4 | Gemini API 기반 검색 조건 생성이 동작한다 | :white_check_mark: |
| 5 | 알선상세정보 유무에 따라 저장 방식이 다르게 처리된다 | :white_check_mark: |
| 6 | `IAP후`, `미취업사후관리` 참여자에게만 AI 추천 저장 버튼이 동작한다 | :white_check_mark: |
| 7 | 링크 복사 기능이 동작한다 | :white_check_mark: |
| 8 | 카카오톡으로 추천 채용정보를 공유할 수 있다 | :white_check_mark: |
| 9 | AI 추천 저장 완료 시 WebSocket 실시간 알림이 표시된다 | :white_check_mark: |
| 10 | 추천 진행 중 버튼 클릭이 차단된다 (모달 닫았다 열어도 유지) | :white_check_mark: |
| 11 | 24시간 이내 재추천 시 차단 메시지가 표시된다 | :white_check_mark: |

---

## 리스크 관리 현황

| 리스크 | 대응 방안 | 상태 |
|--------|-----------|------|
| 참여자/채용정보 데이터 정합성 문제 | 매칭 전 정규화 및 예외처리 구현 | :white_check_mark: 대응 완료 |
| Gemini API 비용 및 호출량 증가 | 저장형 추천 구조 유지, 24시간 캐싱 | :white_check_mark: 대응 완료 |
| 추천 품질 부족 | 검색 조건 생성 + 2차 판정 단계 분리 | :white_check_mark: 대응 완료 |
| 응답 지연 | 후보군 건수 제한(20건), 저장 결과 재사용 | :white_check_mark: 대응 완료 |
| 카카오 앱 심사 필요 | JS SDK 방식은 앱 심사 불필요 (공유 기능은 심사 면제) | :white_check_mark: 해소 |
| 카카오 OAuth 팝업 차단 | JS SDK 방식 전환으로 OAuth 불필요, 사용자 클릭 이벤트 기반 공유 | :white_check_mark: 해소 |
| WebSocket 연결 유지 실패 | SockJS fallback + 5초 자동 재연결 로직 구현 | :white_check_mark: 대응 완료 |
| 동시 접속자 증가 시 알림 부하 | SimpleBroker 사용, 사용자별 개별 토픽 격리 구현 | :white_check_mark: 대응 완료 |

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 (PRD 기반 일정 및 진행 현황 문서화) | SD |
| 2026-04-22 | v1.0 | 1~9주차 완료 처리, 카카오톡 공유 계획으로 전환, 불필요 상세 내용 정리 | SD |
| 2026-04-24 | v1.1 | 4단계 WebSocket AI 추천 완료 알림 기능 계획 추가, 성공기준 #9 추가, 리스크 항목 추가 | SD |
| 2026-04-24 | v1.2 | 3단계 카카오톡 공유 완료 처리 (JS SDK 방식), 카카오 관련 리스크 해소 처리, 구현 방식 변경 내역 반영 | SD |
| 2026-04-24 | v1.3 | 4단계 구현 완료 — WebSocket 알림 + 버튼 중복 클릭 방지 + 24시간 쿨다운 통합 구현, 성공기준 #10~#11 추가 및 완료, 리스크 2건 대응 완료 | SD |
| 2026-04-24 | v1.4 | 24시간 쿨다운 DB 기준 변경 (Java → MSSQL DATEDIFF), GNB 알림 고도화 계획 추가 (`AI_Function_GNB_Notification_Plan.md`) | SD |