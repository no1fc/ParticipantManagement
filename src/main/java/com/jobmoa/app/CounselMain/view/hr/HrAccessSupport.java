package com.jobmoa.app.CounselMain.view.hr;

import com.jobmoa.app.CounselMain.biz.hr.HrLoginBean;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * HR(입퇴사자관리) 독립 세션 접근 헬퍼. 잡모아 메인 {@code AdminAccessSupport}의 HR 버전이다.
 * <p>HR 세션은 메인({@code JOBMOA_LOGIN_DATA})과 분리된 {@code HR_LOGIN_DATA} 속성을 사용한다.
 * 같은 servlet 세션을 공유하므로 로그아웃 시 HR 키만 제거해야 한다(메인 세션 보존).</p>
 * <p>인증(로그인 여부) 헬퍼 외에, 세션 {@code HR_MENU_ACCESS}(메뉴코드 → 접근레벨)를 인가 소스로
 * 사용하는 역할 가드 헬퍼({@link #hasRead}/{@link #canWrite})를 제공한다. 인터셉터가 이를 호출해
 * 페이지·API 접근을 역할 기준으로 차단한다.</p>
 *
 * @see com.jobmoa.app.CounselMain.biz.hr.HrAuthService
 * @see HrLoginController
 */
public class HrAccessSupport {

    /** HR 로그인 세션 속성 키. */
    public static final String HR_LOGIN_DATA = "HR_LOGIN_DATA";
    /** HR 메뉴 접근맵 세션 속성 키 (메뉴코드 → 접근레벨). */
    public static final String HR_MENU_ACCESS = "HR_MENU_ACCESS";

    /** 화면 메뉴코드 (J_사이트_메뉴.메뉴코드). */
    public static final String MENU_DASHBOARD = "HR_DASHBOARD";
    public static final String MENU_DEPARTMENT = "HR_DEPARTMENT";
    public static final String MENU_ACCOUNT = "HR_ACCOUNT";
    public static final String MENU_SITE_ACCESS = "HR_SITE_ACCESS";

    /** 접근레벨 (J_사이트_메뉴권한.접근레벨). 순서: 읽기 < 쓰기 < 전체. */
    public static final String LEVEL_READ = "읽기";
    public static final String LEVEL_WRITE = "쓰기";
    public static final String LEVEL_FULL = "전체";

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

    /** 세션의 HR 메뉴 접근맵(메뉴코드 → 접근레벨)을 조회한다. 미적재면 {@code null}. */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getMenuAccess(HttpSession session) {
        if (session == null) return null;
        Object attr = session.getAttribute(HR_MENU_ACCESS);
        return (attr instanceof Map) ? (Map<String, String>) attr : null;
    }

    /**
     * 해당 메뉴에 대한 읽기 이상 권한 여부.
     * <p>맵이 비었으면(시드 미적용·구세션) 전체 허용 — {@code hrGnb.tag} 폴백과 일치. 아니면
     * 메뉴코드 행 존재(=읽기 이상)로 판정한다.</p>
     */
    public static boolean hasRead(HttpSession session, String menuCode) {
        Map<String, String> access = getMenuAccess(session);
        if (access == null || access.isEmpty()) return true;
        return access.containsKey(menuCode);
    }

    /**
     * 해당 메뉴에 대한 쓰기 권한 여부.
     * <p>맵이 비었으면 전체 허용(읽기와 동일 폴백). 아니면 접근레벨이 {@code 쓰기} 또는 {@code 전체}일 때만 허용.</p>
     */
    public static boolean canWrite(HttpSession session, String menuCode) {
        Map<String, String> access = getMenuAccess(session);
        if (access == null || access.isEmpty()) return true;
        String level = access.get(menuCode);
        return LEVEL_WRITE.equals(level) || LEVEL_FULL.equals(level);
    }

    /**
     * 요청 URI를 데모 3화면 메뉴코드로 매핑한다. 매핑 대상이 아니면 {@code null}(가드 제외).
     * <p>페이지(`/hr/departments` 등)와 API(`/hr/api/departments...`, 드롭다운 `/hr/api/sites`)를
     * 동일 규칙으로 처리한다. `/site-access`는 `/sites` 부분문자열을 포함하지 않으므로 순서 무관.</p>
     */
    public static String menuCodeForPath(String uri) {
        if (uri == null) return null;
        if (uri.contains("/dashboard")) return MENU_DASHBOARD;
        if (uri.contains("/departments")) return MENU_DEPARTMENT;
        if (uri.contains("/accounts")) return MENU_ACCOUNT;
        if (uri.contains("/site-access") || uri.contains("/sites")) return MENU_SITE_ACCESS;
        return null;
    }

    /** 변경성(쓰기) HTTP 메서드 여부 (POST/PUT/DELETE/PATCH). */
    public static boolean isWriteMethod(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method);
    }

    /**
     * 데모 3화면(부서/계정/사이트접속) 중 읽기 가능한 첫 페이지 경로를 반환한다. 없으면 {@code null}.
     * <p>페이지 접근 거부 시 무한 리다이렉트를 피하기 위한 안전한 이동 대상으로 쓴다.</p>
     */
    public static String firstAccessiblePage(HttpSession session) {
        if (hasRead(session, MENU_DASHBOARD)) return "/hr/dashboard";
        if (hasRead(session, MENU_DEPARTMENT)) return "/hr/departments";
        if (hasRead(session, MENU_ACCOUNT)) return "/hr/accounts";
        if (hasRead(session, MENU_SITE_ACCESS)) return "/hr/site-access";
        return null;
    }
}
