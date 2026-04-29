package com.jobmoa.app.CounselMain.biz.particcertif;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ParticcertifDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticcertifDAO.";

    public boolean insert(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO insert : [{}]",particcertifDTO);
        boolean flag = sqlSession.insert(ns+"particcertifInsert",particcertifDTO) > 0;
        log.info("particcertifDTO insert : [{}]",flag);
        return flag;
    }
    public boolean update(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO update : [{}]",particcertifDTO);
        boolean flag = sqlSession.update(ns+"particcertifUpdate",particcertifDTO) > 0;
        log.info("particcertifDTO update : [{}]",flag);
        return flag;
    }
    public boolean delete(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO delete : [{}]",particcertifDTO);
        boolean flag = sqlSession.delete(ns+"particcertifDelete",particcertifDTO) > 0;
        log.info("particcertifDTO delete : [{}]",flag);
        return flag;
    }
    public ParticcertifDTO selectOne(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO selectOne : [{}]",particcertifDTO);
        return sqlSession.selectOne(ns+particcertifDTO.getParticcertifCondition(),particcertifDTO);
    }
    public List<ParticcertifDTO> selectAll(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO selectAll : [{}]",particcertifDTO);
        return sqlSession.selectList(ns+particcertifDTO.getParticcertifCondition(),particcertifDTO);
    }
}
