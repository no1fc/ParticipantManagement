package com.jobmoa.app.CounselMain.view.adminpage;

import com.jobmoa.app.CounselMain.biz.adminpage.AdminDTO;
import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import jakarta.servlet.http.HttpSession;

/**
 * 관리자 페이지 접근 권한을 검증하는 유틸리티 클래스.
 * <p>
 * 세션 기반으로 로그인 여부, 관리자/지점관리자 권한, 권한 그룹을 확인하며,
 * 지점관리자의 경우 검색 범위를 소속 지점으로 제한하는 기능을 제공한다.
 * </p>
 *
 * @see AdminApiController
 * @see AdminPageController
 */
public class AdminAccessSupport {

    /**
     * 세션에서 로그인 사용자 정보를 조회한다.
     *
     * @param session HTTP 세션
     * @return 로그인된 사용자의 {@link LoginBean}, 세션이 없거나 로그인되지 않은 경우 {@code null}
     */
    public static LoginBean getLoginBean(HttpSession session) {
        if (session == null) return null;
        return (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
    }

    /**
     * 시스템 관리자 여부를 확인한다.
     *
     * @param session HTTP 세션
     * @return 시스템 관리자이면 {@code true}
     */
    public static boolean isManager(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("IS_MANAGER"));
    }

    /**
     * 지점 관리자 여부를 확인한다.
     *
     * @param session HTTP 세션
     * @return 지점 관리자이면 {@code true}
     */
    public static boolean isBranchManager(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("IS_BRANCH_MANAGER"));
    }

    /**
     * 관리자 페이지 접근 가능 여부를 확인한다.
     * 시스템 관리자 또는 지점 관리자 중 하나라도 해당되면 접근을 허용한다.
     *
     * @param session HTTP 세션
     * @return 관리자 접근 권한이 있으면 {@code true}
     */
    public static boolean hasAdminAccess(HttpSession session) {
        return isManager(session) || isBranchManager(session);
    }

    /**
     * 세션에 저장된 권한 그룹명을 조회한다.
     *
     * @param session HTTP 세션
     * @return 권한 그룹명 (없을 경우 {@code "NORMAL"})
     */
    public static String getPermissionGroup(HttpSession session) {
        Object group = session.getAttribute("PERMISSION_GROUP");
        return group != null ? group.toString() : "NORMAL";
    }

    /**
     * 글로벌 관리자(GLOBAL_ADMIN) 여부를 확인한다.
     *
     * @param session HTTP 세션
     * @return 글로벌 관리자이면 {@code true}
     */
    public static boolean isGlobalAdmin(HttpSession session) {
        return "GLOBAL_ADMIN".equals(getPermissionGroup(session));
    }

    /**
     * 지점관리자인 경우 검색 범위를 소속 지점으로 강제 제한한다.
     * <p>
     * 시스템 관리자가 아닌 지점관리자는 자신의 소속 지점 데이터만 조회할 수 있도록
     * DTO의 검색 지점을 세션의 소속 지점으로 설정한다.
     * </p>
     *
     * @param session HTTP 세션
     * @param dto     검색 조건이 담긴 AdminDTO
     */
    public static void enforceBranchScope(HttpSession session, AdminDTO dto) {
        LoginBean login = getLoginBean(session);
        if (login == null) return;
        if (!isManager(session) && isBranchManager(session)) {
            dto.setSearchBranch(login.getMemberBranch());
        }
    }

    /**
     * 특정 지점 데이터에 대한 접근 가능 여부를 확인한다.
     * <p>
     * 목록 조회는 {@link #enforceBranchScope(HttpSession, AdminDTO)}로 검색 범위가 제한되지만,
     * 단건 조회·수정은 식별자(구직번호/등록번호)만으로 접근하므로 레코드의 소속 지점을 직접 검증해야 한다.
     * 시스템 관리자는 전 지점, 지점관리자는 본인 소속 지점 데이터에만 접근을 허용한다.
     * </p>
     *
     * @param session      HTTP 세션
     * @param recordBranch 접근 대상 레코드의 소속 지점 (조회 결과의 {@code branch})
     * @return 접근 가능하면 {@code true}
     */
    public static boolean canAccessBranch(HttpSession session, String recordBranch) {
        if (isManager(session)) return true;
        LoginBean login = getLoginBean(session);
        if (login == null) return false;
        String myBranch = login.getMemberBranch();
        return myBranch != null && myBranch.equals(recordBranch);
    }
}
