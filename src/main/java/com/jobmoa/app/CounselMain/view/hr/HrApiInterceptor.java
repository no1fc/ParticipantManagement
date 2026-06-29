package com.jobmoa.app.CounselMain.view.hr;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * HR REST API({@code /hr/api/**}) 공통 인증·인가 인터셉터. 잡모아 메인 {@code AdminApiInterceptor}의 HR 버전이다.
 *
 * <p>① 미인증({@code HR_LOGIN_DATA} 없음) 시 401 JSON. ② 인증되었으나 요청 메뉴에 대한 역할 권한이
 * 부족하면 403 JSON. 인가는 세션 {@code HR_MENU_ACCESS}(메뉴코드 → 접근레벨)를 소스로 하며,
 * GET=읽기 / POST·PUT·DELETE=쓰기 권한을 요구한다(맵이 비면 전체 허용, gnb 폴백과 일치).
 * 페이지 이동이 아닌 API 경로에만 등록한다.</p>
 *
 * @see HrPageInterceptor
 * @see HrAccessSupport
 * @see com.jobmoa.app.config.WebMvcConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Slf4j
public class HrApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (!HrAccessSupport.isAuthed(session)) {
            writeJson(response, 401, "로그인이 필요합니다.");
            return false;
        }

        // 역할 인가: 요청 URI→메뉴코드 + 메서드→읽기/쓰기 판정. 매핑 대상이 아니면 통과(인증만).
        String menuCode = HrAccessSupport.menuCodeForPath(request.getRequestURI());
        if (menuCode != null) {
            boolean write = HrAccessSupport.isWriteMethod(request.getMethod());
            boolean allowed = write
                    ? HrAccessSupport.canWrite(session, menuCode)
                    : HrAccessSupport.hasRead(session, menuCode);
            if (!allowed) {
                log.debug("HrApiInterceptor - 인가 차단: [{} {}] menu={} write={}",
                        request.getMethod(), request.getRequestURI(), menuCode, write);
                writeJson(response, 403, "접근 권한이 없습니다.");
                return false;
            }
        }
        return true;
    }

    /** 상태코드 + 메시지를 JSON으로 응답한다(UTF-8). */
    private void writeJson(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
