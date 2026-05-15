package com.jobmoa.app.CounselMain.biz.adminpage;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class PermissionDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "PermissionDAO.";

    public List<Map<String, Object>> selectPermissionsByRole(String role) {
        log.info("PermissionDAO selectPermissionsByRole role={}", role);
        return sqlSession.selectList(ns + "selectPermissionsByRole", Map.of("role", role));
    }

    public String selectMenuAccess(String role, String menuCode) {
        log.info("PermissionDAO selectMenuAccess role={}, menuCode={}", role, menuCode);
        return sqlSession.selectOne(ns + "selectMenuAccess", Map.of("role", role, "menuCode", menuCode));
    }

    public List<Map<String, Object>> selectAllPermissions() {
        log.info("PermissionDAO selectAllPermissions");
        return sqlSession.selectList(ns + "selectAllPermissions");
    }

    public boolean insertPermission(Map<String, Object> param) {
        log.info("PermissionDAO insertPermission");
        return sqlSession.insert(ns + "insertPermission", param) > 0;
    }

    public boolean updatePermission(Map<String, Object> param) {
        log.info("PermissionDAO updatePermission pk={}", param.get("pk"));
        return sqlSession.update(ns + "updatePermission", param) > 0;
    }

    public boolean deletePermission(int pk) {
        log.info("PermissionDAO deletePermission pk={}", pk);
        return sqlSession.delete(ns + "deletePermission", Map.of("pk", pk)) > 0;
    }
}
