package com.jobmoa.app.CounselMain.biz.login;

import java.util.List;
import java.util.Map;

/**
 * 회원(로그인 사용자) 관리 서비스 인터페이스.
 * 로그인 인증, 회원 CRUD, 활성 지점 조회 등을 정의한다.
 */
public interface MemberService {

    /**
     * 회원 단건을 조회한다 (로그인 인증 포함).
     * @param loginDTO 조회 조건이 담긴 DTO
     * @return 회원 상세 정보
     */
    MemberDTO selectOne(MemberDTO loginDTO);

    /**
     * 회원 목록을 조회한다.
     * @param loginDTO 검색 조건이 담긴 DTO
     * @return 회원 목록
     */
    List<MemberDTO> selectAll(MemberDTO loginDTO);

    /**
     * 신규 회원을 등록한다.
     * @param loginDTO 등록할 회원 정보가 담긴 DTO
     * @return 등록 성공 여부
     */
    boolean insert(MemberDTO loginDTO);

    /**
     * 조건에 해당하는 회원 수를 조회한다.
     * @param loginDTO 검색 조건이 담긴 DTO
     * @return 회원 수
     */
    int selectCount(MemberDTO loginDTO);

    /**
     * 활성 상태인 지점 목록을 조회한다.
     * @return 활성 지점 목록
     */
    List<Map<String, Object>> selectActiveBranchList();

    /**
     * 회원 정보를 수정한다.
     * @param loginDTO 수정할 회원 정보가 담긴 DTO
     * @return 수정 성공 여부
     */
    boolean update(MemberDTO loginDTO);

    /**
     * 회원을 삭제한다.
     * @param loginDTO 삭제 대상 회원 정보가 담긴 DTO
     * @return 삭제 성공 여부
     */
    boolean delete(MemberDTO loginDTO);
}
