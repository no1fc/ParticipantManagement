package com.jobmoa.app.CounselMain.view.adminpage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 관리자 REST API({@code /admin/api/**}) 공통 권한 인터셉터.
 *
 * <p>모든 {@code /admin/api/**} 요청에 대해 인증 여부와 관리자 접근 권한을 일괄 검증한다.
 * 미인증 시 401, 관리자 권한이 없으면 403을 JSON으로 응답하여, 각 API 메서드에서
 * 반복되던 인라인 접근 검증({@code checkAccess})을 제거한다.
 *
 * <p>시스템 관리자 전용 검증({@code isManager})은 더 엄격한 조건이므로 개별 API 메서드에
 * 그대로 남겨 둔다. 지점관리자의 검색 범위 제한({@link AdminAccessSupport#enforceBranchScope})은
 * DTO 바인딩 이후 단계라 인터셉터가 아닌 컨트롤러에서 수행한다.
 *
 * <p>JSON 응답을 반환하므로 페이지 이동({@code /admin/**})이 아닌 API 경로에만 등록한다.
 *
 * @see AdminAccessSupport
 * @see AdminApiController
 * @see com.jobmoa.app.config.WebMvcConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Slf4j
public class AdminApiInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 실행 전 관리자 API 접근 권한을 확인한다.
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler  실행 대상 핸들러
     * @return 인증·권한이 모두 충족되면 {@code true}, 아니면 JSON 오류 응답 후 {@code false}
     * @throws Exception 응답 기록 중 발생한 예외
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (AdminAccessSupport.getLoginBean(session) == null) {
            writeJson(response, 401, "로그인이 필요합니다.");
            return false;
        }
        if (!AdminAccessSupport.hasAdminAccess(session)) {
            writeJson(response, 403, "관리자 권한이 필요합니다.");
            return false;
        }
        return true;
    }

    /**
     * 상태 코드와 메시지를 JSON 본문으로 기록한다.
     *
     * @param response HTTP 응답
     * @param status   HTTP 상태 코드
     * @param message  사용자에게 전달할 메시지
     * @throws java.io.IOException 응답 기록 실패 시
     */
    private void writeJson(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
