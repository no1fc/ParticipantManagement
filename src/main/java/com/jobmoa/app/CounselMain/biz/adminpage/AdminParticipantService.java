package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;

/**
 * 관리자 - 참여자 관리 서비스 인터페이스.
 */
public interface AdminParticipantService {

    /**
     * 참여자 목록을 조회한다 (관리자 전용).
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 참여자 목록
     */
    List<AdminDTO> getParticipantList(AdminDTO dto);

    /**
     * 참여자 단건을 조회한다 (관리자 전용).
     *
     * @param dto 조회 대상 참여자 정보가 담긴 DTO
     * @return 참여자 상세 정보
     */
    AdminDTO getParticipantOne(AdminDTO dto);

    /**
     * 참여자를 삭제한다 (관리자 전용).
     *
     * @param dto 삭제 대상 참여자 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeParticipant(AdminDTO dto);
}
