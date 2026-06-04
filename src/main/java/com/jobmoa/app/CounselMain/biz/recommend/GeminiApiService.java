package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Google Gemini AI API 연동 서비스.
 * 참여자 정보를 기반으로 검색 조건을 생성하고, 채용정보 후보군에서 최적 공고를 선별한다.
 */
@Slf4j
@Service
public class GeminiApiService {

    // Stage별 Gemini Model 설정
    @Value("${gemini.api.model.stage1}")
    private String modelNameStage1;

    @Value("${gemini.api.model.stage2}")
    private String modelNameStage2;

    @Autowired
    private Client geminiClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 참여자 기본정보를 Gemini AI에 전달하여 채용정보 검색 조건을 생성한다.
     *
     * @param participant       참여자 기본 정보
     * @param referralInfo      참여자 알선 상세 정보
     * @param categoryList      참여자 희망직무 카테고리 목록
     * @param relatedCategories 관련 직종분류코드 목록
     * @param certificates      참여자 보유 자격증 목록
     * @param trainings         참여자 직업훈련 이력 목록
     * @return AI가 생성한 검색 조건 DTO
     */
    public SearchConditionDTO generateSearchCondition(
            RecommendParticipantDTO participant,
            RecommendReferralDTO referralInfo,
            List<RecommendCategoryDTO> categoryList,
            List<JobCategoryDTO> relatedCategories,
            List<String> certificates,
            List<String> trainings) {
        String prompt = buildSearchConditionPrompt(participant, referralInfo, categoryList, relatedCategories, certificates, trainings);
        log.info("generateSearchCondition prompt length: {}", prompt.length());
        String response = callGeminiApiWithRetry(prompt, modelNameStage1, buildSearchConditionSchema());
        log.info("generateSearchCondition response length: {}", response != null ? response.length() : 0);
        return parseSearchConditionResponse(response);
    }

    // 프롬프트 작성 메서드
    private String buildSearchConditionPrompt(
            RecommendParticipantDTO p,
            RecommendReferralDTO referralInfo,
            List<RecommendCategoryDTO> categoryList,
            List<JobCategoryDTO> relatedCategories,
            List<String> certificates,
            List<String> trainings) {

        // 희망직무 카테고리 정보 조립
        int listCount = 1;
        ArrayList<String> categoryArrayList = new ArrayList<>();
        if (categoryList != null && !categoryList.isEmpty()) {
            for (RecommendCategoryDTO category : categoryList) {
                String categoryString =
                        "  희망 " + listCount + "순위:\n"
                        + "    - 카테고리 대분류: " + nullSafe(category.getCategoryMain()) + "\n"
                        + "    - 카테고리 중분류: " + nullSafe(category.getCategoryMiddle()) + "\n"
                        + "    - 희망직무: " + nullSafe(category.getInfoJob()) + "\n";
                categoryArrayList.add(categoryString);
                listCount++;
            }
        }

        // 알선상세정보 및 추천사 조립
        String referralString = "정보없음";
        String additionalInfoString = "정보없음";
        if (referralInfo != null) {
            referralString = nullSafe(referralInfo.getInfoAlsonDetail());
            additionalInfoString = nullSafe(referralInfo.getInfoAdditionalInfo());
        }

        StringBuilder sb = new StringBuilder();

        // 역할 부여
        sb.append("당신은 한국 공공고용서비스 전문 상담사입니다. ");
        sb.append("구직자의 프로필을 분석하여 고용24 채용정보 DB에서 가장 적합한 채용공고를 찾기 위한 검색 조건을 생성합니다.\n\n");

        // 구직자 정보
        sb.append("■ 구직자 기본정보:\n");
        sb.append("- 학력: ").append(nullSafe(p.getInfoEducation())).append("\n");
        sb.append("- 전공: ").append(nullSafe(p.getInfoMajor())).append("\n");
        sb.append("- 경력: ").append(nullSafe(p.getInfoCareer())).append("\n");
        sb.append("- 주소(거주지): ").append(nullSafe(p.getInfoAddress())).append("\n");
        sb.append("- 희망급여: ").append(nullSafe(p.getInfoDesiredSalary())).append("\n");
        sb.append("- 특정계층: ").append(nullSafe(p.getSpecialClass())).append("\n");
        sb.append("- 취업역량: ").append(nullSafe(p.getEmploymentCapacity())).append("\n");
        sb.append("- 알선상세정보: ").append(referralString).append("\n");
        sb.append("- 상담사 추천사: ").append(additionalInfoString).append("\n");

        // 보유 자격증
        sb.append("- 보유 자격증: ");
        if (certificates != null && !certificates.isEmpty()) {
            sb.append(String.join(", ", certificates));
        } else {
            sb.append("정보없음");
        }
        sb.append("\n");

        // 직업훈련 이력
        sb.append("- 직업훈련 이력: ");
        if (trainings != null && !trainings.isEmpty()) {
            sb.append(String.join(", ", trainings));
        } else {
            sb.append("정보없음");
        }
        sb.append("\n\n");

        // 희망직무
        sb.append("■ 희망직무 카테고리:\n");
        for (String cat : categoryArrayList) {
            sb.append(cat);
        }

        // 추천키워드 (상담사가 직접 입력한 키워드)
        StringBuilder keywordsFromCounselor = new StringBuilder();
        if (categoryList != null) {
            for (RecommendCategoryDTO category : categoryList) {
                if (category.getRecommendedKeywords() != null && !category.getRecommendedKeywords().trim().isEmpty()) {
                    if (keywordsFromCounselor.length() > 0) keywordsFromCounselor.append(", ");
                    keywordsFromCounselor.append(category.getRecommendedKeywords());
                }
            }
        }
        if (keywordsFromCounselor.length() > 0) {
            sb.append("  상담사 추천키워드: ").append(keywordsFromCounselor).append(" (※ 이 키워드를 검색 조건에 최우선 반영하세요)\n");
        }
        sb.append("\n");

        // 직종분류코드 목록
        sb.append("■ 직종분류코드 목록:\n");
        sb.append("카테고리ID    카테고리명칭\n");
        if (relatedCategories != null && !relatedCategories.isEmpty()) {
            for (JobCategoryDTO cat : relatedCategories) {
                String indent;
                switch (cat.getCategoryType()) {
                    case 1:  indent = "";           break; // 대분류
                    case 2:  indent = "   ";        break; // 중분류
                    case 3:  indent = "         ";  break; // 소분류
                    default: indent = "";
                }
                sb.append(cat.getCategoryId())
                  .append(indent)
                  .append(cat.getCategoryName())
                  .append("\n");
            }
        }

        // 작업 지시
        sb.append("\n■ 작업 지시:\n");
        sb.append("1. 위 직종분류코드를 참고하여 구직자에게 가장 적합한 직종분류코드를 추출하세요.\n");
        sb.append("2. 키워드는 직무명, 업종명, 기술명 위주로 3~7개 생성하세요.\n");
        sb.append("   - 상담사 추천키워드가 있으면 이를 최우선으로 반영하세요.\n");
        sb.append("   - 추천사나 알선상세정보에 언급된 구체적 직무/기술을 우선 반영하세요.\n");
        sb.append("   - 보유 자격증을 관련 직무 키워드로 변환하여 포함하세요 (예: 정보처리기사 → IT, 소프트웨어).\n");
        sb.append("   - '회사', '직원', '근무', '채용', '모집' 등 지나치게 일반적인 키워드는 제외하세요.\n");

        // 지역 지시 — 거주지 기반 기본 검색 포함
        sb.append("3. 지역 설정 규칙:\n");
        sb.append("   - 1순위: 알선상세정보에 희망 근무지역이 명시되어 있으면 해당 지역을 사용하세요.\n");
        sb.append("   - 2순위: 희망지역이 없으면 구직자 주소(거주지)를 기반으로 해당 지역 및 인접 지역을 근무 가능 지역으로 추정하세요.\n");
        sb.append("   - 구직자 주소 정보가 있으면 isAddress를 반드시 true로 설정하세요.\n");
        sb.append("   - 광역자치 단위는 '시', '도'를 제외하고 작성 (예: 서울, 경기, 인천, 제주, 대전)\n");
        sb.append("   - 기초자치 단위는 '구', '시', '군'을 포함하여 작성 (예: 강남구, 수원시, 서귀포시)\n");
        sb.append("   - 인접 지역 예시: 서울 강남구 → 서초구, 송파구 포함 / 경기 수원시 → 용인시, 화성시 포함\n\n");

        // 응답 형식
        sb.append("응답 형식 (JSON만 출력):\n");
        sb.append("{\n");
        sb.append("  \"keywords\": [\"키워드1\", \"키워드2\", \"키워드3\"],\n");
        sb.append("  \"jobCategory\": \"직무분류\",\n");
        sb.append("  \"educationLevel\": \"학력조건\",\n");
        sb.append("  \"industryType\": \"업종분류\",\n");
        sb.append("  \"jobs_code\": [\"직종분류코드1\", \"직종분류코드2\"],\n");
        sb.append("  \"jobs_nm\": \"모집직종\",\n");
        sb.append("  \"isAddress\": true,\n");
        sb.append("  \"largescaleUnits\": [\"광역자치단위\"],\n");
        sb.append("  \"localUnits\": [\"기초자치단위1\", \"인접기초자치단위2\"]\n");
        sb.append("}");

        return sb.toString();
    }

    /**
     * 참여자 정보 충분 여부 확인
     * 학력, 전공, 카테고리, 희망직무 중 2개 이상 있어야 추천 진행 가능
     */
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

    // null 값 처리 메서드
    private String nullSafe(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "정보없음";
    }

    /**
     * 채용정보 후보군과 참여자 정보를 Gemini AI에 전달하여 최적 채용공고를 선별한다.
     *
     * @param candidates      채용정보 후보군 목록
     * @param participant     참여자 기본 정보
     * @param alsonDetail     알선 상세 정보 텍스트
     * @param additionalInfo  상담사 추천사 텍스트
     * @param certificates    참여자 보유 자격증 목록
     * @param trainings       참여자 직업훈련 이력 목록
     * @return 베스트 선별 결과 (최적 공고 번호 및 각 후보별 점수)
     */
    public BestSelectionResultDTO selectBestFromCandidates(
            List<JobCandidateDTO> candidates,
            RecommendParticipantDTO participant,
            String alsonDetail,
            String additionalInfo,
            List<String> certificates,
            List<String> trainings) {
        log.info("------------ selectBestFromCandidates 시작 -------------");
        String prompt = buildBestSelectionPrompt(candidates, participant, alsonDetail, additionalInfo, certificates, trainings);
        log.info("selectBestFromCandidates prompt length: {}", prompt.length());
        String response = callGeminiApiWithRetry(prompt, modelNameStage2, buildBestSelectionSchema());
        log.info("selectBestFromCandidates response length: {}", response != null ? response.length() : 0);
        log.info("------------ selectBestFromCandidates 종료 -------------");
        return parseBestSelectionResponse(response);
    }

    // 후보군 + 참여자정보 + 알선상세정보 + 추천사 + 자격증/훈련 전달로 최적 채용정보 선별 프롬프트 작성 메서드
    private String buildBestSelectionPrompt(
            List<JobCandidateDTO> candidates,
            RecommendParticipantDTO participant,
            String alsonDetail,
            String additionalInfo,
            List<String> certificates,
            List<String> trainings) {
        StringBuilder sb = new StringBuilder();

        // 역할 부여
        sb.append("당신은 한국 공공고용서비스 전문 상담사입니다. ");
        sb.append("구직자 프로필과 채용공고 후보군을 종합적으로 비교·분석하여 가장 적합한 채용공고를 선정하고 점수를 매기세요.\n\n");

        // 구직자 프로필 정보
        sb.append("■ 구직자 프로필:\n");
        sb.append("- 학력: ").append(nullSafe(participant.getInfoEducation())).append("\n");
        sb.append("- 전공: ").append(nullSafe(participant.getInfoMajor())).append("\n");
        sb.append("- 주소: ").append(nullSafe(participant.getInfoAddress())).append("\n");
        sb.append("- 경력: ").append(nullSafe(participant.getInfoCareer())).append("\n");
        sb.append("- 희망급여: ").append(nullSafe(participant.getInfoDesiredSalary())).append("\n");
        sb.append("- 알선상세정보: ").append(nullSafe(alsonDetail)).append("\n");
        sb.append("- 상담사 추천사: ").append(nullSafe(additionalInfo)).append("\n");
        sb.append("- 보유 자격증: ");
        if (certificates != null && !certificates.isEmpty()) {
            sb.append(String.join(", ", certificates));
        } else {
            sb.append("정보없음");
        }
        sb.append("\n");
        sb.append("- 직업훈련 이력: ");
        if (trainings != null && !trainings.isEmpty()) {
            sb.append(String.join(", ", trainings));
        } else {
            sb.append("정보없음");
        }
        sb.append("\n\n");

        // 채용공고 후보군 상세정보
        sb.append("■ 채용공고 후보군:\n");
        for (int i = 0; i < candidates.size(); i++) {
            JobCandidateDTO c = candidates.get(i);
            sb.append((i + 1)).append(". [구인인증번호: ").append(c.getCertNo()).append("]\n");
            sb.append("   기업명: ").append(nullSafe(c.getCompanyName())).append("\n");
            sb.append("   채용제목: ").append(nullSafe(c.getRecruitTitle())).append("\n");
            sb.append("   업종: ").append(nullSafe(c.getIndustryType())).append("\n");
            sb.append("   급여: ").append(nullSafe(c.getSalaryDesc())).append("\n");
            sb.append("   학력요건: ").append(nullSafe(c.getMinEducation()));
            if (c.getMaxEducation() != null && !c.getMaxEducation().isEmpty()) {
                sb.append(" ~ ").append(c.getMaxEducation());
            }
            sb.append("\n");
            sb.append("   경력요건: ").append(nullSafe(c.getCareer())).append("\n");
            sb.append("   회사규모: ").append(nullSafe(c.getBusinessSize())).append("\n");
            sb.append("   관련직종: ").append(nullSafe(c.getRelatedJobs())).append("\n");
            sb.append("   전공: ").append(nullSafe(c.getMajor())).append("\n");
            sb.append("   자격면허: ").append(nullSafe(c.getCertificate())).append("\n");
            sb.append("   직무내용: ").append(nullSafe(c.getJobContent())).append("\n\n");
        }

        // 가중치 평가 기준 안내
        sb.append("■ 평가 기준 (가중치 — 총합 100점):\n");
        sb.append("1. 직무 적합도 (30점): 알선상세정보·추천사와 직무내용·관련직종의 연관성\n");
        sb.append("2. 자격 부합도 (25점): 구직자 보유 자격과 채용공고 자격면허 요건의 일치도\n");
        sb.append("3. 학력·전공 일치도 (20점): 학력 수준 및 전공 분야 부합 여부\n");
        sb.append("4. 경력 적합성 (15점): 신입/경력 요건 부합 여부\n");
        sb.append("5. 급여·근무조건 (10점): 희망급여 대비 공고 급여 적합성\n\n");

        sb.append("■ 점수 산출 규칙:\n");
        sb.append("- 각 항목별 배점 내에서 점수를 산출하고, 5개 항목 합계가 총점(score)이 되도록 하세요.\n");
        sb.append("- 추천사유(reason)는 해당 공고가 구직자에게 적합한 핵심 이유를 1~2문장으로 작성하세요.\n\n");

        // 응답 형식
        sb.append("응답 형식 (JSON만 출력):\n")
                .append("{\n")
                .append("  \"bestGujinNo\": \"가장 높은 점수의 구인인증번호\",\n")
                .append("  \"scores\": [\n")
                .append("    {\n")
                .append("      \"구인인증번호\": \"...\",\n")
                .append("      \"score\": 85,\n")
                .append("      \"reason\": \"추천사유\"\n")
                .append("    }\n")
                .append("  ]\n")
                .append("}");
        return sb.toString();
    }

    // =========================================================
    // Structured Output 스키마 정의
    // =========================================================

    /** Stage 1 응답 스키마: 검색 조건 JSON */
    private Schema buildSearchConditionSchema() {
        Schema stringArraySchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(Schema.builder().type(Type.Known.STRING).build())
                .build();

        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "keywords", stringArraySchema,
                        "jobCategory", Schema.builder().type(Type.Known.STRING).build(),
                        "educationLevel", Schema.builder().type(Type.Known.STRING).build(),
                        "industryType", Schema.builder().type(Type.Known.STRING).build(),
                        "jobs_code", stringArraySchema,
                        "jobs_nm", Schema.builder().type(Type.Known.STRING).build(),
                        "isAddress", Schema.builder().type(Type.Known.BOOLEAN).build(),
                        "largescaleUnits", stringArraySchema,
                        "localUnits", stringArraySchema
                ))
                .build();
    }

    /** Stage 2 응답 스키마: 베스트 선별 결과 JSON */
    private Schema buildBestSelectionSchema() {
        Schema scoreItemSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "구인인증번호", Schema.builder().type(Type.Known.STRING).build(),
                        "score", Schema.builder().type(Type.Known.INTEGER).build(),
                        "reason", Schema.builder().type(Type.Known.STRING).build()
                ))
                .build();

        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "bestGujinNo", Schema.builder().type(Type.Known.STRING).build(),
                        "scores", Schema.builder()
                                .type(Type.Known.ARRAY)
                                .items(scoreItemSchema)
                                .build()
                ))
                .build();
    }

    // =========================================================
    // Gemini API 호출 + 재시도
    // =========================================================

    /** Gemini API 호출 (Structured Output 지원) */
    private String callGeminiApi(String prompt, String modelName, Schema responseSchema) {
        try {
            GenerateContentConfig.Builder configBuilder = GenerateContentConfig.builder()
                    .temperature(0.1f);

            // Structured Output 적용 (스키마가 있으면 JSON 응답 보장)
            if (responseSchema != null) {
                configBuilder.responseMimeType("application/json")
                             .responseSchema(responseSchema);
            }

            GenerateContentResponse response = geminiClient.models.generateContent(
                    modelName,
                    Content.fromParts(Part.fromText(prompt)),
                    configBuilder.build()
            );
            return response.text();
        } catch (Exception e) {
            throw new RuntimeException("Gemini API 호출 실패: " + e.getMessage(), e);
        }
    }

    /** 재시도 래퍼: maxRetry 2, 점진적 딜레이 (500ms, 1000ms) */
    String callGeminiApiWithRetry(String prompt, String modelName, Schema responseSchema) {
        int maxRetry = 2;
        for (int i = 0; i <= maxRetry; i++) {
            try {
                return callGeminiApi(prompt, modelName, responseSchema);
            } catch (Exception e) {
                log.warn("[Gemini API] 시도 {}/{} 실패 (model={}): {}", i + 1, maxRetry + 1, modelName, e.getMessage());
                if (i == maxRetry) {
                    throw new RuntimeException("Gemini API 최종 실패: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(500L * (i + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 중단", ie);
                }
            }
        }
        return null;
    }

    // Gemini API 응답에서 JSON 추출 메서드 (Structured Output 미적용 시 안전장치)
    String extractJsonFromResponse(String text) {
        if (text.contains("```json")) {
            text = text.substring(text.indexOf("```json") + 7);
            text = text.substring(0, text.lastIndexOf("```")).trim();
        } else if (text.contains("```")) {
            text = text.substring(text.indexOf("```") + 3);
            text = text.substring(0, text.lastIndexOf("```")).trim();
        }
        return text.trim();
    }

    // Gemini API 응답에서 JSON 추출 및 파싱 메서드
    SearchConditionDTO parseSearchConditionResponse(String responseText) {
        try {
            return objectMapper.readValue(extractJsonFromResponse(responseText), SearchConditionDTO.class);
        } catch (Exception e) {
            SearchConditionDTO fallback = new SearchConditionDTO();
            fallback.setKeywords(Collections.emptyList());
            fallback.setParseError(true);
            return fallback;
        }
    }

    // 후보군 + 알선상세정보 전달로 최적 채용정보 선별 응답 파싱 메서드
    BestSelectionResultDTO parseBestSelectionResponse(String responseText) {
        try {
            return objectMapper.readValue(extractJsonFromResponse(responseText), BestSelectionResultDTO.class);
        } catch (Exception e) {
            return new BestSelectionResultDTO();
        }
    }
}
