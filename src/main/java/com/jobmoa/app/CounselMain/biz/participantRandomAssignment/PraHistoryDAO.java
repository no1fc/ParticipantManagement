package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * 참여자 랜덤 배정 히스토리 데이터 접근 객체.
 * <p>일일업무보고 UPSERT, 산정식 설정 히스토리, CSV 업로드 히스토리 관련 데이터를 담당한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "PraDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class PraHistoryDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "PraDAO.";

    /**
     * 일일업무보고를 UPSERT(존재하면 수정, 없으면 등록)한다.
     *
     * @param dto 일일업무보고 정보
     * @return 처리 성공 여부
     */
    public boolean upsertDailyReport(PraDailyReportDTO dto) {
        log.info("PraHistoryDAO upsertDailyReport Start");
        boolean flag = sqlSession.insert(ns + "praDailyReportUpsert", dto) > 0;
        log.info("PraHistoryDAO upsertDailyReport Flag : [{}]", flag);
        return flag;
    }

    /**
     * 일일업무보고 단건을 조회한다.
     *
     * @param dto 조회 조건이 담긴 DTO
     * @return 일일업무보고 데이터, 없으면 null
     */
    public PraDailyReportDTO getPraDailyReport(PraDailyReportDTO dto) {
        log.info("PraHistoryDAO getPraDailyReport Start");
        PraDailyReportDTO result = sqlSession.selectOne(ns + "praDailyReportSelect", dto);
        log.info("PraHistoryDAO getPraDailyReport Result : [{}]", result);
        return result;
    }

    /**
     * 배정 산정식 설정 히스토리를 등록한다.
     *
     * @param dto 산정식 설정 히스토리 정보
     * @return 등록 성공 여부
     */
    public boolean insertScoringConfig(PraScoringConfigHistoryDTO dto) {
        log.info("PraHistoryDAO insertScoringConfig Start");
        boolean flag = sqlSession.insert(ns + "praScoringConfigHistoryInsert", dto) > 0;
        log.info("PraHistoryDAO insertScoringConfig Flag : [{}]", flag);
        return flag;
    }

    /**
     * CSV 업로드 히스토리를 등록한다.
     *
     * @param dto CSV 히스토리 정보
     * @return 등록 성공 여부
     */
    public boolean insertCsvHistory(PraCsvHistoryDTO dto) {
        log.info("PraHistoryDAO insertCsvHistory Start");
        boolean flag = sqlSession.insert(ns + "praCsvHistoryInsert", dto) > 0;
        log.info("PraHistoryDAO insertCsvHistory Flag : [{}]", flag);
        return flag;
    }
}
