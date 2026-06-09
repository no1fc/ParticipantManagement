package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmoa.app.CounselMain.biz.recommend.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 추천 관련 DTO 직렬화/역직렬화 테스트.
 * Gemini API 응답 JSON이 DTO로 정확하게 매핑되는지 검증한다.
 */
class RecommendDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("SearchConditionDTO JSON 매핑")
    class SearchConditionMappingTests {

        @Test
        @DisplayName("전체 필드 역직렬화")
        void deserializeFullFields() throws Exception {
            String json = "{"
                    + "\"keywords\":[\"웹개발\",\"React\",\"프론트엔드\"],"
                    + "\"jobs_code\":[\"1301\",\"1302\"],"
                    + "\"jobCategory\":\"소프트웨어개발\","
                    + "\"educationLevel\":\"대졸\","
                    + "\"industryType\":\"IT서비스\","
                    + "\"jobs_nm\":\"웹개발자\","
                    + "\"isAddress\":true,"
                    + "\"largescaleUnits\":[\"서울\"],"
                    + "\"localUnits\":[\"강남구\",\"서초구\"]"
                    + "}";

            SearchConditionDTO dto = objectMapper.readValue(json, SearchConditionDTO.class);

            assertEquals(3, dto.getKeywords().size());
            assertEquals("웹개발", dto.getKeywords().get(0));
            assertEquals(2, dto.getJobsCode().size());
            assertEquals("1301", dto.getJobsCode().get(0));
            assertEquals("소프트웨어개발", dto.getJobCategory());
            assertTrue(dto.getIsAddress());
            assertEquals(1, dto.getLargescaleUnits().size());
            assertEquals(2, dto.getLocalUnits().size());
        }

        @Test
        @DisplayName("최소 필드 역직렬화 (키워드만)")
        void deserializeMinimalFields() throws Exception {
            String json = "{\"keywords\":[\"사무직\"]}";

            SearchConditionDTO dto = objectMapper.readValue(json, SearchConditionDTO.class);

            assertEquals(1, dto.getKeywords().size());
            assertNull(dto.getJobsCode());
            assertFalse(dto.getIsAddress());
        }

        @Test
        @DisplayName("미지 필드 무시 (@JsonIgnoreProperties)")
        void ignoreUnknownFields() throws Exception {
            String json = "{\"keywords\":[\"IT\"],\"unknownField\":123,\"anotherUnknown\":\"test\"}";

            SearchConditionDTO dto = objectMapper.readValue(json, SearchConditionDTO.class);
            assertNotNull(dto);
            assertEquals(1, dto.getKeywords().size());
        }

        @Test
        @DisplayName("직렬화 → 역직렬화 왕복 검증")
        void serializeDeserializeRoundTrip() throws Exception {
            SearchConditionDTO original = new SearchConditionDTO();
            original.setKeywords(Arrays.asList("개발", "IT"));
            original.setJobsCode(Arrays.asList("1301"));
            original.setIsAddress(true);
            original.setLargescaleUnits(List.of("서울"));
            original.setLocalUnits(List.of("강남구"));

            String json = objectMapper.writeValueAsString(original);
            SearchConditionDTO deserialized = objectMapper.readValue(json, SearchConditionDTO.class);

            assertEquals(original.getKeywords(), deserialized.getKeywords());
            assertEquals(original.getJobsCode(), deserialized.getJobsCode());
            assertEquals(original.getIsAddress(), deserialized.getIsAddress());
        }
    }

    @Nested
    @DisplayName("BestSelectionResultDTO JSON 매핑")
    class BestSelectionMappingTests {

        @Test
        @DisplayName("다수 후보 점수 역직렬화")
        void deserializeMultipleScores() throws Exception {
            String json = "{"
                    + "\"bestGujinNo\":\"K151012025050002\","
                    + "\"scores\":["
                    + "  {\"구인인증번호\":\"K151012025050002\",\"score\":92,\"reason\":\"전공 및 자격증 완벽 일치\"},"
                    + "  {\"구인인증번호\":\"K151012025050003\",\"score\":78,\"reason\":\"경력 부합, 급여 적합\"},"
                    + "  {\"구인인증번호\":\"K151012025050004\",\"score\":65,\"reason\":\"지역 근접\"}"
                    + "]"
                    + "}";

            BestSelectionResultDTO dto = objectMapper.readValue(json, BestSelectionResultDTO.class);

            assertEquals("K151012025050002", dto.getBestGujinNo());
            assertEquals(3, dto.getScores().size());

            RecommendationScoreDTO best = dto.getScores().get(0);
            assertEquals("K151012025050002", best.getCertNo());
            assertEquals(92, best.getScore());
            assertTrue(best.getReason().contains("전공"));
        }

        @Test
        @DisplayName("빈 scores 배열 처리")
        void deserializeEmptyScores() throws Exception {
            String json = "{\"bestGujinNo\":null,\"scores\":[]}";

            BestSelectionResultDTO dto = objectMapper.readValue(json, BestSelectionResultDTO.class);
            assertNull(dto.getBestGujinNo());
            assertTrue(dto.getScores().isEmpty());
        }
    }

    @Nested
    @DisplayName("RecommendParticipantDTO 필드 검증")
    class ParticipantDTOTests {

        @Test
        @DisplayName("Phase 2에서 추가된 필드 동작 확인")
        void newFieldsWork() {
            RecommendParticipantDTO dto = new RecommendParticipantDTO();
            dto.setSpecialClass("장애인");
            dto.setEmploymentCapacity("B");

            assertEquals("장애인", dto.getSpecialClass());
            assertEquals("B", dto.getEmploymentCapacity());
        }

        @Test
        @DisplayName("RecommendCategoryDTO 추천키워드 필드")
        void categoryKeywordsField() {
            RecommendCategoryDTO dto = new RecommendCategoryDTO();
            dto.setRecommendedKeywords("웹개발, 프론트엔드, React");

            assertEquals("웹개발, 프론트엔드, React", dto.getRecommendedKeywords());
        }
    }
}
