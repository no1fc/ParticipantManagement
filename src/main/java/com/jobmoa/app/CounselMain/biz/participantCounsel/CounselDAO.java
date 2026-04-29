package com.jobmoa.app.CounselMain.biz.participantCounsel;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class CounselDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "CounselDAO.";

    public CounselDTO selectOne(CounselDTO counselDTO) {
        String condition = counselDTO.getCounselCondition();
        log.info("counsel selectOne SQL counselDTO : [{}]",condition);
        CounselDTO data = sqlSession.selectOne(ns+condition, counselDTO);
        log.info("data : [{}]",data);
        return data;
    }
    public List<CounselDTO> selectAll(CounselDTO counselDTO) {
        return null;
    }
    public boolean insert(CounselDTO counselDTO) {
        log.info("counsel insert SQL counselDTO : [{}]",counselDTO);
        String condition = counselDTO.getCounselCondition();
        log.info("counsel insert SQL counselDTO condition : [{}]",condition);
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        boolean flag = sqlSession.insert(ns+condition, counselDTO) > 0;
        log.info("counsel insert SQL flag : [{}]",flag);
        return flag;
    }
    public boolean update(CounselDTO counselDTO) {
        log.info("counsel update SQL counselDTO : [{}]",counselDTO);
        String condition = counselDTO.getCounselCondition();
        log.info("counsel update SQL counselDTO condition : [{}]",condition);
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        boolean flag = sqlSession.update(ns+condition, counselDTO) > 0;
        log.info("counsel update SQL flag : [{}]",flag);
        return flag;
    }
    public boolean delete(CounselDTO counselDTO) {
        log.info("counsel delete SQL counselDTO : [{}]",counselDTO);
        String condition = counselDTO.getCounselCondition();
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        boolean flag = sqlSession.delete(ns+condition, counselDTO) > 0;
        log.info("counsel delete SQL flag : [{}]",flag);
        return flag;
    }
}
