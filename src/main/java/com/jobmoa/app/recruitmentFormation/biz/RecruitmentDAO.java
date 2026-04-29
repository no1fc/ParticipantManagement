package com.jobmoa.app.recruitmentFormation.biz;

import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentDetailDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentPostingDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentResultDTO;
import com.jobmoa.app.recruitmentFormation.biz.dto.RecruitmentSearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JOB_POSTING 테이블 MyBatis DAO
 */
@Slf4j
@Repository
public class RecruitmentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NS = "RecruitmentDAO.";

    // ── 사용자 검색 (DB 조회) ─────────────────────────────────────

    /**
     * 검색 조건에 맞는 채용공고 목록 조회 (페이지네이션 포함)
     */
    public List<RecruitmentResultDTO.JobItem> searchPostings(RecruitmentSearchDTO dto) {
        return sqlSession.selectList(NS + "searchPostings", dto);
    }

    /**
     * 검색 조건에 맞는 총 건수 조회 (페이지네이션 총 페이지 계산용)
     */
    public int countPostings(RecruitmentSearchDTO dto) {
        Integer count = sqlSession.selectOne(NS + "countPostings", dto);
        return count != null ? count : 0;
    }

    // ── 스케줄러 동기화 (DB 저장) ──────────────────────────────────

    /**
     * 채용공고 UPSERT (MSSQL MERGE — wanted_auth_no 기준)
     */
    public void upsertPosting(RecruitmentPostingDTO dto) {
        sqlSession.update(NS + "upsertPosting", dto);
    }

    /**
     * syncDtm 이전에 갱신되지 않은 공고 삭제 (마감된 공고 처리)
     */
    public int deleteOldPostings(String syncDtm) {
        return sqlSession.delete(NS + "deleteOldPostings", syncDtm);
    }

    // ── 상세정보 수집 (스케줄러 Phase 2) ──────────────────────────────

    /**
     * detail_fetched=0 인 신규 공고 wantedAuthNo 목록 조회
     *
     * @param limit 최대 조회 건수 (1회 스케줄 기준 500건 권장)
     */
    public List<String> selectNewPostingIds(int limit) {
        return sqlSession.selectList(NS + "selectNewPostingIds", limit);
    }

    /**
     * 상세 API 수집 결과를 JOB_POSTING 테이블에 UPDATE + detail_fetched=1 마킹
     */
    public void updateDetailFetched(RecruitmentDetailDTO dto) {
        sqlSession.update(NS + "updateDetailFetched", dto);
    }

    /**
     * 리스트 전체 UPSERT (스케줄러 배치 저장)
     */
    public void upsertBatch(List<RecruitmentPostingDTO> list) {
        for (RecruitmentPostingDTO dto : list) {
            upsertPosting(dto);
        }
        log.debug("upsertBatch 완료: {}건", list.size());
    }
}