# AI_Function_5Week_Progress.md — 5주차: 참여자 정보 및 추천 채용정보 모달 데이터 연동

> **기간:** 2026-05-01 ~ 2026-05-08  
> **상태:** ✅ 완료  
> **목표:** 모달에 참여자 정보 및 기저장 추천 채용정보를 실제 데이터로 출력

---

## 1. 이번 주 목표

4주차에 완성한 모달 UI에 실제 데이터를 연동한다.  
Controller API 추가 → AJAX 호출 → 모달 DOM 바인딩 순서로 진행하며,  
AI 추천 저장 버튼의 진행단계 조건 제어도 이번 주에 완성한다.

---

## 2. 세부 작업 목록

### 5-1. 모달 데이터 조회 API 구현 (Controller)

> 수정 파일: `CounselMain/view/[참여자 Controller].java`

- [x] 모달용 데이터 조회 엔드포인트 추가
  - URL 결정 (예: `/counsel/participant/recommend/modal.login`)
  - HTTP Method: `GET` 또는 `POST` (AJAX 방식에 맞게)
  - 요청 파라미터: `구직번호`
  - 응답 형식: JSON (`@ResponseBody`)

- [x] Controller 메서드 작성
  ```java
    @PostMapping(value = "/detail",
            consumes = "application/json; charset=utf-8",
            produces = "application/json; charset=utf-8")
    public ResponseEntity<Map<String, Object>> recommend(@RequestBody Map<String, Object> request) {
        try {
            Object jobSeekerNoObj = request.get("jobSeekerNo");
            if (jobSeekerNoObj == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "구직번호가 필요합니다."
                ));
            }

            int jobSeekerNo = Integer.parseInt(String.valueOf(jobSeekerNoObj));
            Map<String, Object> response = recommendService.getRecommendDetailResponse(jobSeekerNo);

            log.info("recommend jobSeekerNo : [{}]", jobSeekerNo);
            log.info("recommend response : [{}]", response);

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            log.error("Invalid jobSeekerNo format", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 구직번호 형식입니다."
            ));
        } catch (Exception e) {
            log.error("Error recommending job", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "추천 처리 중 오류가 발생했습니다."
            ));
        }
    }
  ```

- [x] 참여자 단건 상세 조회 Service 메서드 존재 여부 확인
  - 있으면 재사용, 없으면 기존 참여자 Service에 추가
  - 조회 항목: 구직번호, 참여자명, 진행단계, 집중알선여부, 학력, 전공, 카테고리대/중, 희망직무, 알선상세정보

---

### 5-2. 참여자 단건 조회 쿼리 확인 및 보완

> 수정 파일: `src/main/resources/mappings/[참여자 mapper].xml`

- [x] 참여자 단건 조회 쿼리 존재 여부 확인
  - 없으면 신규 작성
    ```xml
    <!-- 참여자 정보 조회 쿼리 작성 -->
    <select id="selectParticipantInfo"  resultType="recommendParticipant">
        SELECT
            구직번호 AS jobSeekerNo,
            참여자 AS infoName,
            성별 AS summaryGender,
            진행단계 AS infoStage,
            집중알선여부 AS infoFocus,
            학력 AS infoEducation,
            전공 AS infoMajor
        FROM J_참여자관리
        WHERE 구직번호 = #{jobSeekerNo}
    </select>
    ```
- [x] `집중알선여부` 컬럼이 SELECT 결과에 포함되어 있는지 확인

---

### 5-3. 모달 데이터 연동 JS (AJAX)

> 수정 파일: 참여자관리 JS 파일

#### 5-3-1. `openRecommendModal` 함수에 AJAX 호출 추가

- [x] 4주차에 주석 처리한 `loadRecommendData(gujikNo)` 함수 구현
  ```javascript
  function loadRecommendData(gujikNo) {
    // 로딩 표시
    document.getElementById('recommendLoading').style.display = 'block';

    $.ajax({
        url: '/api/recommend/detail',
        type: 'POST',
        data: JSON.stringify({ jobSeekerNo: gujikNo }),
        dataType: 'json',
        contentType: 'application/json; charset=UTF-8',
        success: function(response) {
            document.getElementById('recommendLoading').style.display = 'none';
            if (response.success) {
                bindParticipantInfo(response.participant);
                bindRecommendList(response.recommendList);
                bindBestRecommend(response.bestRecommend);
            } else {
                document.getElementById('recommendStatusMsg').innerText = response.message;
            }
        },
        error: function() {
            document.getElementById('recommendLoading').style.display = 'none';
            document.getElementById('recommendStatusMsg').innerText
                = '서버 오류가 발생했습니다.';
        }
    });
  }
  ```

#### 5-3-2. 참여자 정보 DOM 바인딩 함수

- [x] `bindParticipantInfo` 함수 작성
  ```javascript
  function bindParticipantInfo(p) {
    if (!p) return;
    document.getElementById('infoName').innerText      = p.infoName || '-'; // 참여자명
    document.getElementById('infoGujikNo').innerText   = p.infoGujikNo || '-'; // 구직번호
    document.getElementById('infoStage').innerText     = p.infoStage || '-'; // 진행단계
    document.getElementById('infoJibjoong').innerText
        = p.infoFocus ? '집중알선 대상' : '일반'; // 집중알선 여부
    document.getElementById('infoEducation').innerText = p.infoEducation || '-'; // 학력
    document.getElementById('infoMajor').innerText     = p.infoMajor || '-'; // 전공

    const categoryList = p.categoryList;
    if (categoryList && categoryList.length > 0) {
        console.log(categoryList);
        for (let i = 0; i < categoryList.length; i++) {
            console.log(categoryList[i]);
            const category = categoryList[i];
            document.getElementById('infoCategory').innerHTML
                += `<p class="category-rank">${i + 1}. ${(category.categoryMain || '') + ' > ' + (category.categoryMiddle || '')}</p>`; // 카테고리(대,중)분류
            document.getElementById('infoWantedJob').innerHTML
                += `<p class="wanted-job-rank">${i + 1}. ${category.infoJob || '-'}</p>`; // 희망직무
        }
    } else {
        document.getElementById('infoCategory').innerHTML = '-';
        document.getElementById('infoWantedJob').innerHTML = '-';
    }

    const referral = p.Referral;
    document.getElementById('infoAlsonDetail').innerText = referral.infoAlsonDetail || '-'; // 알선상세정보
    document.getElementById('infoAdditionalInfo').innerText = referral.infoAdditionalInfo || '-'; // 추천사
  }
  ```

#### 5-3-3. 추천 채용정보 리스트 DOM 바인딩 함수

- [x] `bindRecommendList` 함수 작성
  ```javascript
  function bindRecommendList(list) {
    let tbody = document.getElementById('recommendListBody');
    tbody.innerHTML = '';

    if (!list || list.length === 0) {
        document.getElementById('recommendListEmpty').style.display = 'block';
        document.getElementById('recommendListTable').style.display = 'none';
        return;
    }

    document.getElementById('recommendListEmpty').style.display = 'none';
    document.getElementById('recommendListTable').style.display = 'table';

    list.forEach(function(item, idx) {
        let row = '<tr' + (item.bestJobInfo? ' class="best-row"' : '') + '>' // 추천채용정보
            + '<td>' + (idx + 1) + '</td>'
            + '<td>' + (item.recommendedJobCompany || '-') + '</td>' // 추천채용정보 기업명
            + '<td>'
            + (item.recommendedJobUrl // 추천채용정보 URL
                ? '<a href="' + item.recommendedJobUrl + '" target="_blank">'
                + (item.recommendedJobTitle || '-') + '</a>' // 추천채용정보 제목
                : (item.recommendedJobTitle || '-'))
            + '</td>'
            + '<td>' + (item.recommendedJobIndustry || '-') + '</td>' // 추천채용정보 업종
            + '<td>' + (item.recommendationScore != null ? item.recommendationScore + '점' : '-') + '</td>' // 추천채용정보 점수
            + '<td>' + (item.recommendationReason || '-') + '</td>' // 채용정보 추천 사유
            + '</tr>';
        tbody.innerHTML += row;
    });
  }
  ```

#### 5-3-4. 베스트 추천채용정보 DOM 바인딩 함수

- [x] `bindBestRecommend` 함수 작성
  ```javascript
  function bindBestRecommend(best) {
    if (!best) {
        document.getElementById('bestRecommendArea').style.display = 'none';
        return;
    }
    document.getElementById('bestRecommendArea').style.display = 'block';
    var html = '<p><strong>기업명:</strong> ' + (best.recommendedJobCompany || '-') + '</p>' // 베스트 추천채용정보 기업명
        + '<p><strong>채용공고:</strong> '
        + (best.recommendedJobUrl // 베스트 추천채용정보 URL
            ? '<a href="' + best.recommendedJobUrl + '" target="_blank">'
            + (best.recommendedJobTitle || '-') + '</a>' // 베스트 추천채용정보 제목
            : (best.recommendedJobTitle || '-'))
        + '</p>'
        + '<p><strong>업종:</strong> ' + (best.recommendedJobIndustry || '-') + '</p>' // 베스트 추천채용정보 업종
        + '<p><strong>추천점수:</strong> ' + (best.recommendationScore != null ? best.recommendationScore + '점' : '-') + '</p>' // 베스트 추천채용정보 점수
        + '<p><strong>추천사유:</strong> ' + (best.recommendationReason || '-') + '</p>'; // 베스트 추천채용정보 사유
    document.getElementById('bestRecommendContent').innerHTML = html;
  }
  ```

---

### 5-4. 진행단계 조건 버튼 제어 완성

- [x] `openRecommendModal` 함수에서 AJAX 응답 후 진행단계 재확인 처리
  - Controller 응답의 `participant.진행단계` 값으로 버튼 노출 여부 최종 결정
  - 클라이언트 `data-stage` 값과 서버 응답 진행단계 값 일치 여부 검증
  ```javascript
  function bindParticipantInfo(p) {
      // ... 기존 바인딩 코드 ...

      // AI 추천 저장 버튼 제어 (서버 응답 기준)
      const allowStages = ['IAP후', '미취업사후관리'];
      const btnAi = document.getElementById('btnAiRecommend');
      if (p.진행단계 && allowStages.indexOf(p.진행단계) !== -1) {
          btnAi.style.display = 'inline-block';
          btnAi.setAttribute('data-gujik', p.구직번호);
      } else {
          btnAi.style.display = 'none';
      }
  }
  ```
- [x] AI 추천 저장 버튼 클릭 핸들러 placeholder 추가 (6주차 구현 예정)
  ```javascript
  function saveAiRecommend() {
      // TODO: 6주차에 Gemini API 연동 구현
      alert('AI 추천 저장 기능은 준비 중입니다.');
  }
  ```

---

### 5-5. JSON 응답 구조 표준화

> 향후 카카오톡 공유 기능 확장을 고려한 응답 구조

- [x] Controller 응답 JSON 구조 확정
  ```json
  {
    "success": true,
    "participant": {
      "infoGujikNo": "...",
      "summaryGender": "...",
      "infoName": "...",
      "infoStage": "...",
      "infoFocus": true,
      "infoEducation": "...",
      "infoMajor": "...",
      "categoryList":[
        {
          "categoryMain": "...",
          "categoryMiddle": "...",
          "infoJob": "...",
          "infoRank": "..."
        }
      ],
      "Referral": {
        "infoAlsonDetail": "...",
        "infoAdditionalInfo": "..."
      }
    },
    "recommendList": [
      {
        "recommendedJobCompany": "...",
        "recommendedJobTitle": "...",
        "recommendedJobUrl": "...",
        "recommendedJobIndustry": "...",
        "recommendationScore": 85,
        "recommendationReason": "...",
        "bestJobInfo": false
      }
    ],
    "bestRecommend": {},
    "shareSummary": null
  }
  ```
  한국어 번역 JSON
  ```json
  {
    "success": true,
    "participant": {
      "구직번호": "...",
      "성별": "...",
      "참여자명": "...",
      "진행단계": "...",
      "집중알선여부": true,
      "학력": "...",
      "전공": "...",
      "categoryList":[
        {
          "카테고리대분류": "...",
          "카테고리중분류": "...",
          "희망직무": "...",
          "희망순위": "..."
        }
      ],
      "Referral": {
        "상세정보": "...",
        "추천사": "..."
      }
    },
    "recommendList": [
      {
        "추천채용정보_기업명": "...",
        "추천채용정보_제목": "...",
        "추천채용정보_URL": "...",
        "추천채용정보_업종": "...",
        "추천점수": 85,
        "추천사유": "...",
        "베스트채용정보": false
      }
    ],
    "bestRecommend": {},
    "shareSummary": null
  }
  ```
  - `shareSummary`: 카카오톡 공유용 요약 (현재 null, 10주차에 구현)
- [ ] Jackson ObjectMapper 직렬화 시 null 필드 처리 방식 결정
  - 옵션: `@JsonInclude(JsonInclude.Include.NON_NULL)` 적용 여부

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|-------|
| Controller API | 모달 데이터 조회 엔드포인트 | ✅     |
| mapper XML | 참여자 단건 조회 쿼리 (없으면 추가) | ✅    |
| JS 완성 | AJAX + 참여자정보 바인딩 + 리스트 바인딩 + 베스트 바인딩 | ✅    |
| 버튼 제어 | 진행단계 조건 AI 추천 저장 버튼 노출 완성 | ✅    |
| 응답 구조 | JSON 응답 구조 확정 및 적용 | ✅    |

---

## 4. 동작 확인 체크리스트

- [x] 리스트 버튼 클릭 → 모달 오픈 → 참여자 정보 출력 정상
- [x] 기저장 추천 리스트 있을 때 테이블 출력 정상
- [x] 기저장 추천 리스트 없을 때 빈 상태 메시지 출력 정상
- [x] 베스트 채용정보 있을 때 카드 강조 표시 정상
- [x] `IAP후` 참여자 → AI 추천 저장 버튼 노출 정상
- [x] `미취업사후관리` 참여자 → AI 추천 저장 버튼 노출 정상
- [x] 그 외 진행단계 참여자 → AI 추천 저장 버튼 미노출 정상
- [x] 서버 오류 발생 시 오류 메시지 출력 정상
- [x] 로딩 인디케이터 표시/숨김 정상

---

## 5. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 | SD |