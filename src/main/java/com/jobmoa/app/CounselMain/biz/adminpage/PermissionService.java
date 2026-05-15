package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;
import java.util.Map;

public interface PermissionService {
    List<Map<String, Object>> getPermissionsByRole(String role);
    String getMenuAccess(String role, String menuCode);
    boolean hasMenuAccess(String role, String menuCode);
    List<Map<String, Object>> getAllPermissions();
    boolean addPermission(Map<String, Object> param);
    boolean modifyPermission(Map<String, Object> param);
    boolean removePermission(int pk);
}
