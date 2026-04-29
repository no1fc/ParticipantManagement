package test;

import com.jobmoa.app.CounselMain.biz.recommend.*;
import com.jobmoa.app.config.RootConfig;
import com.jobmoa.app.config.WebMvcConfig;
import com.jobmoa.app.config.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@SpringJUnitWebConfig
@Transactional // 각각의 테스트 메서드에 대해 트랜잭션을 시작하고, 테스트가 종료되면 롤백
@ContextConfiguration(classes = {RootConfig.class, WebMvcConfig.class, WebSocketConfig.class})
@WebAppConfiguration
public class testClass {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ParticipantJobRecommendServiceImpl geminiApiService;

    @Autowired
    private ParticipantJobRecommendDAO participantJobRecommendDAO;

    @Autowired
    private GeminiApiService geminiApi;

    private MockMvc mvc;

    // 테스트 대상 구직번호
    private static final int TEST_JOB_SEEKER_NO = 88493;

    @Test
    public void test() {
        System.out.println("test");
    }

    /**
     * [테스트 1] 참여자 기본정보 조회 - 경력, 희망급여 포함 확인
     */
    @Test
    public void testParticipantInfo() {
        System.out.println("========== [테스트 1] 참여자 기본정보 조회 ==========");
        RecommendParticipantDTO participant = participantJobRecommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);

        if (participant == null) {
            System.out.println("[WARN] 참여자 정보 없음 (구직번호: " + TEST_JOB_SEEKER_NO + ")");
            return;
        }

        System.out.println("구직번호: " + participant.getJobSeekerNo());
        System.out.println("참여자명: " + participant.getInfoName());
        System.out.println("학력: " + participant.getInfoEducation());
        System.out.println("전공: " + participant.getInfoMajor());
        System.out.println("주소: " + participant.getInfoAddress());
        System.out.println("경력: " + participant.getInfoCareer());
        System.out.println("희망급여: " + participant.getInfoDesiredSalary());
        System.out.println("========== [테스트 1] 완료 ==========\n");
    }

    /**
     * [테스트 2] 희망직무 카테고리 조회 + 관련 JOB_CATEGORY DB 조회
     */
    @Test
    public void testRelatedJobCategories() {
        System.out.println("========== [테스트 2] 관련 직종 카테고리 DB 조회 ==========");

        // 2-1. 참여자 희망직무 조회
        List<RecommendCategoryDTO> categoryList =
                participantJobRecommendDAO.getParticipantCategory(TEST_JOB_SEEKER_NO);

        System.out.println("[희망직무 목록] 총 " + categoryList.size() + "건");
        for (RecommendCategoryDTO cat : categoryList) {
            System.out.println("  순위: " + cat.getInfoRank()
                    + " | 대분류: " + cat.getCategoryMain()
                    + " | 중분류: " + cat.getCategoryMiddle()
                    + " | 희망직무: " + cat.getInfoJob());
        }

        // 2-2. 중분류명 추출
        List<String> midCategoryNames = categoryList.stream()
                .map(RecommendCategoryDTO::getCategoryMiddle)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        System.out.println("\n[추출된 중분류명] " + midCategoryNames);

        // 2-3. 관련 카테고리 서브트리 DB 조회
        if (midCategoryNames.isEmpty()) {
            System.out.println("[WARN] 중분류명이 없어 DB 조회 건너뜀");
            return;
        }

        List<JobCategoryDTO> relatedCategories =
                participantJobRecommendDAO.getRelatedJobCategories(midCategoryNames);

        System.out.println("\n[관련 카테고리 서브트리] 총 " + relatedCategories.size() + "건");
        for (JobCategoryDTO jc : relatedCategories) {
            String typeLabel;
            switch (jc.getCategoryType()) {
                case 1: typeLabel = "[대분류]"; break;
                case 2: typeLabel = "  [중분류]"; break;
                case 3: typeLabel = "    [소분류]"; break;
                default: typeLabel = "[?]";
            }
            System.out.println(typeLabel
                    + " ID: " + jc.getCategoryId()
                    + " | 명칭: " + jc.getCategoryName()
                    + " | 대분류ID: " + jc.getLargeCategoryId()
                    + " | 중분류ID: " + jc.getMidCategoryId());
        }
        System.out.println("========== [테스트 2] 완료 ==========\n");
    }

    /**
     * [테스트 3] 채용정보 후보군 조회 - 상세 필드(급여, 학력, 경력, 직무내용 등) 포함 확인
     */
    @Test
    public void testJobCandidatesDetail() {
        System.out.println("========== [테스트 3] 채용정보 후보군 상세 조회 ==========");

        // JSON 형식에 맞는 검색 조건으로 후보군 조회
        SearchConditionDTO condition = new SearchConditionDTO();
        List<String> keywords = List.of("컴퓨터공학", "통신공학", "IT 감리", "컴퓨터 하드웨어");
        condition.setKeywords(new ArrayList<>(keywords));
        List<String> jobsCode = List.of("131200", "132003", "131100");
        condition.setJobsCode(new ArrayList<>(jobsCode));
        condition.setJobCategory("연구 및 공학기술");
        condition.setIsAddress(false);
        condition.setMaxCount(5);

        List<JobCandidateDTO> candidates =
                participantJobRecommendDAO.selectJobInfoCandidates(condition);

        System.out.println("[후보군] 총 " + candidates.size() + "건\n");
        for (int i = 0; i < candidates.size(); i++) {
            JobCandidateDTO c = candidates.get(i);
            System.out.println("--- 후보 " + (i + 1) + " ---");
            System.out.println("  구인인증번호: " + c.getCertNo());
            System.out.println("  기업명: " + c.getCompanyName());
            System.out.println("  채용제목: " + c.getRecruitTitle());
            System.out.println("  업종: " + c.getIndustryType());
            System.out.println("  급여상세: " + c.getSalaryDesc());
            System.out.println("  최소학력: " + c.getMinEducation());
            System.out.println("  최대학력: " + c.getMaxEducation());
            System.out.println("  경력: " + c.getCareer());
            System.out.println("  회사규모: " + c.getBusinessSize());
            System.out.println("  관련직종: " + c.getRelatedJobs());
            System.out.println("  전공: " + c.getMajor());
            System.out.println("  자격면허: " + c.getCertificate());
            System.out.println("  직무내용: " + (c.getJobContent() != null
                    ? c.getJobContent().substring(0, Math.min(100, c.getJobContent().length())) + "..."
                    : "null"));
            System.out.println();
        }
        System.out.println("========== [테스트 3] 완료 ==========\n");
        /*
        ========== [테스트 3] 채용정보 후보군 상세 조회 ==========
[후보군] 총 5건

--- 후보 1 ---
  구인인증번호: KJKD002604220003
  기업명: 알에프에이치아이씨(주)
  채용제목: GaN RF &amp; Microwave 연구개발...
  업종: 기타 무선 통신장비 제조업
  급여상세: 215만원 ~ 215만원
  최소학력: 학력무관
  최대학력:
  경력: 관계없음
  회사규모: 중소기업
  관련직종: null
  전공: null
  자격면허: null
  직무내용: 통신,방산,산업/의료/과학 부문 제품 개발...

--- 후보 2 ---
  구인인증번호: K131412604200040
  기업명: 주식회사굿링크
  채용제목: 네트워크, 정보보안시스템, CCTV관제솔루션 구축...
  업종: 컴퓨터 및 주변장치, 소프트웨어 소매업
  급여상세: 3000만원
  최소학력: 학력무관
  최대학력:
  경력: 관계없음
  회사규모: 중소기업
  관련직종: IT 기술지원 전문가 통신·인터넷 케이블 설치·수리원
  전공: 정보·통신공학(학과 : 네트워크전공) 전산학·컴퓨터공학(학과 : 전산정보학과)
  자격면허: 정보통신산업기사,정보처리기사
  직무내용: [모집직무]
네트워크 엔지니어, 시스템 엔지니어, 정보보안 전문가, CCTV 엔지니어, IT 관리자

[직무소개]
우리 회사는 다양한 IT 분야의 솔루션을 개발하고 제공합니...

--- 후보 3 ---
  구인인증번호: KF10742602260002
  기업명: (주) 포스텍
  채용제목: Fiber Array Engineer[Siliph...
  업종: 유선 통신장비 제조업
  급여상세: 5000만원 ~ 7000만원
  최소학력: 대졸(4년)
  최대학력: 석사
  경력: 관계없음
  회사규모: 중소기업
  관련직종: 기계 부품 조립원
  전공: 반도체·세라믹공학(학과 : 반도체전공) 전자공학(학과 : 전기전자공학계열)
  자격면허: null
  직무내용: [안산 919취업광장 현장면접]
-일시 : 2026년 3월 19일(목) 14:00 ~16:00
-장소 : 안산올림픽기념관 체육관
-문의 : 안산새일센터 031-439-2060

1...

--- 후보 4 ---
  구인인증번호: K131132603060035
  기업명: 주식회사 선진기술
  채용제목: AMR로봇 센서 융합, 자율주행 프로그램 개발자
  업종: 그 외 기타 일반목적용 기계 제조업
  급여상세: 4000만원 ~ 6000만원
  최소학력: 학력무관
  최대학력:
  경력: 경력
  회사규모: null
  관련직종: 로봇공학 기술자 및 연구원 시스템 소프트웨어 개발자(프로그래머)
  전공: null
  자격면허: null
  직무내용: 1. 자율주행 시스템 개발 및 통합
함정(Ship) 견인을 위한 AMR(Autonomous Mobile Robot) 자율주행 알고리즘 설계 및 구현
ROS2 기반 시스템 아키텍...

--- 후보 5 ---
  구인인증번호: K120252603110012
  기업명: 주식회사 디자인빌드아키텍트
  채용제목: 전자,하드웨어,설계
  업종: 인테리어 디자인업
  급여상세: 3500만원 ~ 5000만원
  최소학력: 대졸(2~3년)
  최대학력: 석사
  경력: 경력
  회사규모: 중소기업
  관련직종: null
  전공: null
  자격면허: null
  직무내용: 2026년 (주) 디자인빌드 아키텍트 경력직 모집

포지션  하드웨어 개발직군 채용

담당업무     -사업용 Display 하드웨어 회로 개발

            ...

========== [테스트 3] 완료 ==========
         */
    }

    /**
     * [테스트 4] Gemini 1단계 - 검색 조건 생성 (동적 카테고리 프롬프트 확인)
     * AI 호출 포함 - generateSearchCondition의 프롬프트와 응답 로그 확인
     */
    @Test
    public void testGenerateSearchCondition() {
        System.out.println("========== [테스트 4] Gemini 1단계: 검색 조건 생성 ==========");

        RecommendParticipantDTO participant =
                participantJobRecommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);
        List<RecommendCategoryDTO> categoryList =
                participantJobRecommendDAO.getParticipantCategory(TEST_JOB_SEEKER_NO);
        RecommendReferralDTO referralInfo =
                participantJobRecommendDAO.getParticipantReferral(TEST_JOB_SEEKER_NO);

        // 관련 카테고리 DB 조회
        List<String> midCategoryNames = categoryList.stream()
                .map(RecommendCategoryDTO::getCategoryMiddle)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        List<JobCategoryDTO> relatedCategories = Collections.emptyList();
        if (!midCategoryNames.isEmpty()) {
            relatedCategories = participantJobRecommendDAO.getRelatedJobCategories(midCategoryNames);
        }

        System.out.println("[입력 데이터]");
        System.out.println("  참여자: " + participant.getInfoName()
                + " | 학력: " + participant.getInfoEducation()
                + " | 전공: " + participant.getInfoMajor()
                + " | 경력: " + participant.getInfoCareer()
                + " | 희망급여: " + participant.getInfoDesiredSalary());
        System.out.println("  희망직무: " + categoryList.size() + "건");
        System.out.println("  관련 카테고리: " + relatedCategories.size() + "건");
        System.out.println("  알선상세정보: " + (referralInfo != null ? referralInfo.getInfoAlsonDetail() : "없음"));

        // Gemini API 호출 (콘솔에 프롬프트/응답 자동 출력됨)
        System.out.println("\n[Gemini API 호출 시작]");
        SearchConditionDTO result = geminiApi.generateSearchCondition(
                participant, referralInfo, categoryList, relatedCategories);

        System.out.println("\n[검색 조건 생성 결과]");
        System.out.println("  keywords: " + result.getKeywords());
        System.out.println("  jobs_code: " + result.getJobsCode());
        System.out.println("  jobCategory: " + result.getJobCategory());
        System.out.println("  isAddress: " + result.getIsAddress());
        System.out.println("  largescaleUnits: " + result.getLargescaleUnits());
        System.out.println("  localUnits: " + result.getLocalUnits());
        System.out.println("  parseError: " + result.getParseError());
        System.out.println("========== [테스트 4] 완료 ==========\n");
        /*
          ========== [테스트 4] Gemini 1단계: 검색 조건 생성 ==========
          [입력 데이터]
            참여자: 서지윤 | 학력: 고졸 | 전공: 컴퓨터공학 | 경력: 5 | 희망급여: 350
            희망직무: 3건
            관련 카테고리: 12건
            알선상세정보: 핵심 역량이 뛰어난 인제로 기술능력 학습이 매우 뛰어남

          [Gemini API 호출 시작]
          generateSearchCondition prompt: [다음 구직자 정보를 기반으로 고용24 채용정보 DB에서 검색할 조건을 JSON 형식으로 생성해 주세요.
          구직자 정보:
          - 학력: 고졸
          - 전공: 컴퓨터공학
          - 주소: 강원특별자치도 강릉시 사천면 가둔지길 2
          - 상세정보: 핵심 역량이 뛰어난 인제로 기술능력 학습이 매우 뛰어남
          [희망 0순위 :  - 카테고리 대분류: 연구 및 공학기술
            - 카테고리 중분류: 컴퓨터하드웨어·통신공학
            - 희망직무: 통신공학 기술자 및 연구원
          , 희망 1순위 :  - 카테고리 대분류: 연구 및 공학기술
            - 카테고리 중분류: 컴퓨터시스템
            - 희망직무: IT 감리 전문가(시스템 감리)
          , 희망 2순위 :  - 카테고리 대분류: 연구 및 공학기술
            - 카테고리 중분류: 컴퓨터하드웨어·통신공학
            - 희망직무: 컴퓨터 하드웨어 기술자 및 연구원
          ]
          직종분류코드 목록:
          카테고리 ID    카테고리명칭
          2연구 및 공학기술
          22   컴퓨터하드웨어·통신공학
          131100         컴퓨터 하드웨어 기술자 및 연구원
          131200         통신공학 기술자 및 연구원
          131201         통신기기·장비 개발자 및 연구원
          131202         통신기술 개발자 및 통신망 운영 기술자
          131203         통신공사 감리 기술자
          132000         컴퓨터시스템 전문가
          23   컴퓨터시스템
          132001         IT 컨설턴트
          132002         컴퓨터시스템 설계 및 분석가
          132003         IT 감리 전문가(시스템 감리)

          위 직종분류코드를 참고하여 구직자에게 가장 적합한 직종분류코드를 추출하세요.

           상세정보에 희망지역이 있다면 isAddress true로 변경하고 광역자치 단위, 기초자치 단위를 작성하세요.

           광역자치 단위는 서울, 제주, 대전, 경기도, 인천 등 (시, 도)를 제외하고 작성하세요.

           기초자치 단위는 강남구, 서귀포시, 유성구, 수원시 등 (구, 시, 군)을 작성하세요.
          응답 형식 (JSON만 출력):
          {
            "keywords": ["키워드1", "키워드2"],
            "jobCategory": "직무분류",
            "educationLevel": "학력조건",
            "industryType": "업종분류",
            "jobs_code": ["직종분류코드1", "직종분류코드2"],
            "jobs_nm": "모집직종",
            "isAddress": false,
            "largescaleUnits": ["광역자치 단위1", "광역자치 단위2"],
            "localUnits": ["기초자치 단위1", "기초자치 단위2"]
          }]
          generateSearchCondition response: [```json
          {
            "keywords": ["컴퓨터공학", "통신공학", "IT 감리", "컴퓨터 하드웨어"],
            "jobCategory": "연구 및 공학기술",
            "educationLevel": "고졸",
            "industryType": "IT/정보통신",
            "jobs_code": ["131200", "132003", "131100"],
            "jobs_nm": "통신공학 기술자 및 연구원, IT 감리 전문가(시스템 감리), 컴퓨터 하드웨어 기술자 및 연구원",
            "isAddress": false,
            "largescaleUnits": [],
            "localUnits": []
          }
          ```]

          [검색 조건 생성 결과]
            keywords: [컴퓨터공학, 통신공학, IT 감리, 컴퓨터 하드웨어]
            jobs_code: [131200, 132003, 131100]
            jobCategory: 연구 및 공학기술
            isAddress: false
            largescaleUnits: []
            localUnits: []
            parseError: false
          ========== [테스트 4] 완료 ==========

         */
    }

    /**
     * [테스트 5] Gemini 2단계 - 최적 채용정보 선별 (향상된 프롬프트 확인)
     * AI 호출 포함 - selectBestFromCandidates의 프롬프트와 응답 로그 확인
     */
    @Test
    public void testSelectBestFromCandidates() {
        System.out.println("========== [테스트 5] Gemini 2단계: 최적 채용정보 선별 ==========");

        // 참여자 정보 조회
        RecommendParticipantDTO participant =
                participantJobRecommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);
        RecommendReferralDTO referralInfo =
                participantJobRecommendDAO.getParticipantReferral(TEST_JOB_SEEKER_NO);

        String alsonDetail = (referralInfo != null) ? referralInfo.getInfoAlsonDetail() : "";

        System.out.println("[참여자 프로필]");
        System.out.println("  학력: " + participant.getInfoEducation());
        System.out.println("  전공: " + participant.getInfoMajor());
        System.out.println("  주소: " + participant.getInfoAddress());
        System.out.println("  경력: " + participant.getInfoCareer());
        System.out.println("  희망급여: " + participant.getInfoDesiredSalary());
        System.out.println("  알선상세정보: " + alsonDetail);

        // 후보군 조회 (간단 키워드로)
        SearchConditionDTO condition = new SearchConditionDTO();
        List<String> keywords = List.of("컴퓨터공학", "통신공학", "IT 감리", "컴퓨터 하드웨어");
        condition.setKeywords(new ArrayList<>(keywords));
        List<String> jobsCode = List.of("131200", "132003", "131100");
        condition.setJobsCode(new ArrayList<>(jobsCode));
        condition.setJobCategory("연구 및 공학기술");
        condition.setIsAddress(false);
        condition.setMaxCount(5);

        List<JobCandidateDTO> candidates =
                participantJobRecommendDAO.selectJobInfoCandidates(condition);

        System.out.println("\n[후보군] " + candidates.size() + "건");
        for (JobCandidateDTO c : candidates) {
            System.out.println("  - " + c.getCertNo() + " | " + c.getCompanyName()
                    + " | " + c.getRecruitTitle()
                    + " | 급여: " + c.getSalaryDesc()
                    + " | 학력: " + c.getMinEducation()
                    + " | 경력: " + c.getCareer());
        }

        if (candidates.isEmpty()) {
            System.out.println("[WARN] 후보군이 없어 Gemini 2단계 테스트 건너뜀");
            return;
        }

        // Gemini API 호출 (콘솔에 프롬프트/응답 자동 출력됨)
        System.out.println("\n[Gemini API 호출 시작]");
        BestSelectionResultDTO result = geminiApi.selectBestFromCandidates(
                candidates, participant, alsonDetail);

        System.out.println("\n[최적 채용정보 선별 결과]");
        System.out.println("  bestGujinNo: " + result.getBestGujinNo());
        if (result.getScores() != null) {
            for (RecommendationScoreDTO score : result.getScores()) {
                System.out.println("  - 구인인증번호: " + score.getCertNo()
                        + " | 점수: " + score.getScore()
                        + " | 사유: " + score.getReason());
            }
        }
        System.out.println("========== [테스트 5] 완료 ==========\n");
        /*
        ========== [테스트 5] Gemini 2단계: 최적 채용정보 선별 ==========
[참여자 프로필]
  학력: 고졸
  전공: 컴퓨터공학
  주소: 강원특별자치도 강릉시 사천면 가둔지길 2
  경력: 5
  희망급여: 350
  알선상세정보: 핵심 역량이 뛰어난 인제로 기술능력 학습이 매우 뛰어남

[후보군] 5건
  - KJKD002604220003 | 알에프에이치아이씨(주) | GaN RF &amp; Microwave 연구개발... | 급여: 215만원 ~ 215만원 | 학력: 학력무관 | 경력: 관계없음
  - K131412604200040 | 주식회사굿링크 | 네트워크, 정보보안시스템, CCTV관제솔루션 구축... | 급여: 3000만원 | 학력: 학력무관 | 경력: 관계없음
  - KF10742602260002 | (주) 포스텍 | Fiber Array Engineer[Siliph... | 급여: 5000만원 ~ 7000만원 | 학력: 대졸(4년) | 경력: 관계없음
  - K131132603060035 | 주식회사 선진기술 | AMR로봇 센서 융합, 자율주행 프로그램 개발자 | 급여: 4000만원 ~ 6000만원 | 학력: 학력무관 | 경력: 경력
  - K120252603110012 | 주식회사 디자인빌드아키텍트 | 전자,하드웨어,설계 | 급여: 3500만원 ~ 5000만원 | 학력: 대졸(2~3년) | 경력: 경력

[Gemini API 호출 시작]
------------ selectBestFromCandidates 시작 -------------
prompt: 다음 구직자 프로필과 채용공고 후보군을 종합적으로 비교하여 가장 적합한 채용공고를 선정하고 각 점수를 매겨주세요.

■ 구직자 프로필:
- 학력: 고졸
- 전공: 컴퓨터공학
- 주소: 강원특별자치도 강릉시 사천면 가둔지길 2
- 경력: 5
- 희망급여: 350
- 알선상세정보: 핵심 역량이 뛰어난 인제로 기술능력 학습이 매우 뛰어남

■ 채용공고 후보군:
1. [구인인증번호: KJKD002604220003]
   기업명: 알에프에이치아이씨(주)
   채용제목: GaN RF &amp; Microwave 연구개발...
   업종: 기타 무선 통신장비 제조업
   급여: 215만원 ~ 215만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 통신,방산,산업/의료/과학 부문 제품 개발

2. [구인인증번호: K131412604200040]
   기업명: 주식회사굿링크
   채용제목: 네트워크, 정보보안시스템, CCTV관제솔루션 구축...
   업종: 컴퓨터 및 주변장치, 소프트웨어 소매업
   급여: 3000만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: IT 기술지원 전문가 통신·인터넷 케이블 설치·수리원
   전공: 정보·통신공학(학과 : 네트워크전공) 전산학·컴퓨터공학(학과 : 전산정보학과)
   자격면허: 정보통신산업기사,정보처리기사
   직무내용: [모집직무]
네트워크 엔지니어, 시스템 엔지니어, 정보보안 전문가, CCTV 엔지니어, IT 관리자

[직무소개]
우리 회사는 다양한 IT 분야의 솔루션을 개발하고 제공합니다. 네트워크, 정보보안시스템, CCTV관제솔루션 등을 전문으로 다루며, 이번 채용을 통해 이러한 시스템의 구축 및 유지보수에 핵심 역할을 수행할 인재를 찾고 있습니다.

[주요업무]
- 네트워크 인프라 설계, 구축 및 관리
- 정보보안 시스템 구축 및 운영
- CCTV 관제 시스템 구축 및 관리
- 시스템 성능 최적화 및 문제 해결
- IT 기술 관련 도구 및 소프트웨어 활용

3. [구인인증번호: KF10742602260002]
   기업명: (주) 포스텍
   채용제목: Fiber Array Engineer[Siliph...
   업종: 유선 통신장비 제조업
   급여: 5000만원 ~ 7000만원
   학력요건: 대졸(4년) ~ 석사
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 기계 부품 조립원
   전공: 반도체·세라믹공학(학과 : 반도체전공) 전자공학(학과 : 전기전자공학계열)
   자격면허: 정보없음
   직무내용: [안산 919취업광장 현장면접]
-일시 : 2026년 3월 19일(목) 14:00 ~16:00
-장소 : 안산올림픽기념관 체육관
-문의 : 안산새일센터 031-439-2060

1. 직무
Fiber Array 공정관리 , 광전복합케이블 Assy
-Fiber Array Engineer[Siliphotonics] 제품설계/공정작업/검사
-광전복합케이블, 전기커이블  lnterconnect
-공정관리, 표준화

2. 경력
-전자통신부품 제조
-광통신부품제조
-반도체 후공정(패키징, 테스트)

3. 우대사항
-유관업무 경력자
-관련전공자
-인근거주, 자차통근

4. [구인인증번호: K131132603060035]
   기업명: 주식회사 선진기술
   채용제목: AMR로봇 센서 융합, 자율주행 프로그램 개발자
   업종: 그 외 기타 일반목적용 기계 제조업
   급여: 4000만원 ~ 6000만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 정보없음
   관련직종: 로봇공학 기술자 및 연구원 시스템 소프트웨어 개발자(프로그래머)
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 1. 자율주행 시스템 개발 및 통합
함정(Ship) 견인을 위한 AMR(Autonomous Mobile Robot) 자율주행 알고리즘 설계 및 구현
ROS2 기반 시스템 아키텍처 설계 및 노드 개발
센서 데이터 처리 및 융합(Fusion) — LiDAR, UWB, IMU, GNSS, Vision 등 다중 센서의 실시간 통합
3D SLAM (Simultaneous Localization and Mapping) 구현 및 지도 생성/갱신

2. 경로 계획 및 주행 제어
복잡한 조선소/해군 정비창 환경에서의 경로 계획(Path Planning) 및 장애물 회피 알고리즘 개발
자율주행을 위한 로컬/글로벌 네비게이션 및 동적 재계획(Local Re-planning) 기능 구현

3. 센서 인터페이스 및 통신
UWB·RTK·IMU를 통한 위치인식 시스템 개발 및 보정 알고리즘 구현
ROS2 DDS 기반 통신 네트워크 구성 및 최적화

4. 시스템 통합 및 실증
실제 함정 견인 환경에서의 센서 캘리브레이션, 경로 테스트, 주행 로그 분석
자율주행 데이터 로깅 및 Excel/DB 시각화 시스템 개발
안전 규격(ISO 3691-4 등) 기반의 자율주행 안전로직 설계

5. AI 기반 인식 및 판단 고도화
Vision 기반 Object Detection / Obstacle Classification 알고리즘 적용
자율주행 의사결정(Decision-Making) 및 예측제어 모델 개발

5. [구인인증번호: K120252603110012]
   기업명: 주식회사 디자인빌드아키텍트
   채용제목: 전자,하드웨어,설계
   업종: 인테리어 디자인업
   급여: 3500만원 ~ 5000만원
   학력요건: 대졸(2~3년) ~ 석사
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 2026년 (주) 디자인빌드 아키텍트 경력직 모집

포지션  하드웨어 개발직군 채용

담당업무     -사업용 Display 하드웨어 회로 개발

                 -회로 및 PCB설계

                 -제품 테스트 및 인증대응

■ 평가 기준:
- 구직자의 학력·전공과 채용공고의 학력요건·전공 일치도
- 구직자의 경력과 채용공고의 경력요건 부합 여부
- 구직자의 희망급여와 채용공고 급여 수준 적합성
- 알선상세정보와 직무내용·관련직종의 연관성
- 자격면허 보유 여부에 따른 가점

만약 후보군이 없다면 가장 최신 정보를 제공해주세요.

응답 형식 (JSON만 출력):
{
  "bestGujinNo": "최적채용공고구인인증번호",
  "scores": [
    {"구인인증번호": "...", "score": 85, "reason": "추천사유"}
  ]
}
response: ```json
{
  "bestGujinNo": "K131132603060035",
  "scores": [
    {
      "구인인증번호": "K131132603060035",
      "score": 95,
      "reason": "학력 무관 조건으로 구직자의 학력 사항이 충족되며, 5년의 경력과 컴퓨터공학 전공이 AMR 로봇 프로그램 개발 직무와 매우 높은 연관성을 가집니다. 특히 희망급여(월 350만 원 ≒ 연 4,200만 원)가 공고의 급여 범위(4,000만~6,000만 원) 내에 정확히 포함되며, 구직자의 '뛰어난 기술 학습 능력'이 ROS2, SLAM 등 고난도 기술 스택이 요구되는 직무 특성에 매우 적합합니다."
    },
    {
      "구인인증번호": "K131412604200040",
      "score": 75,
      "reason": "학력 무관이며 컴퓨터공학 전공 및 IT 기술지원 직무가 구직자의 전공과 일치합니다. 다만, 제시된 급여(3,000만 원)가 구직자의 희망 수준보다 낮아 급여 적합성에서 감점이 있었습니다."
    },
    {
      "구인인증번호": "KJKD002604220003",
      "score": 40,
      "reason": "학력 및 경력 요건은 충족하나, 제시된 급여(월 215만 원)가 희망급여와 큰 차이가 있으며, RF/마이크로웨이브 개발 직무가 일반적인 컴퓨터공학 전공 역량과는 다소 거리가 있습니다."
    },
    {
      "구인인증번호": "K120252603110012",
      "score": 30,
      "reason": "급여 수준은 적절하나, 학력 요건(전문대졸 이상)을 충족하지 못하며, 직무 내용이 소프트웨어가 아닌 하드웨어 회로 및 PCB 설계 중심이라 전공 적합성이 낮습니다."
    },
    {
      "구인인증번호": "KF10742602260002",
      "score": 20,
      "reason": "급여 수준은 매우 높으나, 학력 요건(대졸 4년 이상)이 구직자의 학력과 맞지 않으며, 직무 내용(광전복합케이블 공정관리)이 컴퓨터공학 전공 및 소프트웨어 역량과 연관성이 매우 낮습니다."
    }
  ]
}
```
------------ selectBestFromCandidates 종료 -------------

[최적 채용정보 선별 결과]
  bestGujinNo: K131132603060035
  - 구인인증번호: K131132603060035 | 점수: 95 | 사유: 학력 무관 조건으로 구직자의 학력 사항이 충족되며, 5년의 경력과 컴퓨터공학 전공이 AMR 로봇 프로그램 개발 직무와 매우 높은 연관성을 가집니다. 특히 희망급여(월 350만 원 ≒ 연 4,200만 원)가 공고의 급여 범위(4,000만~6,000만 원) 내에 정확히 포함되며, 구직자의 '뛰어난 기술 학습 능력'이 ROS2, SLAM 등 고난도 기술 스택이 요구되는 직무 특성에 매우 적합합니다.
  - 구인인증번호: K131412604200040 | 점수: 75 | 사유: 학력 무관이며 컴퓨터공학 전공 및 IT 기술지원 직무가 구직자의 전공과 일치합니다. 다만, 제시된 급여(3,000만 원)가 구직자의 희망 수준보다 낮아 급여 적합성에서 감점이 있었습니다.
  - 구인인증번호: KJKD002604220003 | 점수: 40 | 사유: 학력 및 경력 요건은 충족하나, 제시된 급여(월 215만 원)가 희망급여와 큰 차이가 있으며, RF/마이크로웨이브 개발 직무가 일반적인 컴퓨터공학 전공 역량과는 다소 거리가 있습니다.
  - 구인인증번호: K120252603110012 | 점수: 30 | 사유: 급여 수준은 적절하나, 학력 요건(전문대졸 이상)을 충족하지 못하며, 직무 내용이 소프트웨어가 아닌 하드웨어 회로 및 PCB 설계 중심이라 전공 적합성이 낮습니다.
  - 구인인증번호: KF10742602260002 | 점수: 20 | 사유: 급여 수준은 매우 높으나, 학력 요건(대졸 4년 이상)이 구직자의 학력과 맞지 않으며, 직무 내용(광전복합케이블 공정관리)이 컴퓨터공학 전공 및 소프트웨어 역량과 연관성이 매우 낮습니다.
========== [테스트 5] 완료 ==========
         */
    }

    /**
     * [테스트 6] 전체 통합 테스트 - processAndSaveRecommend 전체 플로우 실행
     * DB 커밋 포함 - 1단계(검색조건생성) → 후보군조회 → 2단계(최적선별) → 저장
     */
    @Test
    @Commit // 트랜잭션을 롤백하지 않고 실제 DB에 커밋
    public void geminiApiServiceTest() {
        System.out.println("------------ geminiApiServiceTest 시작 -------------");
        ProcessRecommendResultDTO p  =  geminiApiService.processAndSaveRecommend(TEST_JOB_SEEKER_NO, false);
        System.out.println("geminiApiServiceTest");
        System.out.println("geminiApiServiceTest result : ["+p+"]");

        System.out.println("------------ geminiApiServiceTest 종료 -------------");

        /*
        ------------ geminiApiServiceTest 시작 -------------
2026-04-23 16:17:47.855 [main] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [processAndSaveRecommend] 메서드 수행 전 호출
2026-04-23 16:17:47.961 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendServiceImpl - [추천저장] 관련 카테고리 조회 midCategoryNames=[경영지원 사무], 결과=20건
generateSearchCondition prompt: [다음 구직자 정보를 기반으로 고용24 채용정보 DB에서 검색할 조건을 JSON 형식으로 생성해 주세요.
구직자 정보:
- 학력: 정보없음
- 전공: 가족사회복지학
- 주소: 서울특별시 양천구
- 상세정보: - 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함
[희망 0순위 :  - 카테고리 대분류: 경영·사무·금융·보험
  - 카테고리 중분류: 경영지원 사무
  - 희망직무: 사무보조원
]
직종분류코드 목록:
카테고리 ID    카테고리명칭
1경영·사무·금융·보험
17   경영지원 사무
215200         대학 교육 조교(TA) 및 연구 조교(RA)
222000         법률 사무원
26101         경영 기획 사무원
26102         마케팅·광고·홍보·상품기획 사무원
26103         영업 기획·관리·지원 사무원
26104         분양·임대 사무원
26200         인사·교육·훈련 사무원
26201         인사 사무원
26202         노무 사무원
26203         교육·훈련 사무원
26300         총무 사무원 및 대학 행정조교
26301         총무 및 일반 사무원
26302         병원행정 사무원(원무)
26303         학교행정 사무원(교무)
26304         대학 행정조교
26305         협회·회원단체 사무원
26306         기숙사사감 및 독서실·고시원 총무
26400         감사 사무원

위 직종분류코드를 참고하여 구직자에게 가장 적합한 직종분류코드를 추출하세요.

 상세정보에 희망지역이 있다면 isAddress true로 변경하고 광역자치 단위, 기초자치 단위를 작성하세요.

 광역자치 단위는 서울, 제주, 대전, 경기도, 인천 등 (시, 도)를 제외하고 작성하세요.

 기초자치 단위는 강남구, 서귀포시, 유성구, 수원시 등 (구, 시, 군)을 작성하세요.
응답 형식 (JSON만 출력):
{
  "keywords": ["키워드1", "키워드2"],
  "jobCategory": "직무분류",
  "educationLevel": "학력조건",
  "industryType": "업종분류",
  "jobs_code": ["직종분류코드1", "직종분류코드2"],
  "jobs_nm": "모집직종",
  "isAddress": false,
  "largescaleUnits": ["광역자치 단위1", "광역자치 단위2"],
  "localUnits": ["기초자치 단위1", "기초자치 단위2"]
}]
generateSearchCondition response: [```json
{
  "keywords": ["일반사무", "사무보조"],
  "jobCategory": "경영지원 사무",
  "educationLevel": "",
  "industryType": "",
  "jobs_code": ["26301"],
  "jobs_nm": "총무 및 일반 사무원",
  "isAddress": true,
  "largescaleUnits": ["서울"],
  "localUnits": ["양천구"]
}
```]
2026-04-23 16:18:12.971 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendServiceImpl - [추천저장] Gemini 검색조건 생성 jobSeekerNo=88493, searchCondition=SearchConditionDTO(keywords=[일반사무, 사무보조], jobsCode=[26301], jobCategory=경영지원 사무, maxCount=null, parseError=false, isAddress=true, largescaleUnits=[서울], localUnits=[양천구])
2026-04-23 16:18:12.973 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendServiceImpl - [추천저장] Gemini 검색조건 생성 jobSeekerNo=88493, searchCondition=SearchConditionDTO(keywords=[일반사무, 사무보조], jobsCode=[26301], jobCategory=경영지원 사무, maxCount=20, parseError=false, isAddress=true, largescaleUnits=[서울], localUnits=[양천구])
------------ selectBestFromCandidates 시작 -------------
prompt: 다음 구직자 프로필과 채용공고 후보군을 종합적으로 비교하여 가장 적합한 채용공고를 선정하고 각 점수를 매겨주세요.

■ 구직자 프로필:
- 학력: 정보없음
- 전공: 가족사회복지학
- 주소: 서울특별시 양천구
- 경력: 정보없음
- 희망급여: 250
- 알선상세정보: - 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함

■ 채용공고 후보군:
1. [구인인증번호: K141322604010008]
   기업명: 주식회사 산업전기
   채용제목: -채용대행-산업전기 사무보조 직원 채용
   업종: 건설업본사
   급여: 2947만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 경리 사무원(건설) 사무 보조원(일반사업체)
   전공: 정보없음
   자격면허: 정보없음
   직무내용: ▶ 영주고용센터 채용대행사업장(서류심사대행)입니다
▶ 접수방법: 고용24 입사지원(이력서, 자기소개서,경력증명서)
▶ 채용담당자에게 지원서류 이메일 전송 시 제목은 사업장명(지원분야)-지원자 이름으로 표기하여 주시기 바랍니다.
▶ 기타 문의사항은 054-639-1146로 전화 주시기 바랍니다.

[주요업무]
- 영수증 정리 및 관리
- 세금 계산서 발행 및 관리
- 월별 자재 마감 및 거래명세표 관리
- 총무 업무 지원(신용평가신청, 명함제작, 우편물관리등)
- 입찰업무(투찰, 적격심사서류 준비등)
- 기술자 승급 교육 신청 및 자격증 관리
[근무시간]:08:30~17:30(휴게시간1시간)

2. [구인인증번호: K160012604140025]
   기업명: 삼양물류(주)
   채용제목: 경리/사무보조 채용
   업종: 일반 화물자동차 운송업
   급여: 2700만원
   학력요건: 학력무관
   경력요건: 신입
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: * 담당업무

문서 작성, 사무실 전화 응대,매입 매출 회계 관리, 급여 관리

위수탁 차주 관리, 계산서 발행 등

경리 및 사무보조 업무

*근무시간
월~금 (주5일 근무) 9시~17시반까지

상여금 별도, 휴가비 지원

3. [구인인증번호: K140112604230001]
   기업명: (주)대경티엠에스
   채용제목: iM뱅크 현수송센터 사무원 모집
   업종: 기타 사무지원 서비스업
   급여: 219만원
   학력요건: 대졸(2~3년)
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: iM뱅크 현수송센터 사무원 모집

■ 채용분야 - iM뱅크 현수송센터 사무원 모집

■ 채용인원 - 1명

■ 근 무 지 –  iM뱅크 본점 (대구시 수성구 수성동2가 달구벌대로 2310)

■ 업무내용 - 현금 정사 업무

■ 계약기간 - 1년 + 1년

■ 지원자격
- 전문대졸 이상의 학력 소유자
- 신용불량자 지원불가
- 관련직종 경력자 우대

■ 급여  - <매년 급여 인상!!>
- 월급여 : 2,191,880원 이상 + 연차수당(별도지급)


■ 근무조건
[평일] 09:00~18:00
[토일요일 및 국공휴일] 휴무

■ 제출서류
- 첨부된 자사 입사지원서 및 자기소개서

■ 선발방법
① 입사지원서 워크넷 입사지원 및 e-메일 지원
② 서류전형 합격자에 한해 개별통보


■ 채용일정
- 서류마감 : 2026년 05월 7일까지
- 합격발표 : 추후 개별통보
상황에 따라 조기 마감될 수 있으니 빠른 지원 부탁드립니다.
-------------------------------------------------------------------------
▶대구고용센터 서류접수 채용대행 중입니다.

4. [구인인증번호: K163012604230002]
   기업명: 주식회사하요
   채용제목: (주)하요 경리 사무직 직원구합니다
   업종: 전기용품 및 조명장치 소매업
   급여: 240만원 ~ 240만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 매장 정리원(매장 보조원) 전산 자료 입력원
   전공: 정보없음
   자격면허: 자동차운전면허1종보통
   직무내용: 전기자재 및 잡화 도소매 경리(매장관리 및 사무보조) 구합니다.


* 모집요강

○ 담당업무
1. 거래처 매입, 매출 자료관리
2. 매장관리(물품 운반, 정리, 진열, 판매 및 지원)
3. 자재 재고파악 및 정리, 관리
4. 기타 사무 업무관리



○ 자격요건
- 고졸이상(대졸우대),
- 장기근무 가능자(최소 1년)
- 평생직장생각시 우대조건 있음.
- 필수: 운전면허 및 차량보유자
- 고용형태 : 정규직
- 모집인원 : 1명



○ 근무조건

- 급여 월급 240만원 ~ 240만원

- 근무시간
 월~금 08:30~17:30 (협의가능)

- 복리후생

식사제공(중식시간대 내근시), 연차제공(수습2달이후), 법정공휴일 휴무, 신정 설 추석 명절연휴 휴무

- 기타사항 : 4대보험 가입, 퇴직금 지급

5. [구인인증번호: K130232604230003]
   기업명: 제일산업
   채용제목: 관리부 사무원 모집 (단순경리업무)
   업종: 구조용 금속 판제품 및 공작물 제조업
   급여: 3000만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 정보없음
   관련직종: 단순 경리 사무원 총무 및 일반 사무원
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 제일산업

당사는 1997년 11월 20일 설립된 덕트(Duct), 댐퍼(Damper), 소음기(Silencer), Filter Housing, 제관물을 설계, 제조하는 중소기업입니다.
다년간 축적된 기술력과 현장 경험을 바탕으로 설계부터 제작까기 전 공정을 직접 수행하며,
항상 최고 품질의 제품을 제공하며, 고객 만족을 최우선 가치로 삼고 있습니다.

또한 단순 제조에 그치지 않고,
기술 영업 역량 강화 및 다양한 개발 과제를 위한 R&D 기술 연구를 지속적으로 추진하며,
변화하는 산업 환경에 능동적으로 대응하고 있습니다.

안정적인 기반 위에서 기술과 사람이 함께 성장하는 회사로,
책임감 있고 성실한 인재와의 만남을 기대합니다.

-------------------------------------------------------------------------

• 주요업무 : 근태 및 식수 관리
                  비품 및 소무품 구매 및 관리
                  운송관리 (차량 요청, 대장관리정리, 인수증 작성 및 관리)
                  사내 그룹웨어 관리 (다우오피스)
		  광고관리 (온라인광고)
		  인사관리 (입사신고, 외국인근로자 관리)
		  세무업무 (거래명세서 & 세금계산서 발행 , 발행 내역 등록, 카드내역 정리)
                  복리후생관리 (경조사,작업복 관리)
                  전화응대

6. [구인인증번호: KC5T332604230001]
   기업명: 주식회사 젊음티엔에스
   채용제목: 쇼핑몰관리 및 SNS(인스타그램 등)채널 관리, ...
   업종: 응용 소프트웨어 개발 및 공급업
   급여: 215만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 정보없음
   관련직종: 마케팅·광고·홍보·상품기획 사무원 총무 및 일반 사무원
   전공: 경영학(학과 : e-경영학과) 경영학(학과 : 광고마케팅학전공)
   자격면허: 정보없음
   직무내용: 담당업무:
- 자사 홈페이지 및 sns(네이버카페, 인스타그램) 관리
- 자사 상품 공동구매 셀러 매칭 및 상품발주(거래처 응대 및 고객관리)
- 전반적인 쇼핑몰 사무업무

우대사항
- 디자인 (포토샵, 캔바, 미리캔버스 등) 프로그램 사용 가능자
- 네이버카페 및 인스타그램 운영 및 관리 경험자
- ppt, excel 등 문서 작업 빠른 분
- 쇼핑몰 관리 업무 경력 보유자

-필독사항-
- 같은 의정부로 이사할 계획 중에 있으며 회사를 확장중에 있습니다. 열정있게 회사와 함께 성장할 분을 기다립니다.
- 팀워크를 요구하기보단 혼자 조용히 처리해야 할 일이 많습니다.
- 현재 외근직이 많아 사무실에 1명~2명 정도가 상주 합니다.

7. [구인인증번호: K160012604230003]
   기업명: (주)한미전력
   채용제목: 건설 공무 (전기공사) 사무보조 직원을 구합니다....
   업종: 일반전기 공사업
   급여: 250만원 ~ 270만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 전기공학(학과 : 전기공학과)
   자격면허: 전기공사산업기사,전기산업기사
   직무내용: 현장사무실에서의 사무보조, 문서수발, 문서작성 등

8. [구인인증번호: K120032604230001]
   기업명: (주)커리어텍
   채용제목: [SK에코플랜트/청주 흥덕구] 사무지원 채용
   업종: 상용 인력 공급 및 인사관리 서비스업
   급여: 296만원 ~ 326만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 1. 관리팀 사무지원 (1명)
    구성원 상시출입증 관리 및 근로자 안면인식 SCON 시스템 관리
    총무 업무 보조 [업무용차량관리, 현장사무실AS, 숙소업무보조(계약,가스,전기, 고지서 관리)]
    휴무계획표 취합 및 공지

2. 공정 사무지원 (1명)
    * 문서관리(Document Controller) 및 사무지원
    일정/물량 데이터 취합 및 데이터베이스 관리
    보고자료 취합 및 업데이트
    물량 및 Manpower 데이터 누적 관리 및 관련 비고표 작성
    시공사진관리 - 공정/일자/BP 별 데이터베이스 관리

3. 현장관리 사무지원 (1명)
    * 현장출입관리시스템 관리
   현장 출입인원 안면인식 등록 및 출력(출퇴근) Data 관리
   S.CON Data 관리 지원
   현장 서무와 총무 업무 지원

4. 안전보건교육 사무지원 (1명)
    * 안전보건교육 업무 지원
    신규채용자 교육 신청 접수 및 교육 지원
    특성화교육 Test 및 감독 (certification 발행)
    근로자 법정교육 이수 및 건강검진 실시 이력관리

* 우대사항 : 관련 경력자, MS Office 숙련자

커리어텍 소속의 파견직 ( SK에코플랜트 경력증명서 발급가능/ 발행처:커리어텍 )
채용시~1년  / 현장 상황에 따라 연장 가능성 有
급여 : 신입 월 2,961,840원 (세전기준) / 경력에 따라 차등
         만 3년 이상 월 3,139,560원 (세전기준)
         만 5년 이상 월 3,265,140원 (세전기준)

시간 외 수당 포함 급여  /  현장의 여건상 시업 시간 조정 필요 시 최대 주 52시간 범위 내 탄력적 운영 가능
평일 주 5일 근무, 토/일/공휴일 휴무

4대 보험, 연차 (미사용 연차 수당 지급), 퇴직금 별도 지급
통신비  월 55,000원 (중도 입사 시 일할 계산) 지원
복리후생비  375,000원 (분기별로 지급 3월, 6월, 9월,12월 / 중도 입사 시 분기별 일할 계산) 지원
중소기업 소득세감면 적용 신청 가능 (최초1회한)

9. [구인인증번호: K172322604230001]
   기업명: 태안군선거관리위원회
   채용제목: 제9회 전국동시지방선거 선거비용 실사 보조요원 모...
   업종: 정부기관 일반 보조 행정
   급여: 90830원 ~ 90830원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 정보없음
   관련직종: 정보없음
   전공: 정보없음
   자격면허: (기타: 컴퓨터활용능력 2급 이상(우대), 워드프로세서(우대), 1종 보통 이상 운전면허(우대))
   직무내용: * 반드시 첨부된 '모집안내문'를 확인하시어 '지원서 등 양식'에 따라 지원하시기 바랍니다.

* 근무기간 : 2026. 6. 15. ~ 7. 31.

* 직무내용
- 선거비용 보전청구서 접수
- 서면심사 및 현지실사 등 보전 업무 보조

10. [구인인증번호: K152412604230004]
   기업명: 주식회사 대창
   채용제목: 콘크리트 2차제품  생산관리 / 출하관리 사무직 ...
   업종: 콘크리트 타일, 기와, 벽돌 및 블록 제조업
   급여: 3000만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 생산관리 사무원(건설) 총무 및 일반 사무원
   전공: 토목공학(학과 : 건설·환경·화학공학과군)
   자격면허: 정보없음
   직무내용: 콘크리트 2차 제품(대형구조물 및 상하수도 구조물)에 대한

생산관리 및 출하관리 직원을 모집합니다.

경력자 및 신입사원 채용을 구분(연봉 및 직책)하여 진행합니다.

급여조건 명시는 신입사원에 준하여 책정되어 있습니다.

11. [구인인증번호: K120312604230013]
   기업명: 서대문구선거관리위원회
   채용제목: 서대문구선거관리위원회 선거비용 실사 보조요원 모집
   업종: 기타 일반 공공 행정
   급여: 90830원 ~ 90830원
   학력요건: 학력무관
   경력요건: 신입
   회사규모: 정보없음
   관련직종: 사무 보조원(공공기관)
   전공: 정보없음
   자격면허: 정보없음
   직무내용: [선거비용 실사 보조요원]
- 모집인원 : 7명(장애인 1명 포함, 비장애인은 최대 6명까지 채용)
- 근무기간 : 2026.06.15. ~ 2026.08.07.
- 보수 : 일 90,830원
- 직무내용
  · 회계보고서 및 선거비용 보전청구서 점검
  · 정치자금 관련 기록물 정리
  · 기타 부서장이 지정하는 업무 등

※ 자세한 내용은 서대문구선거관리위원회 홈페이지에 게시한 안내문을 확인하여 주시기 바랍니다.
※ 면접장소와 근무지는 지도계 임시사무실(서울특별시 서대문구 증가로 17, 2층)일 수 있음.

12. [구인인증번호: K130042604230010]
   기업명: 사회복지시설그리스도요양원
   채용제목: 그리스도요양원 사회복지시설 돌봄 보조 인력 채용 ...
   업종: 정신질환, 정신지체 및 약물 중독자 거주 복지시설 운영업
   급여: 215만원 ~ 240만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 정보없음
   관련직종: 사회복지사
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 사회복지법인 그리스도구원선 산하시설 '그리스도요양원'에서는 사회복지시설 돌봄 보조 인력을 다음과 같이 모집하오니 유능한 인재의 많은 응모를 바랍니다.

1. 모집직종 및 인원 :  돌봄 보조 인력 1명

2. 지원자격 : ｢2026년 정신요양시설 운영지원 사업｣ 및 관계법령 상 사업지원 지침에 따라 청년(19세이상~39세이하)의 연령에 해당하는 자
3. 근무조건
                가) 근무기간 : 2026. 5. 1. ~ 2026.12.31.(8개월)
                나) 급여 : 240.4만원(※세전, 4대보험 공제 전)
                ※ ｢2026년 정신요양시설 운영지원 사업｣ 사회복지시설 돌봄 보조 인력지원 지침에 따름.
                다) 근무시간: 09:00 ~ 18:00 (주 40시간)
                그 외, 자세한 내용은 첨부자료 참조.

4.  채용방법  :  1차 서류심사, 2차 면접(서류심사 합격자에 한함)
                      → 자세한 내용은 첨부파일 참조

5.  접수방법  :  접수기간 내에 제출서류를 접수처에 이메일(우선), 등기우편, (직접)내방 으로 제출

6.  제출서류  :  입사지원서(시설양식 준수), 자기소개서(시설양식 준수), 특수관계부존재 각서(시설양식 준수)

7.  채용자격 : <지원결격사유>
  가. 사회복지사업법(제35조제2항 및 제35조의2 제2항) 및 노인복지법(제39조의17), 아동복지법(제29조의3)에서 정하는 결격사유에 해당함.
  나.  제 19조 제 1항 제 1호의7 또는 제 1호의 8에 해당하는 사람.
  다. 제 1호에도 불구하고 종사자로 재직하는 동안 시설이용자를 대상으로「성폭력범죄의 처벌등에 관한 특례법」제 2조에 따른 성폭력범죄 및「아동․청소년의 성보호에 관한 법률」제2조 제2호에 따른 아동․청소년 대상 성범죄를 저질러 금고 이상의 형 또는 치료감호를 선고받고 그 형이 확정된 사람.
  라.  100만원 이상의 벌금형을 선고받고 그 형이 확정된 후 5년이 지나지 아니한 사람.
  마.  형의 집행유예를 선고받고 그 형이 확정된 후 7년이 지나지 아니한 사람.
  바. 징역형을 선고받고 그 집행이 끝나거나 집행이 면제된 날부터 7년이 지나지 아니한 사람.
  사.  그 밖에 업무수행이 불가능하다고 판단되는 사람.

8. 기타 사항 :
  가. 지원희망자는 자격요건이 적합한지 철저히 검토한 후 지원하시기 바랍니다.
  나. 채용 절차의 공정성 확보 및 종교의 자유 침해 방지를 위해 공개채용 서류에 종교, 가족관계, 출신지역 등의 개인정보를 취합하는 것을 금지하고 있습니다. 작성 서류에는 학교명(대학원 포함), 종교, 추천인, 주민등록번호, 신체적 조건(사진, 키, 체중 등), 혼인여부, 가족 학력 등 개인 신상을 직·간접적으로 파악할 수 있는 내용은 기재하지 마십시오.
  다. 제출된 서류는 일체 반환하지 않으며, 기재 착오 또는 누락이나 연락 불능 등으로 인한 불이익은 일체 지원자의 책임입니다.
  라. 접수된 서류는 반환하지 않습니다. 채용절차의 공정화에 관한 법률 타법개정 2020.05.26 [법률 제17326호, 시행 2020.5.26.] 합격자를 제외한 서류는 180일 이후 자동 파기 됩니다.
  마. 적격자 없을 경우 선발하지 않을 수 있고, 최종 합격자가 포기, 결격사유 등의 사유로 결원을 보충할 필요가 있는 경우에는 추가 합격자를 결정할 수 있습니다.
바. 제출된 서류에 허위사실이나 결격사유 조회 등을 통하여 부적합한 결격사유가 있을 경우에 합격 이후 에도 취소 됩니다.

13. [구인인증번호: KEC0882604230001]
   기업명: 주식회사케이티에스잡
   채용제목: [농협유통센터]사무보조원 채용
   업종: 사업시설 유지․관리 서비스업
   급여: 230만원 ~ 230만원
   학력요건: 대졸(2~3년) ~ 대졸(4년)
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: [농협유통센터]사무보조원 채용
-김해시 내삼리 농협유통센터(포크벨리)
-2차가공품(햄, 소세지) 재고확인 및 발주 등의 업무
-주5일(토일 휴무), 9~18시(휴게포함)
-월 급여 230만원
-근무지 특성상 자차출퇴근 가능하신분
-유통업무 경력자 우대

14. [구인인증번호: K130042604230013]
   기업명: 오케이손해사정주식회사
   채용제목: [KB손해보험 전담] 장기인보험 보상정보입력 사원...
   업종: 손해 사정업
   급여: 215만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 사무 보조원(일반사업체) 보험 심사원 및 사무원
   전공: 금융·회계·세무학(학과 : 금융보험전공) 보건학(학과 : 보건의료계열)
   자격면허: 정보없음
   직무내용: [KB손해보험 전담법인]

* 담당업무
  - 장기인보험 보상정보입력
  - 보험금 청구건이 접수되면 신청 정보를 확인하고 전산에 입력하는 작업

* 우대사항
  - (장기인)보험에 대한 관심이나 이해도가 높으신 분
  - 금융보험, 의무행정, 병원경영 등 관련 전공자
  - 유관 업무 경력자
  - 신체손해사정사

* 급여
  - 기본급 + 매월 인센티브 추가계산

* 채용일정
  - 접수기간에도 면접&입사 진행
  - 채용완료시 조기마감될 수 있으며, 면접은 문자로 개별안내드립니다.

* 기타
  - 업무는 교육을 통하여 충분히 알려드립니다. (1:1 멘토 배정)
  - 영업 관련 업무 전혀 없습니다.
  - 4대사회보험, 연/월차, 퇴직연금

15. [구인인증번호: KJ21552604230002]
   기업명: 주식회사 영광와이케이엠씨
   채용제목: [아산시 둔포} (주)영광와이케이엠씨 인사총무팀 ...
   업종: 그 외 기타 금속가공업
   급여: 3400만원
   학력요건: 대졸(4년)
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 총무 및 일반 사무원
   전공: 정보없음
   자격면허: 컴퓨터활용능력2급(기타: 컴퓨터활용능력 우수자)
   직무내용: ㆍ 주요업무 :  HR 전반 (아래 각 업무 중 분장 예정이며, 특별히 희망하는 업무가 있다면 면접 시 말씀하여 주십시오)
1 급여 (4대보험 / 퇴직연금 / 연말정산 / 결산 등)
2 근태 (출퇴근 / 연차 등)
3 채용
@ 교육
1 ER (규정/ 위원회 / 협의회 / 산업기능요원 / 모성보호 / 외국인 / 장애인 / 산학협력 등)
1 조직 (TO / 직무 / 문화 등)

▶ 지원학력 : 4년대졸 이상~(HR 경력 1~5년이하)
(직무 :산업, 비즈니스,기술,언어,공학 중 어느 하나라도 직접적인 연관성이 없는 예체능,철학 계열등은 제외)
신입기준 : 연 3,500~4,500만원(경력자 협의)

▶ 지원자격
1 스케쥴관리 능숙하신 분
- 꼼꼼하고 기억력 좋으며 채용 근태 등 각 일정 누락없이 정리하여 보고 가능한 수준
2 분석적사고 능력 있으신 분
- 문제상황에 대한 데이터를 분석하고 주요사항을 파악 및 정리하여 보고 가능한 수준
3 적극적으로 문제해결을 노력하시는 분
4 회계 및 MS Office 활용 능력 있으신 분
- 분개를 이해하고, 액셀 및 PPT 작업 요청 시 실행 가능한 수준

16. [구인인증번호: K151222604230018]
   기업명: 주식회사 파람
   채용제목: 회계 및 행정 업무 팀원모집
   업종: 인쇄 및 제책용 기계 제조업
   급여: 3000만원 ~ 3200만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 경력
   회사규모: 정보없음
   관련직종: 총무 및 일반 사무원
   전공: 정보없음
   자격면허: 전산회계1급
   직무내용: [주요업무]
- 월별 정산 작업
- 세금계산서 작성 및 관리
- 예산 및 결산서 작성 및 검토
- 고객 문의 처리 및 응대
- 간단한 행정 업무 및 사무 지원

[우대사항]
- 회계 관련 경험
- 고객 응대 경험
- MS Office 프로그램에 능숙
- 문서 작성 및 관리 능력
- 처리 효율성과 정확도에 대한 강한 의식

17. [구인인증번호: K131462604230001]
   기업명: （주）성광테크
   채용제목: 사무보조  1명
   업종: 강관 가공품 및 관 연결구류 제조업
   급여: 2700만원
   학력요건: 학력무관
   경력요건: 신입
   회사규모: 중소기업
   관련직종: 사무 보조원(일반사업체)
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 사무 지원 업무 - 1명

-근면 성실하신 분

- 엑셀 잘하시는 분

- 건강하고 성격이 밝고 주위와 소통이 잘 되시는 분

18. [구인인증번호: KJFD002604210003]
   기업명: 코오롱정수산업개발
   채용제목: [서구청채용대행] [코오롱정수산업개발] 경리사무원...
   업종: 가전제품 소매업
   급여: 240만원
   학력요건: 고졸 ~ 대졸(2~3년)
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: - 정수기 도소매업종
- 전산입력, 거래처 관리, 세금계산서발행, 사무실 경비 입출금 관리 고객상담
- 급여: 월 240만원


★ 실업급여 수급을 위한 형식적 지원 확인 시, 실업급여 담당자에게 통보되오니 각별히 유의하시기 바랍니다. ★

19. [구인인증번호: K171112604160010]
   기업명: (주)농협유통 청주농산물종합유통센터
   채용제목: 농협하나로마트 청주점  매장관리 업무 보조 및 사...
   업종: 그 외 기타 종합 소매업
   급여: 10320원 ~ 10320원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 정보없음
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 근무지 : 농협하나로마트 청주점 소매

○ 소매 매장 관리 보조(매장업무 보조 및 사무업무 보조)
○ 주5일 근무(주휴무 1일 유급 / 1일 무급)

20. [구인인증번호: KJLD002604220003]
   기업명: 주식회사 천하
   채용제목: 경리 및 일반사무 직원 모집 (경력 우대)
   업종: 신선, 냉동 및 기타 수산물 도매업
   급여: 230만원 ~ 230만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 장부 정리 및 매입ㆍ매출 정리 외 기타 사무 업무

■ 평가 기준:
- 구직자의 학력·전공과 채용공고의 학력요건·전공 일치도
- 구직자의 경력과 채용공고의 경력요건 부합 여부
- 구직자의 희망급여와 채용공고 급여 수준 적합성
- 알선상세정보와 직무내용·관련직종의 연관성
- 자격면허 보유 여부에 따른 가점

만약 후보군이 없다면 가장 최신 정보를 제공해주세요.

응답 형식 (JSON만 출력):
{
  "bestGujinNo": "최적채용공고구인인증번호",
  "scores": [
    {"구인인증번호": "...", "score": 85, "reason": "추천사유"}
  ]
}
2026-04-23 16:18:47.377 [MessageBroker-1] INFO  o.s.w.s.c.WebSocketMessageBrokerStats - WebSocketSession[0 current WS(0)-HttpStream(0)-HttpPoll(0), 0 total, 0 closed abnormally (0 connect failure, 0 send limit, 0 transport error)], stompSubProtocol[processed CONNECT(0)-CONNECTED(0)-DISCONNECT(0)], stompBrokerRelay[null], inboundChannel[pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0], outboundChannel[pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0], sockJsScheduler[pool size = 3, active threads = 1, queued tasks = 2, completed tasks = 0]
response: ```json
{
  "bestGujinNo": "KJFD002604210003",
  "scores": [
    {
      "구인인증번호": "KJFD002604210003",
      "score": 85,
      "reason": "신입 지원이 가능하며, 희망하는 일반 사무(경리) 직무와 일치합니다. 특히 급여가 월 240만 원으로 후보군 중 희망 급여(250만 원)에 가장 근접하여 적합도가 매우 높습니다."
    },
    {
      "구인인증번호": "K160012604140025",
      "score": 78,
      "reason": "신입 지원이 가능하고 주 5일 근무의 일반 사무/경리직으로 구직자의 요청 사항에 부합합니다. 급여는 연봉 2,700만 원(월 약 225만 원)으로 희망 수준보다 약간 낮으나 직무 적합성이 높습니다."
    },
    {
      "구인인증번호": "K131462604230001",
      "score": 78,
      "reason": "신입 지원이 가능한 일반 사무보조직이며, 급여 수준(연봉 2,700만 원)이 적절합니다. 구직자가 원하는 일반 사무직 범주에 정확히 해당합니다."
    },
    {
      "구인인증번호": "K120312604230013",
      "score": 70,
      "reason": "근무지가 서울특별시 서대문구로 구직자의 주소지(양천구)와 매우 가까워 '근거리' 조건에 가장 부합하며 일반 사무직입니다. 다만, 단기 계약직이며 일급제로 급여가 희망 수준보다 낮아 감점되었습니다."
    },
    {
      "구인인증번호": "K130042604230013",
      "score": 65,
      "reason": "학력 무관 및 신입 지원이 가능한 사무직이며 교육 시스템이 갖춰져 있어 접근성이 좋습니다. 하지만 급여(215만 원)가 희망 급여와 차이가 있습니다."
    }
  ]
}
```
------------ selectBestFromCandidates 종료 -------------
2026-04-23 16:20:09.406 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K141322604010008, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K141322604010008&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 산업전기, recommendedJobTitle=-채용대행-산업전기 사무보조 직원 채용, recommendedJobIndustry=건설업본사, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.425 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K160012604140025, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K160012604140025&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=삼양물류(주), recommendedJobTitle=경리/사무보조 채용, recommendedJobIndustry=일반 화물자동차 운송업, bestJobInfo=false, recommendationScore=78, recommendationReason=신입 지원이 가능하고 주 5일 근무의 일반 사무/경리직으로 구직자의 요청 사항에 부합합니다. 급여는 연봉 2,700만 원(월 약 225만 원)으로 희망 수준보다 약간 낮으나 직무 적합성이 높습니다., createdAt=null, updatedAt=null)
2026-04-23 16:20:09.437 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K140112604230001, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K140112604230001&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)대경티엠에스, recommendedJobTitle=iM뱅크 현수송센터 사무원 모집, recommendedJobIndustry=기타 사무지원 서비스업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.443 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K163012604230002, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K163012604230002&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사하요, recommendedJobTitle=(주)하요 경리 사무직 직원구합니다, recommendedJobIndustry=전기용품 및 조명장치 소매업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.450 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K130232604230003, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K130232604230003&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=제일산업, recommendedJobTitle=관리부 사무원 모집 (단순경리업무), recommendedJobIndustry=구조용 금속 판제품 및 공작물 제조업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.457 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=KC5T332604230001, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KC5T332604230001&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 젊음티엔에스, recommendedJobTitle=쇼핑몰관리 및 SNS(인스타그램 등)채널 관리, ..., recommendedJobIndustry=응용 소프트웨어 개발 및 공급업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.463 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K160012604230003, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K160012604230003&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)한미전력, recommendedJobTitle=건설 공무 (전기공사) 사무보조 직원을 구합니다...., recommendedJobIndustry=일반전기 공사업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.474 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K120032604230001, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K120032604230001&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)커리어텍, recommendedJobTitle=[SK에코플랜트/청주 흥덕구] 사무지원 채용, recommendedJobIndustry=상용 인력 공급 및 인사관리 서비스업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.480 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K172322604230001, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K172322604230001&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=태안군선거관리위원회, recommendedJobTitle=제9회 전국동시지방선거 선거비용 실사 보조요원 모..., recommendedJobIndustry=정부기관 일반 보조 행정, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.487 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K152412604230004, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K152412604230004&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 대창, recommendedJobTitle=콘크리트 2차제품  생산관리 / 출하관리 사무직 ..., recommendedJobIndustry=콘크리트 타일, 기와, 벽돌 및 블록 제조업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.493 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K120312604230013, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K120312604230013&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=서대문구선거관리위원회, recommendedJobTitle=서대문구선거관리위원회 선거비용 실사 보조요원 모집, recommendedJobIndustry=기타 일반 공공 행정, bestJobInfo=false, recommendationScore=70, recommendationReason=근무지가 서울특별시 서대문구로 구직자의 주소지(양천구)와 매우 가까워 '근거리' 조건에 가장 부합하며 일반 사무직입니다. 다만, 단기 계약직이며 일급제로 급여가 희망 수준보다 낮아 감점되었습니다., createdAt=null, updatedAt=null)
2026-04-23 16:20:09.499 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K130042604230010, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K130042604230010&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=사회복지시설그리스도요양원, recommendedJobTitle=그리스도요양원 사회복지시설 돌봄 보조 인력 채용 ..., recommendedJobIndustry=정신질환, 정신지체 및 약물 중독자 거주 복지시설 운영업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.506 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=KEC0882604230001, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KEC0882604230001&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사케이티에스잡, recommendedJobTitle=[농협유통센터]사무보조원 채용, recommendedJobIndustry=사업시설 유지․관리 서비스업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.512 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K130042604230013, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K130042604230013&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=오케이손해사정주식회사, recommendedJobTitle=[KB손해보험 전담] 장기인보험 보상정보입력 사원..., recommendedJobIndustry=손해 사정업, bestJobInfo=false, recommendationScore=65, recommendationReason=학력 무관 및 신입 지원이 가능한 사무직이며 교육 시스템이 갖춰져 있어 접근성이 좋습니다. 하지만 급여(215만 원)가 희망 급여와 차이가 있습니다., createdAt=null, updatedAt=null)
2026-04-23 16:20:09.519 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=KJ21552604230002, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KJ21552604230002&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 영광와이케이엠씨, recommendedJobTitle=[아산시 둔포} (주)영광와이케이엠씨 인사총무팀 ..., recommendedJobIndustry=그 외 기타 금속가공업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.526 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K151222604230018, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K151222604230018&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 파람, recommendedJobTitle=회계 및 행정 업무 팀원모집, recommendedJobIndustry=인쇄 및 제책용 기계 제조업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.533 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K131462604230001, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K131462604230001&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=（주）성광테크, recommendedJobTitle=사무보조  1명, recommendedJobIndustry=강관 가공품 및 관 연결구류 제조업, bestJobInfo=false, recommendationScore=78, recommendationReason=신입 지원이 가능한 일반 사무보조직이며, 급여 수준(연봉 2,700만 원)이 적절합니다. 구직자가 원하는 일반 사무직 범주에 정확히 해당합니다., createdAt=null, updatedAt=null)
2026-04-23 16:20:09.539 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=KJFD002604210003, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KJFD002604210003&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=코오롱정수산업개발, recommendedJobTitle=[서구청채용대행] [코오롱정수산업개발] 경리사무원..., recommendedJobIndustry=가전제품 소매업, bestJobInfo=true, recommendationScore=85, recommendationReason=신입 지원이 가능하며, 희망하는 일반 사무(경리) 직무와 일치합니다. 특히 급여가 월 240만 원으로 후보군 중 희망 급여(250만 원)에 가장 근접하여 적합도가 매우 높습니다., createdAt=null, updatedAt=null)
2026-04-23 16:20:09.545 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=K171112604160010, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K171112604160010&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)농협유통 청주농산물종합유통센터, recommendedJobTitle=농협하나로마트 청주점  매장관리 업무 보조 및 사..., recommendedJobIndustry=그 외 기타 종합 소매업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.550 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=88493, participantName=이현용, progressStage=고보일반, education=null, major=가족사회복지학, categoryMajor=경영·사무·금융·보험, categoryMiddle=경영지원 사무, desiredJob=사무보조원 , referralDetail=- 사회복지사쪽으로는 지원을 원하지 않음
- 일반사무직으로 지원요청
- 근거리와 5일 근무이면 좋다고 함, generatedSearchCondition={"keywords":["일반사무","사무보조"],"jobCategory":"경영지원 사무","maxCount":20,"parseError":false,"isAddress":true,"largescaleUnits":["서울"],"localUnits":["양천구"],"jobs_code":["26301"]}, recommendedJobCertNo=KJLD002604220003, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KJLD002604220003&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 천하, recommendedJobTitle=경리 및 일반사무 직원 모집 (경력 우대), recommendedJobIndustry=신선, 냉동 및 기타 수산물 도매업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-04-23 16:20:09.556 [main] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [processAndSaveRecommend] 메서드 수행 성공
geminiApiServiceTest
geminiApiServiceTest result : [ProcessRecommendResultDTO(success=true, message=null, reused=null, searchCondition=SearchConditionDTO(keywords=[일반사무, 사무보조], jobsCode=[26301], jobCategory=경영지원 사무, maxCount=20, parseError=false, isAddress=true, largescaleUnits=[서울], localUnits=[양천구]), savedCount=20)]
------------ geminiApiServiceTest 종료 -------------


         */
    }

    @Test
    public void BestFromCandidtessTest() {
        System.out.println("BestFromCandidtessTest");
    }


}
