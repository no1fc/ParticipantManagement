package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 - 사용자(전담자 로그인정보) 관리 데이터 접근 객체.
 * <p>MyBatis 매퍼 네임스페이스 "AdminUserDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminUserDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminUserDAO.";

    /**
     * 사용자(로그인정보) 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 사용자 목록
     */
    public List<AdminDTO> selectUserList(AdminDTO dto) {
        log.info("AdminUserDAO selectUserList");
        return sqlSession.selectList(ns + "selectUserList", dto);
    }

    /**
     * 사용자 단건 상세 정보를 조회한다.
     *
     * @param dto memberNo(회원번호)가 설정된 DTO
     * @return 해당 사용자 정보, 없으면 null
     */
    public AdminDTO selectUserOne(AdminDTO dto) {
        log.info("AdminUserDAO selectUserOne pk={}", dto.getMemberNo());
        return sqlSession.selectOne(ns + "selectUserOne", dto);
    }

    /**
     * 신규 사용자를 등록한다.
     *
     * @param dto 등록할 사용자 정보
     * @return 등록 성공 여부
     */
    public boolean insertUser(AdminDTO dto) {
        log.info("AdminUserDAO insertUser");
        return sqlSession.insert(ns + "insertUser", dto) > 0;
    }

    /**
     * 사용자 정보를 수정한다.
     *
     * @param dto memberNo(회원번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateUser(AdminDTO dto) {
        log.info("AdminUserDAO updateUser pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "updateUser", dto) > 0;
    }

    /**
     * 사용자를 삭제한다.
     *
     * @param dto memberNo(회원번호)가 설정된 삭제 대상 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteUser(AdminDTO dto) {
        log.info("AdminUserDAO deleteUser pk={}", dto.getMemberNo());
        return sqlSession.delete(ns + "deleteUser", dto) > 0;
    }

    /**
     * 사용자 비밀번호를 초기화한다.
     *
     * @param dto memberNo(회원번호)가 설정된 DTO
     * @return 초기화 성공 여부
     */
    public boolean resetUserPassword(AdminDTO dto) {
        log.info("AdminUserDAO resetUserPassword pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "resetUserPassword", dto) > 0;
    }

    /**
     * 사용자 가입 승인 처리를 수행한다.
     *
     * @param dto memberNo(회원번호)가 설정된 DTO
     * @return 승인 성공 여부
     */
    public boolean approveUser(AdminDTO dto) {
        log.info("AdminUserDAO approveUser pk={}", dto.getMemberNo());
        return sqlSession.update(ns + "approveUser", dto) > 0;
    }

    /**
     * 다음 회원번호 시퀀스 값을 조회한다.
     *
     * @return 다음 회원번호
     */
    public int selectNextMemberNo() {
        log.info("AdminUserDAO selectNextMemberNo");
        return sqlSession.selectOne(ns + "selectNextMemberNo");
    }

    /**
     * 사용자 ID 중복 여부를 확인한다.
     *
     * @param dto userId(사용자ID)가 설정된 DTO
     * @return 중복 건수 (0이면 사용 가능)
     */
    public int selectUserIdExists(AdminDTO dto) {
        log.info("AdminUserDAO selectUserIdExists userId={}", dto.getUserId());
        return sqlSession.selectOne(ns + "selectUserIdExists", dto);
    }
}
