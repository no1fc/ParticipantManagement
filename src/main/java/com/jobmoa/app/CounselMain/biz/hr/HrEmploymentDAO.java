package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 입퇴사 관리 DAO. MyBatis 네임스페이스 "HrEmploymentDAO." 사용.
 * <p>대상: {@code J_직원_재직기간}(cycle) + 재입사/퇴사 시 {@code J_직원}·{@code J_직원_계정}·{@code J_직원_발령이력}.</p>
 */
@Slf4j
@Repository
public class HrEmploymentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrEmploymentDAO.";

    public List<HrEmploymentDTO> selectEmployeeList(HrEmploymentDTO dto) {
        log.info("HrEmploymentDAO selectEmployeeList");
        return sqlSession.selectList(ns + "selectEmployeeList", dto);
    }

    public List<HrEmploymentDTO> selectCycleList(HrEmploymentDTO dto) {
        log.info("HrEmploymentDAO selectCycleList userId={}", dto.getUserId());
        return sqlSession.selectList(ns + "selectCycleList", dto);
    }

    public int selectOpenCycleCount(HrEmploymentDTO dto) {
        return sqlSession.selectOne(ns + "selectOpenCycleCount", dto);
    }

    public boolean rehire(HrEmploymentDTO dto) {
        log.info("HrEmploymentDAO rehire userId={}", dto.getUserId());
        return sqlSession.insert(ns + "rehire", dto) > 0;
    }

    public boolean resign(HrEmploymentDTO dto) {
        log.info("HrEmploymentDAO resign userId={}", dto.getUserId());
        return sqlSession.update(ns + "resign", dto) > 0;
    }

    public boolean updateCycle(HrEmploymentDTO dto) {
        log.info("HrEmploymentDAO updateCycle cyclePk={}", dto.getCyclePk());
        return sqlSession.update(ns + "updateCycle", dto) > 0;
    }
}
