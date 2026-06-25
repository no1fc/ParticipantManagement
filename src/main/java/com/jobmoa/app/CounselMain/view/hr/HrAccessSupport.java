package com.jobmoa.app.CounselMain.view.hr;

import com.jobmoa.app.CounselMain.biz.hr.HrLoginBean;
import jakarta.servlet.http.HttpSession;

/**
 * HR(입퇴사자관리) 독립 세션 접근 헬퍼. 잡모아 메인 {@code AdminAccessSupport}의 HR 버전이다.
 * <p>HR 세션은 메인({@code JOBMOA_LOGIN_DATA})과 분리된 {@code HR_LOGIN_DATA} 속성을 사용한다.
 * 같은 servlet 세션을 공유하므로 로그아웃 시 HR 키만 제거해야 한다(메인 세션 보존).</p>
 *
 * @see com.jobmoa.app.CounselMain.biz.hr.HrAuthService
 * @see HrLoginController
 */
public class HrAccessSupport {

    /** HR 로그인 세션 속성 키. */
    public static final String HR_LOGIN_DATA = "HR_LOGIN_DATA";
    /** HR 메뉴 접근맵 세션 속성 키 (메뉴코드 → 접근레벨). */
    public static final String HR_MENU_ACCESS = "HR_MENU_ACCESS";

    private HrAccessSupport() {
    }

    /** 세션에서 HR 로그인 빈을 조회한다. 미인증이면 {@code null}. */
    public static HrLoginBean getHrLoginBean(HttpSession session) {
        if (session == null) return null;
        return (HrLoginBean) session.getAttribute(HR_LOGIN_DATA);
    }

    /** HR 인증 여부. */
    public static boolean isAuthed(HttpSession session) {
        return getHrLoginBean(session) != null;
    }

    /** 세션에서 HR 관련 속성만 제거한다(메인 잡모아 세션은 보존). */
    public static void clear(HttpSession session) {
        if (session == null) return;
        session.removeAttribute(HR_LOGIN_DATA);
        session.removeAttribute(HR_MENU_ACCESS);
    }
}
