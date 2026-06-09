package com.jobmoa.app.CounselMain.biz.adminpage;

import java.util.List;
import java.util.Map;

/**
 * 역할(Role) 기반 메뉴 접근 권한 관리 서비스 인터페이스.
 * 역할별 권한 조회, 메뉴 접근 여부 확인, 권한 CRUD를 정의한다.
 */
public interface PermissionService {

    /**
     * 특정 역할에 부여된 권한 목록을 조회한다.
     * @param role 역할 코드
     * @return 권한 목록
     */
    List<Map<String, Object>> getPermissionsByRole(String role);

    /**
     * 특정 역할의 메뉴 접근 레벨을 조회한다.
     * @param role 역할 코드
     * @param menuCode 메뉴 코드
     * @return 접근 레벨 문자열 (접근 불가 시 null)
     */
    String getMenuAccess(String role, String menuCode);

    /**
     * 특정 역할이 해당 메뉴에 접근 가능한지 확인한다.
     * @param role 역할 코드
     * @param menuCode 메뉴 코드
     * @return 접근 가능하면 true, 아니면 false
     */
    boolean hasMenuAccess(String role, String menuCode);

    /**
     * 전체 권한 목록을 조회한다.
     * @return 전체 권한 목록
     */
    List<Map<String, Object>> getAllPermissions();

    /**
     * 신규 권한을 등록한다.
     * @param param 등록할 권한 정보
     * @return 등록 성공 여부
     */
    boolean addPermission(Map<String, Object> param);

    /**
     * 권한 정보를 수정한다.
     * @param param 수정할 권한 정보
     * @return 수정 성공 여부
     */
    boolean modifyPermission(Map<String, Object> param);

    /**
     * 권한을 삭제한다.
     * @param pk 삭제할 권한의 기본키
     * @return 삭제 성공 여부
     */
    boolean removePermission(int pk);
}
