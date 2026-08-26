package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 연계 실적 대시보드 데이터 접근 객체.
 * <p>DTO 내 condition 값에 따라 MyBatis 매퍼 ID를 동적으로 결정한다.
 * 매퍼 네임스페이스 "LinkageDashboardDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class LinkageDashboardDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "LinkageDashboardDAO.";

    /**
     * 조건에 맞는 연계 현황 목록을 조회한다(지점별/상담사별).
     *
     * @param dto condition(조회 조건)이 설정된 DTO
     * @return 연계 현황 목록
     */
    public List<LinkageDashboardDTO> selectAll(LinkageDashboardDTO dto) {
        log.info("LinkageDashboardDAO selectAll condition : [{}]", dto.getCondition());
        return sqlSession.selectList(ns + dto.getCondition(), dto);
    }

    /**
     * 조건에 맞는 연계 현황 단건을 조회한다(전체 합계).
     *
     * @param dto condition(조회 조건)이 설정된 DTO
     * @return 연계 현황 단건, 없으면 null
     */
    public LinkageDashboardDTO selectOne(LinkageDashboardDTO dto) {
        log.info("LinkageDashboardDAO selectOne condition : [{}]", dto.getCondition());
        return sqlSession.selectOne(ns + dto.getCondition(), dto);
    }
}
