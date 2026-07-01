package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 근속정책 관리 DAO. MyBatis 네임스페이스 "HrTenurePolicyDAO." 사용.
 * <p>대상 테이블: {@code J_근속산정정책}.</p>
 */
@Slf4j
@Repository
public class HrTenurePolicyDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrTenurePolicyDAO.";

    public List<HrTenurePolicyDTO> selectTenurePolicyList(HrTenurePolicyDTO dto) {
        log.info("HrTenurePolicyDAO selectTenurePolicyList");
        return sqlSession.selectList(ns + "selectTenurePolicyList", dto);
    }

    public HrTenurePolicyDTO selectTenurePolicyOne(HrTenurePolicyDTO dto) {
        log.info("HrTenurePolicyDAO selectTenurePolicyOne key={}", dto.getPolicyKey());
        return sqlSession.selectOne(ns + "selectTenurePolicyOne", dto);
    }

    public int selectPolicyKeyExists(HrTenurePolicyDTO dto) {
        return sqlSession.selectOne(ns + "selectPolicyKeyExists", dto);
    }

    public boolean insertTenurePolicy(HrTenurePolicyDTO dto) {
        log.info("HrTenurePolicyDAO insertTenurePolicy key={}", dto.getPolicyKey());
        return sqlSession.insert(ns + "insertTenurePolicy", dto) > 0;
    }

    public boolean updateTenurePolicy(HrTenurePolicyDTO dto) {
        log.info("HrTenurePolicyDAO updateTenurePolicy key={}", dto.getPolicyKey());
        return sqlSession.update(ns + "updateTenurePolicy", dto) > 0;
    }

    public boolean softDeleteTenurePolicy(HrTenurePolicyDTO dto) {
        log.info("HrTenurePolicyDAO softDeleteTenurePolicy (soft) key={}", dto.getPolicyKey());
        return sqlSession.update(ns + "softDeleteTenurePolicy", dto) > 0;
    }
}
