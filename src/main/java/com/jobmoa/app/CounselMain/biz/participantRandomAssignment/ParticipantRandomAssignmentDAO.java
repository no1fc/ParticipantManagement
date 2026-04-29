package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ParticipantRandomAssignmentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "PraDAO.";

    public boolean insert(ParticipantRandomAssignmentDTO praDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        log.info("ParticipantRandomAssignmentDAO insert Start");
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO insert SQL condition : [{}]",condition);
        boolean flag =  sqlSession.insert(ns+praDTO.getCondition(), praDTO) > 0;
        log.info("ParticipantRandomAssignmentDAO insert SQL flag : [{}]",flag);
        log.info("ParticipantRandomAssignmentDAO insert End");
        return flag;
    }

    public boolean update(ParticipantRandomAssignmentDTO praDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        log.info("ParticipantRandomAssignmentDAO update Start");
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO update SQL condition : [{}]",condition);
        boolean flag = sqlSession.update(ns+praDTO.getCondition(), praDTO) > 0;
        log.info("ParticipantRandomAssignmentDAO update SQL flag : [{}]",flag);
        log.info("ParticipantRandomAssignmentDAO update End");
        return flag;
    }
    public boolean delete(ParticipantRandomAssignmentDTO praDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        log.info("ParticipantRandomAssignmentDAO delete Start");
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO delete SQL condition : [{}]",condition);
        boolean flag = sqlSession.delete(ns+praDTO.getCondition(), praDTO) > 0;
        log.info("ParticipantRandomAssignmentDAO delete SQL flag : [{}]",flag);
        log.info("ParticipantRandomAssignmentDAO delete End");
        return flag;
    }
    public ParticipantRandomAssignmentDTO selectOne(ParticipantRandomAssignmentDTO praDTO) {
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO Select SQL praDTO condition : [{}]",condition);
        ParticipantRandomAssignmentDTO data = sqlSession.selectOne(ns+condition, praDTO);
        log.info("ParticipantRandomAssignmentDAO Select SQL data : [{}]",data);
        return data;
    }
    public List<ParticipantRandomAssignmentDTO> selectAll(ParticipantRandomAssignmentDTO praDTO) {
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO List<ParticipantRandomAssignmentDTO> SQL praDTO condition : [{}]",condition);
        List<ParticipantRandomAssignmentDTO> datas = sqlSession.selectList(ns+condition, praDTO);
        log.info("ParticipantRandomAssignmentDAO List<ParticipantRandomAssignmentDTO> SQL data : [{}]",datas);
        return datas;
    }
}
