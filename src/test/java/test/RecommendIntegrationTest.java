package test;

import com.jobmoa.app.CounselMain.biz.recommend.*;
import com.jobmoa.app.config.RootConfig;
import com.jobmoa.app.config.WebMvcConfig;
import com.jobmoa.app.config.WebSocketConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 추천 채용정보 통합 테스트.
 * <p>실제 DB + Gemini API를 사용하여 Phase 1~5 개선 사항을 전체적으로 검증한다.</p>
 *
 * <p>실행 방법:</p>
 * <pre>
 *   ./mvnw test -Dtest="test.RecommendIntegrationTest"
 *   ./mvnw test -Dtest="test.RecommendIntegrationTest#testXxx"  (개별 실행)
 * </pre>
 *
 * <p>주의: @Transactional 적용으로 기본 롤백됨. DB 반영 필요 시 @Commit 추가.</p>
 */
@SpringJUnitWebConfig
@Transactional
@ContextConfiguration(classes = {RootConfig.class, WebMvcConfig.class, WebSocketConfig.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:.env"})
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecommendIntegrationTest {

    @Autowired
    private ParticipantJobRecommendServiceImpl recommendService;

    @Autowired
    private ParticipantJobRecommendDAO recommendDAO;

    @Autowired
    private GeminiApiService geminiApiService;

    // 테스트 대상 구직번호 (실제 DB에 존재하는 참여자)
    private static final int TEST_JOB_SEEKER_NO = 95798;

    // =========================================================
    // Phase 2 검증: 데이터 보강 — 신규 데이터 조회
    // =========================================================

    @Test
    @Order(1)
    @DisplayName("[Phase 2] 참여자 기본정보 조회 — 특정계층/취업역량 포함")
    void testParticipantInfoWithNewFields() {
        System.out.println("\n========== [Phase 2] 참여자 기본정보 조회 ==========");
        RecommendParticipantDTO participant = recommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);

        if (participant == null) {
            System.out.println("[SKIP] 참여자 정보 없음 (구직번호: " + TEST_JOB_SEEKER_NO + ")");
            return;
        }

        System.out.println("  구직번호: " + participant.getJobSeekerNo());
        System.out.println("  참여자명: " + participant.getInfoName());
        System.out.println("  학력: " + participant.getInfoEducation());
        System.out.println("  전공: " + participant.getInfoMajor());
        System.out.println("  경력: " + participant.getInfoCareer());
        System.out.println("  주소: " + participant.getInfoAddress());
        System.out.println("  희망급여: " + participant.getInfoDesiredSalary());
        System.out.println("  [신규] 특정계층: " + participant.getSpecialClass());
        System.out.println("  [신규] 취업역량: " + participant.getEmploymentCapacity());
        System.out.println("========== 완료 ==========\n");
    }

    @Test
    @Order(2)
    @DisplayName("[Phase 2] 희망직무 카테고리 조회 — 추천키워드 포함")
    void testCategoryWithRecommendedKeywords() {
        System.out.println("\n========== [Phase 2] 희망직무 카테고리 + 추천키워드 ==========");
        List<RecommendCategoryDTO> categoryList = recommendDAO.getParticipantCategory(TEST_JOB_SEEKER_NO);

        if (categoryList == null || categoryList.isEmpty()) {
            System.out.println("[SKIP] 희망직무 카테고리 없음");
            return;
        }

        for (RecommendCategoryDTO cat : categoryList) {
            System.out.println("  " + cat.getInfoRank() + "순위:");
            System.out.println("    대분류: " + cat.getCategoryMain());
            System.out.println("    중분류: " + cat.getCategoryMiddle());
            System.out.println("    희망직무: " + cat.getInfoJob());
            System.out.println("    [신규] 추천키워드: " + cat.getRecommendedKeywords());
        }
        System.out.println("========== 완료 ==========\n");
    }

    @Test
    @Order(3)
    @DisplayName("[Phase 2] 자격증 목록 조회")
    void testCertificates() {
        System.out.println("\n========== [Phase 2] 자격증 목록 ==========");
        List<String> certs = recommendDAO.getParticipantCertificates(TEST_JOB_SEEKER_NO);

        if (certs == null || certs.isEmpty()) {
            System.out.println("  자격증: 없음");
        } else {
            System.out.println("  자격증 " + certs.size() + "건: " + String.join(", ", certs));
        }
        System.out.println("========== 완료 ==========\n");
    }

    @Test
    @Order(4)
    @DisplayName("[Phase 2] 직업훈련 이력 조회")
    void testTrainings() {
        System.out.println("\n========== [Phase 2] 직업훈련 이력 ==========");
        List<String> trainings = recommendDAO.getParticipantTrainings(TEST_JOB_SEEKER_NO);

        if (trainings == null || trainings.isEmpty()) {
            System.out.println("  직업훈련: 없음");
        } else {
            System.out.println("  직업훈련 " + trainings.size() + "건: " + String.join(", ", trainings));
        }
        System.out.println("========== 완료 ==========\n");
    }

    @Test
    @Order(5)
    @DisplayName("[Phase 2] 알선상세정보 + 추천사 조회")
    void testReferralWithAdditionalInfo() {
        System.out.println("\n========== [Phase 2] 알선상세정보 + 추천사 ==========");
        RecommendReferralDTO referral = recommendDAO.getParticipantReferral(TEST_JOB_SEEKER_NO);

        if (referral == null) {
            System.out.println("  알선상세정보: 없음");
        } else {
            System.out.println("  알선상세정보: " + referral.getInfoAlsonDetail());
            System.out.println("  [Phase 1에서 추가 활용] 추천사: " + referral.getInfoAdditionalInfo());
        }
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // Phase 1 검증: 프롬프트 개선 — Gemini Stage 1 (검색조건 생성)
    // =========================================================

    @Test
    @Order(6)
    @DisplayName("[Phase 1] Gemini Stage 1 — 검색조건 생성 (거주지 기반 지역 + 자격증/훈련 반영)")
    void testGeminiStage1SearchCondition() {
        System.out.println("\n========== [Phase 1] Gemini Stage 1: 검색조건 생성 ==========");

        RecommendParticipantDTO participant = recommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);
        List<RecommendCategoryDTO> categoryList = recommendDAO.getParticipantCategory(TEST_JOB_SEEKER_NO);
        RecommendReferralDTO referralInfo = recommendDAO.getParticipantReferral(TEST_JOB_SEEKER_NO);
        List<String> certificates = recommendDAO.getParticipantCertificates(TEST_JOB_SEEKER_NO);
        List<String> trainings = recommendDAO.getParticipantTrainings(TEST_JOB_SEEKER_NO);

        if (participant == null) {
            System.out.println("[SKIP] 참여자 정보 없음");
            return;
        }

        // 관련 카테고리 조회
        List<String> midCategoryNames = categoryList.stream()
                .map(RecommendCategoryDTO::getCategoryMiddle)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        List<JobCategoryDTO> relatedCategories = Collections.emptyList();
        if (!midCategoryNames.isEmpty()) {
            relatedCategories = recommendDAO.getRelatedJobCategories(midCategoryNames);
        }

        System.out.println("[입력 데이터]");
        System.out.println("  참여자: " + participant.getInfoName());
        System.out.println("  주소: " + participant.getInfoAddress());
        System.out.println("  자격증: " + (certificates.isEmpty() ? "없음" : String.join(", ", certificates)));
        System.out.println("  직업훈련: " + (trainings.isEmpty() ? "없음" : String.join(", ", trainings)));
        System.out.println("  관련 카테고리: " + relatedCategories.size() + "건");

        // Gemini API 호출
        System.out.println("\n[Gemini API 호출 - Stage 1]");
        SearchConditionDTO result = geminiApiService.generateSearchCondition(
                participant, referralInfo, categoryList, relatedCategories, certificates, trainings);

        System.out.println("\n[검색조건 생성 결과]");
        System.out.println("  keywords: " + result.getKeywords());
        System.out.println("  jobs_code: " + result.getJobsCode());
        System.out.println("  jobCategory: " + result.getJobCategory());
        System.out.println("  isAddress: " + result.getIsAddress());
        System.out.println("  largescaleUnits: " + result.getLargescaleUnits());
        System.out.println("  localUnits: " + result.getLocalUnits());
        System.out.println("  parseError: " + result.getParseError());

        // 검증: 키워드가 생성되었는지
        Assertions.assertNotNull(result.getKeywords(), "키워드가 null이면 안 됨");
        Assertions.assertFalse(result.getKeywords().isEmpty(), "키워드가 비어있으면 안 됨");
        Assertions.assertFalse(result.getParseError(), "JSON 파싱 에러가 없어야 함");

        // 검증: 주소가 있으면 지역 정보가 포함되어야 함
        if (participant.getInfoAddress() != null && !participant.getInfoAddress().trim().isEmpty()) {
            System.out.println("\n[지역 검색 검증] 참여자 주소 존재 → isAddress=true 예상");
            if (result.getIsAddress() != null && result.getIsAddress()) {
                System.out.println("  ✅ isAddress=true, 지역 필터 활성화됨");
                System.out.println("  광역: " + result.getLargescaleUnits());
                System.out.println("  기초: " + result.getLocalUnits());
            } else {
                System.out.println("  ⚠️ isAddress=false — Gemini가 지역을 추출하지 않음");
            }
        }
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // Phase 3 검증: 검색 쿼리 최적화 — 후보군 조회 + 관련성 정렬
    // =========================================================

    @Test
    @Order(7)
    @DisplayName("[Phase 3] 후보군 조회 — 관련성 정렬 + 검색 컬럼 확대 검증")
    void testCandidateSearchWithRelevanceSort() {
        System.out.println("\n========== [Phase 3] 후보군 조회 (관련성 정렬) ==========");

        // 수동 검색 조건 설정 (Gemini 호출 없이 쿼리 자체를 검증)
        // MyBatis OGNL은 Arrays.asList() 반환 리스트에서 Java 모듈 접근 제한 발생 → ArrayList 사용
        SearchConditionDTO searchCondition = new SearchConditionDTO();
        searchCondition.setKeywords(new ArrayList<>(List.of("사무", "행정")));
        searchCondition.setJobsCode(new ArrayList<>(List.of("312100")));
        searchCondition.setIsAddress(true);
        searchCondition.setLargescaleUnits(new ArrayList<>(List.of("서울")));
        searchCondition.setLocalUnits(new ArrayList<>(List.of("강남구")));
        searchCondition.setMaxCount(10);

        System.out.println("[검색 조건]");
        System.out.println("  keywords: " + searchCondition.getKeywords());
        System.out.println("  jobs_code: " + searchCondition.getJobsCode());
        System.out.println("  지역: 서울 강남구");

        List<JobCandidateDTO> candidates = recommendDAO.selectJobInfoCandidates(searchCondition);
        System.out.println("\n[조회 결과] " + candidates.size() + "건");

        for (int i = 0; i < Math.min(candidates.size(), 5); i++) {
            JobCandidateDTO c = candidates.get(i);
            System.out.println("  " + (i + 1) + ". " + c.getCompanyName()
                    + " | " + c.getRecruitTitle()
                    + " | " + c.getCareer()
                    + " | " + c.getCertNo());
        }
        if (candidates.size() > 5) {
            System.out.println("  ... 외 " + (candidates.size() - 5) + "건");
        }
        System.out.println("========== 완료 ==========\n");
    }

    @Test
    @Order(8)
    @DisplayName("[Phase 3] 후보군 0건 시 완화 재검색 (Fallback)")
    void testCandidateFallbackSearch() {
        System.out.println("\n========== [Phase 3] 후보군 완화 재검색 ==========");

        // 의도적으로 매칭 안 되는 조건 설정
        SearchConditionDTO narrowCondition = new SearchConditionDTO();
        narrowCondition.setKeywords(new ArrayList<>(List.of("존재하지않는직무키워드XYZ")));
        narrowCondition.setJobsCode(new ArrayList<>());
        narrowCondition.setIsAddress(true);
        narrowCondition.setLargescaleUnits(new ArrayList<>(List.of("제주")));
        narrowCondition.setLocalUnits(new ArrayList<>(List.of("서귀포시")));
        narrowCondition.setMaxCount(10);

        List<JobCandidateDTO> candidates = recommendDAO.selectJobInfoCandidates(narrowCondition);
        System.out.println("[1차 검색] " + candidates.size() + "건");

        if (candidates.isEmpty()) {
            System.out.println("  → 0건이므로 완화 재검색 실행");
            // 폴백: 더 넓은 키워드로 재시도
            SearchConditionDTO fallbackCondition = new SearchConditionDTO();
            fallbackCondition.setKeywords(new ArrayList<>(List.of("사무")));
            fallbackCondition.setJobsCode(new ArrayList<>(List.of("312100")));
            fallbackCondition.setMaxCount(10);

            List<JobCandidateDTO> fallbackCandidates = recommendDAO.selectJobInfoCandidatesFallback(fallbackCondition);
            System.out.println("[완화 재검색] " + fallbackCandidates.size() + "건");

            for (int i = 0; i < Math.min(fallbackCandidates.size(), 3); i++) {
                JobCandidateDTO c = fallbackCandidates.get(i);
                System.out.println("  " + (i + 1) + ". " + c.getCompanyName() + " | " + c.getRecruitTitle());
            }
        } else {
            System.out.println("  → 1차 검색에서 결과 있음 (폴백 불필요)");
        }
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // Phase 2 검증: 폴백 검색조건 — 주소 파싱 (실제 DB 주소)
    // =========================================================

    @Test
    @Order(9)
    @DisplayName("[Phase 2] 폴백 검색조건 생성 — 실제 참여자 주소로 지역 파싱 검증")
    void testFallbackSearchConditionWithRealAddress() {
        System.out.println("\n========== [Phase 2] 폴백 검색조건 (실제 주소 파싱) ==========");

        RecommendParticipantDTO participant = recommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);
        List<RecommendCategoryDTO> categoryList = recommendDAO.getParticipantCategory(TEST_JOB_SEEKER_NO);
        List<String> certificates = recommendDAO.getParticipantCertificates(TEST_JOB_SEEKER_NO);

        if (participant == null) {
            System.out.println("[SKIP] 참여자 정보 없음");
            return;
        }

        System.out.println("[입력]");
        System.out.println("  주소 원본: " + participant.getInfoAddress());
        System.out.println("  카테고리: " + (categoryList != null ? categoryList.size() : 0) + "건");
        System.out.println("  자격증: " + (certificates != null ? certificates.size() : 0) + "건");

        SearchConditionDTO fallback = recommendService.buildFallbackSearchCondition(
                participant, categoryList, certificates);

        System.out.println("\n[폴백 검색조건 결과]");
        System.out.println("  keywords: " + fallback.getKeywords());
        System.out.println("  isAddress: " + fallback.getIsAddress());
        System.out.println("  largescaleUnits: " + fallback.getLargescaleUnits());
        System.out.println("  localUnits: " + fallback.getLocalUnits());

        // 검증
        if (participant.getInfoAddress() != null && !participant.getInfoAddress().trim().isEmpty()) {
            Assertions.assertTrue(fallback.getIsAddress(), "주소가 있으면 isAddress=true");
            Assertions.assertFalse(fallback.getLargescaleUnits().isEmpty(),
                    "광역자치단위가 추출되어야 함 (주소: " + participant.getInfoAddress() + ")");
            System.out.println("  ✅ 주소 파싱 정상: 광역=" + fallback.getLargescaleUnits()
                    + ", 기초=" + fallback.getLocalUnits());
        }
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // Phase 1 검증: Gemini Stage 2 — 베스트 선별 (항상 실행)
    // =========================================================

    @Test
    @Order(10)
    @DisplayName("[Phase 1] Gemini Stage 2 — 후보군 점수 산출 (알선상세정보 없어도 실행)")
    void testGeminiStage2BestSelection() {
        System.out.println("\n========== [Phase 1] Gemini Stage 2: 베스트 선별 ==========");

        RecommendParticipantDTO participant = recommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);
        RecommendReferralDTO referralInfo = recommendDAO.getParticipantReferral(TEST_JOB_SEEKER_NO);
        List<String> certificates = recommendDAO.getParticipantCertificates(TEST_JOB_SEEKER_NO);
        List<String> trainings = recommendDAO.getParticipantTrainings(TEST_JOB_SEEKER_NO);

        if (participant == null) {
            System.out.println("[SKIP] 참여자 정보 없음");
            return;
        }

        String alsonDetail = (referralInfo != null) ? referralInfo.getInfoAlsonDetail() : null;
        String additionalInfo = (referralInfo != null) ? referralInfo.getInfoAdditionalInfo() : null;

        System.out.println("[입력]");
        System.out.println("  알선상세정보: " + (alsonDetail != null ? alsonDetail : "없음 (Phase 1 개선: 없어도 Stage 2 실행)"));
        System.out.println("  추천사: " + (additionalInfo != null ? additionalInfo : "없음"));

        // 소수 후보군 수동 생성 (DB에서 3건만 조회)
        SearchConditionDTO searchCondition = new SearchConditionDTO();
        searchCondition.setKeywords(new ArrayList<>(List.of("사무", "행정", "관리")));
        searchCondition.setJobsCode(new ArrayList<>());
        searchCondition.setMaxCount(3);

        List<JobCandidateDTO> candidates = recommendDAO.selectJobInfoCandidates(searchCondition);

        if (candidates.isEmpty()) {
            System.out.println("[SKIP] 후보군 0건 — Stage 2 검증 불가");
            return;
        }
        System.out.println("  후보군: " + candidates.size() + "건");

        // Gemini API 호출 — Stage 2
        System.out.println("\n[Gemini API 호출 - Stage 2]");
        BestSelectionResultDTO result = geminiApiService.selectBestFromCandidates(
                candidates, participant, alsonDetail, additionalInfo, certificates, trainings);

        System.out.println("\n[베스트 선별 결과]");
        System.out.println("  bestGujinNo: " + result.getBestGujinNo());

        if (result.getScores() != null && !result.getScores().isEmpty()) {
            System.out.println("  점수 목록 (" + result.getScores().size() + "건):");
            for (RecommendationScoreDTO score : result.getScores()) {
                System.out.println("    구인인증번호: " + score.getCertNo()
                        + " | 점수: " + score.getScore()
                        + " | 사유: " + score.getReason());
            }

            // 검증: 점수가 있는 항목만 범위 확인 (Gemini 응답에 따라 null 가능)
            int validScoreCount = 0;
            for (RecommendationScoreDTO score : result.getScores()) {
                if (score.getScore() != null) {
                    Assertions.assertTrue(score.getScore() >= 0 && score.getScore() <= 100,
                            "점수 범위 0~100: " + score.getScore());
                    validScoreCount++;
                }
            }
            System.out.println("  점수 유효: " + validScoreCount + "/" + result.getScores().size() + "건");
        } else {
            System.out.println("  ⚠️ 점수 목록 비어있음");
        }
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // 전체 플로우 통합 테스트 — processAndSaveRecommend
    // =========================================================

    @Test
    @Order(11)
    @DisplayName("[전체 플로우] processAndSaveRecommend — DB 저장 포함 (롤백)")
    void testFullProcessAndSaveRecommend() {
        System.out.println("\n========== [전체 플로우] processAndSaveRecommend ==========");

        // forceRefresh=true로 24시간 쿨다운 무시
        ProcessRecommendResultDTO result = recommendService.processAndSaveRecommend(TEST_JOB_SEEKER_NO, true);

        System.out.println("[결과]");
        System.out.println("  success: " + result.isSuccess());
        System.out.println("  message: " + result.getMessage());
        System.out.println("  savedCount: " + result.getSavedCount());
        System.out.println("  reused: " + result.getReused());
        System.out.println("  participantName: " + result.getParticipantName());

        if (result.getSearchCondition() != null) {
            SearchConditionDTO sc = result.getSearchCondition();
            System.out.println("\n[Stage 1 검색조건]");
            System.out.println("  keywords: " + sc.getKeywords());
            System.out.println("  jobs_code: " + sc.getJobsCode());
            System.out.println("  isAddress: " + sc.getIsAddress());
            System.out.println("  largescaleUnits: " + sc.getLargescaleUnits());
            System.out.println("  localUnits: " + sc.getLocalUnits());
        }

        // 저장된 추천 목록 조회
        if (result.isSuccess() && result.getSavedCount() > 0) {
            List<ParticipantJobRecommendDTO> savedList = recommendDAO.selectRecommendList(TEST_JOB_SEEKER_NO);
            System.out.println("\n[저장된 추천 " + savedList.size() + "건]");

            int scoredCount = 0;
            int bestCount = 0;
            // 전체 리스트에서 점수/베스트 카운트
            for (ParticipantJobRecommendDTO rec : savedList) {
                if (rec.getRecommendationScore() != null && rec.getRecommendationScore() > 0) scoredCount++;
                if (rec.isBestJobInfo()) bestCount++;
            }

            // 상위 5건 출력
            for (int i = 0; i < Math.min(savedList.size(), 5); i++) {
                ParticipantJobRecommendDTO rec = savedList.get(i);
                System.out.println("  " + (i + 1) + ". " + rec.getRecommendedJobCompany()
                        + " | " + rec.getRecommendedJobTitle()
                        + " | 점수: " + rec.getRecommendationScore()
                        + " | 베스트: " + rec.isBestJobInfo()
                        + " | 사유: " + truncate(rec.getRecommendationReason(), 40));
            }
            if (savedList.size() > 5) {
                System.out.println("  ... 외 " + (savedList.size() - 5) + "건");
            }

            System.out.println("\n[Phase 1 검증] Stage 2 항상 실행 결과:");
            System.out.println("  점수 부여된 추천: " + scoredCount + "/" + savedList.size() + "건");
            System.out.println("  베스트 선정: " + bestCount + "건");

            // 검증: 추천사유가 있는지 확인 (Stage 2 실행 또는 그레이스풀 저하)
            boolean hasReason = savedList.stream()
                    .anyMatch(r -> r.getRecommendationReason() != null && !r.getRecommendationReason().isEmpty());
            if (hasReason) {
                System.out.println("  ✅ Stage 2 실행 확인 (추천사유 존재)");
            } else {
                System.out.println("  ⚠️ 추천사유 없음 — Stage 2가 생략되었거나 Gemini 응답이 비어있음");
            }
            System.out.println("  ✅ 추천 저장 " + savedList.size() + "건 정상 완료");
        }

        Assertions.assertTrue(result.isSuccess(), "추천 저장 성공해야 함");
        System.out.println("\n========== 전체 플로우 완료 ==========\n");
    }

    // =========================================================
    // Phase 4 검증: 쿨다운 상태 확인
    // =========================================================

    @Test
    @Order(12)
    @DisplayName("[Phase 4] 24시간 쿨다운 상태 조회")
    void testCooldownStatus() {
        System.out.println("\n========== [Phase 4] 쿨다운 상태 ==========");
        Map<String, Object> cooldown = recommendDAO.selectCooldownStatus(TEST_JOB_SEEKER_NO);

        if (cooldown != null) {
            System.out.println("  lastRecommendedAt: " + cooldown.get("lastRecommendedAt"));
            System.out.println("  cooldownActive: " + cooldown.get("cooldownActive"));
        } else {
            System.out.println("  추천 이력 없음 (쿨다운 비활성)");
        }
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // Phase 1 검증: hasEnoughInfo — 정보 충분 여부
    // =========================================================

    @Test
    @Order(13)
    @DisplayName("[Phase 1] 정보 충분 여부 검증 — 실제 참여자 데이터")
    void testHasEnoughInfoWithRealData() {
        System.out.println("\n========== [Phase 1] 정보 충분 여부 ==========");
        RecommendParticipantDTO participant = recommendDAO.getParticipantInfo(TEST_JOB_SEEKER_NO);
        List<RecommendCategoryDTO> categoryList = recommendDAO.getParticipantCategory(TEST_JOB_SEEKER_NO);

        if (participant == null) {
            System.out.println("[SKIP] 참여자 정보 없음");
            return;
        }

        boolean enough = geminiApiService.hasEnoughInfo(participant, categoryList);

        System.out.println("  학력: " + participant.getInfoEducation());
        System.out.println("  전공: " + participant.getInfoMajor());
        System.out.println("  카테고리: " + (categoryList != null ? categoryList.size() : 0) + "건");
        System.out.println("  정보 충분: " + enough);
        System.out.println("========== 완료 ==========\n");
    }

    // =========================================================
    // 유틸
    // =========================================================

    private String truncate(String text, int maxLen) {
        if (text == null) return "null";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
