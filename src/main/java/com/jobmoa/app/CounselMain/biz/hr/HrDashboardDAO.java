package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * HR - 인원현황 대시보드 DAO. MyBatis 네임스페이스 "HrDashboardDAO." 사용.
 * <p>읽기전용 집계: {@code J_직원}, {@code J_직원_재직기간}, {@code J_직원_부서배치}, {@code J_직원_발령이력}, {@code J_부서}.</p>
 */
@Slf4j
@Repository
public class HrDashboardDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrDashboardDAO.";

    /** 요약 카드: 재직/휴직/퇴사/당월입사/당월퇴사. */
    public HrDashboardDTO selectSummary() {
        log.info("HrDashboardDAO selectSummary");
        return sqlSession.selectOne(ns + "selectSummary");
    }

    /** 부서별 현재 주부서 배치 인원. */
    public List<Map<String, Object>> selectByDepartment() {
        return sqlSession.selectList(ns + "selectByDepartment");
    }

    /** 재직중 현재 cycle 근속 구간 분포. */
    public List<Map<String, Object>> selectTenureDistribution() {
        return sqlSession.selectList(ns + "selectTenureDistribution");
    }

    /** 최근 발령 이력 10건. */
    public List<Map<String, Object>> selectRecentTimeline() {
        return sqlSession.selectList(ns + "selectRecentTimeline");
    }
}
