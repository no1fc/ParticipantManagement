package com.jobmoa.app.CounselMain.biz.participantEducation;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class EducationDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "EducationDAO.";

    public boolean insert(EducationDTO educationDTO) {
//        log.info("EducationDTO insert : [{}]", educationDTO);
        log.info("EducationDAO insert Start");
        boolean flag = sqlSession.insert(ns+"educationInsert", educationDTO) > 0;
        log.info("EducationDTO insert : [{}]",flag);
        log.info("EducationDAO insert End");
        return flag;
    }
    public boolean update(EducationDTO educationDTO) {
//        log.info("EducationDTO update : [{}]", educationDTO);
        log.info("EducationDAO update Start");
        boolean flag = sqlSession.update(ns+educationDTO.getEducationCondition(), educationDTO) > 0;
        log.info("EducationDTO update : [{}]",flag);
        log.info("EducationDAO update End");
        return flag;
    }
    public boolean delete(EducationDTO educationDTO) {
//        log.info("EducationDTO delete : [{}]", educationDTO);
        log.info("EducationDAO delete Start");
        boolean flag = sqlSession.delete(ns+"educationDelete", educationDTO) > 0;
        log.info("EducationDTO delete : [{}]",flag);
        log.info("EducationDAO delete End");
        return flag;
    }
    public EducationDTO selectOne(EducationDTO educationDTO) {
//        log.info("EducationDTO selectOne : [{}]", educationDTO);
        log.info("EducationDAO selectOne Start");
        log.info("EducationDTO selectOne Condition : [{}]", educationDTO.getEducationCondition());
        return sqlSession.selectOne(ns+ educationDTO.getEducationCondition(), educationDTO);
    }
    public List<EducationDTO> selectAll(EducationDTO educationDTO) {
//        log.info("EducationDTO selectAll : [{}]", educationDTO);
        log.info("EducationDAO selectAll Start");
        log.info("EducationDTO selectAll Condition : [{}]", educationDTO.getEducationCondition());
        return sqlSession.selectList(ns+ educationDTO.getEducationCondition(), educationDTO);
    }
}
