package com.jobmoa.app.CounselMain.biz.recommend;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ParticipantJobRecommendDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticipantJobRecommendDAO.";

    // 추천 저장/갱신 (UPSERT)
    public int insertOrUpdateRecommend(ParticipantJobRecommendDTO dto) {
        log.info("Inserting or updating participant job recommend: {}", dto);
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

    public RecommendParticipantDTO getParticipantInfo(int jobSeekerNo) {
        log.debug("Fetching participant info for job seeker: {}", jobSeekerNo);
        return sqlSession.selectOne(ns + "selectParticipantInfo", jobSeekerNo);
    }

    public List<RecommendCategoryDTO> getParticipantCategory(int jobSeekerNo) {
        log.debug("Fetching participant category for job seeker: {}", jobSeekerNo);
        return sqlSession.selectList(ns + "selectParticipantCategory", jobSeekerNo);
    }

    public RecommendReferralDTO getParticipantReferral(int jobSeekerNo) {
        log.debug("Fetching participant referral for job seeker: {}", jobSeekerNo);
        return sqlSession.selectOne(ns + "selectParticipantReferral", jobSeekerNo);
    }

    // 가장 최근 저장된 추천 1건 조회 (24h 재사용 판단용)
    public ParticipantJobRecommendDTO selectLatestRecommend(int jobSeekerNo) {
        return sqlSession.selectOne(ns + "selectLatestRecommend", jobSeekerNo);
    }

    // 24시간 이내 추천 존재 여부 확인 (DB 서버 시간 기준 — DATEDIFF)
    public Map<String, Object> selectCooldownStatus(int jobSeekerNo) {
        return sqlSession.selectOne(ns + "selectCooldownStatus", jobSeekerNo);
    }

    // 구직번호 + 구인인증번호로 개별 추천 채용정보 조회
    public ParticipantJobRecommendDTO selectRecommendByCertNo(int jobSeekerNo, String certNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("jobSeekerNo", jobSeekerNo);
        params.put("certNo", certNo);
        return sqlSession.selectOne(ns + "selectRecommendByCertNo", params);
    }

    // 희망직무 관련 카테고리 서브트리 조회 (중분류명 기준)
    public List<JobCategoryDTO> getRelatedJobCategories(List<String> midCategoryNames) {
        Map<String, Object> params = new HashMap<>();
        params.put("midCategoryNames", midCategoryNames);
        return sqlSession.selectList(ns + "selectRelatedJobCategories", params);
    }

    // 채용정보 후보군 조회 (Gemini 생성 검색 조건 기반)
    public List<JobCandidateDTO> selectJobInfoCandidates(SearchConditionDTO searchCondition) {
        log.debug("Fetching job info candidates for search condition: {}", searchCondition);
        return sqlSession.selectList(ns + "selectJobInfoCandidates", searchCondition);
    }
}
