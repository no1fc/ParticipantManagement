package com.jobmoa.app.CounselMain.biz.participant;

import java.util.List;

/**
 * 참여자 관리 서비스 인터페이스.
 * 참여자 목록 조회, 상세 조회, 등록, 수정, 삭제(연관 데이터 포함)를 정의한다.
 */
public interface ParticipantService {

    /**
     * 참여자 목록을 조회한다.
     * @param participantDTO 검색 조건이 담긴 DTO
     * @return 참여자 목록
     */
    List<ParticipantDTO> selectAll(ParticipantDTO participantDTO);

    /**
     * 참여자 단건을 조회한다.
     * @param participantDTO 조회 조건이 담긴 DTO
     * @return 참여자 상세 정보
     */
    ParticipantDTO selectOne(ParticipantDTO participantDTO);

    /**
     * 참여자를 등록한다.
     * @param participantDTO 등록할 참여자 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(ParticipantDTO participantDTO);

    /**
     * 참여자 정보를 수정한다.
     * @param participantDTO 수정할 참여자 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(ParticipantDTO participantDTO);

    /**
     * 참여자를 삭제한다 (자격증, 교육 등 연관 데이터 포함).
     * @param participantDTO 삭제 대상 참여자 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(ParticipantDTO participantDTO);
}
