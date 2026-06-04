package com.jobmoa.app.CounselMain.biz.participantCounsel;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 상담 데이터 접근 객체.
 * <p>상담 일지 조회, 등록, 수정, 삭제를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "CounselDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class CounselDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "CounselDAO.";

    /**
     * 조건에 맞는 상담 일지 단건을 조회한다.
     *
     * @param counselDTO counselCondition(조회 조건)이 설정된 DTO
     * @return 상담 일지 단건 데이터, 없으면 null
     */
    public CounselDTO selectOne(CounselDTO counselDTO) {
        String condition = counselDTO.getCounselCondition();
        log.info("counsel selectOne SQL counselDTO : [{}]",condition);
        CounselDTO data = sqlSession.selectOne(ns+condition, counselDTO);
        log.info("data : [{}]",data);
        return data;
    }
    /**
     * 상담 일지 목록을 조회한다. (미구현)
     *
     * @param counselDTO 조회 조건이 담긴 DTO
     * @return 항상 null (미구현)
     */
    public List<CounselDTO> selectAll(CounselDTO counselDTO) {
        return null;
    }
    /**
     * 상담 일지를 등록한다.
     *
     * @param counselDTO counselCondition(조건) 및 등록할 상담 정보가 설정된 DTO
     * @return 등록 성공 여부
     */
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
    /**
     * 상담 일지를 수정한다.
     *
     * @param counselDTO counselCondition(조건) 및 수정할 상담 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
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
    /**
     * 상담 일지를 삭제한다.
     *
     * @param counselDTO counselCondition(조건)이 설정된 삭제 대상 DTO
     * @return 삭제 성공 여부
     */
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
