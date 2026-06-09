package com.jobmoa.app.CounselMain.biz.participantRandomAssignment;

import java.util.List;

/**
 * 참여자 랜덤 배정 관리 서비스.
 * 랜덤 배정 정보의 조회, 등록, 수정, 삭제 기능을 제공한다.
 */
public interface ParticipantRandomAssignmentService {

    /**
     * 랜덤 배정 정보 등록.
     *
     * @param praDTO 등록할 랜덤 배정 정보
     * @return 등록 성공 여부
     */
    boolean insert(ParticipantRandomAssignmentDTO praDTO);

    /**
     * 랜덤 배정 정보 수정.
     *
     * @param praDTO 수정할 랜덤 배정 정보
     * @return 수정 성공 여부
     */
    boolean update(ParticipantRandomAssignmentDTO praDTO);

    /**
     * 랜덤 배정 정보 삭제.
     *
     * @param praDTO 삭제 대상 랜덤 배정 정보
     * @return 삭제 성공 여부
     */
    boolean delete(ParticipantRandomAssignmentDTO praDTO);

    /**
     * 랜덤 배정 정보 단건 조회.
     *
     * @param praDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 랜덤 배정 정보, 없으면 {@code null}
     */
    ParticipantRandomAssignmentDTO selectOne(ParticipantRandomAssignmentDTO praDTO);

    /**
     * 랜덤 배정 정보 목록 조회.
     *
     * @param praDTO 조회 조건이 담긴 DTO
     * @return 조건에 해당하는 랜덤 배정 정보 목록
     */
    List<ParticipantRandomAssignmentDTO> selectAll(ParticipantRandomAssignmentDTO praDTO);
}
