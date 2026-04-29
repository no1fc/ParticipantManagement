# AI_Function_6Week_Progress.md — 6주차: Gemini API 연동 및 추천 저장 로직 1차 구현

> **기간:** 2026-05-08 ~ 2026-05-15  
> **상태:** ✅ 완료  
> **목표:** Gemini API 호출 서비스 구현, 검색 조건 생성 로직, 알선상세정보 유무 분기, 추천 저장 API 완성

---

## 1. 이번 주 목표

AI 추천 기능의 핵심 로직을 구현한다.  
`com.google.genai:google-genai` SDK를 이용해 검색 조건을 생성하고,  
알선상세정보 유무에 따라 분기 처리 후 추천 결과를 DB에 저장한다.  
추천 저장 버튼 클릭부터 DB 저장까지 전체 흐름을 완성한다.

---

## 2. 세부 작업 목록

### 6-1. pom.xml 의존성 확인

- [x] `pom.xml`에 Gen AI SDK 의존성 추가 완료
  ```xml
  <dependency>
      <groupId>com.google.genai</groupId>
      <artifactId>google-genai</artifactId>
      <version>1.47.0</version>
  </dependency>
  ```
- [x] `jackson-databind` 포함 확인 (이미 포함됨)
- [x] `RestTemplate` Bean: `RootConfig.java`에 이미 등록됨

> **주의:** Gen AI SDK는 자체 HTTP 클라이언트를 사용한다.  
> Gemini API 호출에 `RestTemplate`을 사용하지 않는다.

---

### 6-2. application.properties에 Gemini 설정 추가

- [x] Gemini API Key 및 모델 설정 추가
  ```properties
  # Gemini API 설정
  gemini.api.key=YOUR_GEMINI_API_KEY
  gemini.api.model=gemini-2.0-flash
  # 채용정보 후보군 최대 조회 건수 (Gemini 응답 후보 수와 무관)
  gemini.recommend.max-candidates=20
  ```

> **설정 주의 사항:**
> - `gemini.api.url` 불필요 — Gen AI SDK가 URL 내부 처리
> - `gemini.api.max-candidates` 사용 금지 — Gemini `candidateCount`와 혼동 (비용 배수 증가)
> - 채용공고 검색 최대 건수는 `gemini.recommend.max-candidates`로만 관리

---

### 6-3. 기존 XML 버그 수정 — 우선 처리 필수

> **수정 파일:** `src/main/resources/mappings/ParticipantJobRecommend-mapping.xml`

- [x] `selectRecommendList` 및 `selectBestRecommend` 쿼리의 alias 오류 수정
  ```xml
  <!-- 변경 전 (버그): DTO 필드 bestJobInfo 와 불일치 → SELECT 결과 항상 null -->
  베스트채용정보 AS bestJobRecommend,

  <!-- 변경 후 (수정): DTO 필드명과 일치 -->
  베스트채용정보 AS bestJobInfo,
  ```
  > `ParticipantJobRecommendDTO` 필드: `private String bestJobInfo`  
  > `selectRecommendList`와 `selectBestRecommend` 두 쿼리 모두 수정 필요

- [x] `selectBestRecommend` WHERE 조건 확인
  ```xml
  <!-- 현재 조건 -->
  베스트채용정보 = 1
  ```
  > DB 컬럼 타입이 varchar인 경우: `"1"`/`"0"` 저장 시 MSSQL 묵시적 변환으로 동작함  
  > **결론:** `bestJobInfo`에는 반드시 `"1"` 또는 `"0"` (String)을 저장해야 함  
  > `"Y"`/`"N"` 사용 시 `selectBestRecommend` 조건 불일치로 베스트 조회 실패

---

### 6-4. RootConfig.java에 Gemini Client Bean 추가

> **수정 파일:** `src/main/java/com/jobmoa/app/config/RootConfig.java`

- [x] `@Value` 필드 및 `Client` Bean 추가
  ```java
  @Value("${gemini.api.key}")
  private String geminiApiKey;

  /**
   * Google Gen AI Client Bean
   * SDK 방식 사용 — RestTemplate 방식 아님
   */
  @Bean
  public Client geminiClient() {
      return new Client.Builder()
          .apiKey(geminiApiKey)
          .build();
  }
  ```
  > import: `com.google.genai.Client`

---

### 6-5. GeminiApiService 구현

> **신규 파일:** `CounselMain/biz/recommend/GeminiApiService.java`

#### 6-5-1. 클래스 기본 구조

```java
@Service
public class GeminiApiService {

    @Value("${gemini.api.model:gemini-2.0-flash}")
    private String modelName;

    @Autowired
    private Client geminiClient;        // Gen AI SDK Client — RestTemplate 사용 안 함

    @Autowired
    private ObjectMapper objectMapper;
}
```

#### 6-5-2. 검색 조건 생성 메서드 (1단계)

- [x] 실제 DTO 파라미터 기반으로 구현
  ```java
  public Map<String, Object> generateSearchCondition(
          RecommendParticipantDTO participant,
          List<RecommendCategoryDTO> categoryList) {
      String prompt = buildSearchConditionPrompt(participant, categoryList);
      String response = callGeminiApi(prompt);
      return parseSearchConditionResponse(response);
  }

  private String buildSearchConditionPrompt(
          RecommendParticipantDTO p,
          List<RecommendCategoryDTO> categoryList) {

      // 카테고리 희망순위 1순위 기준 (infoRank가 String 타입)
      String categoryMain   = "정보없음";
      String categoryMiddle = "정보없음";
      String desiredJob     = "정보없음";
      if (categoryList != null && !categoryList.isEmpty()) {
          RecommendCategoryDTO primary = categoryList.get(0);
          categoryMain   = nullSafe(primary.getCategoryMain());
          categoryMiddle = nullSafe(primary.getCategoryMiddle());
          desiredJob     = nullSafe(primary.getInfoJob());
      }

      return "다음 구직자 정보를 기반으로 채용정보 DB에서 검색할 조건을 JSON 형식으로 생성해 주세요.\n"
           + "구직자 정보:\n"
           + "- 학력: "            + nullSafe(p.getInfoEducation()) + "\n"
           + "- 전공: "            + nullSafe(p.getInfoMajor())     + "\n"
           + "- 카테고리 대분류: " + categoryMain                   + "\n"
           + "- 카테고리 중분류: " + categoryMiddle                 + "\n"
           + "- 희망직무: "        + desiredJob                     + "\n\n"
           + "응답 형식 (JSON만 출력):\n"
           + "{\n"
           + "  \"keywords\": [\"키워드1\", \"키워드2\"],\n"
           + "  \"jobCategory\": \"직무분류\",\n"
           + "  \"educationLevel\": \"학력조건\",\n"
           + "  \"industryType\": \"업종분류\"\n"
           + "}";
  }

  private String nullSafe(String value) {
      return (value != null && !value.trim().isEmpty()) ? value : "정보없음";
  }
  ```

#### 6-5-3. 후보군 정밀 판단 메서드 (2단계 — 알선상세정보 있는 경우)

- [x] SQL 조회 컬럼명(`채용제목`)과 일치하도록 작성
  ```java
  public Map<String, Object> selectBestFromCandidates(
          List<Map<String, Object>> candidates,
          String alsonDetail) {
      String prompt = buildBestSelectionPrompt(candidates, alsonDetail);
      String response = callGeminiApi(prompt);
      return parseBestSelectionResponse(response);
  }

  private String buildBestSelectionPrompt(
          List<Map<String, Object>> candidates, String alsonDetail) {
      StringBuilder sb = new StringBuilder();
      sb.append("다음 알선상세정보와 채용공고 후보군을 비교하여 가장 적합한 채용공고를 선정하고 각 점수를 매겨주세요.\n\n");
      sb.append("알선상세정보:\n").append(alsonDetail).append("\n\n");
      sb.append("채용공고 후보군:\n");
      for (int i = 0; i < candidates.size(); i++) {
          Map<String, Object> c = candidates.get(i);
          sb.append((i + 1)).append(". 기업명: ").append(c.get("기업명"))
            .append(", 제목: ").append(c.get("채용제목"))   // SQL 컬럼명: 채용제목
            .append(", 업종: ").append(c.get("업종"))
            .append(", 구인인증번호: ").append(c.get("구인인증번호")).append("\n");
      }
      sb.append("\n응답 형식 (JSON만 출력):\n")
        .append("{\n")
        .append("  \"bestGujinNo\": \"최적채용공고구인인증번호\",\n")
        .append("  \"scores\": [\n")
        .append("    {\"구인인증번호\": \"...\", \"score\": 85, \"reason\": \"추천사유\"}\n")
        .append("  ]\n")
        .append("}");
      return sb.toString();
  }
  ```

#### 6-5-4. Gemini API 호출 메서드 (SDK 방식)

- [x] RestTemplate 아닌 Gen AI SDK 사용
  ```java
  private String callGeminiApi(String prompt) {
      try {
          GenerateContentResponse response = geminiClient.models.generateContent(
              modelName,
              Content.fromParts(Part.fromText(prompt)),
              null
          );
          return response.text();
      } catch (Exception e) {
          throw new RuntimeException("Gemini API 호출 실패: " + e.getMessage(), e);
      }
  }
  ```
  > import: `com.google.genai.types.Content`, `com.google.genai.types.Part`, `com.google.genai.types.GenerateContentResponse`

- [x] 응답 JSON 추출 (Gemini 마크다운 코드블록 제거)
  ```java
  private String extractJsonFromResponse(String text) {
      if (text.contains("```json")) {
          text = text.substring(text.indexOf("```json") + 7);
          text = text.substring(0, text.lastIndexOf("```")).trim();
      } else if (text.contains("```")) {
          text = text.substring(text.indexOf("```") + 3);
          text = text.substring(0, text.lastIndexOf("```")).trim();
      }
      return text.trim();
  }

  private Map<String, Object> parseSearchConditionResponse(String responseText) {
      try {
          return objectMapper.readValue(extractJsonFromResponse(responseText), Map.class);
      } catch (Exception e) {
          Map<String, Object> fallback = new HashMap<>();
          fallback.put("keywords", Collections.emptyList());
          fallback.put("parseError", true);
          return fallback;
      }
  }

  private Map<String, Object> parseBestSelectionResponse(String responseText) {
      try {
          return objectMapper.readValue(extractJsonFromResponse(responseText), Map.class);
      } catch (Exception e) {
          return new HashMap<>();
      }
  }
  ```

---

### 6-6. Service 인터페이스에 메서드 추가

> **수정 파일:** `CounselMain/biz/recommend/ParticipantJobRecommendService.java`

- [x] `processAndSaveRecommend` 메서드 인터페이스 선언 추가
  ```java
  public interface ParticipantJobRecommendService {
      // ... 기존 메서드 유지 ...

      // AI 추천 저장 처리 (Gemini 호출 + DB 저장 전체 흐름)
      Map<String, Object> processAndSaveRecommend(int jobSeekerNo);
      Map<String, Object> processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh);
  }
  ```

---

### 6-7. DAO 및 XML에 채용정보 후보군 조회 추가

> **수정 파일:** `CounselMain/biz/recommend/ParticipantJobRecommendDAO.java`  
> **수정 파일:** `mappings/ParticipantJobRecommend-mapping.xml`

- [x] DAO 메서드 추가
  ```java
  // 채용정보 후보군 조회 (Gemini 생성 검색 조건 기반)
  public List<Map<String, Object>> selectJobInfoCandidates(Map<String, Object> searchCondition) {
      return sqlSession.selectList(ns + "selectJobInfoCandidates", searchCondition);
  }
  ```

- [x] XML 쿼리 추가 (`ParticipantJobRecommend-mapping.xml` 하단에 추가)
  ```xml
  <!-- 채용정보 후보군 조회 -->
  <!-- searchCondition에 maxCount 반드시 포함: searchCondition.put("maxCount", maxCandidates) -->
  <select id="selectJobInfoCandidates" parameterType="map" resultType="map">
      SELECT TOP #{maxCount}
          구인인증번호,
          기업명,
          채용제목,
          업종,
          공고URL
      FROM [J_참여자관리_JOB_POSTING]    <!-- TODO: 실제 테이블명으로 교체 필요 -->
      WHERE 마감여부 = 'N'
      <if test="keywords != null and keywords.size() > 0">
          AND (
              채용제목 LIKE '%' + #{keywords[0]} + '%'
              OR 업종   LIKE '%' + #{keywords[0]} + '%'
          )
      </if>
      <if test="jobCategory != null and jobCategory != ''">
          AND 직무분류 LIKE '%' + #{jobCategory} + '%'
      </if>
      ORDER BY 등록일시 DESC
  </select>
  ```

---

### 6-8. ParticipantJobRecommendServiceImpl — processAndSaveRecommend 구현

> **수정 파일:** `CounselMain/biz/recommend/ParticipantJobRecommendServiceImpl.java`

#### 전체 흐름
```
참여자 정보 조회 (DAO: getParticipantInfo / getParticipantCategory / getParticipantReferral)
    ↓
[1단계] Gemini → generateSearchCondition (항상 호출)
    ↓
searchCondition에 maxCount 추가 후 selectJobInfoCandidates 조회
    ↓
후보군 0건? → 바로 반환 (저장 없음)
    ↓
기존 추천 삭제: deleteRecommendByGujikNo
    ↓
알선상세정보(infoAlsonDetail) 있음?
    → Yes → [2단계] Gemini → selectBestFromCandidates → bestJobInfo = "1"/"0" 설정
    → No  → bestJobInfo = "0" 으로 모두 저장
    ↓
insertOrUpdateRecommend (MERGE UPSERT)
```

- [x] 클래스 상단에 필드 추가
  ```java
  @Autowired
  private GeminiApiService geminiApiService;

  @Value("${gemini.recommend.max-candidates:20}")
  private int maxCandidates;
  ```

- [x] `processAndSaveRecommend` 구현
  ```java
  @Override
  public Map<String, Object> processAndSaveRecommend(int jobSeekerNo) {
      return processAndSaveRecommend(jobSeekerNo, false);
  }

  @Override
  public Map<String, Object> processAndSaveRecommend(int jobSeekerNo, boolean forceRefresh) {
      Map<String, Object> result = new HashMap<>();

      // 1. 참여자 정보 조회 — 실제 DAO 메서드 사용
      RecommendParticipantDTO participant =
          participantJobRecommendDAO.getParticipantInfo(jobSeekerNo);
      if (participant == null) {
          result.put("success", false);
          result.put("message", "참여자 정보를 찾을 수 없습니다.");
          return result;
      }
      List<RecommendCategoryDTO> categoryList =
          participantJobRecommendDAO.getParticipantCategory(jobSeekerNo);
      RecommendReferralDTO referralInfo =
          participantJobRecommendDAO.getParticipantReferral(jobSeekerNo);

      // 2. Gemini 1단계: 검색 조건 생성
      Map<String, Object> searchCondition;
      try {
          searchCondition = geminiApiService.generateSearchCondition(participant, categoryList);
      } catch (Exception e) {
          result.put("success", false);
          result.put("message", "AI 추천 서비스가 일시적으로 응답하지 않습니다. 잠시 후 다시 시도해주세요.");
          return result;
      }

      // 3. 채용정보 후보군 조회 — maxCount 반드시 추가
      searchCondition.put("maxCount", maxCandidates);
      List<Map<String, Object>> candidates =
          participantJobRecommendDAO.selectJobInfoCandidates(searchCondition);

      if (candidates.isEmpty()) {
          result.put("success", true);
          result.put("savedCount", 0);
          result.put("message", "조건에 맞는 채용정보를 찾지 못했습니다.");
          return result;
      }

      // 4. 기존 추천 삭제 후 재저장
      participantJobRecommendDAO.deleteRecommendByGujikNo(jobSeekerNo);

      // 5. 알선상세정보 유무에 따라 분기
      String alsonDetail = (referralInfo != null) ? referralInfo.getInfoAlsonDetail() : null;
      boolean hasAlsonDetail = (alsonDetail != null && !alsonDetail.trim().isEmpty());

      if (hasAlsonDetail) {
          saveWithGeminiJudgment(participant, categoryList, referralInfo,
                                  candidates, alsonDetail, searchCondition);
      } else {
          saveWithoutGeminiJudgment(participant, categoryList, referralInfo,
                                     candidates, searchCondition);
      }

      result.put("success", true);
      result.put("savedCount", candidates.size());
      return result;
  }
  ```

- [x] 알선상세정보 있는 경우 저장 — `bestJobInfo = "1"` or `"0"`
  ```java
  private void saveWithGeminiJudgment(
          RecommendParticipantDTO participant,
          List<RecommendCategoryDTO> categoryList,
          RecommendReferralDTO referralInfo,
          List<Map<String, Object>> candidates,
          String alsonDetail,
          Map<String, Object> searchCondition) {

      Map<String, Object> judgment =
          geminiApiService.selectBestFromCandidates(candidates, alsonDetail);
      String bestGujinNo = (String) judgment.get("bestGujinNo");

      List<Map<String, Object>> scores =
          (List<Map<String, Object>>) judgment.get("scores");
      Map<String, Map<String, Object>> scoreMap = new HashMap<>();
      if (scores != null) {
          for (Map<String, Object> s : scores) {
              scoreMap.put((String) s.get("구인인증번호"), s);
          }
      }

      for (Map<String, Object> candidate : candidates) {
          String gujinNo = (String) candidate.get("구인인증번호");
          Map<String, Object> scoreInfo = scoreMap.getOrDefault(gujinNo, new HashMap<>());

          ParticipantJobRecommendDTO dto =
              buildRecommendDto(participant, categoryList, referralInfo, candidate, searchCondition);

          // bestJobInfo: "1" = 베스트, "0" = 일반
          // selectBestRecommend SQL 조건이 베스트채용정보 = 1 이므로 "1"/"0" 사용
          dto.setBestJobInfo(gujinNo.equals(bestGujinNo) ? "1" : "0");

          Object scoreObj = scoreInfo.get("score");
          dto.setRecommendationScore(scoreObj instanceof Integer ? (Integer) scoreObj : null);
          dto.setRecommendationReason((String) scoreInfo.get("reason"));

          participantJobRecommendDAO.insertOrUpdateRecommend(dto);
      }
  }
  ```

- [x] 알선상세정보 없는 경우 저장 — `bestJobInfo = "0"`
  ```java
  private void saveWithoutGeminiJudgment(
          RecommendParticipantDTO participant,
          List<RecommendCategoryDTO> categoryList,
          RecommendReferralDTO referralInfo,
          List<Map<String, Object>> candidates,
          Map<String, Object> searchCondition) {

      for (Map<String, Object> candidate : candidates) {
          ParticipantJobRecommendDTO dto =
              buildRecommendDto(participant, categoryList, referralInfo, candidate, searchCondition);
          dto.setBestJobInfo("0");   // "N" 아님 — SQL 조건: 베스트채용정보 = 1
          participantJobRecommendDAO.insertOrUpdateRecommend(dto);
      }
  }
  ```

- [x] DTO 조립 헬퍼 — 실제 DTO 필드명 기준
  ```java
  private ParticipantJobRecommendDTO buildRecommendDto(
          RecommendParticipantDTO participant,
          List<RecommendCategoryDTO> categoryList,
          RecommendReferralDTO referralInfo,
          Map<String, Object> candidate,
          Map<String, Object> searchCondition) {

      ParticipantJobRecommendDTO dto = new ParticipantJobRecommendDTO();
      // RecommendParticipantDTO 필드
      dto.setJobSeekerNo(participant.getJobSeekerNo());
      dto.setParticipantName(participant.getInfoName());
      dto.setProgressStage(participant.getInfoStage());
      dto.setEducation(participant.getInfoEducation());
      dto.setMajor(participant.getInfoMajor());

      // RecommendCategoryDTO — 희망순위 1순위 (infoRank: String)
      if (categoryList != null && !categoryList.isEmpty()) {
          RecommendCategoryDTO primary = categoryList.get(0);
          dto.setCategoryMajor(primary.getCategoryMain());
          dto.setCategoryMiddle(primary.getCategoryMiddle());
          dto.setDesiredJob(primary.getInfoJob());
      }

      // RecommendReferralDTO
      if (referralInfo != null) {
          dto.setReferralDetail(referralInfo.getInfoAlsonDetail());
      }

      // 생성된 검색 조건 JSON 직렬화
      try {
          dto.setGeneratedSearchCondition(
              objectMapper.writeValueAsString(searchCondition));
      } catch (Exception ignored) {}

      // 채용정보 — SQL SELECT 컬럼명과 일치
      dto.setRecommendedJobCertNo((String) candidate.get("구인인증번호"));
      dto.setRecommendedJobUrl((String)     candidate.get("공고URL"));
      dto.setRecommendedJobCompany((String) candidate.get("기업명"));
      dto.setRecommendedJobTitle((String)   candidate.get("채용제목"));
      dto.setRecommendedJobIndustry((String) candidate.get("업종"));

      return dto;
  }
  ```

---

### 6-9. Controller API 구현

> **수정 파일:** `CounselMain/view/[참여자 Controller].java`

- [x] AI 추천 저장 엔드포인트 추가
  ```java
  @RequestMapping(value = "/counsel/participant/recommend/save.login",
                  method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> saveAiRecommend(
          @RequestParam("jobSeekerNo") int jobSeekerNo,
          @RequestParam(value = "forceRefresh", defaultValue = "false") boolean forceRefresh,
          HttpSession session) {
      Map<String, Object> result = new HashMap<>();
      try {
          result = recommendService.processAndSaveRecommend(jobSeekerNo, forceRefresh);
      } catch (Exception e) {
          result.put("success", false);
          result.put("message", "추천 저장 중 오류: " + e.getMessage());
      }
      return result;
  }
  ```

- [x] JS `saveAiRecommend()` 함수 (4주차 placeholder 대체)
  ```javascript
  function saveAiRecommend() {
      var jobSeekerNo = parseInt(
          document.getElementById('btnAiRecommend').getAttribute('data-gujik'));
      if (!jobSeekerNo) return;

      if (!confirm('AI 추천 채용정보를 저장하시겠습니까?\n기존 저장된 추천 정보는 갱신됩니다.')) return;

      document.getElementById('recommendStatusMsg').innerText = 'AI 추천 처리 중...';
      document.getElementById('btnAiRecommend').disabled = true;

      $.ajax({
          url: '/counsel/participant/recommend/save.login',
          type: 'POST',
          data: { jobSeekerNo: jobSeekerNo, forceRefresh: false },
          dataType: 'json',
          timeout: 30000,
          success: function(response) {
              document.getElementById('btnAiRecommend').disabled = false;
              if (response.success) {
                  var count = response.savedCount || 0;
                  document.getElementById('recommendStatusMsg').innerText =
                      count > 0
                          ? count + '개의 채용정보가 추천 저장되었습니다.'
                          : (response.message || '조건에 맞는 채용정보를 찾지 못했습니다.');
                  if (count > 0) loadRecommendData(jobSeekerNo);
              } else {
                  document.getElementById('recommendStatusMsg').innerText =
                      '저장 실패: ' + (response.message || '알 수 없는 오류');
              }
          },
          error: function(xhr, status) {
              document.getElementById('btnAiRecommend').disabled = false;
              document.getElementById('recommendStatusMsg').innerText =
                  status === 'timeout'
                      ? 'AI 처리 시간이 초과되었습니다. 다시 시도해주세요.'
                      : '서버 오류가 발생했습니다.';
          }
      });
  }
  ```

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| XML 버그 수정 | `bestJobRecommend` → `bestJobInfo` alias 수정 (2곳) | 🔲 |
| `RootConfig.java` 수정 | `Client` Bean 추가 | 🔲 |
| `GeminiApiService` 신규 | SDK 방식 API 호출, 검색조건 생성, 정밀판단 | 🔲 |
| `ParticipantJobRecommendService` 수정 | `processAndSaveRecommend` 인터페이스 선언 추가 | 🔲 |
| `ParticipantJobRecommendDAO` 수정 | `selectJobInfoCandidates` 메서드 추가 | 🔲 |
| XML 쿼리 추가 | `selectJobInfoCandidates` 쿼리 추가 | 🔲 |
| `ParticipantJobRecommendServiceImpl` 수정 | `processAndSaveRecommend` + 분기 저장 로직 구현 | 🔲 |
| Controller API | `/recommend/save.login` 엔드포인트 추가 | 🔲 |
| JS `saveAiRecommend` | 저장 버튼 AJAX 동작 | 🔲 |

---

## 4. 동작 확인 체크리스트

- [x] XML alias 수정 후 `selectRecommendList` 결과 `bestJobInfo` 필드 정상 매핑 확인
- [x] `selectBestRecommend` 조건 `= 1` → `bestJobInfo = "1"` 저장 시 정상 조회 확인
- [x] `RootConfig`에서 `Client` Bean 정상 로드 (서버 기동 로그)
- [x] Gemini API 호출 성공 및 JSON 응답 파싱 확인
- [x] `searchCondition`에 `maxCount` 포함 → SQL `TOP #{maxCount}` 정상 동작
- [x] 알선상세정보 없는 참여자 → 전체 `bestJobInfo = "0"` 저장
- [x] 알선상세정보 있는 참여자 → 베스트 1건 `bestJobInfo = "1"`, 나머지 `"0"`
- [x] `selectBestRecommend`로 베스트 1건 정상 조회
- [x] 재저장 시 기존 삭제 후 재삽입 (중복 없음)
- [x] 후보군 0건 시 저장 없이 메시지 반환
- [x] Gemini API 실패 시 오류 메시지 반환

---

## 5. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 | SD |
| 2026-04-13 | v0.2 | RestTemplate → Gen AI SDK; DTO 클래스명 수정; `gemini.api.url` 제거; `bestJobInfo` "Y"/"N" → "1"/"0" 수정; SQL maxCount 파라미터 주입 추가 | SD |
| 2026-04-13 | v0.3 | 실제 코드 기반 전면 재검토: XML alias 버그(`bestJobRecommend`→`bestJobInfo`) 발견 및 수정 항목 추가; `bestJobInfo` 값 "1"/"0" 근거 명시(SQL `= 1` 조건); `ParticipantJobRecommendService` 인터페이스에 `processAndSaveRecommend` 추가 항목 반영; DAO `selectJobInfoCandidates` 신규; `RecommendCategoryDTO.infoRank` String 타입 확인 | SD |