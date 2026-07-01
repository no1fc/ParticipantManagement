package com.jobmoa.app.CounselMain.view.hr;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * HR(입퇴사자관리) 데모 페이지 컨트롤러.
 * <p>
 * 부서/조직, 계정, 사이트 접속·권한 관리 화면을 제공한다. HR은 별도 사이트(사이트코드='HR', 경로 '/hr')로,
 * 잡모아 스택을 재사용한 확인용 데모이다. 접근은 HR 독립 로그인({@code HR_LOGIN_DATA}) 전용이다.
 * 미인증 접근은 {@link HrPageInterceptor}가 공통 차단하며, 각 메서드의 가드는 방어적 이중 확인이다.
 * </p>
 *
 * @see HrApiController
 * @see HrAccessSupport
 */
@Slf4j
@Controller
@RequestMapping("/hr")
public class HrPageController {

    /** 인원현황 대시보드 페이지(읽기전용). 로그인 후 기본 랜딩. */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (!HrAccessSupport.isAuthed(session)) return "redirect:/hr/login";
        return "hr/hrDashboard";
    }

    /** 부서/조직 관리 페이지. */
    @GetMapping("/departments")
    public String departments(HttpSession session) {
        if (!HrAccessSupport.isAuthed(session)) return "redirect:/hr/login";
        return "hr/hrDepartmentManagement";
    }

    /** 계정 관리 페이지. */
    @GetMapping("/accounts")
    public String accounts(HttpSession session) {
        if (!HrAccessSupport.isAuthed(session)) return "redirect:/hr/login";
        return "hr/hrAccountManagement";
    }

    /** 사이트 접속·권한 관리 페이지. */
    @GetMapping("/site-access")
    public String siteAccess(HttpSession session) {
        if (!HrAccessSupport.isAuthed(session)) return "redirect:/hr/login";
        return "hr/hrSiteAccessManagement";
    }
}
