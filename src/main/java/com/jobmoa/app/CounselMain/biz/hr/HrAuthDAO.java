package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * HR 로그인 인증 DAO. MyBatis 네임스페이스 "HrAuthDAO." 사용.
 * <p>비밀번호를 조회하는 보안 민감 쿼리를 한 곳에 격리한다(기존 HrAccount/HrSiteAccess 매퍼는 비밀번호 미노출).</p>
 */
@Slf4j
@Repository
public class HrAuthDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "HrAuthDAO.";

    /** 로그인 자격(비밀번호·계정상태 포함) 조회. 없으면 null. */
    public HrAccountDTO selectAccountForLogin(String userId) {
        return sqlSession.selectOne(ns + "selectAccountForLogin", userId);
    }

    /** HR 사이트 접근 허용 행 수(접속허용=1). */
    public int selectHrSiteAccessCount(String userId) {
        Integer count = sqlSession.selectOne(ns + "selectHrSiteAccessCount", userId);
        return count == null ? 0 : count;
    }

    /** HR 사이트 역할(사이트내권한). 없거나 NULL이면 null. */
    public String selectHrSiteRole(String userId) {
        return sqlSession.selectOne(ns + "selectHrSiteRole", userId);
    }

    /** 역할별 메뉴 접근레벨 목록({menuCode, accessLevel}). */
    public List<Map<String, Object>> selectHrMenuAccess(String role) {
        return sqlSession.selectList(ns + "selectHrMenuAccess", role);
    }

    /** 마지막 로그인 일시 기록. */
    public void updateLastLogin(String userId) {
        sqlSession.update(ns + "updateLastLogin", userId);
    }
}
