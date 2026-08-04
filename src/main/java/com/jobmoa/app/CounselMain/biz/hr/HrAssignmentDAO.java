package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 부서배치·겸직 관리 DAO. MyBatis 네임스페이스 "HrAssignmentDAO." 사용.
 * <p>대상: {@code J_직원_부서배치}(M:N) + 주부서 변경 시 {@code J_직원_재직}·{@code J_직원_발령이력}.</p>
 */
@Slf4j
@Repository
public class HrAssignmentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrAssignmentDAO.";

    public List<HrAssignmentDTO> selectEmployeeList(HrAssignmentDTO dto) {
        log.info("HrAssignmentDAO selectEmployeeList");
        return sqlSession.selectList(ns + "selectEmployeeList", dto);
    }

    public List<HrAssignmentDTO> selectAssignmentList(HrAssignmentDTO dto) {
        log.info("HrAssignmentDAO selectAssignmentList userId={}", dto.getUserId());
        return sqlSession.selectList(ns + "selectAssignmentList", dto);
    }

    public List<HrAssignmentDTO> selectDeptList() {
        return sqlSession.selectList(ns + "selectDeptList");
    }

    public int selectOpenAssignmentToDeptCount(HrAssignmentDTO dto) {
        return sqlSession.selectOne(ns + "selectOpenAssignmentToDeptCount", dto);
    }

    public boolean addAssignment(HrAssignmentDTO dto) {
        log.info("HrAssignmentDAO addAssignment userId={} deptCode={}", dto.getUserId(), dto.getDeptCode());
        return sqlSession.insert(ns + "addAssignment", dto) > 0;
    }

    public boolean changePrimary(HrAssignmentDTO dto) {
        log.info("HrAssignmentDAO changePrimary userId={} assignPk={}", dto.getUserId(), dto.getAssignPk());
        return sqlSession.update(ns + "changePrimary", dto) > 0;
    }

    public boolean endAssignment(HrAssignmentDTO dto) {
        log.info("HrAssignmentDAO endAssignment assignPk={}", dto.getAssignPk());
        return sqlSession.update(ns + "endAssignment", dto) > 0;
    }
}
