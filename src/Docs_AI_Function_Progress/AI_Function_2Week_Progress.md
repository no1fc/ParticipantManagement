# AI_Function_2Week_Progress.md — 2주차: DB 수정 설계 및 쿼리 변경안 수립

> **기간:** 2026-04-10 ~ 2026-04-17  
> **상태:** ✅ 완료  
> **목표:** 1주차 분석 결과를 바탕으로 DB 변경 DDL 스크립트 작성 및 쿼리 변경안 확정

---

## 1. 이번 주 목표

실제 DB에 반영하기 전 모든 DDL 스크립트와 기존 쿼리 변경안을 작성·검토한다.  
이번 주는 **설계 및 스크립트 작성** 단계이며, 실제 DB 반영은 3주차에 진행한다.

---

## 2. 세부 작업 목록

### 2-1. 참여자 테이블 집중 알선 컬럼 추가 설계

- [x] 컬럼명 확정
  - 프로젝트 한국어 네이밍 규칙 확인 후 결정
  - 후보: `집중알선여부`
  - 데이터 타입: `bit(1)` (`1` / `0`)
  - 기본값: `'0'`
  - NOT NULL 제약 적용
  - 저장 가능 값: 0 (False), 1 (True)
- [x] ALTER TABLE DDL 스크립트 작성
  ```sql
  -- J_참여자관리 테이블에 집중알선여부 컬럼 추가
  ALTER TABLE J_참여자관리
  ADD 집중알선여부 CHAR(1) NOT NULL DEFAULT '0';
  ```
- [x] 기존 데이터 일괄 업데이트 스크립트 작성
  ```sql
  -- 기존 참여자 전체 기본값 'N' 설정 (컬럼 추가 후 DEFAULT로 자동 처리되나 명시적 확인)
  UPDATE J_참여자관리
  SET 집중알선여부 = '0'
  WHERE 집중알선여부 IS NULL;
  ```
- [x] 확인 항목:
  - [x] 컬럼명 최종 확정: `집중알선여부`
  - [x] 데이터 타입 최종 확정: `bit`

---

### 2-2. 추천채용정보 저장 테이블 설계

- [x] `J_참여자관리_참여자추천채용정보` 테이블 DDL 작성
  ```sql
  CREATE TABLE J_참여자관리_참여자추천채용정보 (
      PK                    INT           IDENTITY(1,1)  NOT NULL,  -- 자동증가 PK
      구직번호              INT  NOT NULL,                  -- 참여자 식별키
      참여자명              NVARCHAR(100) NULL,
      진행단계              NVARCHAR(50)  NULL,
      학력                  NVARCHAR(50)  NULL,
      전공                  NVARCHAR(100) NULL,
      카테고리대분류        NVARCHAR(100) NULL,
      카테고리중분류        NVARCHAR(100) NULL,
      카테고리소분류        NVARCHAR(100) NULL,
      희망직무              NVARCHAR(200) NULL,
      알선상세정보          NVARCHAR(MAX) NULL,
      생성된검색조건        NVARCHAR(MAX) NULL,                       -- Gemini가 생성한 검색 조건 JSON
      추천채용정보_구인인증번호  NVARCHAR(50)  NULL,
      추천채용정보_URL      NVARCHAR(500) NULL,
      추천채용정보_기업명   NVARCHAR(200) NULL,
      추천채용정보_제목     NVARCHAR(500) NULL,
      추천채용정보_업종     NVARCHAR(100) NULL,
      베스트채용정보        bit       NOT NULL DEFAULT '0',       -- 1: 베스트, 0: 일반
      추천점수              INT           NULL,                       -- 0~100 점수
      추천사유              NVARCHAR(MAX) NULL,                       -- Gemini 추천 근거
      저장일시              DATETIME      NOT NULL DEFAULT GETDATE(),
      수정일시              DATETIME      NULL,
      CONSTRAINT PK_참여자추천채용정보 PRIMARY KEY (PK)
  );
  ```
- [x] 중복 저장 방지 UNIQUE 제약 조건 설계
  - 동일 구직번호 + 동일 구인인증번호 조합으로 중복 방지
  ```sql
  ALTER TABLE J_참여자관리_참여자추천채용정보
  ADD CONSTRAINT UQ_참여자추천채용정보
  UNIQUE (구직번호, 추천채용정보_구인인증번호);
  ```
- [x] 인덱스 설계
  ```sql
  -- 구직번호 기준 조회 인덱스
  CREATE INDEX IDX_참여자추천채용정보_구직번호
  ON J_참여자관리_참여자추천채용정보 (구직번호);

  -- 저장일시 기준 정렬 인덱스
  CREATE INDEX IDX_참여자추천채용정보_저장일시
  ON J_참여자관리_참여자추천채용정보 (저장일시 DESC);
  ```

---

### 2-3. 희망직무 분리 테이블 설계 (J_참여자관리_희망직무)

- [x] `J_참여자관리_희망직무` 테이블 DDL 작성
  - 한 참여자가 여러 희망직무를 등록할 수 있도록 분리
  ```sql
  CREATE TABLE J_참여자관리_희망직무 (
      PK               INT IDENTITY(1,1)  NOT NULL,
      구직번호          INT  NOT NULL,
      희망순위          INT           NULL,
      희망직무          NVARCHAR(200) NULL,
      직무_카테고리_소   NVARCHAR(100) NULL,
      직무_카테고리_중   NVARCHAR(100) NULL,
      직무_카테고리_대   NVARCHAR(100) NULL,
      등록일시          DATETIME      NOT NULL DEFAULT GETDATE(),
      CONSTRAINT PK_참여자희망직무 PRIMARY KEY (PK)
  );
  ```
- [x] 중복 방지 제약 조건
  ```sql
  ALTER TABLE J_참여자관리_희망직무
  ADD CONSTRAINT UQ_참여자희망직무
  UNIQUE (구직번호, 희망직무);
  ```
- [x] 기존 `J_참여자관리` 데이터 이관 스크립트
  ```sql
  -- 기존 참여자관리 테이블에서 희망직무 데이터 이관
  INSERT INTO J_참여자관리_희망직무 (구직번호, 희망직무, 직무_카테고리_소, 직무_카테고리_중, 직무_카테고리_대)
  SELECT 구직번호, 희망직무, 직무_카테고리_소, 직무_카테고리_중, 직무_카테고리_대
  FROM J_참여자관리
  WHERE 희망직무 IS NOT NULL AND 희망직무 <> '';
  ```
- [x] 확인 항목:
  - [x] `J_참여자관리`에 `직무_카테고리_소/중/대` 컬럼 존재 여부 확인: `확인 완료`
  - [x] 이관 후 기존 컬럼 유지 또는 제거 여부 결정: `제거 진행`

---

### 2-4. 기존 참여자 검색 쿼리 영향 분석

- [x] 참여자 mapper XML에서 수정 필요 쿼리 목록 확정
  - [x] 참여자 리스트 조회 쿼리: WHERE 절에 `집중알선여부` 조건 추가 가능 여부 검토
  - [x] 참여자 전체 건수 조회 쿼리(페이징용): 동일 조건 추가 필요 여부 확인
  - [x] 참여자 단건 조회 쿼리: `집중알선여부` SELECT 항목 추가 여부 결정
    - DTO 값 participantISIntensiveMediation로 등록
- [x] 쿼리 변경안 초안 작성
  ```xml
  <!-- 집중알선여부 검색 조건 추가 예시 -->
  <if test="집중알선여부 != null and 집중알선여부 != ''">
      AND 집중알선여부 = #{집중알선여부}
  </if>
  
  <!-- [mapper xml] 검색 조건 추가 -->
  <!-- 참여자관리 페이지에서 검색 시 isIntesiveMediation로 검색하도록 등록 -->
  <choose>
    <when test="searchType != null and searchType =='isIntesiveMediation'">
     A.집중알선여부 = 1
    </when>
  </choose>
  
  ```
- [x] 기존 쿼리와 AND/OR 결합 방식 결정
  - 집중 알선 여부는 단독 필터이므로 `AND` 방식 적용

---

### 2-5. 추천채용정보 UPSERT 쿼리 설계

> 동일 구직번호 + 구인인증번호 조합이 이미 있으면 UPDATE, 없으면 INSERT

- [x] MERGE 문 방식 (MSSQL T-SQL)
    ```sql
    MERGE INTO J_참여자관리_참여자추천채용정보 AS target
    USING (
        SELECT #{구직번호} AS 구직번호, #{추천채용정보_구인인증번호} AS 추천채용정보_구인인증번호
    ) AS source
    ON (target.구직번호 = source.구직번호
        AND target.추천채용정보_구인인증번호 = source.추천채용정보_구인인증번호)
    WHEN MATCHED THEN
        UPDATE SET
            추천점수    = #{추천점수},
            추천사유    = #{추천사유},
            베스트채용정보 = #{베스트채용정보},
            수정일시    = GETDATE()
    WHEN NOT MATCHED THEN
        INSERT (구직번호, 참여자명, 진행단계, 학력, 전공, 카테고리대분류, 카테고리중분류,
                희망직무, 알선상세정보, 생성된검색조건, 추천채용정보_구인인증번호,
                추천채용정보_URL, 추천채용정보_기업명, 추천채용정보_제목,
                추천채용정보_업종, 베스트채용정보, 추천점수, 추천사유, 저장일시)
        VALUES (#{구직번호}, #{참여자명}, #{진행단계}, #{학력}, #{전공}, #{카테고리대분류},
                #{카테고리중분류}, #{희망직무}, #{알선상세정보}, #{생성된검색조건},
                #{추천채용정보_구인인증번호}, #{추천채용정보_URL}, #{추천채용정보_기업명},
                #{추천채용정보_제목}, #{추천채용정보_업종}, #{베스트채용정보},
                #{추천점수}, #{추천사유}, GETDATE());
    ```
- [x] UPSERT 쿼리 최종 방식 결정: `MERGE 문 / IF EXISTS-UPDATE-ELSE-INSERT` : MERGE 문 방식 선택

---

### 2-6. 추천채용정보 조회 쿼리 설계

- [x] 특정 구직번호의 추천 목록 전체 조회 (모달 진입 시)
  ```sql
  SELECT *
  FROM J_참여자관리_참여자추천채용정보
  WHERE 구직번호 = #{구직번호}
  ORDER BY 베스트채용정보 DESC, 추천점수 DESC, 저장일시 DESC;
  ```
- [x] 베스트 채용정보 단건 조회 쿼리
  ```sql
  SELECT TOP 1 *
  FROM J_참여자관리_참여자추천채용정보
  WHERE 구직번호 = #{구직번호} AND 베스트채용정보 = 'Y';
  ```

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|-------|
| DDL 스크립트 | 집중알선여부 컬럼 ALTER, 추천채용정보 테이블 CREATE, 희망직무 테이블 CREATE | ☑     |
| 데이터 이관 스크립트 | 희망직무 기존 데이터 → 신규 테이블 이관 SQL | ☑    |
| 쿼리 변경안 | 기존 참여자 검색 쿼리 수정 초안 | ☑    |
| UPSERT 쿼리 | 추천채용정보 저장/갱신 쿼리 | ☑    |

---

## 4. 검토 체크리스트

- [x] DDL 스크립트 MSSQL 문법 이상 없음 확인
- [x] UNIQUE 제약 조건이 기존 운영 데이터와 충돌하지 않음 확인
- [x] 기존 참여자 검색 쿼리 WHERE 조건 변경이 성능에 영향 없음 검토
- [x] 희망직무 이관 SQL 실행 전 건수 카운트로 데이터 검증 계획 수립
  ```sql
  -- 이관 전 건수 확인
  SELECT COUNT(*) FROM J_참여자관리 WHERE 희망직무 IS NOT NULL;
  -- 이관 후 건수 확인
  SELECT COUNT(*) FROM J_참여자관리_희망직무;
  ```

---

## 5. 변경 이력

| 날짜         | 버전   | 변경 내용 | 작성자 |
|------------|------|-------|-----|
| 2026-04-03 | v0.1 | 최초 작성 | SD  |
| 2026-04-09 | v0.2 | 적용 완료 | SD  |