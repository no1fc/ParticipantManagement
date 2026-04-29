# AI_Function_7Week_Progress.md — 7주차: 추천 결과 UI 고도화, 예외 처리, 성능 보완

> **기간:** 2026-05-15 ~ 2026-05-22  
> **상태:** ✅ 완료  
> **목표:** 추천 결과 출력 품질 개선, 모든 예외 케이스 처리, 성능 최적화

---

## 1. 이번 주 목표

6주차에 구현한 AI 추천 기능의 품질을 높인다.  
추천 결과 UI를 상담사가 실무에서 바로 활용할 수 있는 수준으로 개선하고,  
Gemini API 실패, 데이터 없음, 저장 실패 등 모든 예외 케이스를 처리한다.

---

## 2. 세부 작업 목록

### 7-1. 추천 결과 UI 고도화

#### 7-1-1. 모달 추천 리스트 UI 개선

- [x] 추천 사유 출력 영역 개선
  - 긴 텍스트 처리: 접기/펼치기 버튼 추가
  ```html
  <td class="reason-cell">
      <span class="reason-short"></span>
      <a href="#" class="btn-reason-toggle" onclick="toggleReason(this); return false;">더보기</a>
      <span class="reason-full" style="display:none;"></span>
  </td>
  ```
  ```javascript
  function toggleReason(btn) {
      let cell = btn.parentElement;
      let short = cell.querySelector('.reason-short');
      let full  = cell.querySelector('.reason-full');
      if (full.style.display === 'none') {
          short.style.display = 'none';
          full.style.display  = 'inline';
          btn.innerText = '접기';
      } else {
          short.style.display = 'inline';
          full.style.display  = 'none';
          btn.innerText = '더보기';
      }
  }
  ```
  ```javascript
  // bindRecommendList 내 수정 — DTO 필드명 기준 (recommendationReason)
  var reason = item.recommendationReason || '';
  var reasonShort = reason.length > 50 ? reason.substring(0, 50) + '...' : (reason || '-');
  var reasonFull  = reason;
  ```

- [x] 추천 점수 시각화 개선 (점수 바 표시)
  ```html
  <td>
      <div class="score-bar-wrap">
          <div class="score-bar" style="width: ${item.recommendationScore}%"></div>
          <span class="score-text">${item.recommendationScore}점</span>
      </div>
  </td>
  ```
  ```css
  .score-bar-wrap {
      width: 80px;
      background: #eee;
      border-radius: 4px;
      position: relative;
      height: 18px;
  }
  .score-bar {
      background: #27ae60;
      height: 100%;
      border-radius: 4px;
  }
  .score-text {
      position: absolute;
      top: 0; left: 0; right: 0;
      text-align: center;
      font-size: 11px;
      line-height: 18px;
      color: #333;
  }
  ```

- [x] 채용공고 링크 클릭 시 새 탭으로 열기 (보안 속성 추가)
  ```html
  <a href="${url}" target="_blank" rel="noopener noreferrer">공고 보기</a>
  ```

#### 7-1-2. 베스트 채용정보 카드 개선

- [ ] 베스트 카드에 추천 근거 주요 포인트 표시 (`recommendationReason` 필드 활용)
- [ ] 베스트 채용정보 추천 일시 표시 (`createdAt` 필드 활용)
- [x] 베스트 배지(★) 표시 추가
  ```html
  <div class="best-badge">★ 베스트 추천</div>
  ```

#### 7-1-3. 추천 결과 없음 상태 개선

- [x] 빈 상태 UI 개선
  ```html
  <div id="recommendListEmpty" class="empty-state">
      <p>아직 저장된 추천 채용정보가 없습니다.</p>
      <p class="empty-sub">
          AI 추천 저장 버튼을 클릭하면<br>참여자 조건에 맞는 채용정보를 자동으로 추천합니다.
      </p>
  </div>
  ```

---

### 7-2. 예외 처리 강화

#### 7-2-1. Gemini API 실패 예외 처리

- [x] `GeminiApiService`에 재시도 로직 추가 (최대 1회 재시도)
  ```java
  // GeminiApiService — callGeminiApi()를 내부적으로 감싸는 재시도 래퍼
  String callGeminiApiWithRetry(String prompt) {
      int maxRetry = 1;
      for (int i = 0; i <= maxRetry; i++) {
          try {
              return callGeminiApi(prompt);
          } catch (Exception e) {
              if (i == maxRetry) {
                  throw new RuntimeException("Gemini API 최종 실패: " + e.getMessage(), e);
              }
          }
      }
      return null;
  }
  ```
  > `generateSearchCondition()` 및 `selectBestFromCandidates()` 내부에서  
  > `callGeminiApi()` → `callGeminiApiWithRetry()`로 교체  
  > SDK(`geminiClient.models.generateContent()`) 호출이 내부에 있음 — RestTemplate 아님

- [x] Gemini 응답 JSON 파싱 실패 시 처리
  ```java
  // GeminiApiService — parseSearchConditionResponse
      private SearchConditionDTO parseSearchConditionResponse(String responseText) {
        try {
            return objectMapper.readValue(extractJsonFromResponse(responseText), SearchConditionDTO.class);
        } catch (Exception e) {
            SearchConditionDTO fallback = new SearchConditionDTO();
            fallback.setKeywords(Collections.emptyList());
            fallback.setParseError(true);
            return fallback;
        }
    }
  ```

- [x] ServiceImpl에서 Gemini 실패 시 사용자 메시지 처리
  ```java
  // ParticipantJobRecommendServiceImpl — processAndSaveRecommend 예외 분기
  } catch (Exception e) {
      result.put("success", false);
      String msg = (e.getMessage() != null && e.getMessage().contains("Gemini"))
          ? "AI 추천 서비스가 일시적으로 응답하지 않습니다. 잠시 후 다시 시도해주세요."
          : "추천 저장 중 오류가 발생했습니다.";
      result.put("message", msg);
      return result;
  }
  ```

#### 7-2-2. 채용정보 후보군 0건 처리

- [x] 후보군이 0건일 때 저장 건너뛰고 안내 메시지 반환 (6주차 구현 확인)
  ```java
  if (candidates.isEmpty()) {
      result.put("success", true);
      result.put("savedCount", 0);
      result.put("message", "조건에 맞는 채용정보를 찾지 못했습니다.");
      return result;
  }
  ```

#### 7-2-3. 참여자 데이터 불완전 시 처리

- [x] 필수 필드 null-safe 처리 메서드 확인 (`GeminiApiService`의 `nullSafe()` 메서드)
- [x] 학력/전공/카테고리/희망직무 중 2개 이상 null이면 추천 불가 처리
  ```java
  // GeminiApiService — 실제 DTO 파라미터 기준
  // RecommendParticipantDTO: getInfoEducation(), getInfoMajor()
  // RecommendCategoryDTO: getCategoryMain(), getInfoJob()
  // RecommendCategoryDTO.infoRank 는 String 타입
  public boolean hasEnoughInfo(
          RecommendParticipantDTO participant,
          List<RecommendCategoryDTO> categoryList) {
      int count = 0;
      if (participant.getInfoEducation() != null
              && !participant.getInfoEducation().isEmpty()) count++;
      if (participant.getInfoMajor() != null
              && !participant.getInfoMajor().isEmpty()) count++;
      if (categoryList != null && !categoryList.isEmpty()) count++;
      if (categoryList != null && !categoryList.isEmpty()
              && categoryList.get(0).getInfoJob() != null
              && !categoryList.get(0).getInfoJob().isEmpty()) count++;
      return count >= 2;
  }
  ```
  > `processAndSaveRecommend` 내 Gemini 호출 전에 `hasEnoughInfo()` 검사 추가:
  ```java
  if (!geminiApiService.hasEnoughInfo(participant, categoryList)) {
      result.put("success", false);
      result.put("message", "참여자 기본 정보가 부족하여 추천을 진행할 수 없습니다.");
      return result;
  }
  ```

#### 7-2-4. DB 저장 실패 시 처리

- [x] 전체 저장이 AOP 트랜잭션으로 묶이는지 확인
  - `RootConfig.java` AOP pointcut: `execution(* com.jobmoa.app.CounselMain.biz..*.*Impl.*(..))`
  - `processAndSaveRecommend`는 `CounselMain.biz.recommend.*Impl`에 해당 → 자동 적용 확인
  - `select*` 아닌 메서드는 `PROPAGATION_REQUIRED`로 처리됨

#### 7-2-5. JS 레이어 예외 처리

- [x] AJAX 타임아웃 설정 추가 (6주차 JS 수정 확인)
  ```javascript
  $.ajax({
      url: '/counsel/participant/recommend/save.login',
      type: 'POST',
      timeout: 30000,  // 30초 타임아웃
      error: function(xhr, status, error) {
          var msg = status === 'timeout'
              ? 'AI 처리 시간이 초과되었습니다. 다시 시도해주세요.'
              : '서버 오류가 발생했습니다.';
          document.getElementById('btnAiRecommend').disabled = false;
          document.getElementById('recommendStatusMsg').innerText = msg;
      }
  });
  ```
- [x] 버튼 중복 클릭 방지 (저장 중 버튼 비활성화 유지 확인)

---

### 7-3. 성능 보완

#### 7-3-1. 후보군 최대 건수 제한

- [x] `application.properties`에 최대 건수 설정 확인 (6주차 추가 항목)
  ```properties
  gemini.recommend.max-candidates=20
  ```
- [x] ServiceImpl에서 설정값 주입 확인 (6주차 구현 확인)
  ```java
  @Value("${gemini.recommend.max-candidates:20}")
  private int maxCandidates;
  ```
- [x] `searchCondition.put("maxCount", maxCandidates)` 호출 및 SQL `TOP #{maxCount}` 동작 확인

#### 7-3-2. 기저장 추천 결과 재사용 로직

- [x] DAO에 `selectLatestRecommend` 메서드 추가 (재사용 판단용)
  ```java
  // ParticipantJobRecommendDAO
  public ParticipantJobRecommendDTO selectLatestRecommend(int jobSeekerNo) {
      return sqlSession.selectOne(
          "ParticipantJobRecommendDAO.selectLatestRecommend", jobSeekerNo);
  }
  ```
  ```xml
  <!-- ParticipantJobRecommend-mapping.xml 에 추가 -->
  <!-- resultType: sql-map-config.xml 등록 alias "jobRecommend" 사용 -->
  <!-- alias 주의: 베스트채용정보 AS bestJobInfo (bestJobRecommend 아님) -->
  <select id="selectLatestRecommend" parameterType="int" resultType="jobRecommend">
      SELECT TOP 1
          PK                          AS pk,
          구직번호                     AS jobSeekerNo,
          베스트채용정보               AS bestJobInfo,
          추천점수                     AS recommendationScore,
          추천사유                     AS recommendationReason,
          저장일시                     AS createdAt,
          수정일시                     AS updatedAt
      FROM J_참여자관리_참여자추천채용정보
      WHERE 구직번호 = #{jobSeekerNo}
      ORDER BY 저장일시 DESC
  </select>
  ```
  > `resultType="jobRecommend"` — `sql-map-config.xml`에 등록된 alias  
  > alias는 `bestJobInfo` 사용 (`bestJobRecommend` 아님 — 6주차 XML 버그 수정 이후 기준)

- [x] 저장 후 24시간 이내 재요청 시 기존 결과 반환 옵션 검토
  ```java
  // processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh) 내 로직
  // forceRefresh = false 이고 최근 저장 24h 이내면 재사용
  if (!forceRefresh) {
      ParticipantJobRecommendDTO latest =
          participantJobRecommendDAO.selectLatestRecommend(jobSeekerNo);
      // ParticipantJobRecommendDTO.createdAt → String 타입 (DB: 저장일시)
      if (latest != null && latest.getCreatedAt() != null) {
          try {
              LocalDateTime savedAt = LocalDateTime.parse(
                  latest.getCreatedAt(),
                  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
              long hoursDiff = ChronoUnit.HOURS.between(savedAt, LocalDateTime.now());
              if (hoursDiff < 24) {
                  result.put("success", true);
                  result.put("reused", true);
                  result.put("message", "최근 24시간 이내 저장된 추천 결과를 사용합니다.");
                  return result;
              }
          } catch (Exception ignored) {}
      }
  }
  ```
  > `createdAt` 필드는 `ParticipantJobRecommendDTO`의 `String` 타입  
  > `DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")` — MSSQL GETDATE() 반환 형식

- [x] `processAndSaveRecommend(int, boolean)` 오버로딩 — 6주차 Service 인터페이스에 이미 선언됨 확인
  ```java
  // ParticipantJobRecommendService 인터페이스
  Map<String, Object> processAndSaveRecommend(int jobSeekerNo);
  Map<String, Object> processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh);
  ```
  - Controller: `@RequestParam(value = "forceRefresh", defaultValue = "false") boolean forceRefresh`

#### 7-3-3. 모달 응답 속도 측정

- [x] 모달 API 호출 응답 시간 로그 추가
  ```java
  // Controller 또는 ServiceImpl에서 시간 측정
  long startTime = System.currentTimeMillis();
  // ... 처리 로직 ...
  long elapsed = System.currentTimeMillis() - startTime;
  log.info("[추천모달] jobSeekerNo={}, 응답시간={}ms", jobSeekerNo, elapsed);
  ```
- [ ] 3초 이상 소요 시 개선 대상으로 표시 (`log.warn` 사용)
  ```java
  if (elapsed > 3000) {
      log.warn("[추천모달] 응답 지연 jobSeekerNo={}, {}ms", jobSeekerNo, elapsed);
  }
  ```

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| UI 개선 | 추천사유 접기/펼치기, 점수 바, 베스트 배지 | 🔲 |
| 예외 처리 | Gemini 실패, 후보군 0건, 데이터 불완전, 저장 실패 | 🔲 |
| 성능 보완 | 후보군 건수 제한, 재사용 로직, 응답 시간 로그 | 🔲 |

---

## 4. 예외 케이스 시나리오 테스트

| 시나리오 | 예상 동작 | 확인 여부 |
|---------|-----------|----------|
| Gemini API Key 만료 | "AI 서비스 일시 불가" 메시지 | 🔲 |
| Gemini 응답 JSON 파싱 실패 | 기본 빈 조건으로 후보군 조회 시도 | 🔲 |
| 채용정보 후보군 0건 | "조건에 맞는 채용정보 없음" 메시지, 저장 안 함 | 🔲 |
| 참여자 기본정보 2개 미만 | "정보 부족으로 추천 불가" 메시지 | 🔲 |
| DB 저장 실패 | 전체 롤백, 오류 메시지 | 🔲 |
| AJAX 30초 타임아웃 | "시간 초과" 메시지, 버튼 재활성화 | 🔲 |
| 모달 중복 클릭 | 두 번째 클릭 무시 | 🔲 |

---

## 5. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 | SD |
| 2026-04-13 | v0.2 | `ParticipantBean` → `RecommendParticipantDTO + List<RecommendCategoryDTO>`로 수정; `hasEnoughInfo` 실제 DTO 필드 기준 재작성; `selectLatestRecommend` 재사용 로직에서 `createdAt` String → `LocalDateTime.parse()` 변환; JS 필드명 `추천사유` → `recommendationReason`, `추천점수` → `recommendationScore` | SD |
| 2026-04-13 | v0.3 | 실제 코드 기반 재검토: `selectLatestRecommend` XML alias `bestJobInfo` 명시(6주차 수정 기준); `hasEnoughInfo` public 접근 제어자로 변경; `RecommendCategoryDTO.infoRank` String 타입 주석; `forceRefresh` 파라미터가 6주차 인터페이스에 이미 정의됨 교차 확인 | SD |