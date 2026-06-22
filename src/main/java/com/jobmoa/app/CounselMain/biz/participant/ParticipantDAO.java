package com.jobmoa.app.CounselMain.biz.participant;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 데이터 접근 객체.
 * <p>참여자 기본 목록 조회, 단건 조회, 등록, 수정, 삭제를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "ParticipantDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ParticipantDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticipantDAO.";

    /**
     * 조건에 맞는 참여자 목록을 조회한다.
     *
     * @param participantDTO participantCondition(조회 조건)이 설정된 DTO
     * @return 참여자 목록
     */
    public List<ParticipantDTO> selectAll(ParticipantDTO participantDTO) {
        log.info("ParticipantDTO ParticipantDAO selectAll condition : [{}]",participantDTO.getParticipantCondition());
        List<ParticipantDTO> datas = sqlSession.selectList(ns+participantDTO.getParticipantCondition(), participantDTO);
        return datas;
    }

    /**
     * 조건에 맞는 참여자 목록을 스트리밍 방식으로 조회한다.
     * 전체 결과를 List로 적재하지 않고, ResultHandler로 한 행씩 처리한다(엑셀 대량 다운로드용).
     *
     * @param participantDTO participantCondition(조회 조건)이 설정된 DTO
     * @param handler        행 1건씩 전달받는 ResultHandler
     */
    public void selectAllStream(ParticipantDTO participantDTO, ResultHandler<ParticipantDTO> handler) {
        log.info("ParticipantDTO ParticipantDAO selectAllStream condition : [{}]",participantDTO.getParticipantCondition());
        sqlSession.select(ns+participantDTO.getParticipantCondition(), participantDTO, handler);
    }

    /**
     * 조건에 맞는 참여자 단건 정보를 조회한다.
     *
     * @param participantDTO participantCondition(조회 조건)이 설정된 DTO
     * @return 참여자 단건 데이터, 없으면 null
     */
    public ParticipantDTO selectOne(ParticipantDTO participantDTO) {
        log.info("ParticipantDTO ParticipantDAO selectOne condition : [{}]",participantDTO.getParticipantCondition());
        ParticipantDTO data = sqlSession.selectOne(ns+participantDTO.getParticipantCondition(), participantDTO);

        return data;
    }

    /**
     * 참여자를 등록한다.
     *
     * @param participantDTO participantCondition(조건) 및 등록할 참여자 정보가 설정된 DTO
     * @return 등록 성공 여부
     */
    public boolean insert(ParticipantDTO participantDTO) {
        boolean flag = sqlSession.insert(ns+participantDTO.getParticipantCondition(), participantDTO) > 0;
        log.info("ParticipantDTO ParticipantDAO insert result : [{}]",flag);
        return flag;
    }

    /**
     * 참여자 정보를 수정한다.
     *
     * @param participantDTO participantCondition(조건) 및 수정할 참여자 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean update(ParticipantDTO participantDTO) {
        boolean flag = 0 < sqlSession.update(ns+participantDTO.getParticipantCondition(), participantDTO);
        log.info("ParticipantDTO ParticipantDAO update result : [{}]",flag);
        return flag;
    }

    /**
     * 참여자를 삭제한다.
     *
     * @param participantDTO participantCondition(조건)이 설정된 삭제 대상 DTO
     * @return 삭제 성공 여부
     */
    public boolean delete(ParticipantDTO participantDTO) {
        //쿼리가 실행되고 0이거나 작으면 false 0보다 크면 true 를 반환
        boolean flag = 0 < sqlSession.delete(ns+participantDTO.getParticipantCondition(), participantDTO);
        log.info("ParticipantDTO ParticipantDAO delete result : [{}]",flag);
        return flag;
    }


}
