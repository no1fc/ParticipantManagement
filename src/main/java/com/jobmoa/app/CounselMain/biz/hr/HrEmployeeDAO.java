package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 직원 관리 DAO. MyBatis 네임스페이스 "HrEmployeeDAO." 사용.
 * <p>대상: {@code J_직원}(+재직/계정/부서배치/재직기간/발령이력).</p>
 */
@Slf4j
@Repository
public class HrEmployeeDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrEmployeeDAO.";

    public List<HrEmployeeDTO> selectEmployeeList(HrEmployeeDTO dto) {
        log.info("HrEmployeeDAO selectEmployeeList");
        return sqlSession.selectList(ns + "selectEmployeeList", dto);
    }

    public HrEmployeeDTO selectEmployeeOne(HrEmployeeDTO dto) {
        log.info("HrEmployeeDAO selectEmployeeOne userId={}", dto.getUserId());
        return sqlSession.selectOne(ns + "selectEmployeeOne", dto);
    }

    public int selectUserIdExists(HrEmployeeDTO dto) {
        return sqlSession.selectOne(ns + "selectUserIdExists", dto);
    }

    public boolean insertEmployee(HrEmployeeDTO dto) {
        log.info("HrEmployeeDAO insertEmployee userId={}", dto.getUserId());
        return sqlSession.insert(ns + "insertEmployee", dto) > 0;
    }

    public boolean updateEmployee(HrEmployeeDTO dto) {
        log.info("HrEmployeeDAO updateEmployee userId={}", dto.getUserId());
        return sqlSession.update(ns + "updateEmployee", dto) > 0;
    }

    public boolean resignEmployee(HrEmployeeDTO dto) {
        log.info("HrEmployeeDAO resignEmployee (soft) userId={}", dto.getUserId());
        return sqlSession.update(ns + "resignEmployee", dto) > 0;
    }

    public boolean reactivateEmployee(HrEmployeeDTO dto) {
        log.info("HrEmployeeDAO reactivateEmployee userId={}", dto.getUserId());
        return sqlSession.update(ns + "reactivateEmployee", dto) > 0;
    }
}
