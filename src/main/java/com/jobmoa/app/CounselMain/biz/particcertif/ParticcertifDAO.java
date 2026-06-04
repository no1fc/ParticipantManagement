package com.jobmoa.app.CounselMain.biz.particcertif;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 참여자 자격증 데이터 접근 객체.
 * <p>참여자의 자격증 정보에 대한 등록, 수정, 삭제, 조회를 담당한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "ParticcertifDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ParticcertifDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ParticcertifDAO.";

    /**
     * 참여자 자격증 정보를 등록한다.
     *
     * @param particcertifDTO 등록할 자격증 정보
     * @return 등록 성공 여부
     */
    public boolean insert(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO insert : [{}]",particcertifDTO);
        boolean flag = sqlSession.insert(ns+"particcertifInsert",particcertifDTO) > 0;
        log.info("particcertifDTO insert : [{}]",flag);
        return flag;
    }
    /**
     * 참여자 자격증 정보를 수정한다.
     *
     * @param particcertifDTO 수정할 자격증 정보
     * @return 수정 성공 여부
     */
    public boolean update(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO update : [{}]",particcertifDTO);
        boolean flag = sqlSession.update(ns+"particcertifUpdate",particcertifDTO) > 0;
        log.info("particcertifDTO update : [{}]",flag);
        return flag;
    }
    /**
     * 참여자 자격증 정보를 삭제한다.
     *
     * @param particcertifDTO 삭제할 자격증 정보
     * @return 삭제 성공 여부
     */
    public boolean delete(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO delete : [{}]",particcertifDTO);
        boolean flag = sqlSession.delete(ns+"particcertifDelete",particcertifDTO) > 0;
        log.info("particcertifDTO delete : [{}]",flag);
        return flag;
    }
    /**
     * 조건에 맞는 참여자 자격증 단건을 조회한다.
     *
     * @param particcertifDTO particcertifCondition(조회 조건)이 설정된 DTO
     * @return 자격증 단건 데이터, 없으면 null
     */
    public ParticcertifDTO selectOne(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO selectOne : [{}]",particcertifDTO);
        return sqlSession.selectOne(ns+particcertifDTO.getParticcertifCondition(),particcertifDTO);
    }
    /**
     * 조건에 맞는 참여자 자격증 목록을 조회한다.
     *
     * @param particcertifDTO particcertifCondition(조회 조건)이 설정된 DTO
     * @return 자격증 목록
     */
    public List<ParticcertifDTO> selectAll(ParticcertifDTO particcertifDTO) {
//        log.info("particcertifDTO selectAll : [{}]",particcertifDTO);
        return sqlSession.selectList(ns+particcertifDTO.getParticcertifCondition(),particcertifDTO);
    }
}
