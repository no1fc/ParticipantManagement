package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import java.util.List;

public interface ParticipantRandomAssignmentService {
    boolean insert(ParticipantRandomAssignmentDTO praDTO);
    boolean update(ParticipantRandomAssignmentDTO praDTO);
    boolean delete(ParticipantRandomAssignmentDTO praDTO);
    ParticipantRandomAssignmentDTO selectOne(ParticipantRandomAssignmentDTO praDTO);
    List<ParticipantRandomAssignmentDTO> selectAll(ParticipantRandomAssignmentDTO praDTO);
}
