package com.jobmoa.app.jobPlacement.biz.jobPlacement;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class JobPlacementDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "JobPlacementDAO.";

    public boolean insert(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO insert : [{}]",condition);
//        log.error("JobPlacementDTO insert 사용 금지 : [{}]",false);
        return sqlSession.insert(ns+condition,jobPlacementDTO) > 0;
    }

    public boolean update(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO update : [{}]",condition);
//        log.error("JobPlacementDTO update 사용 금지 : [{}]",false);
        return sqlSession.update(ns+condition,jobPlacementDTO) > 0;
    }

    public boolean delete(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO delete : [{}]",condition);
//        log.error("JobPlacementDTO delete 사용 금지 : [{}]",false);
        return sqlSession.delete(ns+condition,jobPlacementDTO) > 0;
    }

    public JobPlacementDTO selectOne(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO selectOne : [{}]",condition);
        JobPlacementDTO data = sqlSession.selectOne(ns+condition,jobPlacementDTO);
//        log.info("JobPlacementDTO selectOne jobPlacementDTO : [{}]",data);
        return data;
    }


    public List<JobPlacementDTO> selectAll(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO selectAll : [{}]",condition);

        List<JobPlacementDTO> datas = sqlSession.selectList(ns+condition,jobPlacementDTO);
//        log.info("JobPlacementDTO selectAll jobPlacementDTO : [{}]",datas);

        return datas;
    }

    public List<JobPlacementDTO> selectDesiredJobList(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO selectDesiredJobList : [{}]", condition);
        return sqlSession.selectList(ns + condition, jobPlacementDTO);
    }
}
