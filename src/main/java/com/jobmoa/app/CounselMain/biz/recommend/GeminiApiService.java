package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GeminiApiService {

    // 기본 설정 값에 적용되어 있는 Gemini Model 설정
    @Value("${gemini.api.model}")
    private String modelName;

    @Autowired
    private Client geminiClient;

    @Autowired
    private ObjectMapper objectMapper;

    // 참여자 기본정보를 Gemini에 전달하여 검색 조건을 생성하는 메서드
    public SearchConditionDTO generateSearchCondition(
            RecommendParticipantDTO participant,
            RecommendReferralDTO referralInfo,
            List<RecommendCategoryDTO> categoryList,
            List<JobCategoryDTO> relatedCategories) {
        String prompt = buildSearchConditionPrompt(participant, referralInfo, categoryList, relatedCategories);
        System.out.println("generateSearchCondition prompt: [" + prompt + "]");
        String response = callGeminiApiWithRetry(prompt);
        System.out.println("generateSearchCondition response: [" + response + "]");
        return parseSearchConditionResponse(response);
    }

    // 프롬프트 작성 메서드
    private String buildSearchConditionPrompt(
            RecommendParticipantDTO p,
            RecommendReferralDTO referralInfo,
            List<RecommendCategoryDTO> categoryList,
            List<JobCategoryDTO> relatedCategories) {

        int listCount = 0;
        ArrayList<String> categoryArrayList = new ArrayList<>();
        if (categoryList != null && !categoryList.isEmpty()) {
            for (RecommendCategoryDTO category : categoryList) {
                String categoryString =
                        "희망 " + listCount + "순위 :"
                        + "  - 카테고리 대분류: " + nullSafe(category.getCategoryMain())                   + "\n"
                        + "  - 카테고리 중분류: " + nullSafe(category.getCategoryMiddle())                 + "\n"
                        + "  - 희망직무: "       + nullSafe(category.getInfoJob()) + "\n";
                categoryArrayList.add(categoryString);
                listCount++;
            }
        }

        String referralString = "정보없음";
        if (referralInfo != null) {
            referralString = nullSafe(referralInfo.getInfoAlsonDetail());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("다음 구직자 정보를 기반으로 고용24 채용정보 DB에서 검색할 조건을 JSON 형식으로 생성해 주세요.\n");
        sb.append("구직자 정보:\n");
        sb.append("- 학력: ").append(nullSafe(p.getInfoEducation())).append("\n");
        sb.append("- 전공: ").append(nullSafe(p.getInfoMajor())).append("\n");
        sb.append("- 주소: ").append(nullSafe(p.getInfoAddress())).append("\n");
        sb.append("- 상세정보: ").append(nullSafe(referralString)).append("\n");
        sb.append(categoryArrayList).append("\n");
        sb.append("직종분류코드 목록: \n");
        sb.append("카테고리 ID    카테고리명칭\n");

        // DB에서 조회한 관련 카테고리를 동적으로 추가
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

        sb.append("\n위 직종분류코드를 참고하여 구직자에게 가장 적합한 직종분류코드를 추출하세요.\n");
        sb.append("\n 상세정보에 희망지역이 있다면 isAddress true로 변경하고 광역자치 단위, 기초자치 단위를 작성하세요.\n");
        sb.append("\n 광역자치 단위는 서울, 제주, 대전, 경기도, 인천 등 (시, 도)를 제외하고 작성하세요.\n");
        sb.append("\n 기초자치 단위는 강남구, 서귀포시, 유성구, 수원시 등 (구, 시, 군)을 작성하세요.\n");
        sb.append("응답 형식 (JSON만 출력):\n");
        sb.append("{\n");
        sb.append("  \"keywords\": [\"키워드1\", \"키워드2\"],\n");
        sb.append("  \"jobCategory\": \"직무분류\",\n");
        sb.append("  \"educationLevel\": \"학력조건\",\n");
        sb.append("  \"industryType\": \"업종분류\",\n");
        sb.append("  \"jobs_code\": [\"직종분류코드1\", \"직종분류코드2\"],\n");
        sb.append("  \"jobs_nm\": \"모집직종\",\n");
        sb.append("  \"isAddress\": false,\n");
        sb.append("  \"largescaleUnits\": [\"광역자치 단위1\", \"광역자치 단위2\"],\n");
        sb.append("  \"localUnits\": [\"기초자치 단위1\", \"기초자치 단위2\"]\n");
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

    // 후보군 + 참여자정보 + 알선상세정보 전달해서 최적 채용정보 선별 메서드
    public BestSelectionResultDTO selectBestFromCandidates(
            List<JobCandidateDTO> candidates,
            RecommendParticipantDTO participant,
            String alsonDetail) {
        System.out.println("------------ selectBestFromCandidates 시작 -------------");
        String prompt = buildBestSelectionPrompt(candidates, participant, alsonDetail);
        System.out.println("prompt: " + prompt);
        String response = callGeminiApiWithRetry(prompt);
        System.out.println("response: " + response);
        System.out.println("------------ selectBestFromCandidates 종료 -------------");
        return parseBestSelectionResponse(response);
    }

    // 후보군 + 참여자정보 + 알선상세정보 전달로 최적 채용정보 선별 프롬프트 작성 메서드
    private String buildBestSelectionPrompt(
            List<JobCandidateDTO> candidates,
            RecommendParticipantDTO participant,
            String alsonDetail) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음 구직자 프로필과 채용공고 후보군을 종합적으로 비교하여 가장 적합한 채용공고를 선정하고 각 점수를 매겨주세요.\n\n");

        // 구직자 프로필 정보
        sb.append("■ 구직자 프로필:\n");
        sb.append("- 학력: ").append(nullSafe(participant.getInfoEducation())).append("\n");
        sb.append("- 전공: ").append(nullSafe(participant.getInfoMajor())).append("\n");
        sb.append("- 주소: ").append(nullSafe(participant.getInfoAddress())).append("\n");
        sb.append("- 경력: ").append(nullSafe(participant.getInfoCareer())).append("\n");
        sb.append("- 희망급여: ").append(nullSafe(participant.getInfoDesiredSalary())).append("\n");
        sb.append("- 알선상세정보: ").append(nullSafe(alsonDetail)).append("\n\n");

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

        // 평가 기준 안내
        sb.append("■ 평가 기준:\n");
        sb.append("- 구직자의 학력·전공과 채용공고의 학력요건·전공 일치도\n");
        sb.append("- 구직자의 경력과 채용공고의 경력요건 부합 여부\n");
        sb.append("- 구직자의 희망급여와 채용공고 급여 수준 적합성\n");
        sb.append("- 알선상세정보와 직무내용·관련직종의 연관성\n");
        sb.append("- 자격면허 보유 여부에 따른 가점\n\n");

        sb.append("만약 후보군이 없다면 가장 최신 정보를 제공해주세요.\n\n");

        sb.append("응답 형식 (JSON만 출력):\n")
                .append("{\n")
                .append("  \"bestGujinNo\": \"최적채용공고구인인증번호\",\n")
                .append("  \"scores\": [\n")
                .append("    {\"구인인증번호\": \"...\", \"score\": 85, \"reason\": \"추천사유\"}\n")
                .append("  ]\n")
                .append("}");
        return sb.toString();
    }

    // Gemini API 호출 메서드
    private String callGeminiApi(String prompt) {
        try {
            GenerateContentConfig config =
                    GenerateContentConfig
                            .builder()
                            .build();

            GenerateContentResponse response = geminiClient.models.generateContent(
                    modelName,
                    Content.fromParts(Part.fromText(prompt)),
                    config
            );
            return response.text();
        } catch (Exception e) {
            throw new RuntimeException("Gemini API 호출 실패: " + e.getMessage(), e);
        }
    }

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

    // Gemini API 응답에서 JSON 추출 메서드
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

    // Gemini API 응답에서 JSON 추출 및 파싱 메서드
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

    // 후보군 + 알선상세정보 전달로 최적 채용정보 선별 응답 파싱 메서드
    private BestSelectionResultDTO parseBestSelectionResponse(String responseText) {
        try {
            return objectMapper.readValue(extractJsonFromResponse(responseText), BestSelectionResultDTO.class);
        } catch (Exception e) {
            return new BestSelectionResultDTO();
        }
    }
}
