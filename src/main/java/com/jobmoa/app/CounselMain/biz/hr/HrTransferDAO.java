package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 발령 관리 DAO. MyBatis 네임스페이스 "HrTransferDAO." 사용.
 * <p>대상: {@code J_직원_발령이력}(append-only) + 직급변경/휴직/복직 시 {@code J_직원}·{@code J_직원_재직}·{@code J_직원_계정}.</p>
 */
@Slf4j
@Repository
public class HrTransferDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrTransferDAO.";

    public List<HrTransferDTO> selectEmployeeList(HrTransferDTO dto) {
        log.info("HrTransferDAO selectEmployeeList");
        return sqlSession.selectList(ns + "selectEmployeeList", dto);
    }

    public List<HrTransferDTO> selectTransferList(HrTransferDTO dto) {
        log.info("HrTransferDAO selectTransferList userId={}", dto.getUserId());
        return sqlSession.selectList(ns + "selectTransferList", dto);
    }

    public List<HrTransferDTO> selectPositionList() {
        return sqlSession.selectList(ns + "selectPositionList");
    }

    public String selectCurrentStatus(HrTransferDTO dto) {
        return sqlSession.selectOne(ns + "selectCurrentStatus", dto);
    }

    public boolean changePosition(HrTransferDTO dto) {
        log.info("HrTransferDAO changePosition userId={} newPosition={}", dto.getUserId(), dto.getNewPosition());
        return sqlSession.update(ns + "changePosition", dto) > 0;
    }

    public boolean startLeave(HrTransferDTO dto) {
        log.info("HrTransferDAO startLeave userId={}", dto.getUserId());
        return sqlSession.update(ns + "startLeave", dto) > 0;
    }

    public boolean returnFromLeave(HrTransferDTO dto) {
        log.info("HrTransferDAO returnFromLeave userId={}", dto.getUserId());
        return sqlSession.update(ns + "returnFromLeave", dto) > 0;
    }
}
