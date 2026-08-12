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
    public boolean insertAll(List<ParticipantRandomAssignmentDTO> list, String branch) {
        // AOP 트랜잭션(biz..*Impl.*) 안에서 루프를 돌므로 단일 트랜잭션으로 묶인다.
        // 중간 INSERT가 예외를 던지면 앞서 등록된 건까지 함께 롤백된다.
        if (list == null || list.isEmpty()) {
            return false;
        }
        boolean allInserted = true;
        for (ParticipantRandomAssignmentDTO dto : list) {
            dto.setCondition("praInsert");
            dto.setBranch(branch);
            dto.setCareer(dto.getHasCareer());
            allInserted = praDAO.insert(dto) && allInserted;
        }
        return allInserted;
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
