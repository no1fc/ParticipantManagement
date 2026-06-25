package com.jobmoa.app.CounselMain.view.hr;

import com.jobmoa.app.CounselMain.biz.hr.HrAuthResultDTO;
import com.jobmoa.app.CounselMain.biz.hr.HrAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * HR(입퇴사자관리) 로그인 컨트롤러. 잡모아 메인과 분리된 독립 인증의 진입점이다.
 * <p>로그인 페이지({@code GET /hr/login})는 공개 경로이며, 인증 성공 시 HR 전용 세션({@code HR_LOGIN_DATA})을 적재한다.
 * 로그아웃({@code GET /hr/logout})은 같은 servlet 세션을 공유하므로 HR 키만 제거한다(메인 세션 보존).</p>
 *
 * @see HrAuthService
 * @see HrAccessSupport
 */
@Slf4j
@Controller
public class HrLoginController {

    @Autowired
    private HrAuthService hrAuthService;

    /** HR 로그인 페이지. 이미 인증 상태면 부서 관리로 이동. */
    @GetMapping("/hr/login")
    public String loginPage(HttpSession session) {
        if (HrAccessSupport.isAuthed(session)) {
            return "redirect:/hr/departments";
        }
        return "hr/hrLogin";
    }

    /** HR 로그인 처리(AJAX). 성공 시 세션 적재 후 redirect 경로를 JSON으로 반환. */
    @PostMapping("/hr/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam String userId,
                                     @RequestParam String password,
                                     HttpSession session) {
        HrAuthResultDTO result = hrAuthService.login(userId, password);

        Map<String, Object> response = new HashMap<>();
        if (!result.isSuccess()) {
            response.put("success", false);
            response.put("message", result.getMessage());
            return response;
        }

        session.setAttribute(HrAccessSupport.HR_LOGIN_DATA, result.getLoginBean());
        session.setAttribute(HrAccessSupport.HR_MENU_ACCESS, result.getMenuAccess());

        response.put("success", true);
        response.put("redirect", "/hr/departments");
        return response;
    }

    /** HR 로그아웃. HR 세션 속성만 제거(메인 잡모아 세션은 유지)하고 로그인 페이지로 이동. */
    @GetMapping("/hr/logout")
    public String logout(HttpSession session) {
        HrAccessSupport.clear(session);
        return "redirect:/hr/login";
    }
}
