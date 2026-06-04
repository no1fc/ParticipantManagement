package test;

import com.jobmoa.app.CounselMain.biz.recommend.*;
import com.jobmoa.app.config.RootConfig;
import com.jobmoa.app.config.WebMvcConfig;
import com.jobmoa.app.config.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(locations = {"classpath:application.properties", "classpath:.env"})
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
    private static final int TEST_JOB_SEEKER_NO = 95798;

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
                participant, referralInfo, categoryList, relatedCategories,
                java.util.Collections.emptyList(), java.util.Collections.emptyList());

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
                candidates, participant, alsonDetail, null,
                java.util.Collections.emptyList(), java.util.Collections.emptyList());

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
2026-05-19 17:15:53.229 [main] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [processAndSaveRecommend] 메서드 수행 전 호출
2026-05-19 17:15:53.341 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendServiceImpl - [추천저장] 관련 카테고리 조회 midCategoryNames=[기계장비 설치·정비], 결과=24건
generateSearchCondition prompt: [다음 구직자 정보를 기반으로 고용24 채용정보 DB에서 검색할 조건을 JSON 형식으로 생성해 주세요.
구직자 정보:
- 학력: 대졸
- 전공: 기계공학과
- 주소: 경기 수원시 팔달구 정자천로32번길 20 (화서동, 한독.LG아파트)
- 상세정보: *희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)

[희망 0순위 :  - 카테고리 대분류: 설치·정비·생산-기계·금속·재료
  - 카테고리 중분류: 기계장비 설치·정비
  - 희망직무: 생산기술, 개발
]
직종분류코드 목록:
카테고리 ID    카테고리명칭
9설치·정비·생산-기계·금속·재료
91   기계장비 설치·정비
811100         공업기계 설치·정비원
811101         공작기계 설치·정비원
811102         화학기계 설치·정비원
811103         섬유기계 설치·정비원
811104         전자제품 제조기계 설치·정비원
811105         식품기계 설치·정비원
811106         기타 공업기계 설치·정비원
811200         승강기 설치·정비원
811201         엘리베이터 설치·정비원
811202         에스컬레이터 설치·정비원(무빙워크 포함)
811203         기타 승강기 설치·정비원(휠체어리프트, 자동문 포함)
811300         물품이동장비 설치·정비원
811301         크레인·호이스트 설치·정비원
811302         지게차 정비원
811400         냉동·냉장·공조기 설치·정비원
811401         산업용 냉동·냉장·공조기 설치·정비원
811402         건물·가정용 냉동·냉장·공조기 설치·정비원
811500         보일러 설치·정비원
811501         산업용 보일러 설치·수리원
811502         건물·가정용 보일러 설치·수리원
811600         건설·광업용 기계 설치·정비원
811900         농업용 및 기타 기계장비 설치 및 정비원

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
2026-05-19 17:16:00.019 [MessageBroker-1] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [getAlertTargets] 메서드 수행 전 호출
2026-05-19 17:16:00.020 [MessageBroker-1] INFO  c.j.a.C.biz.schedule.ScheduleDAO - ScheduleDAO selectAlertTargets
2026-05-19 17:16:00.027 [MessageBroker-1] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [getAlertTargets] 메서드 수행 성공
========== Gemini 토큰 사용량 ==========
입력 토큰: 1035
출력 토큰: 161
총 토큰: 2127
========================================
generateSearchCondition response: [```json
{
  "keywords": ["생산설비", "유지보수", "공조냉동기계", "공정최적화", "생산기술"],
  "jobCategory": "기계장비 설치·정비",
  "educationLevel": "대졸",
  "industryType": "기계·금속·재료",
  "jobs_code": ["811100", "811400"],
  "jobs_nm": "공업기계 및 냉동·냉장·공조기 설치·정비원",
  "isAddress": false,
  "largescaleUnits": [],
  "localUnits": []
}
```]
2026-05-19 17:16:25.806 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendServiceImpl - [추천저장] Gemini 검색조건 생성 jobSeekerNo=95798, searchCondition=SearchConditionDTO(keywords=[생산설비, 유지보수, 공조냉동기계, 공정최적화, 생산기술], jobsCode=[811100, 811400], jobCategory=기계장비 설치·정비, maxCount=null, parseError=false, isAddress=false, largescaleUnits=[], localUnits=[])
2026-05-19 17:16:25.808 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendServiceImpl - [추천저장] Gemini 검색조건 생성 jobSeekerNo=95798, searchCondition=SearchConditionDTO(keywords=[생산설비, 유지보수, 공조냉동기계, 공정최적화, 생산기술], jobsCode=[811100, 811400], jobCategory=기계장비 설치·정비, maxCount=20, parseError=false, isAddress=false, largescaleUnits=[], localUnits=[])
------------ selectBestFromCandidates 시작 -------------
prompt: 다음 구직자 프로필과 채용공고 후보군을 종합적으로 비교하여 가장 적합한 채용공고를 선정하고 각 점수를 매겨주세요.

■ 구직자 프로필:
- 학력: 대졸
- 전공: 기계공학과
- 주소: 경기 수원시 팔달구 정자천로32번길 20 (화서동, 한독.LG아파트)
- 경력: 정보없음
- 희망급여: 280
- 알선상세정보: *희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)


■ 채용공고 후보군:
1. [구인인증번호: K120412604010008]
   기업명: 케이에이씨공항서비스(주)
   채용제목: KAC공항서비스 김포공항 정비 기간제 채용(자동차...
   업종: 사업시설 유지․관리 서비스업
   급여: 230만원 ~ 260만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: (기타: 정비기능사(우대))
   직무내용: KAC공항서비스(주) 김포공항 장비정비 시설 기간제 채용 공고

- 모집인원: 2명
- 업무: 시설 정비
- 자격조건: 정비기능사
- 근무기간: 채용시부터  ~ 2026.6.30.
* 상황에 따라 근로계약은 연장될 수 있으며, 추후 정규직 전환은 없음
* 급여조건은 근무형태(야간, 연장 등) 수당에 따라 상이

==========================
- 서류전형 마감일:2026.5.28.
- 면접일자: 개별통보
- 특수검진: 개별통보
- 입사일자: 협의 후 통보
==========================
※ KAC공항서비스(주)는 직무수행과 무관한 구직자의 개인정보*를 수집하지 않으므로 작성 시 유의하시기 바랍니다..
  *개인정보: 신체적 조건(용모, 키, 체중 등), 출신지역, 혼인여부(자녀, 시부모 등 혼인상황을 유추할 수 있는 모든 정보), 재산, 직계존비속 및 형제자매의 학력·직업·재산 등

※ 채용서류의 반환
- 최종합격자를 제외한 구직자를 대상으로 “면접 당일” 제출된 채용관련 서류는 「채용절차의 공정화에 관한 법률」에 따라 반환 청구기간 내에 청구된 반환 청구분에 한하여 반환해 드립니다.
- 반환 청구기간: 채용전형 불합격 통보를 받은 날로부터 14일 이내
- 청구 방법: 채용서류 반환청구서*를 작성 후 본인 서명 또는 날인 후 스캔하여 e-mail 제출
 * 「채용절차의 공정화에 관한 법률 시행규칙」 별지 제3호 서식
- 기타사항: 청구기간이 지난 경우나 채용서류를 반환하지 않은 경우 「개인정보 보호법」에 따라 제출한 서류를 파기합니다.
- 온라인으로 제출된 경우나 구직자가 당사의 요구없이 자발적으로 제출한 경우에는 반환의무가 없습니다
- 천재지변이나 그밖에 당사에게 책임 없는 사유로 채용서류가 멸실된 경우에는 반환한 것으로 봅니다.

2. [구인인증번호: KJ21622604030004]
   기업명: (주)사조오양임실공장
   채용제목: 사조오양 임실공장 생산설비 관리 직원 모집
   업종: 기타 식사용 가공처리 조리식품 제조업
   급여: 4000만원 ~ 4000만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 관계없음
   회사규모: 정보없음
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 식품회사 생산설비 관리 직원 모집

- 전반적인 생산설비 정비, 보수 업무
- 생산라인 효율성 극대화를 위한 개선 작업 수행
- 기계설비 관련 업무
  기계수리, 기계셋팅 작업
- 설비 유지보수

- 신입도 지원 가능해당 자격증 소지자 우대, 경력자 우대

- 통근버스 운영 (전주권, 임실권)

3. [구인인증번호: K180522604270030]
   기업명: (주)호연이엔지
   채용제목: 프레스 금형 설계 경력직 채용 (경력자 우대)
   업종: 전자 응용 절삭기계 제조업
   급여: 4500만원 ~ 5000만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 프레스 금형 제조원
   전공: 정보없음
   자격면허: 정보없음
   직무내용: ▪ 프레스 금형 설계(CAD) 및 개선
▪ 금형 수정 및 보완 작업

4. [구인인증번호: K131212605190012]
   기업명: 주식회사한울티앤아이
   채용제목: 플랜트 유지보수 기술공 채용
   업종: 시설물 유지관리 공사업
   급여: 355만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 전기용접원(아크,알곤,티그용접원) 공업기계 설치 및 정비원
   전공: 정보없음
   자격면허: 정보없음
   직무내용: -우대사항
○  수리실, 공무 부서 근무자, 용접
○  공무팀 퇴직자

-작업내용
○  아크, 티그용접
○  기계 유지보수

-근무시간
○ 월-금 8:30 ~ 17:30
○ 유지보수팀 특성상 시간외 잔업, 주말 특근 필수

5. [구인인증번호: K131522605190007]
   기업명: 삼영기업
   채용제목: [삼영기업]자동화설비, 기계장비 유지 보수 업무 ...
   업종: 강선 건조업
   급여: 300만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 제어계측공학(학과 : 제어자동화시스템공학전공) 기계공학(학과 : 기계시스템전공)
   자격면허: 정보없음
   직무내용: 삼성중공업 내 자동화설비, 크레인, 가공장비 유지보수

평균 특잔업 포함 300만원~
경력 여부에 따라 임금 협의가능

6. [구인인증번호: KJSB002605190003]
   기업명: (주)유니에스
   채용제목: 한화리조트 거제 벨버디어 시설관리(설비)원 모집(...
   업종: 고용 알선업
   급여: 300만원 ~ 300만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 중견기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: [거제시 일자리지원센터 채용대행건]

-모집직종: 한화리조트 거제 벨버디어 기계,설비 시설 관리원 모집
-급여: 3,001,249(연차, 퇴직금 별도, 구정 상여금 30만원, 연간 3일 유급휴가)
-기타: 숙소제공, 식사제공
-근무형태: 3교대(주주야야비비)

* 워크넷 구직신청 필수!
- 지원방법: 워크넷 접수 or 이메일(geojejob@korea.kr)
- 지원문의: 055-639-4149

7. [구인인증번호: K170052605130031]
   기업명: 한일통신주식회사
   채용제목: [충주]정보통신 분야의 전문가를 찾습니다: BTL...
   업종: 일반 통신 공사업
   급여: 3000만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 통신·인터넷 케이블 설치·수리원 기타 정보 통신기기 설치 및 수리원
   전공: 정보없음
   자격면허: 자동차운전면허1종보통,정보기기운용기능사(기타: 정보통신협회 경력수첩(필수))
   직무내용: [모집직무]
- BTL 통신망 광케이블 점검 및 유지보수

[직무소개]
1990년 설립된 한일통신은 정보통신 산업의 발전을 위해 끊임없이 도전해왔습니다. 안정적인 통신망 시설과 운영을 책임지는 안정적인 통신망 시설 및 유지보수 사업을 중심으로 하고 있습니다.  본 직무는 BTL 통신망의 광케이블 및 장비 유지보수를 담당하여 우리 회사의 네트워크 안정성에 기여할 것입니다.

[주요업무]
- BTL 통신망 광케이블 점검 및 유지보수
- 관련 서류 작성

8. [구인인증번호: KEC0172604090005]
   기업명: 유니슨에이치케이알 (주)
   채용제목: [구인.구직 만남의 날 ] 유니슨에이치케이알(주)...
   업종: 그 외 기타 분류 안된 금속 가공 제품 제조업
   급여: 3500만원 ~ 3500만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 건설 교량받침 영업 및 영업지원

[구인.구직 만남의 날 행사]
▣ 일시: 2026년 5월 27일 오전 10시
▣ 장소: 천안고용복지플러스센터 9층
▣ 채용관련문의: 041-620-9543
▣ 면접일정안내:  * 2026. 5. 27. -> 1차면접
                        * 2026. 5. 28. -> 1차 면접합격자 중 2차 현장면접있음.

▷이력서 지원시 지원분야를 기재해주세요! (파일명 예시: 홍길동_품질관리)

9. [구인인증번호: K180012605180035]
   기업명: 주식회사 씨와이엘리베이터
   채용제목: 승강기유지보수  경력 및 신입 기사 모집
   업종: 기타 일반 기계 및 장비 수리업
   급여: 250만원 ~ 450만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 승강기기능사,전기기능사(기타: 승강기자체점검자 필수)
   직무내용: * 승강기유지보수(엘리베이터, 에스컬레이터)
   -근무시간: 08:30~17:30 (주 5일)
* 수원,용인.화성,오산 등
* 차량소지자
* 자체점검자

10. [구인인증번호: K120412605180010]
   기업명: 씨엔에스주식회사
   채용제목: 서울 마곡웰튼병원 시설직(3교대) 모집
   업종: 시설물 유지관리 공사업
   급여: 10320원 ~ 10320원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 1) 시설관리직 모집  근무가능일 : 즉시
  [조건] 3교대(주/당/비) 교대근무 가능자
  [조건] 소방보조선임이 가능한자
   -  방재실 근무 및 건물 점검
   - 급여 : 260만원
 ㆍ근무 - 주간 09:00~18:00 / 당직 09:00~익일09:00 / 비번 휴무
 ㆍ휴게 - 12:00~13:30 / 17:00~18:30 / 21:30~07:30  (13시간)

11. [구인인증번호: K131212605180016]
   기업명: 혜성산업주식회사
   채용제목: 플라스틱용기 제조업 생산기술직 채용 공고
   업종: 합성수지 및 기타 플라스틱 물질 제조업
   급여: 4000만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 플라스틱 사출성형기 조작원
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 1. 플라스틱용기 생산에 관련된 기술직 업무

2. 사출/블로우몰딩 경험자우대

    급여 면접후 결정가능

12. [구인인증번호: K172122605180015]
   기업명: 비이에프 주식회사
   채용제목: 설비 유지보수 전문가를 채용합니다.
   업종: 축산 분뇨 처리업
   급여: 3600만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: [모집직무]
설비 관리자, 기계공, 유지보수 기술자, 산업 기계 엔지니어, 정밀기계 정비

[직무소개]
우리 회사는 다양한 산업 분야에 필요한 제품들을 생산하고 있습니다. 본 직무는 필터프레스, 정압설비, 멤브레인, 모터, 펌프 등 다양한 설비의 유지 보수를 통해 안정적인 생산 시스템을 유지하는 역할을 합니다.

[주요업무]
- 필터프레스, 정압설비, 멤브레인, 모터, 펌프 등의 설비 점검 및 관리
- 설비 고장 발생 시 원인 분석 및 수리
- 필요 시 생산 공정에 영향을 미치지 않는 설비 수리 및 부품 교체
- 설비 성능 개선 및 효율성 제고
- 유지보수 기록 관리 및 보고서 작성

13. [구인인증번호: K180612605180037]
   기업명: 오뚜기라면주식회사
   채용제목: 오뚜기라면(주) 제조파트 기계설비 유지보수(전기)...
   업종: 면류, 마카로니 및 유사식품 제조업
   급여: 300만원
   학력요건: 고졸 ~ 대졸(2~3년)
   경력요건: 경력
   회사규모: 중견기업
   관련직종: 공장 전기관리원 공업기계 설치 및 정비원
   전공: 기계공학(학과 : 기계공학과) 전기공학(학과 : 전기계열)
   자격면허: 전기산업기사,전기기능사(기타: 기계정비기능사(우대), 지게차 운전기능사(우대))
   직무내용: 오뚜기라면(주) 라면제품 생산라인 기계설비 유지보수 업무 담당자(전기,기계 유지.보수업무) 정규직 사원 5명을 모집 하오니 관심있는 분들께서는 지원해 주시기 바랍니다.

라면제품 생산라인 전기/기계 설비 유지.보수 업무
전기공사업무
수변전 설비관리
발전기, ESS 설비, 배터리 운영/점검
점검/작업/장애 보고서 작성

*주5일 근무 (주간 근무, 2교대 근무)
 -2조 2교대 근무(평일 5일 근무)
(주간조)08:30~17:30 (야간조)20:30~05:30
*매일 2시간씩 연장근무 실시(주간:08:30~19:30, 야간20:30~07:30)
*공업계열 고등학교 및 공대계열 초대졸 졸업자 우대
*군필자 우대
*상여금450% 별도 지급(기본급의 30% 매월 별도 지급, 설,추석 30%별도 지급, 하계휴가 30%별도 지급)
*통근버스 제공(수원, 향남, 오산,송탄,평택,안성(원곡,공도),팽성,안중,현덕,포승)

14. [구인인증번호: K180612605180038]
   기업명: 오뚜기라면주식회사
   채용제목: 오뚜기라면(주) 라면 생산(생산설비오퍼레이터) 정...
   업종: 면류, 마카로니 및 유사식품 제조업
   급여: 270만원 ~ 300만원
   학력요건: 고졸 ~ 대졸(2~3년)
   경력요건: 관계없음
   회사규모: 중견기업
   관련직종: 정보없음
   전공: 정보없음
   자격면허: 정보없음
   직무내용: 라면 생산 정규직 10명 채용
1. 업무내용 : 제조설비 오퍼레이터(라면제품 생산기계 조작)
2. 근무시간 : 2조 2교대 근무 (주간조)08:30~17:30 (야간조)20:30~05:30
  *매일 2시간씩 연장근무 실시(주간:08:30~19:30, 야간20:30~07:30)
3. 급여 : 연봉4,500만원 이상 (월30시간 연장근무 상여금450% 포함)
4. 근무지 위치 : 평택 안중읍 서해로 1427

15. [구인인증번호: K150012605180058]
   기업명: 새한에이브이텍주식회사
   채용제목: 하동 지역 통신설비 유지보수 직원 모집
   업종: 건설업본사
   급여: 250만원 ~ 250만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 기타 정보 통신기기 설치 및 수리원
   전공: 정보없음
   자격면허: 정보없음
   직무내용: [모집직무]
통신설비 유지보수 직원 1명 모집

[직무소개]
새한에이브이텍(주)는 인천 송림동에 위치한 기업으로 음향, 영상 및 통신 기기를 설치 판매하는 사업을 수행하고 있습니다. 본 채용은 하동 지역에 위치한 통신설비의 유지보수 담당자를 모집합니다.

[주요업무]
- 하동 지역 통신설비 점검 및 수리
- 통신 시스템 유지보수 및 관리
- 필요 시 설비 보완 및 교체
- 고장 발생 시 현장 지원 및 문제 해결

[근무시간]
- 월~목 08:30~18:30
- 금 08:30~15:30

16. [구인인증번호: K160012605180031]
   기업명: 주식회사신우이엔지
   채용제목: [군산(새만금)] 태양광 발전설비 경상정비원(신입...
   업종: 배관 및 냉ㆍ난방 공사업
   급여: 240만원 ~ 250만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 신입
   회사규모: 중소기업
   관련직종: 발전설비 기술자
   전공: 정보없음
   자격면허: 전기기능사
   직무내용: ■ 군산(새만금) 태양광 발전소 / 태양광 발전설비 경상정비 업무 관련 신입 경상정비원 모집합니다.

1. 자격요건 및 연봉
 * 신입: 전기기능사 보유자
 ※ 경력 우대: 전기 및 태양광 발전, 전기 유지보수, 안전 관리 등 기타 전기 및 발전 관련 경력 우대
 ※ 공통: 운전 가능자, 근무 시작 바로 가능하신 분, 성실/근면하신 분, 태양광 발전설비 경상정비 배우실 분

2. 직무내용: 태양광 발전설비 조작 및 유지보수 등 경상정비 업무 전반(과업 지시서 기준)

3. 근무기간: 채용시부터 ~ 2026년 10월 31일까지
                이후 입찰을 통해 업체가 변경될 수 있으며, 협의 후 연속 근무 가능합니다.
                또한 본 계약이 일정기간 연장될 수도 있습니다.

※ 발주처: 한국남동발전(주)
   공사명: 태양광 발전설비 경상정비공사 / 위탁업체

17. [구인인증번호: K130042605180035]
   기업명: 파인넷
   채용제목: [부산진구] 컴퓨터 및 네트워크 유지보수외 정규직...
   업종: 기타 정보기술 및 컴퓨터운영 관련 서비스업
   급여: 3000만원 ~ 3000만원
   학력요건: 고졸 ~ 대졸(4년)
   경력요건: 신입
   회사규모: 중소기업
   관련직종: 컴퓨터 설치 및 수리원(컴퓨터A/S원)
   전공: 정보없음
   자격면허: 자동차운전면허2종보통(기타: 운전가능자)
   직무내용: [부산진구] 컴퓨터 및 네트워크 유지보수외 정규직 모집.
 - 컴퓨터 및 네트워크 유지보수외 관련 정규직 모집
 - 정보통신공사업 면허 업체

업무
- 컴퓨터 및 네트워크 유지보수외
- 운전가능하고 배우시며 일하실분 가능.
- 경력 무관 신입 가능.
- 학력  : 무관
- 업무시간 09시~17시 하루 7시간근무 (토,일,공휴일 휴무)
- 연차 휴가 (1년미만 11개, 1년근속 15개)
- 연봉 3000만원(퇴직연금별도지급, 중식제공)
- 근무지역 부산진구 가야동

지원자격
ㆍ경력 : 신입 또는 경력
ㆍ기타 필수 사항
    - 운전가능자

근무조건
ㆍ근무형태	:	정규직(수습기간)-3개월
ㆍ근무일시	:	주 5일(월~금) 09:00~17:00,  하루7시간, 주35시간
ㆍ근무지역	:	부산 부산진구 가야동 근무

◎부산고용센터 채용대행◎
부산고용센터에서 입사지원서 취합을 돕고 있습니다.
사업장에서 서류 검토 후 개별 연락이 갈 예정입니다.
감사합니다.
지원방법:★고용24 구인공고 아래 보이는 ‘고용24 입사지원’ 클릭★

18. [구인인증번호: K171222605180011]
   기업명: 주식회사 금성에이브이
   채용제목: CCTV, 방송음향장비 설치 및 유지보수, 구내통...
   업종: 일반 통신 공사업
   급여: 300만원
   학력요건: 학력무관
   경력요건: 경력
   회사규모: 중소기업
   관련직종: 방송 케이블 설치·수리원 통신 관련 장비 설치 및 수리원
   전공: 정보없음
   자격면허: 정보통신기술사
   직무내용: 담당업무
- CCTV 설치 및 유지보수
- 방송음형장비 설치 및 유지보수
- 구내통신공사

지원자격
- 경력 : 무관

근무조건
- 급여 : 협의 / 중식비 지급
- 상여: 연 3회지급 (명절2회, 휴가 1회)

필수사항
- 2종보통운전면허

우대사항
- 정보통신기술자 경력수첩
- 해당직무 근무경혐, 정보통신기술자

19. [구인인증번호: KJGE002605180006]
   기업명: (주)세강
   채용제목: (주)세강 온산공단 현대중공업 내 장비점검인원 구...
   업종: 산업설비, 운송장비 및 공공장소 청소업
   급여: 10320원 ~ 10500원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 건설·채굴 단순 종사원 기타 제조 관련 단순 종사원
   전공: 정보없음
   자격면허: 정보없음
   직무내용: ★(주)세강  장비점검인원 구인  모집★

▶ 직무내용: 온산공단 현대중공업 내  장비점검인원
▶ 근무시간: 주5일  07:00~17:00 (토요일 선택근무 가능)
▶ 급여조건: 시급 10,320원~10,500원 (면접 후 협의가능)
▶ 근 무  지: 온산읍 산암로 487 - 현대 중공업 내 (주)세강
▶ 기타사항: 회사 내 이동 시 자전거로 이동

※지원 및 문의 : 울주군청일자리안내지원센터 ☎052-204-1359

20. [구인인증번호: K180142605180011]
   기업명: 주식회사 새인건설
   채용제목: 상하수도 시설 유지 관리를 책임지는 성실한 상하수...
   업종: 건설업본사
   급여: 280만원 ~ 350만원
   학력요건: 학력무관
   경력요건: 관계없음
   회사규모: 중소기업
   관련직종: 건설·채굴 단순 종사원 철근공
   전공: 정보없음
   자격면허: 토목기사
   직무내용: [모집직무]
시설 관리, 유지보수, 수도관 설치 및 점검, 상하수도 공사


[주요업무]
- 상하수도 시설 유지보수
- 상하수도 공사 및 설치
- 시설 문제 발생 시 신속한 대응 및 해결
- 작업 현장 안전 관리
- 관련 기록 및 보고 작성

현장에서 근무하실 책임감 있고 성실하신 분 모십니다!

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
2026-05-19 17:16:52.676 [MessageBroker-2] INFO  o.s.w.s.c.WebSocketMessageBrokerStats - WebSocketSession[0 current WS(0)-HttpStream(0)-HttpPoll(0), 0 total, 0 closed abnormally (0 connect failure, 0 send limit, 0 transport error)], stompSubProtocol[processed CONNECT(0)-CONNECTED(0)-DISCONNECT(0)], stompBrokerRelay[null], inboundChannel[pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0], outboundChannel[pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0], sockJsScheduler[pool size = 5, active threads = 1, queued tasks = 3, completed tasks = 1]
2026-05-19 17:17:00.043 [MessageBroker-3] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [getAlertTargets] 메서드 수행 전 호출
2026-05-19 17:17:00.043 [MessageBroker-3] INFO  c.j.a.C.biz.schedule.ScheduleDAO - ScheduleDAO selectAlertTargets
2026-05-19 17:17:00.049 [MessageBroker-3] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [getAlertTargets] 메서드 수행 성공
========== Gemini 토큰 사용량 ==========
입력 토큰: 7530
출력 토큰: 683
총 토큰: 10738
========================================
response: ```json
{
  "bestGujinNo": "KJ21622604030004",
  "scores": [
    {
      "구인인증번호": "KJ21622604030004",
      "score": 95,
      "reason": "구직자의 전공(기계공학)과 희망직종(생산설비, 유지보수, 공정최적화)에 완벽히 부합하며, 신입 지원이 가능하고 희망급여(280만원)를 상회하는 높은 급여 수준(연 4,000만원)을 제공함."
    },
    {
      "구인인증번호": "K131522605190007",
      "score": 90,
      "reason": "기계공학 전공자를 명시적으로 우대하며, 희망직종인 자동화설비 및 기계장비 유지보수 업무와 일치함. 신입 지원이 가능하고 급여(300만원) 또한 구직자의 희망 수준을 충족함."
    },
    {
      "구인인증번호": "K180612605180038",
      "score": 80,
      "reason": "신입 지원이 가능하고 급여 조건이 매우 우수함. 다만, 유지보수보다는 설비 오퍼레이터(조작) 업무 중심이라 전문 기술직 희망 사항과는 약간의 차이가 있음."
    },
    {
      "구인인증번호": "K180012605180035",
      "score": 70,
      "reason": "구직자의 거주지인 수원 지역 근무가 가능하다는 강력한 장점이 있고 유지보수 직무이나, 승강기자체점검자 자격이 필수 요건으로 되어 있어 자격증 미보유 시 진입장벽이 있음."
    },
    {
      "구인인증번호": "K131212605180016",
      "score": 70,
      "reason": "생산기술직으로 전공 역량을 활용할 수 있고 급여가 높으며 신입 지원이 가능하지만, 구직자가 희망하는 '유지보수'보다는 '생산기술'에 치중된 공고임."
    },
    {
      "구인인증번호": "K120412604010008",
      "score": 60,
      "reason": "시설 정비 직무로 전공 및 희망 직종과 연관성이 높고 신입 지원이 가능하나, 급여 수준(230~260만원)이 구직자의 희망급여보다 낮고 기간제 채용이라는 단점이 있음."
    }
  ]
}
```
------------ selectBestFromCandidates 종료 -------------
2026-05-19 17:17:58.829 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K120412604010008, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K120412604010008&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=케이에이씨공항서비스(주), recommendedJobTitle=KAC공항서비스 김포공항 정비 기간제 채용(자동차..., recommendedJobIndustry=사업시설 유지․관리 서비스업, bestJobInfo=false, recommendationScore=60, recommendationReason=시설 정비 직무로 전공 및 희망 직종과 연관성이 높고 신입 지원이 가능하나, 급여 수준(230~260만원)이 구직자의 희망급여보다 낮고 기간제 채용이라는 단점이 있음., createdAt=null, updatedAt=null)
2026-05-19 17:17:58.840 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=KJ21622604030004, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KJ21622604030004&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)사조오양임실공장, recommendedJobTitle=사조오양 임실공장 생산설비 관리 직원 모집, recommendedJobIndustry=기타 식사용 가공처리 조리식품 제조업, bestJobInfo=true, recommendationScore=95, recommendationReason=구직자의 전공(기계공학)과 희망직종(생산설비, 유지보수, 공정최적화)에 완벽히 부합하며, 신입 지원이 가능하고 희망급여(280만원)를 상회하는 높은 급여 수준(연 4,000만원)을 제공함., createdAt=null, updatedAt=null)
2026-05-19 17:17:58.849 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K180522604270030, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K180522604270030&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)호연이엔지, recommendedJobTitle=프레스 금형 설계 경력직 채용 (경력자 우대), recommendedJobIndustry=전자 응용 절삭기계 제조업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.857 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K131212605190012, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K131212605190012&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사한울티앤아이, recommendedJobTitle=플랜트 유지보수 기술공 채용, recommendedJobIndustry=시설물 유지관리 공사업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.868 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K131522605190007, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K131522605190007&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=삼영기업, recommendedJobTitle=[삼영기업]자동화설비, 기계장비 유지 보수 업무 ..., recommendedJobIndustry=강선 건조업, bestJobInfo=false, recommendationScore=90, recommendationReason=기계공학 전공자를 명시적으로 우대하며, 희망직종인 자동화설비 및 기계장비 유지보수 업무와 일치함. 신입 지원이 가능하고 급여(300만원) 또한 구직자의 희망 수준을 충족함., createdAt=null, updatedAt=null)
2026-05-19 17:17:58.875 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=KJSB002605190003, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KJSB002605190003&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)유니에스, recommendedJobTitle=한화리조트 거제 벨버디어 시설관리(설비)원 모집(..., recommendedJobIndustry=고용 알선업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.881 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K170052605130031, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K170052605130031&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=한일통신주식회사, recommendedJobTitle=[충주]정보통신 분야의 전문가를 찾습니다: BTL..., recommendedJobIndustry=일반 통신 공사업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.887 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=KEC0172604090005, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KEC0172604090005&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=유니슨에이치케이알 (주), recommendedJobTitle=[구인.구직 만남의 날 ] 유니슨에이치케이알(주)..., recommendedJobIndustry=그 외 기타 분류 안된 금속 가공 제품 제조업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.892 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K180012605180035, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K180012605180035&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 씨와이엘리베이터, recommendedJobTitle=승강기유지보수  경력 및 신입 기사 모집, recommendedJobIndustry=기타 일반 기계 및 장비 수리업, bestJobInfo=false, recommendationScore=70, recommendationReason=구직자의 거주지인 수원 지역 근무가 가능하다는 강력한 장점이 있고 유지보수 직무이나, 승강기자체점검자 자격이 필수 요건으로 되어 있어 자격증 미보유 시 진입장벽이 있음., createdAt=null, updatedAt=null)
2026-05-19 17:17:58.899 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K120412605180010, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K120412605180010&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=씨엔에스주식회사, recommendedJobTitle=서울 마곡웰튼병원 시설직(3교대) 모집, recommendedJobIndustry=시설물 유지관리 공사업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.906 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K131212605180016, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K131212605180016&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=혜성산업주식회사, recommendedJobTitle=플라스틱용기 제조업 생산기술직 채용 공고, recommendedJobIndustry=합성수지 및 기타 플라스틱 물질 제조업, bestJobInfo=false, recommendationScore=70, recommendationReason=생산기술직으로 전공 역량을 활용할 수 있고 급여가 높으며 신입 지원이 가능하지만, 구직자가 희망하는 '유지보수'보다는 '생산기술'에 치중된 공고임., createdAt=null, updatedAt=null)
2026-05-19 17:17:58.913 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K172122605180015, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K172122605180015&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=비이에프 주식회사, recommendedJobTitle=설비 유지보수 전문가를 채용합니다., recommendedJobIndustry=축산 분뇨 처리업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.921 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K180612605180037, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K180612605180037&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=오뚜기라면주식회사, recommendedJobTitle=오뚜기라면(주) 제조파트 기계설비 유지보수(전기)..., recommendedJobIndustry=면류, 마카로니 및 유사식품 제조업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.927 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K180612605180038, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K180612605180038&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=오뚜기라면주식회사, recommendedJobTitle=오뚜기라면(주) 라면 생산(생산설비오퍼레이터) 정..., recommendedJobIndustry=면류, 마카로니 및 유사식품 제조업, bestJobInfo=false, recommendationScore=80, recommendationReason=신입 지원이 가능하고 급여 조건이 매우 우수함. 다만, 유지보수보다는 설비 오퍼레이터(조작) 업무 중심이라 전문 기술직 희망 사항과는 약간의 차이가 있음., createdAt=null, updatedAt=null)
2026-05-19 17:17:58.934 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K150012605180058, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K150012605180058&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=새한에이브이텍주식회사, recommendedJobTitle=하동 지역 통신설비 유지보수 직원 모집, recommendedJobIndustry=건설업본사, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.941 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K160012605180031, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K160012605180031&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사신우이엔지, recommendedJobTitle=[군산(새만금)] 태양광 발전설비 경상정비원(신입..., recommendedJobIndustry=배관 및 냉ㆍ난방 공사업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.946 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K130042605180035, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K130042605180035&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=파인넷, recommendedJobTitle=[부산진구] 컴퓨터 및 네트워크 유지보수외 정규직..., recommendedJobIndustry=기타 정보기술 및 컴퓨터운영 관련 서비스업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.954 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K171222605180011, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K171222605180011&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 금성에이브이, recommendedJobTitle=CCTV, 방송음향장비 설치 및 유지보수, 구내통..., recommendedJobIndustry=일반 통신 공사업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.960 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=KJGE002605180006, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=KJGE002605180006&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=(주)세강, recommendedJobTitle=(주)세강 온산공단 현대중공업 내 장비점검인원 구..., recommendedJobIndustry=산업설비, 운송장비 및 공공장소 청소업, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.969 [main] INFO  c.j.a.C.b.r.ParticipantJobRecommendDAO - Inserting or updating participant job recommend: ParticipantJobRecommendDTO(pk=0, jobSeekerNo=95798, participantName=김상훈, progressStage=고보일반, education=대졸, major=기계공학과, categoryMajor=설치·정비·생산-기계·금속·재료, categoryMiddle=기계장비 설치·정비, desiredJob=생산기술, 개발, referralDetail=*희망지역-> 지역무관

*희망직종
- 생산설비, 유지보수, 공조냉동기계기사 자격증 보유,
- 기계설비 유지관리 초급
- 양산이관 개발 공정최적화
- 제조 공정 개설
- 생산설비

*디티엔씨/ 시험보조/ 24.9~24.12  경력/(담당업무: 실험장비 과열방지와 냉각수 점검)
, generatedSearchCondition={"keywords":["생산설비","유지보수","공조냉동기계","공정최적화","생산기술"],"jobCategory":"기계장비 설치·정비","maxCount":20,"parseError":false,"isAddress":false,"largescaleUnits":[],"localUnits":[],"jobs_code":["811100","811400"]}, recommendedJobCertNo=K180142605180011, recommendedJobUrl=https://www.work24.go.kr/wk/a/b/1500/empDetailAuthView.do?wantedAuthNo=K180142605180011&infoTypeCd=VALIDATION&infoTypeGroup=tb_workinfoworknet, recommendedJobCompany=주식회사 새인건설, recommendedJobTitle=상하수도 시설 유지 관리를 책임지는 성실한 상하수..., recommendedJobIndustry=건설업본사, bestJobInfo=false, recommendationScore=null, recommendationReason=null, createdAt=null, updatedAt=null)
2026-05-19 17:17:58.978 [main] INFO  c.j.a.C.biz.common.DaoLogAdvice - 로그 : [processAndSaveRecommend] 메서드 수행 성공
geminiApiServiceTest
geminiApiServiceTest result : [ProcessRecommendResultDTO(success=true, message=null, reused=null, searchCondition=SearchConditionDTO(keywords=[생산설비, 유지보수, 공조냉동기계, 공정최적화, 생산기술], jobsCode=[811100, 811400], jobCategory=기계장비 설치·정비, maxCount=20, parseError=false, isAddress=false, largescaleUnits=[], localUnits=[]), savedCount=20, lastRecommendedAt=null, activeRecommendCount=0, participantName=김상훈, jobSeekerNo=95798)]
------------ geminiApiServiceTest 종료 -------------

         */
    }

    @Test
    public void BestFromCandidtessTest() {
        System.out.println("BestFromCandidtessTest");
    }


}
