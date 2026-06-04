package com.jobmoa.app.CounselMain.biz.participantEmployment;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 취업 데이터 접근 객체.
 * <p>참여자의 취업 이력 및 취업 현황 데이터에 대한 등록, 수정, 삭제, 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "EmploymentDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class EmploymentDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "EmploymentDAO.";

    /**
     * 참여자 취업 정보를 등록한다.
     *
     * @param employmentDTO 등록할 취업 정보
     * @return 등록 성공 여부
     */
    public boolean insert(EmploymentDTO employmentDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        boolean flag = sqlSession.insert(ns+"employmentInsert", employmentDTO) > 0;
        log.info("employment insert SQL flag : [{}]",flag);
        return flag;
    }
    /**
     * 참여자 취업 정보를 수정한다.
     *
     * @param employmentDTO 수정할 취업 정보
     * @return 수정 성공 여부
     */
    public boolean update(EmploymentDTO employmentDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        boolean flag = sqlSession.update(ns+"employmentUpdate", employmentDTO) > 0;
        log.info("employment update SQL flag : [{}]",flag);
        return flag;
    }
    /**
     * 참여자 취업 정보를 삭제한다.
     *
     * @param employmentDTO 삭제할 취업 정보
     * @return 삭제 성공 여부
     */
    public boolean delete(EmploymentDTO employmentDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        boolean flag = sqlSession.delete(ns+"employmentDelete", employmentDTO) > 0;
        log.info("employment delete SQL flag : [{}]",flag);
        return flag;
    }
    /**
     * 조건에 맞는 참여자 취업 정보 단건을 조회한다.
     *
     * @param employmentDTO employmentCondition(조회 조건)이 설정된 DTO
     * @return 취업 정보 단건 데이터, 없으면 null
     */
    public EmploymentDTO selectOne(EmploymentDTO employmentDTO) {
        String condition = employmentDTO.getEmploymentCondition();
        log.info("employment selectOne SQL employmentDTO : [{}]",condition);
        EmploymentDTO data = sqlSession.selectOne(ns+condition, employmentDTO);
//        log.info("employment selectOne data : [{}]",data);
        return data;
    }
    /**
     * 참여자 취업 정보 목록을 조회한다. (미구현)
     *
     * @param employmentDTO 조회 조건이 담긴 DTO
     * @return 항상 null (미구현)
     */
    public List<EmploymentDTO> selectAll(EmploymentDTO employmentDTO) {
        return null;
    }

}
