package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;

/**
 * 관리자 - 알선 관리 서비스 인터페이스.
 */
public interface AdminJobPlacementService {

    /**
     * 알선 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 알선 목록
     */
    List<AdminDTO> getJobPlacementList(AdminDTO dto);

    /**
     * 알선 단건을 조회한다.
     *
     * @param dto 조회 대상 알선 정보가 담긴 DTO
     * @return 알선 상세 정보
     */
    AdminDTO getJobPlacementOne(AdminDTO dto);

    /**
     * 알선 정보를 등록한다.
     *
     * @param dto 등록할 알선 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addJobPlacement(AdminDTO dto);

    /**
     * 알선 정보를 수정한다.
     *
     * @param dto 수정할 알선 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyJobPlacement(AdminDTO dto);

    /**
     * 알선 정보를 삭제한다.
     *
     * @param dto 삭제 대상 알선 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeJobPlacement(AdminDTO dto);
}
