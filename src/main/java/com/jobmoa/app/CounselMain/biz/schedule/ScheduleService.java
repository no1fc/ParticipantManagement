package com.jobmoa.app.CounselMain.biz.schedule;

import java.util.List;

public interface ScheduleService {

    List<ScheduleDTO> getScheduleList(ScheduleDTO dto);

    ScheduleDTO getScheduleDetail(ScheduleDTO dto);

    int insertSchedule(ScheduleDTO dto);

    int updateSchedule(ScheduleDTO dto);

    boolean deleteSchedule(ScheduleDTO dto);

    int updateScheduleDrag(ScheduleDTO dto);

    boolean checkDuplicate(ScheduleDTO dto);

    List<ScheduleDTO> searchParticipant(ScheduleDTO dto);

    List<ScheduleDTO> getTodaySchedule(ScheduleDTO dto);

    List<ScheduleDTO> getAlertTargets();
}
