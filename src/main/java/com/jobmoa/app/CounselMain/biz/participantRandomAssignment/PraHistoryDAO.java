package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Slf4j
@Repository
public class PraHistoryDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "PraDAO.";

    public boolean upsertDailyReport(PraDailyReportDTO dto) {
        log.info("PraHistoryDAO upsertDailyReport Start");
        boolean flag = sqlSession.insert(ns + "praDailyReportUpsert", dto) > 0;
        log.info("PraHistoryDAO upsertDailyReport Flag : [{}]", flag);
        return flag;
    }

    public PraDailyReportDTO getPraDailyReport(PraDailyReportDTO dto) {
        log.info("PraHistoryDAO getPraDailyReport Start");
        PraDailyReportDTO result = sqlSession.selectOne(ns + "praDailyReportSelect", dto);
        log.info("PraHistoryDAO getPraDailyReport Result : [{}]", result);
        return result;
    }

    public boolean insertScoringConfig(PraScoringConfigHistoryDTO dto) {
        log.info("PraHistoryDAO insertScoringConfig Start");
        boolean flag = sqlSession.insert(ns + "praScoringConfigHistoryInsert", dto) > 0;
        log.info("PraHistoryDAO insertScoringConfig Flag : [{}]", flag);
        return flag;
    }

    public boolean insertCsvHistory(PraCsvHistoryDTO dto) {
        log.info("PraHistoryDAO insertCsvHistory Start");
        boolean flag = sqlSession.insert(ns + "praCsvHistoryInsert", dto) > 0;
        log.info("PraHistoryDAO insertCsvHistory Flag : [{}]", flag);
        return flag;
    }
}
