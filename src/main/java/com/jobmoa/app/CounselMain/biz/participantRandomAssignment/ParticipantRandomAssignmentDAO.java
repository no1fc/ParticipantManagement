package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 랜덤 배정 데이터 접근 객체.
 * <p>참여자의 상담사 랜덤 배정 처리에 대한 등록, 수정, 삭제, 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "PraDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ParticipantRandomAssignmentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "PraDAO.";

    /**
     * 참여자 랜덤 배정 데이터를 등록한다.
     *
     * @param praDTO condition(조건) 및 등록할 배정 정보가 설정된 DTO
     * @return 등록 성공 여부
     */
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

    /**
     * 참여자 랜덤 배정 데이터를 수정한다.
     *
     * @param praDTO condition(조건) 및 수정할 배정 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
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
    /**
     * 참여자 랜덤 배정 데이터를 삭제한다.
     *
     * @param praDTO condition(조건)이 설정된 삭제 대상 DTO
     * @return 삭제 성공 여부
     */
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
    /**
     * 조건에 맞는 참여자 랜덤 배정 단건을 조회한다.
     *
     * @param praDTO condition(조회 조건)이 설정된 DTO
     * @return 배정 단건 데이터, 없으면 null
     */
    public ParticipantRandomAssignmentDTO selectOne(ParticipantRandomAssignmentDTO praDTO) {
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO Select SQL praDTO condition : [{}]",condition);
        ParticipantRandomAssignmentDTO data = sqlSession.selectOne(ns+condition, praDTO);
        log.info("ParticipantRandomAssignmentDAO Select SQL data : [{}]",data);
        return data;
    }
    /**
     * 조건에 맞는 참여자 랜덤 배정 목록을 조회한다.
     *
     * @param praDTO condition(조회 조건)이 설정된 DTO
     * @return 배정 목록
     */
    public List<ParticipantRandomAssignmentDTO> selectAll(ParticipantRandomAssignmentDTO praDTO) {
        String condition = praDTO.getCondition();
        log.info("ParticipantRandomAssignmentDAO List<ParticipantRandomAssignmentDTO> SQL praDTO condition : [{}]",condition);
        List<ParticipantRandomAssignmentDTO> datas = sqlSession.selectList(ns+condition, praDTO);
        log.info("ParticipantRandomAssignmentDAO List<ParticipantRandomAssignmentDTO> SQL data : [{}]",datas);
        return datas;
    }
}
