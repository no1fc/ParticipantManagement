package com.jobmoa.app.jobPlacement.biz.jobPlacement;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 알선(취업알선) 데이터 접근 객체.
 * <p>취업알선 정보의 등록, 수정, 삭제, 단건/목록 조회, 희망직무 목록 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "JobPlacementDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class JobPlacementDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "JobPlacementDAO.";

    /**
     * 알선 정보를 등록한다.
     *
     * @param jobPlacementDTO condition(조건) 및 등록할 알선 정보가 설정된 DTO
     * @return 등록 성공 여부
     */
    public boolean insert(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO insert : [{}]",condition);
//        log.error("JobPlacementDTO insert 사용 금지 : [{}]",false);
        return sqlSession.insert(ns+condition,jobPlacementDTO) > 0;
    }

    /**
     * 알선 정보를 수정한다.
     *
     * @param jobPlacementDTO condition(조건) 및 수정할 알선 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean update(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO update : [{}]",condition);
//        log.error("JobPlacementDTO update 사용 금지 : [{}]",false);
        return sqlSession.update(ns+condition,jobPlacementDTO) > 0;
    }

    /**
     * 알선 정보를 삭제한다.
     *
     * @param jobPlacementDTO condition(조건)이 설정된 삭제 대상 DTO
     * @return 삭제 성공 여부
     */
    public boolean delete(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO delete : [{}]",condition);
//        log.error("JobPlacementDTO delete 사용 금지 : [{}]",false);
        return sqlSession.delete(ns+condition,jobPlacementDTO) > 0;
    }

    /**
     * 조건에 맞는 알선 정보 단건을 조회한다.
     *
     * @param jobPlacementDTO condition(조회 조건)이 설정된 DTO
     * @return 알선 정보 단건 데이터, 없으면 null
     */
    public JobPlacementDTO selectOne(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO selectOne : [{}]",condition);
        JobPlacementDTO data = sqlSession.selectOne(ns+condition,jobPlacementDTO);
//        log.info("JobPlacementDTO selectOne jobPlacementDTO : [{}]",data);
        return data;
    }


    /**
     * 조건에 맞는 알선 정보 목록을 조회한다.
     *
     * @param jobPlacementDTO condition(조회 조건)이 설정된 DTO
     * @return 알선 정보 목록
     */
    public List<JobPlacementDTO> selectAll(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO selectAll : [{}]",condition);

        List<JobPlacementDTO> datas = sqlSession.selectList(ns+condition,jobPlacementDTO);
//        log.info("JobPlacementDTO selectAll jobPlacementDTO : [{}]",datas);

        return datas;
    }

    /**
     * 희망직무 목록을 조회한다.
     *
     * @param jobPlacementDTO condition(조회 조건)이 설정된 DTO
     * @return 희망직무 목록
     */
    public List<JobPlacementDTO> selectDesiredJobList(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO selectDesiredJobList : [{}]", condition);
        return sqlSession.selectList(ns + condition, jobPlacementDTO);
    }
}
