package com.jobmoa.app.CounselMain.view.login;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import com.jobmoa.app.CounselMain.view.function.MemberRoleCheck;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인/로그아웃 페이지 컨트롤러.
 * <p>사용자 인증(로그인), 세션 관리, 로그아웃 처리를 담당한다.
 * BCrypt 해시와 평문 비밀번호를 동시에 지원하며(마이그레이션 과도기),
 * 비밀번호 미설정 사용자에게는 비밀번호 설정 모달을 표시한다.</p>
 */
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private InfoBean infoBean;

    /**
     * 루트 경로 접근 시 index.jsp로 리다이렉트한다.
     * @return index.jsp 리다이렉트 경로
     */
    @GetMapping("/")
    public String index(){
        return "redirect:index.jsp";
    }

    /**
     * 로그인 페이지를 표시한다.
     * <p>이미 로그인된 사용자는 대시보드로 리다이렉트한다.
     * setPassword 파라미터가 있으면 비밀번호 설정 모달을 표시한다.</p>
     * @param session HTTP 세션
     * @param model Spring MVC Model
     * @param setPassword 비밀번호 설정 모달 표시 여부 ("true"이면 표시)
     * @return 로그인 페이지(views/login) 또는 대시보드 리다이렉트 경로
     * @throws Exception 예외 발생 시
     */
    @GetMapping("/login.do")
    public String loginController(HttpSession session, Model model,
                                  @RequestParam(value = "setPassword", required = false) String setPassword) throws Exception {
        log.info("-----------------------------------");
        //Session에 저장되어 있는 Login DATA
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");

        String page = "views/login";
        //Session이 비어있지 않다면 dashboard.login 페이지로 이동
        if (loginBean != null) {
            log.info("dashboard.login page 이동");
            page = "redirect:dashboard.login";
        }

        // 비밀번호 설정 모달 표시 여부
        if ("true".equals(setPassword)) {
            String userId = (String) session.getAttribute("SET_PASSWORD_USER_ID");
            if (userId != null) {
                model.addAttribute("setPasswordUserId", userId);
                model.addAttribute("showSetPasswordModal", true);
            }
        }

        log.info("login DATA : [{}]", loginBean);
        log.info("login Controller page : [{}]", page);
        log.info("-----------------------------------");
        return page;
    }

    /**
     * 로그인 인증을 처리한다.
     * <p>사용자 상태(승인대기, 사용, 비활성화) 확인 후 비밀번호를 검증한다.
     * 인증 성공 시 세션에 로그인 정보, 권한 그룹, 관리자 여부 등을 저장한다.</p>
     * @param model Spring MVC Model
     * @param session HTTP 세션
     * @param memberDTO 입력된 아이디/비밀번호를 담은 DTO
     * @param loginBean 세션에 저장될 로그인 정보 빈
     * @param memberRoleCheck 역할 권한 검증 유틸리티
     * @return 알림 페이지(views/info) - 성공 시 대시보드, 실패 시 로그인 페이지로 이동
     */
    @PostMapping("/login.do")
    public String loginController(Model model, HttpSession session, MemberDTO memberDTO, LoginBean loginBean, MemberRoleCheck memberRoleCheck){
        log.info("-----------------------------------");
        log.info("Start loginController");
        String url = "login.do";
        String icon = "error";
        String title = "로그인 실패";
        String message = "";

        String rawPassword = memberDTO.getMemberUserPW();
        String inputUserId = memberDTO.getMemberUserID();

        // 1) 아이디로 상태 확인 (상태 무관 조회)
        MemberDTO statusCheck = new MemberDTO();
        statusCheck.setMemberUserID(inputUserId);
        statusCheck.setMemberCondition("loginCheckStatus");
        MemberDTO statusResult = memberService.selectOne(statusCheck);

        if (statusResult == null) {
            // 아이디 자체가 없음
            message = "아이디 또는 비밀번호를 확인해주세요.";
        } else if ("승인대기".equals(statusResult.getUseStatus())) {
            // 승인대기 상태
            icon = "warning";
            title = "승인대기";
            message = "관리자 승인 후 로그인할 수 있습니다.";
        } else if ("사용".equals(statusResult.getUseStatus())) {
            // 활성 사용자 → 기존 loginSelect 실행
            memberDTO.setMemberCondition("loginSelect");
            memberDTO = memberService.selectOne(memberDTO);
            log.info("loginDTO : [{}]", memberDTO);

            if (memberDTO != null && memberDTO.getMemberUserID() != null) {
                String storedPassword = memberDTO.getMemberUserPW();

                // 비밀번호가 없는 경우 (초기화됨) → 비밀번호 설정 유도
                if (storedPassword == null || storedPassword.isEmpty()) {
                    session.setAttribute("SET_PASSWORD_USER_ID", inputUserId);
                    icon = "info";
                    title = "비밀번호 설정 필요";
                    message = "비밀번호가 설정되지 않았습니다. 새 비밀번호를 입력해주세요.";
                    url = "login.do?setPassword=true";
                } else {
                    // BCrypt 해시($2a$로 시작)와 평문 비밀번호 동시 지원 (마이그레이션 과도기)
                    boolean passwordMatch = storedPassword.startsWith("$2a$")
                            ? passwordEncoder.matches(rawPassword, storedPassword)
                            : rawPassword.equals(storedPassword);

                    if (passwordMatch) {
                        log.info("loginController login Success user ID : [{}]", memberDTO.getMemberUserID());

                        String role = memberDTO.getMemberRole();
                        boolean isManager = memberDTO.isMemberISManager();
                        loginBean.setMemberUserID(memberDTO.getMemberUserID());
                        loginBean.setMemberUserName(memberDTO.getMemberUserName());
                        loginBean.setMemberBranch(memberDTO.getMemberBranch());
                        loginBean.setMemberRole(role);
                        loginBean.setMemberUniqueNumber(memberDTO.getMemberUniqueNumber());
                        boolean branchRole = memberRoleCheck.checkBranchRole(role);
                        boolean praRole = role.equals("PRA");
                        String permissionGroup = MemberRoleCheck.getPermissionGroup(role).name();
                        loginBean.setPermissionGroup(permissionGroup);

                        session.setAttribute("JOBMOA_LOGIN_DATA", loginBean);
                        session.setAttribute("IS_BRANCH_MANAGER", branchRole);
                        session.setAttribute("IS_MANAGER", isManager);
                        session.setAttribute("IS_PRA_MANAGER", praRole);
                        session.setAttribute("PERMISSION_GROUP", permissionGroup);

                        session.setMaxInactiveInterval(21600);
                        log.info("Session MaxInactiveInterval : [{}]", session.getMaxInactiveInterval());
                        session.setAttribute("SESSION_TIME", System.currentTimeMillis());

                        url = "dashboard.login";
                        icon = "success";
                        title = "로그인 성공";
                    }
                }
            }
        } else {
            // 정지/잠금/퇴사
            icon = "warning";
            title = "로그인 불가";
            message = "계정이 비활성화 상태입니다. 관리자에게 문의하세요.";
        }

        //info.jsp 페이지로 넘어갈때 활용
        //로그인 성공 : dashboard.login 페이지로 이동
        //로그인 실패 : login.do 페이지로 이동
        //SweetAlert 사용중 아이콘 선택
        //성공 : success
        //실패 : error
        InfoBean.info(model,url,icon,title,message);
        log.info("로그인 여부 : [{}]",title);

        //info 페이지로 이동
        log.info("-----------------------------------");
        return "views/info";
    }

    /**
     * 로그아웃을 처리한다.
     * <p>세션을 무효화하고 로그인 페이지로 리다이렉트한다.</p>
     * @param session HTTP 세션
     * @return 로그인 페이지 리다이렉트 경로
     */
    @GetMapping("/logout.do")
    public String logoutController(HttpSession session){
        log.info("-----------------------------------");
        //모든 세션 정보 제거
        log.info("session remove : [{}]", session.getAttribute("JOBMOA_LOGIN_DATA"));
        session.invalidate();
        log.info("-----------------------------------");
        return "redirect:login.do";
    }
}
