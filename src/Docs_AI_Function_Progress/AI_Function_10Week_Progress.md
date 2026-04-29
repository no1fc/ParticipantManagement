# AI_Function_10Week_Progress.md — 10주차: 카카오톡 공유 대비 구조 정리 및 확장 포인트 분리

> **기간:** 2026-06-05 ~ 2026-06-12  
> **상태:** :white_check_mark: 완료 — Kakao JS SDK 방식으로 구현 완료  
> **참고:** `AI_Function_KakaoTalk_Share_Plan.md` — 최종 구현 보고서 (JS SDK 방식)  
> **결과:** 카카오톡 공유 기능이 Kakao JS SDK(`Kakao.Share.sendCustom`) 방식으로 구현 완료됨. 본 문서의 REST API 설계는 불필요해졌으나 참고용으로 보존.

---

## 1. 이번 주 목표

이번 주는 **기능 개발이 아닌 확장성 설계** 주차이다.  
카카오톡 공유 기능을 나중에 붙일 때 대규모 리팩토링 없이 추가할 수 있도록  
API 응답 구조, DB 설계, UI 레이아웃을 사전 정비한다.

---

## 2. 세부 작업 목록

### 10-1. 추천 결과 공유용 응답 구조 설계

#### 10-1-1. 현재 응답 구조 재검토

- [ ] 5주차에 확정한 JSON 응답 구조 재검토
  ```json
  {
    "success": true,
    "participant": { ... },
    "recommendList": [ ... ],
    "bestRecommend": { ... },
    "shareSummary": null
  }
  ```
  > `shareSummary`는 이번 주에 구현한다.  
  > `getRecommendDetailResponse()` 메서드(`ParticipantJobRecommendServiceImpl`)에서 현재 `null`로 반환 중임을 확인.

#### 10-1-2. `shareSummary` 필드 구현

> 카카오톡으로 공유할 때 전송할 요약 메시지 초안  
> `RecommendParticipantResponseDTO`에 `shareSummary` 필드가 이미 존재함 (`Object` 타입)

- [ ] `shareSummary` 생성 로직 추가 (ServiceImpl에서 조립)
  ```java
  // ParticipantJobRecommendServiceImpl — 실제 DTO 필드명 기준
  // ParticipantJobRecommendDTO 필드:
  //   getRecommendedJobCompany(), getRecommendedJobTitle(),
  //   getRecommendedJobUrl(), getRecommendationReason()
  // RecommendParticipantDTO 필드: getInfoName()
  private String buildShareSummary(
          RecommendParticipantDTO participantInfo,
          ParticipantJobRecommendDTO bestRecommend) {

      if (bestRecommend == null) return null;
      if (participantInfo == null) return null;

      StringBuilder sb = new StringBuilder();
      sb.append("[AI 추천 채용정보]\n");
      sb.append("이름: ").append(participantInfo.getInfoName()).append("\n");
      sb.append("추천 기업: ").append(bestRecommend.getRecommendedJobCompany()).append("\n");
      sb.append("채용 공고: ").append(bestRecommend.getRecommendedJobTitle()).append("\n");
      if (bestRecommend.getRecommendedJobUrl() != null) {
          sb.append("공고 링크: ").append(bestRecommend.getRecommendedJobUrl()).append("\n");
      }
      if (bestRecommend.getRecommendationReason() != null) {
          sb.append("추천 이유: ").append(bestRecommend.getRecommendationReason()).append("\n");
      }
      return sb.toString();
  }
  ```

- [ ] `getRecommendDetailResponse()`에서 `shareSummary` 포함하도록 수정
  ```java
  // ParticipantJobRecommendServiceImpl — getRecommendDetailResponse() 수정
  // 현재: response.put("shareSummary", null);  ← 이 부분을 교체
  ParticipantJobRecommendDTO bestRecommend = getBestRecommend(jobSeekerNo);
  RecommendParticipantDTO    participantInfo = getParticipantInfo(jobSeekerNo);

  // ... 기존 response 조립 로직 유지 ...

  response.put("shareSummary",
      buildShareSummary(participantInfo, bestRecommend));
  ```
  > `getRecommendDetailResponse()`는 `Map<String, Object>` 반환  
  > `RecommendParticipantResponseDTO.shareSummary`(Object 타입)는 별도 DTO 활용 시 참고

- [ ] `shareSummary` null 처리 확인
  - `bestRecommend == null` (`bestJobInfo = "1"` 레코드 없으면 null) → null 반환
  - `participantInfo == null` → null 반환

---

### 10-2. 공유용 API 엔드포인트 분리 설계

> 현재는 모달 조회 API에서 함께 반환하지만,  
> 공유 기능 추가 시 별도 API로 분리할 수 있도록 구조를 준비한다.

- [ ] 공유용 데이터 조회 API 엔드포인트 설계 (미구현, 자리만 확보)
  ```
  POST /counsel/participant/recommend/share-data.login
  Request: { jobSeekerNo: int }
  Response: {
    infoName,
    infoStage,
    bestRecommend: {
      recommendedJobCompany,
      recommendedJobTitle,
      recommendedJobUrl,
      recommendationReason
    },
    shareSummary: "공유용 텍스트",
    shareLink: null   ← 카카오톡 공유 시 단축 URL 생성 예정
  }
  ```
  > URL suffix: `.login` (LoginInterceptor 인증 필요)

- [ ] Controller에 엔드포인트 stub 메서드 추가 (TODO 주석 포함)
  ```java
  // TODO: 10주차 - 카카오톡 공유 기능 추가 시 구현
  // @RequestMapping(value = "/counsel/participant/recommend/share-data.login",
  //                 method = RequestMethod.POST)
  // @ResponseBody
  // public Map<String, Object> getShareData(
  //         @RequestParam("jobSeekerNo") int jobSeekerNo,
  //         HttpSession session) { ... }
  ```

---

### 10-3. DB `공유용요약` 컬럼 추가 검토

- [ ] `J_참여자관리_참여자추천채용정보` 테이블에 `공유용요약` 컬럼 추가 여부 결정
  - 옵션 A: 별도 컬럼 추가 (조회 시 즉시 사용 가능)
  - 옵션 B: 런타임에 조립 (컬럼 불필요, DB 변경 없음) ← 권장
- [ ] 결정 기준: 공유 기능이 확정된 이후에 컬럼 추가하는 것이 적절
- [ ] 현재는 런타임 조립 방식(옵션 B)으로 진행, `ParticipantJobRecommendDTO`에 컬럼 추가 불필요 확인

---

### 10-4. UI 카카오톡 공유 버튼 자리 확보

#### 10-4-1. 모달 하단 공유 영역 활성화

> 4주차에 `display:none`으로 자리만 잡아둔 영역

- [ ] 공유 영역에 placeholder 버튼 추가 (비활성 상태)
  ```html
  <div class="modal-share-area">
      <div class="share-info">
          <span class="share-label">추천 결과 공유</span>
          <span class="share-desc">베스트 채용정보를 참여자에게 공유할 수 있습니다.</span>
      </div>
      <!-- 카카오톡 공유 버튼 (추후 활성화) -->
      <button type="button" class="btn-kakao-share" disabled
              title="카카오톡 공유 기능 준비 중">
          <img src="${pageContext.request.contextPath}/img/kakao-icon.png"
               alt="카카오톡" style="width:20px; vertical-align:middle;">
          카카오톡으로 공유 (준비 중)
      </button>
      <!-- 링크 복사 버튼 (추후 활성화) -->
      <button type="button" class="btn-copy-link" disabled
              title="링크 복사 기능 준비 중">
          링크 복사 (준비 중)
      </button>
  </div>
  ```
- [ ] 공유 영역 `display:none` 제거하여 UI에 노출 (비활성 버튼으로 향후 기능 예고)
- [ ] 비활성 버튼 스타일 (회색 처리)
  ```css
  .btn-kakao-share:disabled,
  .btn-copy-link:disabled {
      opacity: 0.4;
      cursor: not-allowed;
  }
  ```

#### 10-4-2. `shareSummary` 미리보기 영역

- [ ] 베스트 추천이 있을 때 공유 텍스트 미리보기 표시
  ```html
  <div id="shareSummaryPreview" class="share-summary-preview" style="display:none;">
      <label>공유 메시지 미리보기</label>
      <pre id="shareSummaryText"></pre>
  </div>
  ```
  ```javascript
  // bindBestRecommend 함수 내 추가 — response.shareSummary 참조
  if (response.shareSummary) {
      document.getElementById('shareSummaryText').innerText = response.shareSummary;
      document.getElementById('shareSummaryPreview').style.display = 'block';
  } else {
      document.getElementById('shareSummaryPreview').style.display = 'none';
  }
  ```

---

### 10-5. 확장 포인트 문서화

- [ ] `GeminiApiService` 클래스 상단에 확장 포인트 주석 추가
  ```java
  /**
   * Google Gen AI SDK 기반 Gemini API 연동 서비스
   *
   * [확장 포인트]
   * - AI 모델 교체 시: application.properties의 gemini.api.model 값만 변경
   *   (RestTemplate URL 변경 불필요 — SDK가 내부 처리)
   * - 다른 AI API 연동 시: 이 클래스를 인터페이스로 추상화하고 구현체 교체 방식 권장
   *   예) AiApiService 인터페이스 + GeminiApiServiceImpl
   * - 카카오톡 공유 시: ParticipantJobRecommendServiceImpl.buildShareSummary() 결과를 공유 API에 전달
   * - SDK 업그레이드 시: pom.xml의 google-genai 버전 변경 (현재 1.47.0)
   */
  ```

- [ ] `ParticipantJobRecommendService` 인터페이스에 공유 관련 메서드 stub 추가
  ```java
  // TODO: 카카오톡 공유 기능 추가 시 구현
  // String generateShareSummary(int jobSeekerNo);
  // Map<String, Object> getShareData(int jobSeekerNo);
  ```

- [ ] Controller에 공유 API 자리 stub 확인 (10-2에서 추가한 주석 확인)

---

### 10-6. 확장 설계안 문서 작성

- [ ] `src/DocsServiceDesign/` 에 카카오톡 공유 확장 설계 초안 작성
  - 공유 기능 추가 시 변경 파일 예상 목록
    - `ParticipantJobRecommendService.java` — `getShareData()` 메서드 추가
    - `ParticipantJobRecommendServiceImpl.java` — 구현
    - Controller — stub 메서드 활성화
    - JSP/JS — 공유 버튼 활성화, 카카오 SDK 로드
  - 카카오 SDK 연동 방식 (JavaScript SDK 기준)
  - 공유 시 전달 데이터 구조 (`shareSummary` 활용)
  - 단축 URL 생성 여부 결정 포인트

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| `shareSummary` 구현 | 공유용 요약 텍스트 생성 및 API 응답 포함 | 🔲 |
| UI 공유 영역 | 비활성 공유 버튼 + 미리보기 노출 | 🔲 |
| 확장 포인트 주석 | 코드 내 TODO/확장 포인트 주석 | 🔲 |
| 확장 설계 문서 | 카카오톡 공유 기능 추가 시나리오 초안 | 🔲 |

---

## 4. 확장성 검증 체크리스트

- [ ] Gemini API 모델 교체 시 `application.properties`의 `gemini.api.model` 변경만으로 가능한지 확인
- [ ] 공유 기능 추가 시 기존 추천 저장/조회 로직 수정 없이 가능한지 확인
- [ ] `shareSummary` 응답이 모달에 표시되는지 확인
- [ ] 비활성 공유 버튼이 클릭 불가 상태로 표시되는지 확인

---

## 5. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 | SD |
| 2026-04-13 | v0.2 | `buildShareSummary` 파라미터·필드명을 실제 DTO 기준으로 수정(`ParticipantBean`→`RecommendParticipantDTO`, 한글 getter → Lombok 영문 getter); 공유 API URL suffix `.do` → `.login`으로 수정; `GeminiApiService` 확장 포인트 주석에서 `apiUrl` 제거; 인터페이스 stub 메서드 파라미터 `String` → `int jobSeekerNo`로 수정 | SD |
| 2026-04-13 | v0.3 | 실제 코드 기반 재검토: `RecommendParticipantResponseDTO`에 `shareSummary(Object)` 필드 이미 존재 확인; `bestRecommend` null 판단 기준 `bestJobInfo = "1"` 레코드로 명시; `getRecommendDetailResponse()` 현재 `shareSummary: null` 부분 위치 명시 | SD |
| 2026-04-24 | v0.4 | 카카오톡 공유 기능 Kakao JS SDK 방식으로 구현 완료 — REST API 설계 폐기, 본 문서는 참고용 보존 | SD |