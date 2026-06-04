package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link ParticipantRandomAssignmentService} 구현체.
 * DAO를 통해 참여자 랜덤 배정 정보의 조회, 등록, 수정, 삭제를 처리한다.
 */
@Slf4j
@Service("praService")
public class ParticipantRandomAssignmentServiceImpl implements ParticipantRandomAssignmentService {

    @Autowired
    private ParticipantRandomAssignmentDAO praDAO;

    @Override
    public boolean insert(ParticipantRandomAssignmentDTO praDTO) {
        log.info("insert SQL ParticipantRandomAssignmentDTO : [{}]",praDTO);
        if(praDTO == null || praDTO.getCondition() == null) {
            return false;
        }
        return praDAO.insert(praDTO);
    }

    @Override
    public boolean update(ParticipantRandomAssignmentDTO praDTO) {
        log.info("update SQL ParticipantRandomAssignmentDTO : [{}]",praDTO);
        if(praDTO == null || praDTO.getCondition() == null) {
            return false;
        }
        return praDAO.update(praDTO);
    }

    @Override
    public boolean delete(ParticipantRandomAssignmentDTO praDTO) {
        log.info("delete SQL ParticipantRandomAssignmentDTO : [{}]",praDTO);
//        if(praDTO == null || praDTO.getCondition() == null) {
//            return false;
//        }
        return false;
    }

    @Override
    public ParticipantRandomAssignmentDTO selectOne(ParticipantRandomAssignmentDTO praDTO) {
        if (praDTO == null || praDTO.getCondition() == null){
            return null;
        }
        return praDAO.selectOne(praDTO);
    }

    @Override
    public List<ParticipantRandomAssignmentDTO> selectAll(ParticipantRandomAssignmentDTO praDTO) {
        if (praDTO == null || praDTO.getCondition() == null){
            return null;
        }
        return praDAO.selectAll(praDTO);
    }
}
