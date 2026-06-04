package com.jobmoa.app.CounselMain.biz.participantBasic;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 기본정보 데이터 접근 객체.
 * <p>참여자의 기본 인적사항(이름, 연락처, 주소 등) 데이터에 대한 등록, 수정, 삭제, 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "BasicDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class BasicDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "BasicDAO.";

    /**
     * 참여자 기본정보를 등록한다.
     *
     * @param basicDTO 등록할 기본정보
     * @return 등록 성공 여부
     */
    public boolean insert(BasicDTO basicDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        log.info("basic insert Start");
        boolean flag =  sqlSession.insert(ns+"basicInsert", basicDTO) > 0;
        log.info("basic insert SQL flag : [{}]",flag);
        log.info("basic insert End");
        return flag;
    }

    /**
     * 참여자 기본정보를 수정한다.
     *
     * @param basicDTO basicCondition(조건) 및 수정할 기본정보가 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean update(BasicDTO basicDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        log.info("basic update Start");
        boolean flag = sqlSession.update(ns+basicDTO.getBasicCondition(), basicDTO) > 0;
        log.info("basic update SQL flag : [{}]",flag);
        log.info("basic update End");
        return flag;
    }
    /**
     * 참여자 기본정보를 삭제한다.
     *
     * @param basicDTO 삭제할 기본정보
     * @return 삭제 성공 여부
     */
    public boolean delete(BasicDTO basicDTO) {
        // 0보다 크면 True (성공)
        // 0보다 작거나 같으면 False (실패)
        log.info("basic delete Start");
        boolean flag = sqlSession.delete(ns+"basicDelete", basicDTO) > 0;
        log.info("basic delete SQL flag : [{}]",flag);
        log.info("basic delete End");
        return flag;
    }
    /**
     * 조건에 맞는 참여자 기본정보 단건을 조회한다.
     *
     * @param basicDTO basicCondition(조회 조건)이 설정된 DTO
     * @return 기본정보 단건 데이터, 없으면 null
     */
    public BasicDTO selectOne(BasicDTO basicDTO) {
        String condition = basicDTO.getBasicCondition();
        log.info("basic selectOne SQL basicDTO condition : [{}]",condition);
        BasicDTO data = sqlSession.selectOne(ns+condition, basicDTO);
        log.info("basic selectOne SQL data : [{}]",data);
        return data;
    }
    /**
     * 참여자 기본정보 목록을 조회한다. (미구현)
     *
     * @param basicDTO 조회 조건이 담긴 DTO
     * @return 항상 null (미구현)
     */
    public List<BasicDTO> selectAll(BasicDTO basicDTO) {

        return null;
    }
}
