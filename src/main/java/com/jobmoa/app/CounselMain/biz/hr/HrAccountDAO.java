package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 계정 관리 DAO. MyBatis 네임스페이스 "HrAccountDAO." 사용.
 * <p>대상 테이블: {@code J_직원_계정} (이름은 {@code J_직원} 조인).</p>
 */
@Slf4j
@Repository
public class HrAccountDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrAccountDAO.";

    public List<HrAccountDTO> selectAccountList(HrAccountDTO dto) {
        log.info("HrAccountDAO selectAccountList");
        return sqlSession.selectList(ns + "selectAccountList", dto);
    }

    public HrAccountDTO selectAccountOne(HrAccountDTO dto) {
        log.info("HrAccountDAO selectAccountOne id={}", dto.getUserId());
        return sqlSession.selectOne(ns + "selectAccountOne", dto);
    }

    public boolean updateAccountStatus(HrAccountDTO dto) {
        log.info("HrAccountDAO updateAccountStatus id={} status={}", dto.getUserId(), dto.getAccountStatus());
        return sqlSession.update(ns + "updateAccountStatus", dto) > 0;
    }

    public boolean resetPassword(HrAccountDTO dto) {
        log.info("HrAccountDAO resetPassword id={}", dto.getUserId());
        return sqlSession.update(ns + "resetPassword", dto) > 0;
    }

    public boolean unlockAccount(HrAccountDTO dto) {
        log.info("HrAccountDAO unlockAccount id={}", dto.getUserId());
        return sqlSession.update(ns + "unlockAccount", dto) > 0;
    }
}
