package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 - 지점 관리 데이터 접근 객체.
 * <p>MyBatis 매퍼 네임스페이스 "AdminBranchDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminBranchDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminBranchDAO.";

    /**
     * 지점 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 지점 목록
     */
    public List<AdminDTO> selectBranchList(AdminDTO dto) {
        log.info("AdminBranchDAO selectBranchList");
        return sqlSession.selectList(ns + "selectBranchList", dto);
    }

    /**
     * 지점 단건 상세 정보를 조회한다.
     *
     * @param dto branchNo(지점번호)가 설정된 DTO
     * @return 해당 지점 정보, 없으면 null
     */
    public AdminDTO selectBranchOne(AdminDTO dto) {
        log.info("AdminBranchDAO selectBranchOne pk={}", dto.getBranchNo());
        return sqlSession.selectOne(ns + "selectBranchOne", dto);
    }

    /**
     * 신규 지점을 등록한다.
     *
     * @param dto 등록할 지점 정보
     * @return 등록 성공 여부
     */
    public boolean insertBranch(AdminDTO dto) {
        log.info("AdminBranchDAO insertBranch");
        return sqlSession.insert(ns + "insertBranch", dto) > 0;
    }

    /**
     * 지점 정보를 수정한다.
     *
     * @param dto branchNo(지점번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateBranch(AdminDTO dto) {
        log.info("AdminBranchDAO updateBranch pk={}", dto.getBranchNo());
        return sqlSession.update(ns + "updateBranch", dto) > 0;
    }

    /**
     * 지점에 소속된 사용자 수를 조회한다.
     *
     * @param dto branchNo(지점번호)가 설정된 DTO
     * @return 해당 지점의 소속 사용자 수
     */
    public int selectBranchUserCount(AdminDTO dto) {
        log.info("AdminBranchDAO selectBranchUserCount pk={}", dto.getBranchNo());
        return sqlSession.selectOne(ns + "selectBranchUserCount", dto);
    }

    /**
     * 지점을 소프트 삭제(비활성화)한다.
     *
     * @param dto branchNo(지점번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteBranch(AdminDTO dto) {
        log.info("AdminBranchDAO deleteBranch (soft) pk={}", dto.getBranchNo());
        return sqlSession.update(ns + "deleteBranch", dto) > 0;
    }
}
