package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 - 이력서 요청 데이터 접근 객체.
 * <p>MyBatis 매퍼 네임스페이스 "AdminResumeRequestDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminResumeRequestDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminResumeRequestDAO.";

    /**
     * 이력서 요청 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 이력서 요청 목록
     */
    public List<AdminDTO> selectResumeRequestList(AdminDTO dto) {
        log.info("AdminResumeRequestDAO selectResumeRequestList");
        return sqlSession.selectList(ns + "selectResumeRequestList", dto);
    }

    /**
     * 이력서 요청 단건 상세 정보를 조회한다.
     *
     * @param dto resumeRegNo(이력서등록번호)가 설정된 DTO
     * @return 해당 이력서 요청 정보, 없으면 null
     */
    public AdminDTO selectResumeRequestOne(AdminDTO dto) {
        log.info("AdminResumeRequestDAO selectResumeRequestOne pk={}", dto.getResumeRegNo());
        return sqlSession.selectOne(ns + "selectResumeRequestOne", dto);
    }

    /**
     * 이력서 요청의 상태를 변경한다.
     *
     * @param dto resumeRegNo(이력서등록번호)와 상태값이 설정된 DTO
     * @return 상태 변경 성공 여부
     */
    public boolean updateResumeRequestStatus(AdminDTO dto) {
        log.info("AdminResumeRequestDAO updateResumeRequestStatus pk={}", dto.getResumeRegNo());
        return sqlSession.update(ns + "updateResumeRequestStatus", dto) > 0;
    }
}
