package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 배정 현황 데이터 접근 객체.
 * <p>참여자 배정 관련 통계 및 상세 데이터 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "ArrangementDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ArrangementDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ArrangementDAO.";

    /**
     * 조건에 맞는 배정 현황 목록을 조회한다.
     *
     * @param arrangementDTO arrangementCondition(조회 조건)이 설정된 DTO
     * @return 배정 현황 목록
     */
    public List<ArrangementDTO> selectAll(ArrangementDTO arrangementDTO) {
//        log.info("ArrangementDTO arrangementDTO selectAll : [{}]", arrangementDTO);
        log.info("ArrangementDTO ArrangementDTO selectAll condition : [{}]", arrangementDTO.getArrangementCondition());
        List<ArrangementDTO> datas = sqlSession.selectList(ns+ arrangementDTO.getArrangementCondition(), arrangementDTO);
//        log.info("datas : [{}]",datas);
        return datas;
    }

    /**
     * 조건에 맞는 배정 현황 단건을 조회한다.
     *
     * @param arrangementDTO arrangementCondition(조회 조건)이 설정된 DTO
     * @return 배정 현황 단건 데이터, 없으면 null
     */
    public ArrangementDTO selectOne(ArrangementDTO arrangementDTO) {
        //log.info("ArrangementDAO ArrangementDAO selectOne : [{}]", arrangementDTO);
        log.info("ArrangementDAO ArrangementDAO selectOne condition : [{}]", arrangementDTO.getArrangementCondition());
        ArrangementDTO data = sqlSession.selectOne(ns+ arrangementDTO.getArrangementCondition(), arrangementDTO);

        //log.info("ArrangementDAO ArrangementDAO data : [{}]",data);
        return data;
    }

    /**
     * 배정 데이터를 등록한다. (미구현)
     *
     * @param arrangementDTO 등록할 배정 정보
     * @return 항상 false (미구현)
     */
    public boolean insert(ArrangementDTO arrangementDTO) {
        return false;
    }

    /**
     * 배정 데이터를 수정한다. (미구현)
     *
     * @param arrangementDTO 수정할 배정 정보
     * @return 항상 false (미구현)
     */
    public boolean update(ArrangementDTO arrangementDTO) {
        return false;
    }

    /**
     * 배정 데이터를 삭제한다. (미구현)
     *
     * @param arrangementDTO 삭제할 배정 정보
     * @return 항상 false (미구현)
     */
    public boolean delete(ArrangementDTO arrangementDTO) {
        return false;
    }
}
