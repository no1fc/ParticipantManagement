# AI_Function_3Week_Progress.md — 3주차: 백엔드 검색 쿼리 수정 및 필터 파라미터 연동

> **기간:** 2026-04-17 ~ 2026-04-24  
> **상태:** ✅ 완료  
> **목표:** DB DDL 실제 반영 및 검색/추천 백엔드 전 계층(Mapper → DAO → Service → Controller) 수정·신규 작성

---

## 1. 이번 주 목표

2주차에 설계한 DDL을 실제 DB에 반영하고,  
참여자 검색에 집중 알선 필터를 추가하며,  
추천채용정보 저장/조회 백엔드 기반 코드를 완성한다.

---

## 2. 세부 작업 목록

### 3-1. DB 실제 반영

- [x] 개발 DB에 DDL 스크립트 실행
  - [x] `J_참여자관리` 테이블에 `집중알선여부 bit(1) NOT NULL DEFAULT '0'` 컬럼 추가
  - [x] `J_참여자관리_참여자추천채용정보` 테이블 생성
  - [x] `J_참여자관리_희망직무` 테이블 생성
  - [x] 희망직무 기존 데이터 이관 SQL 실행 및 건수 검증
  - [x] UNIQUE 제약 조건, 인덱스 생성 확인
- [x] 실행 결과 검증
  ```sql
  -- 컬럼 추가 확인
  SELECT COLUMN_NAME, DATA_TYPE, COLUMN_DEFAULT
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_NAME = 'J_참여자관리' AND COLUMN_NAME = '집중알선여부';

  -- 신규 테이블 생성 확인
  SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
  WHERE TABLE_NAME IN ('J_참여자관리_참여자추천채용정보', 'J_참여자관리_희망직무');
  ```

---

### 3-2. MyBatis Mapper XML 수정 — 참여자 검색

> 수정 파일: `src/main/resources/mappings/[참여자 mapper].xml`

- [x] 참여자 검색 쿼리 SELECT 컬럼에 `집중알선여부` 추가
  ```xml
  <!-- 기존 SELECT 절 끝에 추가 -->
  , 집중알선여부
  ```
- [x] 참여자 검색 WHERE 조건에 집중 알선 필터 추가
  ```xml
  <if test="집중알선여부 != null and 집중알선여부 != ''">
      AND 집중알선여부 = #{집중알선여부}
  </if>
  ```
- [x] 페이징 카운트 쿼리(COUNT)에도 동일 조건 추가 여부 확인 및 반영
- [x] XML 수정 후 MyBatis 문법 오류 없음 확인 (빌드 테스트)

---

### 3-3. MyBatis Mapper XML 신규 작성 — 추천채용정보

> 신규 파일: `src/main/resources/mappings/ParticipantJobRecommend-mapping.xml`

- [x] XML 파일 생성 (namespace 결정)
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
      "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="ParticipantJobRecommendDAO">
  ```
- [x] 추천 저장/갱신 쿼리 작성 (`insertOrUpdateRecommend`)
  - MERGE 문 또는 IF EXISTS-UPDATE-ELSE-INSERT 방식 적용 : MERGE 문 사용
- [x] 구직번호 기준 추천 목록 조회 쿼리 작성 (`selectRecommendList`)
- [x] 베스트 채용정보 단건 조회 쿼리 작성 (`selectBestRecommend`)
- [x] 구직번호 기준 전체 삭제 쿼리 작성 (`deleteRecommendByGujikNo`) — 재추천 시 기존 삭제용
- [x] `sql-map-config.xml`에 신규 mapper XML 등록
  ```xml
  <mapper resource="mappings/ParticipantJobRecommend-mapping.xml"/>
  ```

---

### 3-4. Bean(DTO) 수정 및 신규 생성

#### 3-4-1. 참여자 검색 조건 Bean 수정
> 수정 파일: `CounselMain/biz/[참여자SearchBean].java` (1주차에 확인한 클래스명 적용)

- [x] `집중알선여부` 필드 추가
  ```java
  // private String participantISIntensiveMediation; // 집중알선여부;  // "Y" / "N" / null(전체)
  // getter/setter 추가
  // searchType으로 검색을 진행하여 필드 추가없이 진행
  // searchType =='isIntensiveMediation'으로 검색 진행
  ```

#### 3-4-2. 참여자 DTO 수정
> 수정 파일: `CounselMain/biz/[참여자DTO].java`

- [x] `집중알선여부` 필드 추가 (SELECT 결과 매핑용)
  ```java
  private String participantISIntensiveMediation; // 집중알선여부;
  // getter/setter 추가
  ```

#### 3-4-3. 추천채용정보 DTO 신규 생성
> 신규 파일: `CounselMain/biz/recommend/ParticipantJobRecommendDTO.java`

- [x] 테이블 컬럼과 1:1 매핑되는 Bean 작성
  ```java
  public class ParticipantJobRecommendDTO {
    private int pk; // PK of the recommendation record
    private int jobSeekerNo; // FK of the 구직번호
    private String participantName; // 참여자명
    private String progressStage; // 진행단계
    private String education; // 학력
    private String major; // 전공
    private String categoryMajor; // 카테고리대분류
    private String categoryMiddle; // 카테고리중분류
    private String desiredJob; // 희망직무
    private String referralDetail; // 알선상세정보
    private String generatedSearchCondition; // 생성된검색조건
    private String recommendedJobCertNo; // 추천채용정보_구인인증번호
    private String recommendedJobUrl; // 추천채용정보_URL
    private String recommendedJobCompany; // 추천채용정보_기업명
    private String recommendedJobTitle; // 추천채용정보_제목
    private String recommendedJobIndustry; // 추천채용정보_업종
    private String bestJobInfo; // 베스트채용정보
    private Integer recommendationScore; // 추천점수
    private String recommendationReason; // 추천사유
    private String createdAt; // 저장일시
    private String updatedAt; // 수정일시
  }
  ```
- [x] `sql-map-config.xml`에 typeAlias 등록 여부 결정 및 반영

---

### 3-5. DAO 수정 및 신규 작성

#### 3-5-1. 참여자 DAO 수정
> 수정 파일: `CounselMain/biz/[참여자DAO].java`

- [x] 기존 검색 메서드 파라미터 DTO에 `집중알선여부` 포함 여부 확인
  - Bean 방식이면 필드 추가만으로 자동 반영
  - Map 방식이면 파라미터 추가 코드 수정 필요

#### 3-5-2. 추천채용정보 DAO 신규 작성
> 신규 파일: `CounselMain/biz/recommend/ParticipantJobRecommendDAO.java`

- [x] `SqlSessionTemplate` 주입 방식 확인 (기존 DAO 참고)
- [x] 메서드 목록 작성
  ```java
  private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticipantJobRecommendDAO.";

    // 추천 저장/갱신 (UPSERT)
    public int insertOrUpdateRecommend(ParticipantJobRecommendDTO dto) {
        return sqlSession.insert(ns + "insertOrUpdateRecommend", dto);
    }

    // 구직번호 기준 추천 목록 조회
    public List<ParticipantJobRecommendDTO> selectRecommendList(int jobSeekerNo) {
        return sqlSession.selectList(ns + "selectRecommendList", jobSeekerNo);
    }

    // 베스트 채용정보 단건 조회
    public ParticipantJobRecommendDTO selectBestRecommend(int jobSeekerNo) {
        return sqlSession.selectOne(ns + "selectBestRecommend", jobSeekerNo);
    }

    // 구직번호 기준 전체 삭제
    public int deleteRecommendByGujikNo(int jobSeekerNo) {
        return sqlSession.delete(ns + "deleteRecommendByGujikNo", jobSeekerNo);
    }
  ```

---

### 3-6. Service 수정 및 신규 작성

#### 3-6-1. 참여자 Service 수정
> 수정 파일: `CounselMain/biz/[참여자ServiceImpl].java`

- [x] 검색 서비스 메서드에 집중 알선 조건이 자동으로 반영되는지 확인
  - Bean 객체를 DAO에 그대로 전달하는 구조이면 별도 수정 불필요
  - 조건 분기가 Service 레이어에 있다면 수정 필요

#### 3-6-2. 추천채용정보 Service 인터페이스 신규 작성
> 신규 파일: `CounselMain/biz/recommend/ParticipantJobRecommendService.java`

- [x] 인터페이스 메서드 정의
  ```java
  public interface ParticipantJobRecommendService {
    List<ParticipantJobRecommendDTO> getRecommendList(int jobSeekerNo);
    ParticipantJobRecommendDTO getBestRecommend(int jobSeekerNo);
    boolean saveRecommend(ParticipantJobRecommendDTO dto);
    boolean deleteRecommend(int jobSeekerNo);
  }
  ```

#### 3-6-3. 추천채용정보 ServiceImpl 신규 작성
> 신규 파일: `CounselMain/biz/recommend/ParticipantJobRecommendServiceImpl.java`

- [x] `@Service` 어노테이션 적용
- [x] `ParticipantJobRecommendDAO` 주입
- [x] 인터페이스 메서드 구현
- [x] Transaction AOP Pointcut 포함 여부 확인
  - 현재 pointcut: `execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))`
  - `recommend` 하위 패키지가 포함되지 않을 경우 `RootConfig.java` pointcut 수정 필요
    ```java
    // 수정 전
    "execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))"
    // 수정 후 (하위 패키지 포함)
    "execution(* com.jobmoa.app.CounselMain.biz..*.*Impl.*(..))"
    ```

---

### 3-7. Spring Bean 등록 확인

- [x] `RootConfig.java` 에서 신규 DAO/Service 컴포넌트 스캔 대상 포함 여부 확인
  - `@ComponentScan` 범위에 `recommend` 패키지 포함 여부
- [x] 컴포넌트 스캔 범위 조정 필요 시 수정
- [x] 서버 기동 후 Bean 등록 오류 없음 확인

---

### 3-8. 빌드 및 기초 동작 확인

- [x] `./mvnw clean compile` 빌드 성공 확인
- [x] 서버 기동 후 기존 참여자 검색 기능 정상 동작 확인 (기존 기능 회귀 없음)
- [x] 신규 DAO 메서드 단순 호출 테스트 (빈 데이터로 오류 없음 확인)

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| DB 반영 완료 | 3개 DDL 실행 및 검증 | 🔲 |
| mapper XML 수정 | 집중알선여부 조건 추가 | 🔲 |
| mapper XML 신규 | 추천채용정보 CRUD mapper | 🔲 |
| Bean 수정/신규 | 검색조건Bean, 결과Bean, 추천Bean | 🔲 |
| DAO 수정/신규 | 참여자DAO, 추천DAO | 🔲 |
| Service 신규 | 추천Service 인터페이스 + Impl | 🔲 |
| 빌드 확인 | 컴파일 성공 + 서버 기동 성공 | 🔲 |

---

## 4. 변경 이력

| 날짜         | 버전   | 변경 내용                | 작성자 |
|------------|------|----------------------|-----|
| 2026-04-03 | v0.1 | 최초 작성                | SD  |
| 2026-04-09 | v0.2 | DTO,DAO등 생성 및 빌드 테스트 | SD  |