package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;

/**
 * 관리자 - 지점 관리 서비스 인터페이스.
 */
public interface AdminBranchService {

    /**
     * 지점 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 지점 목록
     */
    List<AdminDTO> getBranchList(AdminDTO dto);

    /**
     * 지점 단건을 조회한다.
     *
     * @param dto 조회 대상 지점 정보가 담긴 DTO
     * @return 지점 상세 정보
     */
    AdminDTO getBranchOne(AdminDTO dto);

    /**
     * 신규 지점을 등록한다.
     *
     * @param dto 등록할 지점 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addBranch(AdminDTO dto);

    /**
     * 지점 정보를 수정한다.
     *
     * @param dto 수정할 지점 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyBranch(AdminDTO dto);

    /**
     * 지점을 삭제한다.
     *
     * @param dto 삭제 대상 지점 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeBranch(AdminDTO dto);

    /**
     * 특정 지점에 소속된 사용자 수를 조회한다.
     *
     * @param dto 지점 정보가 담긴 DTO
     * @return 소속 사용자 수
     */
    int getBranchUserCount(AdminDTO dto);
}
