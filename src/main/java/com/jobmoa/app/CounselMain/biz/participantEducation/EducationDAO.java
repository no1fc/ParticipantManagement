package com.jobmoa.app.CounselMain.biz.participantEducation;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 교육 데이터 접근 객체.
 * <p>참여자의 교육 이력(학력, 훈련 등) 데이터에 대한 등록, 수정, 삭제, 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "EducationDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class EducationDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "EducationDAO.";

    /**
     * 참여자 교육 정보를 등록한다.
     *
     * @param educationDTO 등록할 교육 정보
     * @return 등록 성공 여부
     */
    public boolean insert(EducationDTO educationDTO) {
        log.info("EducationDAO insert Start");
        boolean flag = sqlSession.insert(ns+"educationInsert", educationDTO) > 0;
        log.info("EducationDTO insert : [{}]",flag);
        log.info("EducationDAO insert End");
        return flag;
    }
    /**
     * 참여자 교육 정보를 수정한다.
     *
     * @param educationDTO educationCondition(조건) 및 수정할 교육 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean update(EducationDTO educationDTO) {
        log.info("EducationDAO update Start");
        boolean flag = sqlSession.update(ns+educationDTO.getEducationCondition(), educationDTO) > 0;
        log.info("EducationDTO update : [{}]",flag);
        log.info("EducationDAO update End");
        return flag;
    }
    /**
     * 참여자 교육 정보를 삭제한다.
     *
     * @param educationDTO 삭제할 교육 정보
     * @return 삭제 성공 여부
     */
    public boolean delete(EducationDTO educationDTO) {
        log.info("EducationDAO delete Start");
        boolean flag = sqlSession.delete(ns+"educationDelete", educationDTO) > 0;
        log.info("EducationDTO delete : [{}]",flag);
        log.info("EducationDAO delete End");
        return flag;
    }
    /**
     * 조건에 맞는 참여자 교육 정보 단건을 조회한다.
     *
     * @param educationDTO educationCondition(조회 조건)이 설정된 DTO
     * @return 교육 정보 단건 데이터, 없으면 null
     */
    public EducationDTO selectOne(EducationDTO educationDTO) {
        log.info("EducationDAO selectOne Start");
        log.info("EducationDTO selectOne Condition : [{}]", educationDTO.getEducationCondition());
        return sqlSession.selectOne(ns+ educationDTO.getEducationCondition(), educationDTO);
    }
    /**
     * 조건에 맞는 참여자 교육 정보 목록을 조회한다.
     *
     * @param educationDTO educationCondition(조회 조건)이 설정된 DTO
     * @return 교육 정보 목록
     */
    public List<EducationDTO> selectAll(EducationDTO educationDTO) {
        log.info("EducationDAO selectAll Start");
        log.info("EducationDTO selectAll Condition : [{}]", educationDTO.getEducationCondition());
        return sqlSession.selectList(ns+ educationDTO.getEducationCondition(), educationDTO);
    }
}
