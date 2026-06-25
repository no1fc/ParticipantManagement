package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HR - 사이트 접속·권한 관리 DAO. MyBatis 네임스페이스 "HrSiteAccessDAO." 사용.
 * <p>대상 테이블: {@code J_직원_사이트접속} (이름·사이트명은 {@code J_직원}·{@code J_사이트} 조인).</p>
 */
@Slf4j
@Repository
public class HrSiteAccessDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrSiteAccessDAO.";

    public List<HrSiteAccessDTO> selectSiteAccessList(HrSiteAccessDTO dto) {
        log.info("HrSiteAccessDAO selectSiteAccessList");
        return sqlSession.selectList(ns + "selectSiteAccessList", dto);
    }

    public HrSiteAccessDTO selectSiteAccessOne(HrSiteAccessDTO dto) {
        log.info("HrSiteAccessDAO selectSiteAccessOne pk={}", dto.getAccessPk());
        return sqlSession.selectOne(ns + "selectSiteAccessOne", dto);
    }

    /** UNIQUE(직원아이디,사이트코드,부서코드) 충돌 여부. */
    public int selectSiteAccessExists(HrSiteAccessDTO dto) {
        return sqlSession.selectOne(ns + "selectSiteAccessExists", dto);
    }

    public boolean insertSiteAccess(HrSiteAccessDTO dto) {
        log.info("HrSiteAccessDAO insertSiteAccess id={} site={}", dto.getUserId(), dto.getSiteCode());
        return sqlSession.insert(ns + "insertSiteAccess", dto) > 0;
    }

    public boolean updateSiteAccess(HrSiteAccessDTO dto) {
        log.info("HrSiteAccessDAO updateSiteAccess pk={}", dto.getAccessPk());
        return sqlSession.update(ns + "updateSiteAccess", dto) > 0;
    }

    public boolean deleteSiteAccess(HrSiteAccessDTO dto) {
        log.info("HrSiteAccessDAO deleteSiteAccess pk={}", dto.getAccessPk());
        return sqlSession.delete(ns + "deleteSiteAccess", dto) > 0;
    }

    /** 사이트 드롭다운용 목록 ({@code J_사이트}). */
    public List<HrSiteAccessDTO> selectSiteList() {
        return sqlSession.selectList(ns + "selectSiteList");
    }
}
