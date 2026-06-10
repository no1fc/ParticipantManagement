package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 관리자 - 참여자 관리 데이터 접근 객체.
 * <p>참여자 목록/단건 조회 및 삭제를 담당한다. 참여자 Excel 출력은
 * AdminDAO(Excel 빌더와 응집)에 잔존한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "AdminParticipantDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class AdminParticipantDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "AdminParticipantDAO.";

    /**
     * 참여자 목록을 조회한다.
     *
     * @param dto 검색 조건이 담긴 DTO
     * @return 조건에 맞는 참여자 목록
     */
    public List<AdminDTO> selectParticipantList(AdminDTO dto) {
        log.info("AdminParticipantDAO selectParticipantList");
        return sqlSession.selectList(ns + "selectParticipantList", dto);
    }

    /**
     * 참여자 단건 상세 정보를 조회한다.
     *
     * @param dto jobNo(구직번호)가 설정된 DTO
     * @return 해당 참여자 정보, 없으면 null
     */
    public AdminDTO selectParticipantOne(AdminDTO dto) {
        log.info("AdminParticipantDAO selectParticipantOne pk={}", dto.getJobNo());
        return sqlSession.selectOne(ns + "selectParticipantOne", dto);
    }

    /**
     * 참여자를 삭제한다.
     *
     * @param dto jobNo(구직번호)가 설정된 DTO
     * @return 삭제 성공 여부
     */
    public boolean deleteParticipant(AdminDTO dto) {
        log.info("AdminParticipantDAO deleteParticipant pk={}", dto.getJobNo());
        return sqlSession.delete(ns + "deleteParticipant", dto) > 0;
    }
}
