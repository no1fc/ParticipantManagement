package com.jobmoa.app.CounselMain.biz.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("schedule")
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDAO scheduleDAO;

    @Override
    public List<ScheduleDTO> getScheduleList(ScheduleDTO dto) {
        return scheduleDAO.selectScheduleList(dto);
    }

    @Override
    public ScheduleDTO getScheduleDetail(ScheduleDTO dto) {
        return scheduleDAO.selectScheduleDetail(dto);
    }

    @Override
    public int insertSchedule(ScheduleDTO dto) {
        if (checkDuplicate(dto)) {
            log.warn("insertSchedule 중복 일정 감지 counselorId={}, date={}", dto.getCounselorId(), dto.getScheduleDate());
            return -1;
        }
        return scheduleDAO.insertSchedule(dto);
    }

    @Override
    public int updateSchedule(ScheduleDTO dto) {
        if (checkDuplicate(dto)) {
            log.warn("updateSchedule 중복 일정 감지 scheduleId={}", dto.getScheduleId());
            return -1;
        }
        return scheduleDAO.updateSchedule(dto) ? dto.getScheduleId() : 0;
    }

    @Override
    public boolean deleteSchedule(ScheduleDTO dto) {
        return scheduleDAO.deleteSchedule(dto);
    }

    @Override
    public int updateScheduleDrag(ScheduleDTO dto) {
        ScheduleDTO checkDto = new ScheduleDTO();
        checkDto.setScheduleId(dto.getScheduleId());
        checkDto.setCounselorId(dto.getCounselorId());
        checkDto.setScheduleDate(dto.getNewDate());
        checkDto.setStartTime(dto.getNewStartTime());
        checkDto.setEndTime(dto.getNewEndTime());

        if (checkDuplicate(checkDto)) {
            log.warn("updateScheduleDrag 중복 일정 감지 scheduleId={}", dto.getScheduleId());
            return -1;
        }
        return scheduleDAO.updateScheduleDrag(dto) ? dto.getScheduleId() : 0;
    }

    @Override
    public boolean checkDuplicate(ScheduleDTO dto) {
        int count = scheduleDAO.selectDuplicateCheck(dto);
        return count > 0;
    }

    @Override
    public List<ScheduleDTO> searchParticipant(ScheduleDTO dto) {
        return scheduleDAO.selectParticipantSearch(dto);
    }

    @Override
    public List<ScheduleDTO> getTodaySchedule(ScheduleDTO dto) {
        return scheduleDAO.selectTodaySchedule(dto);
    }

    @Override
    public List<ScheduleDTO> getAlertTargets() {
        return scheduleDAO.selectAlertTargets();
    }
}
