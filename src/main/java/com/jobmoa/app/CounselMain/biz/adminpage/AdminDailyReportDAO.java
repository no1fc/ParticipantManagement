package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 - 일일업무보고 데이터 접근 객체.
 * <p>MyBatis 매퍼 네임스페이스 "AdminDailyReportDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminDailyReportDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminDailyReportDAO.";

    /**
     * 일일업무보고 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 일일업무보고 목록
     */
    public List<AdminDTO> selectDailyReportList(AdminDTO dto) {
        log.info("AdminDailyReportDAO selectDailyReportList");
        return sqlSession.selectList(ns + "selectDailyReportList", dto);
    }
}
