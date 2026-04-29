# AI_Function_11_12Week_Progress.md — 11~12주차: 최종 안정화 및 운영 반영

> **기간:** 2026-06-12 ~ 2026-06-26  
> **상태:** :arrow_forward: 진행 가능 — 카카오톡 공유 기능 구현 완료됨  
> **선행 조건:** :white_check_mark: `AI_Function_KakaoTalk_Share_Plan.md` 구현 완료 (Kakao JS SDK 방식)  
> **목표:** 카카오톡 공유 기능 포함 최종 회귀 테스트, 운영 DB 반영, WAR 빌드 및 배포

---

## 1. 이번 주 목표

개발 완료된 전체 기능을 운영 환경에 안전하게 배포한다.  
배포 전 최종 점검 → 운영 DB 반영 → WAR 빌드 → 배포 → 모니터링 순서로 진행한다.

---

## 2. 세부 작업 목록

### 11주차 — 최종 안정화 및 배포 준비

#### 11-1. 최종 회귀 테스트

- [ ] 8주차 통합 테스트 시나리오 전체 재실행
  - 시나리오 A: 기존 참여자 검색 기능 (회귀 없음 확인)
  - 시나리오 B: 집중 알선 필터 검색
  - 시나리오 C: 추천 모달 열기/닫기
  - 시나리오 D: AI 추천 저장 버튼 조건 제어
  - 시나리오 E: AI 추천 저장 전체 흐름
  - 시나리오 F: 기저장 추천 결과 조회
  - 시나리오 G: 예외/오류 케이스
- [ ] 9주차 UX 개선 사항 적용 후 재확인
- [ ] 10주차 공유 버튼 자리 및 `shareSummary` 동작 확인

**최종 회귀 테스트 결과:**
| 시나리오 | 결과 | 비고 |
|---------|------|------|
| A (기존 검색) | | |
| B (집중알선 필터) | | |
| C (모달 열기/닫기) | | |
| D (버튼 조건 제어) | | |
| E (AI 추천 저장) | | |
| F (기저장 조회) | | |
| G (예외 케이스) | | |

---

#### 11-2. 성능 최종 점검

- [ ] 참여자 리스트 조회 응답 시간 측정 (기준: 2초 이내)
  ```sql
  -- 실행 계획으로 쿼리 성능 확인
  SET STATISTICS TIME ON;
  -- 참여자 검색 쿼리 실행
  SET STATISTICS TIME OFF;
  ```
- [ ] 모달 API 응답 시간 측정 (기준: 1초 이내)
- [ ] AI 추천 저장 전체 소요 시간 측정 (기준: 15초 이내, Gemini API 2회 호출 포함)
  - 1단계: 검색 조건 생성 (`generateSearchCondition`)
  - 2단계: 베스트 선별 (`selectBestFromCandidates`, 알선상세정보 있는 경우만)
- [ ] 성능 기준 미달 항목 발견 시 개선 조치
  - 쿼리 인덱스 추가
  - `gemini.recommend.max-candidates` 값 조정 (기본값: 20)

---

#### 11-3. 운영 DB 반영 준비

- [ ] 개발 DB → 운영 DB 반영용 DDL 스크립트 최종 정리
  ```
  01_alter_J_참여자관리_집중알선여부.sql
  02_create_J_참여자관리_참여자추천채용정보.sql
  03_create_J_참여자관리_희망직무.sql
  04_migrate_희망직무_data.sql
  05_create_indexes.sql
  ```
- [ ] 각 스크립트 실행 순서 결정 및 의존성 확인
- [ ] 운영 DB 백업 계획 수립 (반영 전 풀 백업)
- [ ] 롤백 스크립트 준비
  ```sql
  -- 롤백: 집중알선여부 컬럼 제거
  ALTER TABLE J_참여자관리 DROP COLUMN 집중알선여부;

  -- 롤백: 신규 테이블 제거
  DROP TABLE IF EXISTS J_참여자관리_참여자추천채용정보;
  DROP TABLE IF EXISTS J_참여자관리_희망직무;
  ```
- [ ] DBA 또는 팀장과 운영 반영 일정 협의

---

#### 11-4. application.properties 운영 설정 확인

- [ ] 개발용 설정과 운영용 설정 분리 여부 확인
  - Spring Profile 사용 여부 (`spring.profiles.active=prod`)
  - 운영 DB 접속 정보 확인
- [ ] Gemini API Key 운영용 키 설정
  ```properties
  # 운영 환경: 운영 전용 Gemini API Key로 교체
  gemini.api.key=PRODUCTION_GEMINI_API_KEY
  gemini.api.model=gemini-2.0-flash
  gemini.recommend.max-candidates=20
  ```
  > `gemini.api.url`은 설정하지 않는다 (SDK 방식에서 불필요).
- [ ] Redis 설정 운영 환경 기준 확인
- [ ] 서버 포트 확인 (`server.port=8088`)

---

#### 11-5. WAR 빌드 및 검증

- [ ] 로컬에서 최종 빌드 실행
  ```bash
  ./mvnw clean package
  ```
- [ ] 빌드 성공 확인: `target/JobmoaProject-1.3.3-SNAPSHOT.war`
- [ ] WAR 파일 크기 이상 없음 확인 (Gen AI SDK 포함으로 이전 대비 크기 증가 예상)
- [ ] 빌드 경고/오류 로그 확인 (WARN 이상 항목 검토)
- [ ] `google-genai:1.47.0` 의존성 정상 패키징 확인

---

### 12주차 — 운영 반영 및 배포 후 모니터링

#### 12-1. 운영 DB 반영

- [ ] 운영 서버 DB 백업 실행 확인
- [ ] 반영 스크립트 순서대로 실행
  - [ ] `01_alter_J_참여자관리_집중알선여부.sql` 실행
  - [ ] `02_create_J_참여자관리_참여자추천채용정보.sql` 실행
  - [ ] `03_create_J_참여자관리_희망직무.sql` 실행
  - [ ] `04_migrate_희망직무_data.sql` 실행
  - [ ] `05_create_indexes.sql` 실행
- [ ] 반영 후 데이터 검증
  ```sql
  -- 컬럼 추가 확인
  SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_NAME = 'J_참여자관리' AND COLUMN_NAME = '집중알선여부';

  -- 신규 테이블 확인
  SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
  WHERE TABLE_NAME IN ('J_참여자관리_참여자추천채용정보', 'J_참여자관리_희망직무');

  -- 희망직무 이관 건수 확인
  SELECT COUNT(*) FROM J_참여자관리_희망직무;
  ```

---

#### 12-2. 운영 서버 배포

- [ ] 현재 운영 중인 애플리케이션 상태 확인 (접속자 없는 시간대 배포 권장)
- [ ] WAR 파일 운영 서버에 업로드
- [ ] 운영 서버 Tomcat(또는 내장 Tomcat) 재시작
- [ ] 배포 후 서버 기동 로그 확인
  - `Started TestProjectApplication` 로그 확인
  - `geminiClient` Bean 정상 로드 확인 (`Client` Bean 초기화 로그)
  - 오류 로그 없음 확인
- [ ] `application.properties` 운영 설정 적용 여부 확인
- [ ] Gemini API Key 정상 로드 확인 (`gemini.api.key` 값 주입 여부)

---

#### 12-3. 배포 후 기능 확인 (스모크 테스트)

- [ ] 로그인 정상 동작 확인
- [ ] 기존 참여자 검색 기능 정상 동작 확인
- [ ] 집중 알선 필터 검색 정상 동작 확인
- [ ] 추천 채용정보 모달 오픈 및 데이터 출력 확인
  - `participant` 영역: `infoName`, `infoStage`, `infoEducation`, `categoryList` 등 정상 출력
  - `recommendList` 영역: `recommendedJobCompany`, `recommendedJobTitle` 정상 출력
- [ ] AI 추천 저장 버튼 1회 동작 확인
  - `IAP후` 또는 `미취업사후관리` 진행단계 참여자 대상
  - Gemini API 호출 성공 및 저장 확인
- [ ] 운영 DB에 `J_참여자관리_참여자추천채용정보` 테이블에 추천 결과 저장 확인

---

#### 12-4. 배포 후 모니터링 (1주간)

- [ ] 서버 에러 로그 일별 확인
  - Gemini API 호출 오류 발생 빈도 (SDK 호출 실패 로그)
  - DB 저장 실패 건수
  - 응답 시간 이상 여부 (`log.warn` "[추천모달] 응답 지연" 로그)
- [ ] Gemini API 사용량/비용 모니터링 (Google AI Studio 또는 Google Cloud Console)
- [ ] `J_참여자관리_참여자추천채용정보` 테이블 데이터 증가 추이 확인
- [ ] 상담사 사용 현황 확인 (AI 추천 저장 버튼 사용 빈도)

**모니터링 일지:**
| 날짜 | 오류 건수 | Gemini API 호출 수 | 특이사항 |
|------|---------|------------------|---------|
| | | | |
| | | | |
| | | | |

---

#### 12-5. 개발 문서 최종 정리

- [ ] `AI_Function_Progress.md` 최종 업데이트 (전체 주차 상태 완료로 표시)
- [ ] `AI_Function_PRD.md` 성공 기준 최종 체크
- [ ] 운영 반영 완료 내역 기록
  - 반영 일시
  - 반영 DDL 목록
  - 기능 목록
- [ ] 이후 유지보수를 위한 주의사항 메모
  ```
  [운영 유지보수 주의사항]
  1. Gemini API Key는 application.properties에 직접 기재됨
     - 키 노출 주의, 주기적으로 갱신 권장
     - 키 위치: gemini.api.key 속성
     - SDK: com.google.genai:google-genai:1.47.0 (Client Bean: RootConfig.geminiClient())

  2. J_참여자관리_참여자추천채용정보 테이블은 구직번호+구인인증번호 복합 UNIQUE 제약
     - 동일 참여자 재저장 시: deleteRecommendByGujikNo → insertOrUpdateRecommend 순서로 처리
     - bestJobInfo 컬럼은 String("1"/"0") 사용
       → selectBestRecommend SQL WHERE 조건: 베스트채용정보 = 1
       → "Y"/"N" 사용 시 베스트 조회 실패
     - XML mapping alias: 베스트채용정보 AS bestJobInfo (AS bestJobRecommend 아님)

  3. 추천 저장은 진행단계 'IAP후', '미취업사후관리'만 허용
     - 진행단계 조건은 Controller 또는 ServiceImpl에서 관리
     - 진행단계 값 변경 시 해당 위치의 조건문 수정 필요

  4. Gemini API 호출 순서
     - 1단계: generateSearchCondition (항상 호출)
     - 2단계: selectBestFromCandidates (알선상세정보 있는 경우만 호출)
     - 후보군 최대 건수: gemini.recommend.max-candidates (기본값 20)

  5. 카카오톡 공유 기능 — Kakao JS SDK 방식으로 구현 완료
     - Kakao.Share.sendCustom() 사용, 커스텀 템플릿 ID: 132481
     - 개별 공유 + 선택 항목 일괄 공유(큐 기반) 모두 지원
     - kakao.javascript-key는 application.properties에서 관리
  ```
- [ ] git 브랜치 정리 및 main 브랜치 머지 확인

---

## 3. 최종 성공 기준 점검

| # | 성공 기준 | 결과 | 확인일 |
|---|-----------|------|--------|
| 1 | 집중 알선 참여자를 검색할 수 있다 | | |
| 2 | 참여자 리스트에서 추천 채용정보 모달을 열 수 있다 | | |
| 3 | 모달에서 참여자 정보(`infoName`, `infoStage`, 카테고리 등)와 추천 채용정보가 정상 표시된다 | | |
| 4 | Gen AI SDK 기반 Gemini API 검색 조건 생성이 동작한다 | | |
| 5 | 알선상세정보(`infoAlsonDetail`) 유무에 따라 저장 방식이 다르게 처리된다 | | |
| 6 | `IAP후`, `미취업사후관리` 참여자에게만 AI 추천 저장 버튼이 동작한다 | | |
| 7 | 향후 카카오톡 공유 기능을 무리 없이 추가할 수 있는 구조를 확보한다 | | |

---

## 4. 배포 체크리스트 (Go-Live Checklist)

- [ ] 개발 DB와 운영 DB DDL 스크립트 동일 여부 확인
- [ ] 운영 DB 백업 완료
- [ ] WAR 빌드 성공 (`./mvnw clean package`)
- [ ] 운영 `application.properties` Gemini API Key 설정 완료 (`gemini.api.url` 미포함 확인)
- [ ] 배포 후 서버 기동 성공 (geminiClient Bean 로드 확인)
- [ ] 스모크 테스트 전 항목 통과
- [ ] 상담사 사용 안내 공지 준비 완료

---

## 5. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| WAR 파일 | `target/JobmoaProject-1.3.3-SNAPSHOT.war` | 🔲 |
| DDL 스크립트 세트 | 운영 반영용 SQL 5개 파일 | 🔲 |
| 롤백 스크립트 | 롤백용 SQL | 🔲 |
| 운영 문서 | 유지보수 주의사항 기록 | 🔲 |
| 진행 문서 완료 | `AI_Function_Progress.md` 최종 업데이트 | 🔲 |

---

## 6. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 | SD |
| 2026-04-13 | v0.2 | 11-4 운영 설정에서 `gemini.api.url` 제거; `gemini.recommend.max-candidates` 추가; 11-5 Gen AI SDK WAR 크기 증가 주의 추가; 12-2 `geminiClient` Bean 로드 확인 항목 추가; 성공 기준 4번 "Gen AI SDK 기반"으로 수정 | SD |
| 2026-04-13 | v0.3 | 실제 코드 기반 재검토: `bestJobInfo` 값 "Y"/"N" → "1"/"0" 수정 및 근거(XML `= 1` 조건) 명시; XML alias 버그(`bestJobRecommend`→`bestJobInfo`) 수정 사항 유지보수 주의사항에 추가; `ParticipantJobRecommendService` 인터페이스에 `processAndSaveRecommend` 추가 필요 반영 | SD |
| 2026-04-24 | v0.4 | 카카오톡 공유 기능 완료 반영 — 선행 조건 충족, 유지보수 주의사항 #5 업데이트 (JS SDK 방식 구현 완료) | SD |