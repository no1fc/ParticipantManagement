package com.jobmoa.app.CounselMain.view.adminpage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 관리자 페이지({@code /admin/**}, API 제외) 공통 접근 권한 인터셉터.
 *
 * <p>관리자 페이지 진입 시 인증 여부와 관리자 접근 권한({@link AdminAccessSupport#hasAdminAccess})을
 * 일괄 검증한다. 컨트롤러 메서드마다 흩어져 있던 {@code if (!hasAdminAccess) redirect:/} 가드가
 * 누락되더라도 인터셉터가 공통 게이트로 차단하여 URL 직접입력 우회를 구조적으로 막는다.
 *
 * <p>시스템 관리자 전용 페이지({@code /admin/users}, {@code /admin/branches})의 더 엄격한
 * {@code isManager} 검증은 개별 컨트롤러에 그대로 둔다(인터셉터는 최소선 {@code hasAdminAccess}만 보장).
 *
 * <p>JSON 응답을 반환하는 API 경로({@code /admin/api/**})는 {@link AdminApiInterceptor}가 담당하므로
 * 이 인터셉터는 등록 시 해당 경로를 제외한다.
 *
 * @see AdminAccessSupport
 * @see AdminApiInterceptor
 * @see com.jobmoa.app.config.WebMvcConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Slf4j
public class AdminPageInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 실행 전 관리자 페이지 접근 권한을 확인한다.
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler  실행 대상 핸들러
     * @return 인증·권한이 충족되면 {@code true}, 아니면 루트({@code /})로 리다이렉트 후 {@code false}
     * @throws Exception 리다이렉트 처리 중 발생한 예외
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (AdminAccessSupport.getLoginBean(session) == null) {
            log.debug("AdminPageInterceptor - 미인증 접근 차단: [{}]", request.getRequestURI());
            response.sendRedirect("/");
            return false;
        }
        if (!AdminAccessSupport.hasAdminAccess(session)) {
            log.debug("AdminPageInterceptor - 관리자 권한 없음 차단: [{}]", request.getRequestURI());
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
