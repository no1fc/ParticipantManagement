package com.jobmoa.app.CounselMain.biz.adminpage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * {@link PermissionService} 구현체.
 * PermissionDAO를 통해 역할 기반 메뉴 접근 권한을 관리한다.
 */
@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDAO permissionDAO;

    @Override
    public List<Map<String, Object>> getPermissionsByRole(String role) {
        return permissionDAO.selectPermissionsByRole(role);
    }

    @Override
    public String getMenuAccess(String role, String menuCode) {
        return permissionDAO.selectMenuAccess(role, menuCode);
    }

    @Override
    public boolean hasMenuAccess(String role, String menuCode) {
        String accessLevel = permissionDAO.selectMenuAccess(role, menuCode);
        return accessLevel != null;
    }

    @Override
    public List<Map<String, Object>> getAllPermissions() {
        return permissionDAO.selectAllPermissions();
    }

    @Override
    public boolean addPermission(Map<String, Object> param) {
        return permissionDAO.insertPermission(param);
    }

    @Override
    public boolean modifyPermission(Map<String, Object> param) {
        return permissionDAO.updatePermission(param);
    }

    @Override
    public boolean removePermission(int pk) {
        return permissionDAO.deletePermission(pk);
    }
}
