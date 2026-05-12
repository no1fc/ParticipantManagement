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

    // 2단계: 관리자 지점 일정 통합 조회
    List<ScheduleDTO> getBranchScheduleList(ScheduleDTO dto);

    ScheduleDTO getScheduleStats(ScheduleDTO dto);

    List<ScheduleDTO> getCounselorsByBranch(ScheduleDTO dto);

    // 3단계: 공개 일정 인증
    boolean selectVerifyPublicAccess(ScheduleDTO dto);
}
