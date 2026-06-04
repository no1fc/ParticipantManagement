package com.jobmoa.app.CounselMain.view.login;

import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 회원가입 컨트롤러.
 * <p>회원가입 페이지 표시, 신규 회원 등록(승인대기 상태), 아이디 중복 확인 기능을 제공한다.
 * 비밀번호는 BCrypt로 해싱하여 저장한다.</p>
 */
@Slf4j
@Controller
public class RegisterController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 회원가입 페이지를 표시한다.
     * <p>활성 지점 목록을 조회하여 지점 선택 드롭다운에 전달한다.</p>
     * @param model Spring MVC Model
     * @return 회원가입 JSP 뷰 이름 (views/register)
     */
    @GetMapping("/register.do")
    public String registerPage(Model model) {
        log.info("-----------------------------------");
        log.info("회원가입 페이지 접근");
        List<Map<String, Object>> branchList = memberService.selectActiveBranchList();
        model.addAttribute("branchList", branchList);
        log.info("-----------------------------------");
        return "views/register";
    }

    /**
     * 회원가입을 처리한다.
     * <p>필수값 검증, 아이디 중복 체크 후 비밀번호를 BCrypt 해싱하여 승인대기 상태로 등록한다.</p>
     * @param memberDTO 회원가입 정보 (아이디, 비밀번호, 이름, 지점)
     * @return 회원가입 처리 결과를 담은 JSON 응답 (success, message)
     */
    @PostMapping("/register.api")
    @ResponseBody
    public Map<String, Object> register(MemberDTO memberDTO) {
        log.info("-----------------------------------");
        log.info("회원가입 요청 - 아이디: [{}]", memberDTO.getMemberUserID());
        Map<String, Object> result = new HashMap<>();

        // 필수값 검증
        if (memberDTO.getMemberUserID() == null || memberDTO.getMemberUserID().trim().isEmpty()
                || memberDTO.getMemberUserPW() == null || memberDTO.getMemberUserPW().trim().isEmpty()
                || memberDTO.getMemberUserName() == null || memberDTO.getMemberUserName().trim().isEmpty()
                || memberDTO.getMemberBranch() == null || memberDTO.getMemberBranch().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "필수 항목을 모두 입력해주세요.");
            return result;
        }

        // 아이디 중복 체크
        memberDTO.setMemberCondition("checkUserIdDuplicate");
        int count = memberService.selectCount(memberDTO);
        if (count > 0) {
            result.put("success", false);
            result.put("message", "이미 사용 중인 아이디입니다.");
            return result;
        }

        // 회원가입 (승인대기 상태, 비밀번호 해싱)
        memberDTO.setMemberUserPW(passwordEncoder.encode(memberDTO.getMemberUserPW()));
        memberDTO.setMemberCondition("registerUser");
        boolean inserted = memberService.insert(memberDTO);

        if (inserted) {
            log.info("회원가입 성공 - 아이디: [{}], 승인대기 상태", memberDTO.getMemberUserID());
            result.put("success", true);
            result.put("message", "회원가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.");
        } else {
            log.error("회원가입 실패 - 아이디: [{}]", memberDTO.getMemberUserID());
            result.put("success", false);
            result.put("message", "회원가입 처리 중 오류가 발생했습니다.");
        }

        log.info("-----------------------------------");
        return result;
    }

    /**
     * 아이디 중복 여부를 확인한다.
     * @param memberDTO 확인할 아이디를 담은 DTO
     * @return 중복 확인 결과를 담은 JSON 응답 (available, message)
     */
    @GetMapping("/register.api")
    @ResponseBody
    public Map<String, Object> checkDuplicateId(MemberDTO memberDTO) {
        log.info("아이디 중복 체크 - 아이디: [{}]", memberDTO.getMemberUserID());
        Map<String, Object> result = new HashMap<>();

        if (memberDTO.getMemberUserID() == null || memberDTO.getMemberUserID().trim().isEmpty()) {
            result.put("available", false);
            result.put("message", "아이디를 입력해주세요.");
            return result;
        }

        memberDTO.setMemberCondition("checkUserIdDuplicate");
        int count = memberService.selectCount(memberDTO);
        result.put("available", count == 0);
        result.put("message", count == 0 ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");
        return result;
    }
}
