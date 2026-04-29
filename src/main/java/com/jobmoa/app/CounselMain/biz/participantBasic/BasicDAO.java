package com.jobmoa.app.CounselMain.biz.participantBasic;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class BasicDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "BasicDAO.";

    public boolean insert(BasicDTO basicDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
//        log.info("basic insert SQL basicDTO : [{}]",basicDTO);
        log.info("basic insert Start");
        boolean flag =  sqlSession.insert(ns+"basicInsert", basicDTO) > 0;
        log.info("basic insert SQL flag : [{}]",flag);
        log.info("basic insert End");
        return flag;
    }

    public boolean update(BasicDTO basicDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
//        log.info("basic update SQL basicDTO : [{}]",basicDTO);
        log.info("basic update Start");
        boolean flag = sqlSession.update(ns+basicDTO.getBasicCondition(), basicDTO) > 0;
        log.info("basic update SQL flag : [{}]",flag);
        log.info("basic update End");
        return flag;
    }
    public boolean delete(BasicDTO basicDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
//        log.info("basic delete SQL basicDTO : [{}]",basicDTO);
        log.info("basic delete Start");
        boolean flag = sqlSession.delete(ns+"basicDelete", basicDTO) > 0;
        log.info("basic delete SQL flag : [{}]",flag);
        log.info("basic delete End");
        return flag;
    }
    public BasicDTO selectOne(BasicDTO basicDTO) {
        String condition = basicDTO.getBasicCondition();
        log.info("basic selectOne SQL basicDTO condition : [{}]",condition);
        BasicDTO data = sqlSession.selectOne(ns+condition, basicDTO);
        log.info("basic selectOne SQL data : [{}]",data);
        return data;
    }
    public List<BasicDTO> selectAll(BasicDTO basicDTO) {

        return null;
    }
}
