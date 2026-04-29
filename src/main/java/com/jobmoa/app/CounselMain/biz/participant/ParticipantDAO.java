package com.jobmoa.app.CounselMain.biz.participant;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ParticipantDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticipantDAO.";

    public List<ParticipantDTO> selectAll(ParticipantDTO participantDTO) {
//        log.info("ParticipantDTO ParticipantDAO selectAll : [{}]", participantDTO);
        log.info("ParticipantDTO ParticipantDAO selectAll condition : [{}]",participantDTO.getParticipantCondition());
        List<ParticipantDTO> datas = sqlSession.selectList(ns+participantDTO.getParticipantCondition(), participantDTO);
        //log.info("datas : [{}]",datas.toString());
        return datas;
    }

    public ParticipantDTO selectOne(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantDAO selectOne : [{}]",participantDTO);
        log.info("ParticipantDTO ParticipantDAO selectOne condition : [{}]",participantDTO.getParticipantCondition());
        ParticipantDTO data = sqlSession.selectOne(ns+participantDTO.getParticipantCondition(), participantDTO);

        //log.info("ParticipantDTO ParticipantDAO data : [{}]",data);
        return data;
    }

    public boolean insert(ParticipantDTO participantDTO) {
        boolean flag = sqlSession.insert(ns+participantDTO.getParticipantCondition(), participantDTO) > 0;
        log.info("ParticipantDTO ParticipantDAO insert result : [{}]",flag);
        return flag;
    }

    public boolean update(ParticipantDTO participantDTO) {
        boolean flag = 0 < sqlSession.update(ns+participantDTO.getParticipantCondition(), participantDTO);
        log.info("ParticipantDTO ParticipantDAO update result : [{}]",flag);
        return flag;
    }

    public boolean delete(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantDAO delete : [{}]",participantDTO);
        //쿼리가 실행되고 0이거나 작으면 false 0보다 크면 true 를 반환
        boolean flag = 0 < sqlSession.delete(ns+participantDTO.getParticipantCondition(), participantDTO);
        log.info("ParticipantDTO ParticipantDAO delete result : [{}]",flag);
        return flag;
    }


}
