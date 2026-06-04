package com.jobmoa.app.CounselMain.biz.recommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GeminiApiService 단위 테스트.
 * AI API 호출 없이 프롬프트 빌드, JSON 파싱, 정보 충분 여부 검증 등을 테스트한다.
 */
class GeminiApiServiceTest {

    private GeminiApiService geminiApiService;

    @BeforeEach
    void setUp() throws Exception {
        geminiApiService = new GeminiApiService();

        // ObjectMapper 주입 (Autowired 대체)
        Field objectMapperField = GeminiApiService.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(geminiApiService, new ObjectMapper());
    }

    // =========================================================
    // extractJsonFromResponse 테스트
    // =========================================================

    @Nested
    @DisplayName("extractJsonFromResponse")
    class ExtractJsonTests {

        @Test
        @DisplayName("```json 블록에서 JSON 추출")
        void extractFromJsonCodeBlock() {
            String response = "다음은 결과입니다.\n```json\n{\"keywords\":[\"개발\"]}\n```\n끝.";
            String result = geminiApiService.extractJsonFromResponse(response);
            assertEquals("{\"keywords\":[\"개발\"]}", result);
        }

        @Test
        @DisplayName("``` 블록에서 JSON 추출")
        void extractFromPlainCodeBlock() {
            String response = "결과:\n```\n{\"keywords\":[\"웹\"]}\n```";
            String result = geminiApiService.extractJsonFromResponse(response);
            assertEquals("{\"keywords\":[\"웹\"]}", result);
        }

        @Test
        @DisplayName("코드 블록 없는 순수 JSON 반환")
        void extractFromPlainJson() {
            String response = "{\"keywords\":[\"IT\"]}";
            String result = geminiApiService.extractJsonFromResponse(response);
            assertEquals("{\"keywords\":[\"IT\"]}", result);
        }

        @Test
        @DisplayName("빈 문자열 처리")
        void extractFromEmptyString() {
            String result = geminiApiService.extractJsonFromResponse("");
            assertEquals("", result);
        }
    }

    // =========================================================
    // parseSearchConditionResponse 테스트
    // =========================================================

    @Nested
    @DisplayName("parseSearchConditionResponse")
    class ParseSearchConditionTests {

        @Test
        @DisplayName("정상 JSON 파싱")
        void parseValidJson() {
            String json = "{\"keywords\":[\"웹개발\",\"프론트엔드\"],\"jobs_code\":[\"1234\"],\"isAddress\":true,\"largescaleUnits\":[\"서울\"],\"localUnits\":[\"강남구\"]}";
            SearchConditionDTO result = geminiApiService.parseSearchConditionResponse(json);

            assertNotNull(result);
            assertEquals(2, result.getKeywords().size());
            assertEquals("웹개발", result.getKeywords().get(0));
            assertTrue(result.getIsAddress());
            assertEquals("서울", result.getLargescaleUnits().get(0));
            assertEquals("강남구", result.getLocalUnits().get(0));
            assertFalse(result.getParseError());
        }

        @Test
        @DisplayName("```json 래핑된 응답 파싱")
        void parseJsonWithCodeBlock() {
            String response = "```json\n{\"keywords\":[\"데이터분석\"],\"isAddress\":false}\n```";
            SearchConditionDTO result = geminiApiService.parseSearchConditionResponse(response);

            assertNotNull(result);
            assertEquals(1, result.getKeywords().size());
            assertEquals("데이터분석", result.getKeywords().get(0));
        }

        @Test
        @DisplayName("잘못된 JSON 시 parseError=true 폴백")
        void parseInvalidJsonReturnsFallback() {
            String invalidJson = "이것은 JSON이 아닙니다";
            SearchConditionDTO result = geminiApiService.parseSearchConditionResponse(invalidJson);

            assertNotNull(result);
            assertTrue(result.getParseError());
            assertTrue(result.getKeywords().isEmpty());
        }

        @Test
        @DisplayName("미지 필드 포함 JSON도 정상 파싱 (@JsonIgnoreProperties)")
        void parseJsonWithUnknownFields() {
            String json = "{\"keywords\":[\"IT\"],\"unknownField\":\"value\",\"isAddress\":false}";
            SearchConditionDTO result = geminiApiService.parseSearchConditionResponse(json);

            assertNotNull(result);
            assertEquals(1, result.getKeywords().size());
        }
    }

    // =========================================================
    // parseBestSelectionResponse 테스트
    // =========================================================

    @Nested
    @DisplayName("parseBestSelectionResponse")
    class ParseBestSelectionTests {

        @Test
        @DisplayName("정상 점수 응답 파싱")
        void parseValidScoreResponse() {
            String json = "{\"bestGujinNo\":\"K151012025050002\",\"scores\":[{\"구인인증번호\":\"K151012025050002\",\"score\":85,\"reason\":\"전공 일치\"}]}";
            BestSelectionResultDTO result = geminiApiService.parseBestSelectionResponse(json);

            assertNotNull(result);
            assertEquals("K151012025050002", result.getBestGujinNo());
            assertEquals(1, result.getScores().size());
            assertEquals(85, result.getScores().get(0).getScore());
            assertEquals("전공 일치", result.getScores().get(0).getReason());
        }

        @Test
        @DisplayName("다수 후보 점수 파싱")
        void parseMultipleScores() {
            String json = "{\"bestGujinNo\":\"A001\",\"scores\":["
                    + "{\"구인인증번호\":\"A001\",\"score\":90,\"reason\":\"최적 매치\"},"
                    + "{\"구인인증번호\":\"A002\",\"score\":75,\"reason\":\"경력 부합\"},"
                    + "{\"구인인증번호\":\"A003\",\"score\":60,\"reason\":\"지역 근접\"}"
                    + "]}";
            BestSelectionResultDTO result = geminiApiService.parseBestSelectionResponse(json);

            assertEquals(3, result.getScores().size());
            assertEquals("A001", result.getBestGujinNo());
            assertEquals(90, result.getScores().get(0).getScore());
        }

        @Test
        @DisplayName("파싱 실패 시 빈 DTO 반환")
        void parseFailureReturnsEmptyDto() {
            BestSelectionResultDTO result = geminiApiService.parseBestSelectionResponse("not json");

            assertNotNull(result);
            assertNull(result.getBestGujinNo());
            assertNull(result.getScores());
        }
    }

    // =========================================================
    // hasEnoughInfo 테스트
    // =========================================================

    @Nested
    @DisplayName("hasEnoughInfo")
    class HasEnoughInfoTests {

        @Test
        @DisplayName("학력+전공 있으면 충분 (2개)")
        void educationAndMajorIsSufficient() {
            RecommendParticipantDTO p = new RecommendParticipantDTO();
            p.setInfoEducation("대졸");
            p.setInfoMajor("컴퓨터공학");

            assertTrue(geminiApiService.hasEnoughInfo(p, Collections.emptyList()));
        }

        @Test
        @DisplayName("학력만 있으면 부족 (1개)")
        void onlyEducationIsInsufficient() {
            RecommendParticipantDTO p = new RecommendParticipantDTO();
            p.setInfoEducation("고졸");

            assertFalse(geminiApiService.hasEnoughInfo(p, Collections.emptyList()));
        }

        @Test
        @DisplayName("카테고리+희망직무 있으면 충분 (2개)")
        void categoryAndJobIsSufficient() {
            RecommendParticipantDTO p = new RecommendParticipantDTO();
            RecommendCategoryDTO cat = new RecommendCategoryDTO();
            cat.setInfoJob("웹개발자");

            assertTrue(geminiApiService.hasEnoughInfo(p, List.of(cat)));
        }

        @Test
        @DisplayName("모든 정보 null이면 부족")
        void allNullIsInsufficient() {
            RecommendParticipantDTO p = new RecommendParticipantDTO();
            assertFalse(geminiApiService.hasEnoughInfo(p, null));
        }

        @Test
        @DisplayName("학력+카테고리+희망직무 모두 있으면 충분 (4개)")
        void fullInfoIsSufficient() {
            RecommendParticipantDTO p = new RecommendParticipantDTO();
            p.setInfoEducation("대졸");
            p.setInfoMajor("경영학");
            RecommendCategoryDTO cat = new RecommendCategoryDTO();
            cat.setInfoJob("사무직");

            assertTrue(geminiApiService.hasEnoughInfo(p, List.of(cat)));
        }
    }
}
