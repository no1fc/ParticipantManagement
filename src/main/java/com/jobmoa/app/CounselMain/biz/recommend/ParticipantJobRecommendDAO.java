package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 참여자 AI 채용 추천 데이터 접근 객체.
 * <p>Gemini AI 기반 채용정보 추천 결과의 저장, 조회, 삭제 및 추천에 필요한
 * 참여자 정보, 희망직무 카테고리, 채용정보 후보군 조회 등을 담당한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "ParticipantJobRecommendDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ParticipantJobRecommendDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticipantJobRecommendDAO.";

    /**
     * 추천 채용정보를 UPSERT(존재하면 수정, 없으면 등록)한다.
     *
     * @param dto 추천 채용정보
     * @return 영향받은 행 수
     */
    public int insertOrUpdateRecommend(ParticipantJobRecommendDTO dto) {
        log.info("Inserting or updating participant job recommend: {}", dto);
        return sqlSession.insert(ns + "insertOrUpdateRecommend", dto);
    }

    /**
     * 구직번호 기준 추천 채용정보 목록을 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 추천 채용정보 목록
     */
    public List<ParticipantJobRecommendDTO> selectRecommendList(int jobSeekerNo) {
        return sqlSession.selectList(ns + "selectRecommendList", jobSeekerNo);
    }

    /**
     * 구직번호 기준 베스트 추천 채용정보 단건을 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 최우선 추천 채용정보, 없으면 null
     */
    public ParticipantJobRecommendDTO selectBestRecommend(int jobSeekerNo) {
        return sqlSession.selectOne(ns + "selectBestRecommend", jobSeekerNo);
    }

    /**
     * 구직번호 기준 추천 채용정보를 전체 삭제한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 삭제된 행 수
     */
    public int deleteRecommendByGujikNo(int jobSeekerNo) {
        return sqlSession.delete(ns + "deleteRecommendByGujikNo", jobSeekerNo);
    }

    /**
     * AI 추천에 필요한 참여자 기본 정보를 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 참여자 기본 정보
     */
    public RecommendParticipantDTO getParticipantInfo(int jobSeekerNo) {
        log.debug("Fetching participant info for job seeker: {}", jobSeekerNo);
        return sqlSession.selectOne(ns + "selectParticipantInfo", jobSeekerNo);
    }

    /**
     * 참여자의 희망직무 카테고리 목록을 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 희망직무 카테고리 목록
     */
    public List<RecommendCategoryDTO> getParticipantCategory(int jobSeekerNo) {
        log.debug("Fetching participant category for job seeker: {}", jobSeekerNo);
        return sqlSession.selectList(ns + "selectParticipantCategory", jobSeekerNo);
    }

    /**
     * 참여자의 보유 자격증 목록을 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 자격증명 목록
     */
    public List<String> getParticipantCertificates(int jobSeekerNo) {
        return sqlSession.selectList(ns + "selectParticipantCertificates", jobSeekerNo);
    }

    /**
     * 참여자의 직업훈련 이력 목록을 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 직업훈련명 목록
     */
    public List<String> getParticipantTrainings(int jobSeekerNo) {
        return sqlSession.selectList(ns + "selectParticipantTrainings", jobSeekerNo);
    }

    /**
     * 참여자의 알선 정보를 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @return 참여자 알선 정보, 없으면 null
     */
    public RecommendReferralDTO getParticipantReferral(int jobSeekerNo) {
        log.debug("Fetching participant referral for job seeker: {}", jobSeekerNo);
        return sqlSession.selectOne(ns + "selectParticipantReferral", jobSeekerNo);
    }

    /**
     * 가장 최근 저장된 추천 1건을 조회한다. (24시간 재사용 판단용)
     *
     * @param jobSeekerNo 구직번호
     * @return 최근 추천 데이터, 없으면 null
     */
    public ParticipantJobRecommendDTO selectLatestRecommend(int jobSeekerNo) {
        return sqlSession.selectOne(ns + "selectLatestRecommend", jobSeekerNo);
    }

    /**
     * 24시간 이내 추천 존재 여부(쿨다운 상태)를 확인한다. (DB 서버 시간 기준 DATEDIFF)
     *
     * @param jobSeekerNo 구직번호
     * @return 쿨다운 상태 정보 (Map 형태), 없으면 null
     */
    public Map<String, Object> selectCooldownStatus(int jobSeekerNo) {
        return sqlSession.selectOne(ns + "selectCooldownStatus", jobSeekerNo);
    }

    /**
     * 구직번호와 구인인증번호로 개별 추천 채용정보를 조회한다.
     *
     * @param jobSeekerNo 구직번호
     * @param certNo      구인인증번호
     * @return 추천 채용정보, 없으면 null
     */
    public ParticipantJobRecommendDTO selectRecommendByCertNo(int jobSeekerNo, String certNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("jobSeekerNo", jobSeekerNo);
        params.put("certNo", certNo);
        return sqlSession.selectOne(ns + "selectRecommendByCertNo", params);
    }

    /**
     * 희망직무 관련 카테고리 서브트리를 조회한다. (중분류명 기준)
     *
     * @param midCategoryNames 중분류명 목록
     * @return 관련 직업 카테고리 목록
     */
    public List<JobCategoryDTO> getRelatedJobCategories(List<String> midCategoryNames) {
        Map<String, Object> params = new HashMap<>();
        params.put("midCategoryNames", midCategoryNames);
        return sqlSession.selectList(ns + "selectRelatedJobCategories", params);
    }

    /**
     * Gemini AI 생성 검색 조건 기반 채용정보 후보군을 조회한다.
     *
     * @param searchCondition AI가 생성한 검색 조건
     * @return 채용정보 후보군 목록
     */
    public List<JobCandidateDTO> selectJobInfoCandidates(SearchConditionDTO searchCondition) {
        log.debug("Fetching job info candidates for search condition: {}", searchCondition);
        return sqlSession.selectList(ns + "selectJobInfoCandidates", searchCondition);
    }

    /**
     * 상담일지 복사용 채용공고 상세 정보를 단건 조회한다.
     *
     * @param wantedAuthNo 구인인증번호
     * @return 채용공고 상세 정보, 없으면 null
     */
    public JobPostingCopyDTO selectJobPostingDetail(String wantedAuthNo) {
        return sqlSession.selectOne(ns + "selectJobPostingDetail", wantedAuthNo);
    }
}
