package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 부서/조직 관리 DAO. MyBatis 네임스페이스 "HrDepartmentDAO." 사용.
 * <p>대상 테이블: {@code J_부서}.</p>
 */
@Slf4j
@Repository
public class HrDepartmentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrDepartmentDAO.";

    public List<HrDepartmentDTO> selectDepartmentList(HrDepartmentDTO dto) {
        log.info("HrDepartmentDAO selectDepartmentList");
        return sqlSession.selectList(ns + "selectDepartmentList", dto);
    }

    public HrDepartmentDTO selectDepartmentOne(HrDepartmentDTO dto) {
        log.info("HrDepartmentDAO selectDepartmentOne code={}", dto.getDeptCode());
        return sqlSession.selectOne(ns + "selectDepartmentOne", dto);
    }

    public int selectDeptCodeExists(HrDepartmentDTO dto) {
        return sqlSession.selectOne(ns + "selectDeptCodeExists", dto);
    }

    public boolean insertDepartment(HrDepartmentDTO dto) {
        log.info("HrDepartmentDAO insertDepartment code={}", dto.getDeptCode());
        return sqlSession.insert(ns + "insertDepartment", dto) > 0;
    }

    public boolean updateDepartment(HrDepartmentDTO dto) {
        log.info("HrDepartmentDAO updateDepartment code={}", dto.getDeptCode());
        return sqlSession.update(ns + "updateDepartment", dto) > 0;
    }

    public boolean softDeleteDepartment(HrDepartmentDTO dto) {
        log.info("HrDepartmentDAO softDeleteDepartment (soft) code={}", dto.getDeptCode());
        return sqlSession.update(ns + "softDeleteDepartment", dto) > 0;
    }
}
