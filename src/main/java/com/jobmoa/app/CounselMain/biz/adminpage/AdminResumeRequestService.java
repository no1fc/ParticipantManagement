package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;

/**
 * 관리자 - 이력서 요청 서비스 인터페이스.
 */
public interface AdminResumeRequestService {

    /**
     * 이력서 요청 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 이력서 요청 목록
     */
    List<AdminDTO> getResumeRequestList(AdminDTO dto);

    /**
     * 이력서 요청 단건을 조회한다.
     *
     * @param dto 조회 대상 이력서 요청 정보가 담긴 DTO
     * @return 이력서 요청 상세 정보
     */
    AdminDTO getResumeRequestOne(AdminDTO dto);

    /**
     * 이력서 요청 상태를 변경한다.
     *
     * @param dto 상태 변경 정보가 담긴 DTO
     * @return 변경 성공 여부
     */
    boolean updateResumeStatus(AdminDTO dto);
}
