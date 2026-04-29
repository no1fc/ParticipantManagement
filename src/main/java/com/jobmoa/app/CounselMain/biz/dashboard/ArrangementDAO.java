package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ArrangementDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ArrangementDAO.";

    public List<ArrangementDTO> selectAll(ArrangementDTO arrangementDTO) {
//        log.info("ArrangementDTO arrangementDTO selectAll : [{}]", arrangementDTO);
        log.info("ArrangementDTO ArrangementDTO selectAll condition : [{}]", arrangementDTO.getArrangementCondition());
        List<ArrangementDTO> datas = sqlSession.selectList(ns+ arrangementDTO.getArrangementCondition(), arrangementDTO);
//        log.info("datas : [{}]",datas);
        return datas;
    }

    public ArrangementDTO selectOne(ArrangementDTO arrangementDTO) {
        //log.info("ArrangementDAO ArrangementDAO selectOne : [{}]", arrangementDTO);
        log.info("ArrangementDAO ArrangementDAO selectOne condition : [{}]", arrangementDTO.getArrangementCondition());
        ArrangementDTO data = sqlSession.selectOne(ns+ arrangementDTO.getArrangementCondition(), arrangementDTO);

        //log.info("ArrangementDAO ArrangementDAO data : [{}]",data);
        return data;
    }

    public boolean insert(ArrangementDTO arrangementDTO) {
        return false;
    }

    public boolean update(ArrangementDTO arrangementDTO) {
        return false;
    }

    public boolean delete(ArrangementDTO arrangementDTO) {
        return false;
    }
}
