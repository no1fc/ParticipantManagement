# 카카오톡 공유 기능 개발 완료 보고서

> **작성일:** 2026-04-22  
> **완료일:** 2026-04-24  
> **상태:** :white_check_mark: 구현 완료  
> **브랜치:** `Page_Recruitment_information_1.3.3`  
> **선행 완료:** AI 추천 채용정보 기능, 링크 복사 기능

---

## 1. 개요

상담사가 참여자의 AI 추천 채용정보를 카카오톡으로 공유할 수 있는 기능을 구현하였다.

### 구현 방식 변경 이력

| 항목 | 최초 계획 | 실제 구현 |
|------|-----------|-----------|
| **방식** | REST API + OAuth 2.0 (서버 사이드) | **Kakao JS SDK** (클라이언트 사이드) |
| **인증** | OAuth 2.0 토큰 교환 | JavaScript 키 초기화 (인증 불필요) |
| **메시지 전송** | `kapi.kakao.com/v2/api/talk/memo/default/send` | `Kakao.Share.sendCustom()` |
| **템플릿** | Feed 메시지 JSON 직접 조립 | 카카오 커스텀 메시지 템플릿 (ID: 132481) |
| **앱 심사** | `talk_message` 스코프 심사 필요 | 공유 기능은 심사 면제 |
| **백엔드 변경** | Controller, Service, DTO 신규 생성 | `ParticipantController`에 JS Key 주입만 추가 |

**변경 사유:** JS SDK 방식이 OAuth 인증 없이 공유 가능하여 구현 복잡도가 낮고, 앱 심사가 불필요하며, 사용자 경험이 더 직관적임.

---

## 2. 기술 스택

- **Kakao JS SDK:** v2.7.4 (CDN 로드)
- **공유 방식:** `Kakao.Share.sendCustom()` — 커스텀 메시지 템플릿
- **템플릿 ID:** 132481
- **인증:** JavaScript 키 (`kakao.javascript-key` in `application.properties`)

---

## 3. 구현 내용

### 3-1. 백엔드

| 파일 | 변경 내용 |
|------|-----------|
| `application.properties` | `kakao.javascript-key` 속성 추가 |
| `ParticipantController.java` | `@Value("${kakao.javascript-key:}")` 주입, model에 `kakaoJsKey` 전달 |

### 3-2. 프론트엔드 — SDK 초기화

**파일:** `participantMain.jsp`

```html
<!-- Kakao JS SDK v2.7.4 -->
<script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.4/kakao.min.js"></script>
<script>Kakao.init('${kakaoJsKey}');</script>
```

### 3-3. 프론트엔드 — 공유 기능 (recommend-modal_0.0.3.js)

#### 구현된 함수 목록

| 함수 | 역할 |
|------|------|
| `sendKakaoShare(participantName, company, jobTitle, jobUrl)` | `Kakao.Share.sendCustom()` 호출 — 템플릿 변수 매핑 |
| `shareJobViaKakao(company, title, url)` | 개별 행 공유 버튼 — 단건 공유 |
| `shareSelectedViaKakao()` | 체크된 항목 일괄 공유 — 큐 기반 순차 처리 |
| `shareCurrentQueueItem()` | 큐의 현재 항목 공유 및 UI 상태 업데이트 |
| `updateShareProgress(done, total)` | 프로그레스 바 업데이트 |
| `cancelKakaoShareQueue()` | 공유 큐 취소 |
| `finishKakaoShareQueue()` | 큐 종료 및 UI 복원 |
| `toggleAllRecommendCheck(masterCheckbox)` | 전체선택 체크박스 토글 |
| `updateKakaoStatus(message, success)` | 상태 메시지 표시 (성공/오류) |

#### 커스텀 템플릿 변수

```javascript
Kakao.Share.sendCustom({
    templateId: 132481,
    templateArgs: {
        participantName: participantName,  // 참여자명
        company: company,                  // 기업명
        jobTitle: jobTitle,                // 채용공고 제목
        jobUrl: jobPath                    // 채용공고 경로 (도메인 제외)
    }
});
```

> 템플릿 링크: `https://www.work24.go.kr/${jobUrl}` 형태이므로, 전체 URL에서 도메인 부분을 제거하여 경로만 전달.

#### 큐 기반 순차 공유 시스템

브라우저 정책상 사용자 클릭 1회당 팝업 1개만 허용되므로 큐 방식 사용:

1. 체크된 채용정보를 `_kakaoShareQueue` 배열에 담기
2. 첫 번째 건 공유 → 버튼이 "다음 공유 (2/N)"로 변경
3. 사용자가 클릭할 때마다 다음 건 공유
4. 프로그레스 바로 진행률 표시
5. 모든 건 완료 시 원래 상태로 복원
6. 중간 취소 가능

### 3-4. 프론트엔드 — UI (recommendModal.jsp)

| 요소 | 설명 |
|------|------|
| `#btnKakaoShare` | 상단 "선택 항목 카카오톡 공유" 버튼 |
| `#btnKakaoCancelShare` | 공유 큐 취소 버튼 |
| `#kakaoShareProgress` | 프로그레스 바 영역 |
| `#shareProgressFill` | 프로그레스 바 채움 |
| `#shareProgressText` | 진행률 텍스트 |
| `#shareNextInfo` | 다음 공유 항목 안내 |
| `#kakaoShareStatus` | 상태 메시지 |
| `.btn-kakao-share-sm` | 각 행 개별 공유 버튼 |
| `.recommend-check` | 각 행 체크박스 |

### 3-5. 프론트엔드 — 스타일 (recommend-modal_0.0.1.css)

- `.btn-kakao-share`: 카카오 공식 노랑 `#FEE500`, 텍스트 `#191919`
- `.btn-kakao-share-active`: 큐 진행 중 강조 스타일
- `.btn-kakao-share-sm`: 행 내 소형 공유 버튼
- `.kakao-share-status`: 상태 메시지 (`.status-success`, `.status-error`)
- `.kakao-share-progress`: 프로그레스 바 스타일
- 카카오 SVG 아이콘 인라인 사용

---

## 4. 에러 처리

| 상황 | 처리 |
|------|------|
| Kakao SDK 미초기화 | `Kakao.isInitialized()` 체크 → "카카오 SDK가 초기화되지 않았습니다." 메시지 |
| 공유 항목 미선택 | "공유할 채용정보를 선택해주세요." 메시지 |
| 공유 큐 중간 취소 | "N건 중 M건 공유 후 취소되었습니다." 메시지 |
| 공유 성공 | "카카오톡 공유 창이 열렸습니다." 성공 메시지 |

---

## 5. 최종 파일 목록

| 구분 | 파일 | 작업 |
|------|------|------|
| 설정 | `src/main/resources/application.properties` | `kakao.javascript-key` 추가 |
| 수정 | `src/main/java/.../view/participant/ParticipantController.java` | JS Key model 전달 |
| 수정 | `src/main/webapp/WEB-INF/views/participantMain.jsp` | Kakao SDK 로드 및 초기화 |
| 수정 | `src/main/webapp/WEB-INF/views/includeModal/recommendModal.jsp` | 공유 버튼 HTML |
| 수정 | `src/main/webapp/js/recommend-modal_0.0.3.js` | 공유 JS 로직 전체 |
| 수정 | `src/main/webapp/css/participantCss/recommend-modal_0.0.1.css` | 공유 UI 스타일 |

> **REST API 계획 대비 불필요해진 파일:** KakaoApiService, KakaoTokenDTO, KakaoShareRequestDTO, KakaoShareResponseDTO, KakaoShareController, RootConfig/WebMvcConfig 수정 — 모두 불필요.

---

## 6. 검증 결과

- [x] Kakao SDK 정상 초기화 확인
- [x] 개별 행 공유 버튼 → 카카오톡 공유 창 정상 오픈
- [x] 선택 항목 일괄 공유 → 큐 기반 순차 공유 동작
- [x] 프로그레스 바 업데이트 정상
- [x] 공유 큐 취소 기능 정상
- [x] SDK 미초기화 시 에러 메시지 표시
- [x] 공유 항목 미선택 시 안내 메시지 표시

---

## 7. 주의 사항

- 카카오 커스텀 템플릿 ID(132481)가 JS에 하드코딩되어 있음 — 템플릿 변경 시 코드 수정 필요
- `kakao.javascript-key`는 `application.properties`에 저장됨 — 키 노출 주의 (JS Key는 도메인 제한으로 보호)
- JS SDK 공유는 사용자 기기에서 카카오톡 앱/웹으로 직접 공유됨 (서버 경유 안 함)
- 템플릿 링크의 도메인은 `https://www.work24.go.kr`로 고정 — 다른 채용 사이트 URL은 경로 변환 처리됨

---

## 8. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-22 | v1.0 | 최초 작성 — REST API + OAuth 2.0 방식 개발 계획 | SD |
| 2026-04-24 | v2.0 | 구현 완료 보고서로 전환 — Kakao JS SDK 방식으로 구현 완료, REST API 계획 폐기, 실제 구현 내용 기준으로 전면 재작성 | SD |
