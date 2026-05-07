package com.jobmoa.app.CounselMain.view.adminpage;

import com.jobmoa.app.CounselMain.biz.adminpage.AdminDTO;
import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import jakarta.servlet.http.HttpSession;

public class AdminAccessSupport {

    public static LoginBean getLoginBean(HttpSession session) {
        if (session == null) return null;
        return (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
    }

    public static boolean isManager(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("IS_MANAGER"));
    }

    public static boolean isBranchManager(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("IS_BRANCH_MANAGER"));
    }

    public static boolean hasAdminAccess(HttpSession session) {
        return isManager(session) || isBranchManager(session);
    }

    public static void enforceBranchScope(HttpSession session, AdminDTO dto) {
        LoginBean login = getLoginBean(session);
        if (login == null) return;
        if (!isManager(session) && isBranchManager(session)) {
            dto.setSearchBranch(login.getMemberBranch());
        }
    }
}
