package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 권한(메뉴 접근 권한) 데이터 접근 객체.
 * <p>역할별 메뉴 접근 권한의 조회, 등록, 수정, 삭제를 담당한다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "PermissionDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class PermissionDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "PermissionDAO.";

    /**
     * 역할(role)에 해당하는 권한 목록을 조회한다.
     *
     * @param role 역할 코드
     * @return 해당 역할의 권한 목록 (Map 형태)
     */
    public List<Map<String, Object>> selectPermissionsByRole(String role) {
        log.info("PermissionDAO selectPermissionsByRole role={}", role);
        return sqlSession.selectList(ns + "selectPermissionsByRole", Map.of("role", role));
    }

    /**
     * 특정 역할의 메뉴 접근 권한을 조회한다.
     *
     * @param role     역할 코드
     * @param menuCode 메뉴 코드
     * @return 접근 권한 값, 없으면 null
     */
    public String selectMenuAccess(String role, String menuCode) {
        log.info("PermissionDAO selectMenuAccess role={}, menuCode={}", role, menuCode);
        return sqlSession.selectOne(ns + "selectMenuAccess", Map.of("role", role, "menuCode", menuCode));
    }

    /**
     * 전체 권한 목록을 조회한다.
     *
     * @return 모든 권한 목록 (Map 형태)
     */
    public List<Map<String, Object>> selectAllPermissions() {
        log.info("PermissionDAO selectAllPermissions");
        return sqlSession.selectList(ns + "selectAllPermissions");
    }

    /**
     * 신규 권한을 등록한다.
     *
     * @param param 등록할 권한 정보 (Map 형태)
     * @return 등록 성공 여부
     */
    public boolean insertPermission(Map<String, Object> param) {
        log.info("PermissionDAO insertPermission");
        return sqlSession.insert(ns + "insertPermission", param) > 0;
    }

    /**
     * 권한 정보를 수정한다.
     *
     * @param param pk(기본키)가 포함된 수정 대상 정보 (Map 형태)
     * @return 수정 성공 여부
     */
    public boolean updatePermission(Map<String, Object> param) {
        log.info("PermissionDAO updatePermission pk={}", param.get("pk"));
        return sqlSession.update(ns + "updatePermission", param) > 0;
    }

    /**
     * 권한을 삭제한다.
     *
     * @param pk 삭제 대상 기본키
     * @return 삭제 성공 여부
     */
    public boolean deletePermission(int pk) {
        log.info("PermissionDAO deletePermission pk={}", pk);
        return sqlSession.delete(ns + "deletePermission", Map.of("pk", pk)) > 0;
    }
}
