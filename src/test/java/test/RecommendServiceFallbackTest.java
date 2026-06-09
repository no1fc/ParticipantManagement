package test;

import com.jobmoa.app.CounselMain.biz.recommend.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ParticipantJobRecommendServiceImpl 폴백 검색조건 생성 테스트.
 * AI 실패 시 참여자 데이터에서 직접 키워드를 추출하는 buildFallbackSearchCondition() 검증.
 */
class RecommendServiceFallbackTest {

    private ParticipantJobRecommendServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ParticipantJobRecommendServiceImpl();
    }

    @Nested
    @DisplayName("buildFallbackSearchCondition - 키워드 추출")
    class KeywordExtractionTests {

        @Test
        @DisplayName("추천키워드가 최우선 반영됨")
        void recommendedKeywordsFirst() {
            RecommendParticipantDTO participant = createParticipant("서울 강남구 테헤란로 123");
            RecommendCategoryDTO cat = new RecommendCategoryDTO();
            cat.setRecommendedKeywords("웹개발, 프론트엔드");
            cat.setInfoJob("프로그래머");
            cat.setCategoryMiddle("소프트웨어개발");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, List.of(cat), Collections.emptyList());

            assertNotNull(result);
            assertTrue(result.getKeywords().contains("웹개발"));
            assertTrue(result.getKeywords().contains("프론트엔드"));
            assertTrue(result.getKeywords().contains("프로그래머"));
            assertTrue(result.getKeywords().contains("소프트웨어개발"));
        }

        @Test
        @DisplayName("자격증명이 키워드에 포함됨")
        void certificatesIncluded() {
            RecommendParticipantDTO participant = createParticipant("서울 강남구 테헤란로 123");
            RecommendCategoryDTO cat = new RecommendCategoryDTO();
            cat.setInfoJob("IT엔지니어");

            List<String> certificates = Arrays.asList("정보처리기사", "네트워크관리사");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, List.of(cat), certificates);

            assertTrue(result.getKeywords().contains("정보처리기사"));
            assertTrue(result.getKeywords().contains("네트워크관리사"));
        }

        @Test
        @DisplayName("중복 키워드 제거")
        void duplicateKeywordsRemoved() {
            RecommendParticipantDTO participant = createParticipant("서울 강남구 테헤란로 123");
            RecommendCategoryDTO cat = new RecommendCategoryDTO();
            cat.setRecommendedKeywords("웹개발");
            cat.setInfoJob("웹개발");
            cat.setCategoryMiddle("웹개발");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, List.of(cat), Collections.emptyList());

            long webDevCount = result.getKeywords().stream()
                    .filter("웹개발"::equals).count();
            assertEquals(1, webDevCount, "중복 키워드는 1개만 포함되어야 함");
        }

        @Test
        @DisplayName("카테고리/자격증 모두 null이면 빈 키워드")
        void emptyWhenNoData() {
            RecommendParticipantDTO participant = createParticipant(null);

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertNotNull(result);
            assertTrue(result.getKeywords().isEmpty());
        }
    }

    @Nested
    @DisplayName("buildFallbackSearchCondition - 지역 파싱 (실제 DB 주소 형식)")
    class AddressParsingTests {

        @Test
        @DisplayName("도로명 주소 파싱: 서울 강남구 테헤란로 123 (역삼동)")
        void parseRoadAddress() {
            RecommendParticipantDTO participant = createParticipant("서울 강남구 테헤란로 123 (역삼동)");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertTrue(result.getIsAddress());
            assertTrue(result.getLargescaleUnits().contains("서울"));
            assertTrue(result.getLocalUnits().contains("강남구"));
            // 도로명, 번지, 동 등은 포함되지 않아야 함
            assertFalse(result.getLargescaleUnits().contains("테헤란로"));
            assertFalse(result.getLargescaleUnits().contains("123"));
        }

        @Test
        @DisplayName("지번 주소 파싱: 경기 수원시 팔달구 인계동 123-4")
        void parseJibunAddress() {
            RecommendParticipantDTO participant = createParticipant("경기 수원시 팔달구 인계동 123-4");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertTrue(result.getIsAddress());
            assertTrue(result.getLargescaleUnits().contains("경기"));
            assertTrue(result.getLocalUnits().contains("수원시"));
        }

        @Test
        @DisplayName("정식 명칭 주소 파싱: 서울특별시 → 서울")
        void parseFullProvinceName() {
            RecommendParticipantDTO participant = createParticipant("서울특별시 서초구 반포대로 123");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertTrue(result.getIsAddress());
            assertTrue(result.getLargescaleUnits().contains("서울"));
            assertTrue(result.getLocalUnits().contains("서초구"));
        }

        @Test
        @DisplayName("광역시 파싱: 부산광역시 해운대구")
        void parseMetropolitanCity() {
            RecommendParticipantDTO participant = createParticipant("부산광역시 해운대구 우동 123");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertTrue(result.getIsAddress());
            assertTrue(result.getLargescaleUnits().contains("부산"));
            assertTrue(result.getLocalUnits().contains("해운대구"));
        }

        @Test
        @DisplayName("'군' 단위 지역 파싱: 강원 횡성군")
        void parseGunUnit() {
            RecommendParticipantDTO participant = createParticipant("강원 횡성군 횡성읍 읍상리 12");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertTrue(result.getLargescaleUnits().contains("강원"));
            assertTrue(result.getLocalUnits().contains("횡성군"));
        }

        @Test
        @DisplayName("세종(기초자치단체 없음): 세종특별자치시 조치원읍")
        void parseSejong() {
            RecommendParticipantDTO participant = createParticipant("세종특별자치시 조치원읍 신안리 1");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertTrue(result.getIsAddress());
            assertTrue(result.getLargescaleUnits().contains("세종"));
            // 조치원읍은 구/시/군으로 끝나지 않으므로 localUnits에 미포함
            assertTrue(result.getLocalUnits().isEmpty());
        }

        @Test
        @DisplayName("주소 없으면 isAddress=false (null)")
        void noAddressWhenNull() {
            RecommendParticipantDTO participant = createParticipant(null);

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertFalse(result.getIsAddress());
        }

        @Test
        @DisplayName("주소 없으면 isAddress=false (빈문자열)")
        void noAddressWhenEmpty() {
            RecommendParticipantDTO participant = createParticipant("  ");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, null, null);

            assertFalse(result.getIsAddress());
        }
    }

    @Nested
    @DisplayName("buildFallbackSearchCondition - 복합 시나리오")
    class CombinedTests {

        @Test
        @DisplayName("전체 데이터 통합: 추천키워드 + 희망직무 + 자격증 + 주소")
        void fullDataCombination() {
            RecommendParticipantDTO participant = createParticipant("서울 서초구 반포대로 58 (서초동)");

            RecommendCategoryDTO cat1 = new RecommendCategoryDTO();
            cat1.setRecommendedKeywords("React, TypeScript");
            cat1.setInfoJob("프론트엔드 개발자");
            cat1.setCategoryMiddle("소프트웨어개발");

            RecommendCategoryDTO cat2 = new RecommendCategoryDTO();
            cat2.setInfoJob("UI 디자이너");
            cat2.setCategoryMiddle("디자인");

            List<String> certificates = List.of("정보처리기사");

            SearchConditionDTO result = service.buildFallbackSearchCondition(
                    participant, Arrays.asList(cat1, cat2), certificates);

            // 키워드 검증
            List<String> keywords = result.getKeywords();
            assertTrue(keywords.contains("React"));
            assertTrue(keywords.contains("TypeScript"));
            assertTrue(keywords.contains("프론트엔드 개발자"));
            assertTrue(keywords.contains("UI 디자이너"));
            assertTrue(keywords.contains("소프트웨어개발"));
            assertTrue(keywords.contains("디자인"));
            assertTrue(keywords.contains("정보처리기사"));

            // 지역 검증
            assertTrue(result.getIsAddress());
            assertTrue(result.getLargescaleUnits().contains("서울"));
            assertTrue(result.getLocalUnits().contains("서초구"));
        }
    }

    private RecommendParticipantDTO createParticipant(String address) {
        RecommendParticipantDTO p = new RecommendParticipantDTO();
        p.setJobSeekerNo(12345);
        p.setInfoName("테스트참여자");
        p.setInfoAddress(address);
        return p;
    }
}
