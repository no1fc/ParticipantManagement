package com.jobmoa.app.CounselMain.biz.schedule;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ScheduleDAO {

    @Autowired
    @Qualifier("mybatis")
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ScheduleDAO.";

    public List<ScheduleDTO> selectScheduleList(ScheduleDTO dto) {
        log.info("ScheduleDAO selectScheduleList counselorId={}", dto.getCounselorId());
        return sqlSession.selectList(ns + "selectScheduleList", dto);
    }

    public ScheduleDTO selectScheduleDetail(ScheduleDTO dto) {
        log.info("ScheduleDAO selectScheduleDetail scheduleId={}", dto.getScheduleId());
        return sqlSession.selectOne(ns + "selectScheduleDetail", dto);
    }

    public int insertSchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO insertSchedule");
        int result = sqlSession.insert(ns + "insertSchedule", dto);
        return result;
    }

    public boolean updateSchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO updateSchedule scheduleId={}", dto.getScheduleId());
        return sqlSession.update(ns + "updateSchedule", dto) > 0;
    }

    public boolean deleteSchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO deleteSchedule scheduleId={}", dto.getScheduleId());
        return sqlSession.update(ns + "deleteSchedule", dto) > 0;
    }

    public boolean updateScheduleDrag(ScheduleDTO dto) {
        log.info("ScheduleDAO updateScheduleDrag scheduleId={}", dto.getScheduleId());
        return sqlSession.update(ns + "updateScheduleDrag", dto) > 0;
    }

    public int selectDuplicateCheck(ScheduleDTO dto) {
        log.info("ScheduleDAO selectDuplicateCheck counselorId={}, date={}", dto.getCounselorId(), dto.getScheduleDate());
        return sqlSession.selectOne(ns + "selectDuplicateCheck", dto);
    }

    public List<ScheduleDTO> selectParticipantSearch(ScheduleDTO dto) {
        log.info("ScheduleDAO selectParticipantSearch keyword={}", dto.getKeyword());
        return sqlSession.selectList(ns + "selectParticipantSearch", dto);
    }

    public List<ScheduleDTO> selectTodaySchedule(ScheduleDTO dto) {
        log.info("ScheduleDAO selectTodaySchedule counselorId={}", dto.getCounselorId());
        return sqlSession.selectList(ns + "selectTodaySchedule", dto);
    }

    public List<ScheduleDTO> selectAlertTargets() {
        log.info("ScheduleDAO selectAlertTargets");
        return sqlSession.selectList(ns + "selectAlertTargets");
    }
}
