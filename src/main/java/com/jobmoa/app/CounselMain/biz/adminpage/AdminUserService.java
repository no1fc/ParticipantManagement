package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;

/**
 * 관리자 - 사용자(전담자 로그인정보) 관리 서비스 인터페이스.
 */
public interface AdminUserService {

    /**
     * 사용자 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 사용자 목록
     */
    List<AdminDTO> getUserList(AdminDTO dto);

    /**
     * 사용자 단건을 조회한다.
     *
     * @param dto 조회 대상 사용자 정보가 담긴 DTO
     * @return 사용자 상세 정보
     */
    AdminDTO getUserOne(AdminDTO dto);

    /**
     * 신규 사용자를 등록한다.
     *
     * @param dto 등록할 사용자 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean addUser(AdminDTO dto);

    /**
     * 사용자 정보를 수정한다.
     *
     * @param dto 수정할 사용자 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean modifyUser(AdminDTO dto);

    /**
     * 사용자를 삭제한다.
     *
     * @param dto 삭제 대상 사용자 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean removeUser(AdminDTO dto);

    /**
     * 사용자 비밀번호를 초기화한다.
     *
     * @param dto 대상 사용자 정보가 담긴 DTO
     * @return 초기화 성공 여부
     */
    boolean resetPassword(AdminDTO dto);

    /**
     * 사용자 계정을 승인 처리한다.
     *
     * @param dto 승인 대상 사용자 정보가 담긴 DTO
     * @return 승인 성공 여부
     */
    boolean approveUser(AdminDTO dto);

    /**
     * 다음 회원번호를 채번한다.
     *
     * @return 다음 회원번호
     */
    int getNextMemberNo();

    /**
     * 사용자 ID 중복 여부를 확인한다.
     *
     * @param dto 확인할 사용자 ID가 담긴 DTO
     * @return 중복이면 true, 아니면 false
     */
    boolean checkUserIdExists(AdminDTO dto);
}
