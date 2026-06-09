package com.jobmoa.app.CounselMain.biz.report;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 보고서 데이터 접근 객체.
 * <p>참여자 보고서 관련 데이터의 조회 및 수정을 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "ReportDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class ReportDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ReportDAO.";

    /**
     * 조건에 맞는 보고서 단건을 조회한다.
     *
     * @param reportDTO reportCondition(조회 조건)이 설정된 DTO
     * @return 보고서 단건 데이터, 없으면 null
     */
    public ReportDTO selectOne(ReportDTO reportDTO) {
        String condition = reportDTO.getReportCondition();
        log.info("Report selectOne SQL reportDTO : [{}]",condition);
        ReportDTO data = sqlSession.selectOne(ns+condition, reportDTO);
        return data;
    }
    /**
     * 조건에 맞는 보고서 목록을 조회한다.
     *
     * @param reportDTO reportCondition(조회 조건)이 설정된 DTO
     * @return 보고서 목록
     */
    public List<ReportDTO> selectAll(ReportDTO reportDTO) {
        String condition = reportDTO.getReportCondition();
        log.info("Report selectAll SQL reportDTO : [{}]",condition);
        List<ReportDTO> datas = sqlSession.selectList(ns+condition, reportDTO);
        return datas;
    }
    /**
     * 보고서를 등록한다. (미구현)
     *
     * @param reportDTO 등록할 보고서 정보
     * @return 항상 false (미구현)
     */
    public boolean insert(ReportDTO reportDTO) {
        return false;
    }
    /**
     * 보고서 정보를 수정한다.
     *
     * @param reportDTO reportCondition(조건) 및 수정할 보고서 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean update(ReportDTO reportDTO) {
        String condition = reportDTO.getReportCondition();
        log.info("Report update SQL reportDTO : [{}]",condition);
        int result = sqlSession.update(ns+condition, reportDTO);
        return result > 0;
    }
    /**
     * 보고서를 삭제한다. (미구현)
     *
     * @param reportDTO 삭제할 보고서 정보
     * @return 항상 false (미구현)
     */
    public boolean delete(ReportDTO reportDTO) {
        return false;
    }
}
