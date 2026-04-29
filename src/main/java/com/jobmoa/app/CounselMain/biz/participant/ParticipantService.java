package com.jobmoa.app.CounselMain.biz.participant;

import java.util.List;

public interface ParticipantService {
    List<ParticipantDTO> selectAll(ParticipantDTO participantDTO);
    ParticipantDTO selectOne(ParticipantDTO participantDTO);
    boolean insert(ParticipantDTO participantDTO);
    boolean update(ParticipantDTO participantDTO);
    boolean delete(ParticipantDTO participantDTO);
}
