package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 - 알선 관리 데이터 접근 객체.
 * <p>MyBatis 매퍼 네임스페이스 "AdminJobPlacementDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminJobPlacementDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminJobPlacementDAO.";

    /**
     * 알선 상세정보 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 알선 목록
     */
    public List<AdminDTO> selectJobPlacementList(AdminDTO dto) {
        log.info("AdminJobPlacementDAO selectJobPlacementList");
        return sqlSession.selectList(ns + "selectJobPlacementList", dto);
    }

    /**
     * 알선 단건 상세 정보를 조회한다.
     *
     * @param dto registrationNo(등록번호)가 설정된 DTO
     * @return 해당 알선 정보, 없으면 null
     */
    public AdminDTO selectJobPlacementOne(AdminDTO dto) {
        log.info("AdminJobPlacementDAO selectJobPlacementOne pk={}", dto.getRegistrationNo());
        return sqlSession.selectOne(ns + "selectJobPlacementOne", dto);
    }

    /**
     * 신규 알선 정보를 등록한다.
     *
     * @param dto 등록할 알선 정보
     * @return 등록 성공 여부
     */
    public boolean insertJobPlacement(AdminDTO dto) {
        log.info("AdminJobPlacementDAO insertJobPlacement");
        return sqlSession.insert(ns + "insertJobPlacement", dto) > 0;
    }

    /**
     * 알선 정보를 수정한다.
     *
     * @param dto registrationNo(등록번호)가 설정된 수정 대상 DTO
     * @return 수정 성공 여부
     */
    public boolean updateJobPlacement(AdminDTO dto) {
        log.info("AdminJobPlacementDAO updateJobPlacement pk={}", dto.getRegistrationNo());
        return sqlSession.update(ns + "updateJobPlacement", dto) > 0;
    }

    /**
     * 알선 정보를 삭제한다.
     *
     * @param dto registrationNo(등록번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteJobPlacement(AdminDTO dto) {
        log.info("AdminJobPlacementDAO deleteJobPlacement pk={}", dto.getRegistrationNo());
        return sqlSession.delete(ns + "deleteJobPlacement", dto) > 0;
    }
}
