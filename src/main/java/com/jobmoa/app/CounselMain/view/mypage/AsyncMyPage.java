package com.jobmoa.app.CounselMain.view.mypage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
public class AsyncMyPage {

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping(value = "/checkPassword.api", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> checkPassword(@RequestBody MemberDTO memberDTO, HttpSession session) {
        log.info("Start checkPassword.api");

        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        String userID = loginBean.getMemberUserID();

        try {
            String inputPW = memberDTO.getMemberUserPW();

            // 1) 비밀번호 포함 조회 (loginSelect — 비밀번호 컬럼 포함)
            MemberDTO loginDTO = new MemberDTO();
            loginDTO.setMemberUserID(userID);
            loginDTO.setMemberCondition("loginSelect");
            MemberDTO pwCheckDTO = memberService.selectOne(loginDTO);

            if (pwCheckDTO == null) {
                return ResponseEntity.status(401)
                        .body(createErrorResponse("사용자 정보를 찾을 수 없습니다.", "401"));
            }

            String storedPW = pwCheckDTO.getMemberUserPW();
            boolean passwordEmpty = (storedPW == null || storedPW.isEmpty());

            // 2) 비밀번호가 없는 사용자는 비밀번호 설정 유도
            if (passwordEmpty) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "PASSWORD_REQUIRED");
                response.put("message", "비밀번호가 설정되지 않았습니다. 비밀번호를 설정해주세요.");
                response.put("result", "false");
                return ResponseEntity.ok().body(response);
            }

            // 3) 비밀번호가 있는 사용자는 비밀번호 확인 필요
            if (inputPW == null || inputPW.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("비밀번호를 입력해주세요.", "400"));
            }

            // BCrypt/평문 동시 지원
            boolean match = storedPW.startsWith("$2a$")
                    ? passwordEncoder.matches(inputPW, storedPW)
                    : inputPW.equals(storedPW);

            if (!match) {
                return ResponseEntity.status(401)
                        .body(createErrorResponse("비밀번호가 일치하지 않습니다.", "401"));
            }

            // 4) 인증 성공 → 마이페이지 데이터 조회 (비밀번호 미포함 쿼리)
            memberDTO.setMemberUserID(userID);
            memberDTO.setMemberCondition("OneMemberDataSelectNotPasswordCheck");
            MemberDTO checkMemberDTO = memberService.selectOne(memberDTO);
            Map<String, Object> response = getResponse(checkMemberDTO, "비밀번호 확인완료");
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("checkPassword.api Exception : [{}]", e.getMessage());
            return ResponseEntity.status(500)
                    .body(createErrorResponse("서버 오류가 발생했습니다.", "500"));
        }
    }

    @PostMapping(value = "/updateContact.api", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> updateContact(@RequestBody MemberDTO memberDTO, HttpSession session) {
        log.info("Start updateContact.api");
        try {
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            memberDTO.setMemberUserID(loginBean.getMemberUserID());
            memberDTO.setMemberCondition("updateContact");
            boolean result = memberService.update(memberDTO);

            if (result) {
                return ResponseEntity.ok().body(Map.of("success", true, "message", "연락처가 수정되었습니다."));
            }
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "수정에 실패했습니다."));
        } catch (Exception e) {
            log.error("updateContact.api Exception : [{}]", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping(value = "/updateDailyReport.api", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> updateDailyReport(@RequestBody MemberDTO memberDTO, HttpSession session) {
        log.info("Start updateDailyReport.api");
        try {
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            memberDTO.setMemberUserID(loginBean.getMemberUserID());
            memberDTO.setMemberBranch(loginBean.getMemberBranch());
            memberDTO.setMemberCondition("updateDailyReport");
            boolean result = memberService.update(memberDTO);

            if (result) {
                return ResponseEntity.ok().body(Map.of("success", true, "message", "일일보고가 저장되었습니다."));
            }
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "저장에 실패했습니다."));
        } catch (Exception e) {
            log.error("updateDailyReport.api Exception : [{}]", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping(value = "/changeMyPassword.api", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> changeMyPassword(@RequestBody MemberDTO memberDTO, HttpSession session) {
        log.info("Start changeMyPassword.api");
        try {
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            String newPW = memberDTO.getMemberUserChangePW();

            if (newPW == null || newPW.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "새 비밀번호를 입력해주세요."));
            }

            memberDTO.setMemberUserID(loginBean.getMemberUserID());
            memberDTO.setMemberUserPW(passwordEncoder.encode(newPW));
            memberDTO.setMemberCondition("changePassword");
            boolean result = memberService.update(memberDTO);

            if (result) {
                return ResponseEntity.ok().body(Map.of("success", true, "message", "비밀번호가 변경되었습니다."));
            }
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "변경에 실패했습니다."));
        } catch (Exception e) {
            log.error("changeMyPassword.api Exception : [{}]", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }


    @PostMapping(value = "/changeAccount.api", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> changeAccount(@RequestBody MemberDTO memberDTO, HttpSession session){
        log.info("Start changeAccount.api");

        try {
            LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
            String userID = loginBean.getMemberUserID();

            // 계정 정보 업데이트 로직
            memberDTO.setMemberUserID(userID);
            memberDTO.setMemberBranch(loginBean.getMemberBranch());
            memberDTO.setMemberCondition("accountUpdate");
            boolean updateResult = memberService.update(memberDTO);

            if(updateResult) {
                memberDTO.setMemberCondition("OneMemberDataSelectNotPasswordCheck");
                log.info("changeAccount.api user password: {}", memberDTO.getMemberUserPW());
                MemberDTO checkMemberDTO = memberService.selectOne(memberDTO);
                Map<String, Object> response = getResponse(checkMemberDTO, "업데이트 완료");

                return ResponseEntity.ok()
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .body(response);
            } else {
                Map<String, Object> response = createErrorResponse("계정 정보 업데이트에 실패했습니다.", "400");
                return ResponseEntity.badRequest()
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .body(response);
            }

        } catch (Exception e) {
            log.error("changeAccount.api Exception : [{}]", e.getMessage());
            Map<String, Object> response = createErrorResponse("서버 오류가 발생했습니다.", "500");
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        }
    }



    @NotNull
    private static Map<String, Object> getResponse(MemberDTO checkMemberDTO, String returnMessage) {
        // 회원 데이터 객체 생성
        Map<String, Object> memberData = createMemberDataMap(checkMemberDTO);

        // 응답 객체 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", returnMessage);
        response.put("result", "true");
        response.put("status", "200");
        response.put("data", memberData);

        return response;
    }

    @NotNull
    private static Map<String, Object> createMemberDataMap(MemberDTO checkMemberDTO) {
        Map<String, Object> memberData = new LinkedHashMap<>();

        // 지점 정보
        memberData.put("memberBranch", createFieldMap("지점",
                checkMemberDTO.getMemberBranch() != null ? checkMemberDTO.getMemberBranch() : "", "text"));

        // 이름 정보
        memberData.put("memberUserName", createFieldMap("이름",
                checkMemberDTO.getMemberUserName() != null ? checkMemberDTO.getMemberUserName() : "", "text"));

        // 아이디 정보
        memberData.put("memberUserID", createFieldMap("아이디",
                checkMemberDTO.getMemberUserID() != null ? checkMemberDTO.getMemberUserID() : "", "text"));

        // 대표번호 정보
        memberData.put("memberPhoneNumber", createFieldMap("대표번호(내선)",
                checkMemberDTO.getMemberPhoneNumber() != null ? checkMemberDTO.getMemberPhoneNumber() : "", "phone"));

        // 이메일
        memberData.put("memberEmail", createFieldMap("이메일",
                checkMemberDTO.getMemberEmail() != null ? checkMemberDTO.getMemberEmail() : "", "email"));

        // 계정 등록일
        memberData.put("memberRegDate", createFieldMap("계정 등록일",
                checkMemberDTO.getMemberRegDate() != null ? checkMemberDTO.getMemberRegDate() : "", "date"));

        // 고유번호
        memberData.put("memberUniqueNumber", createFieldMap("고유번호",
                checkMemberDTO.getMemberUniqueNumber() != null ? checkMemberDTO.getMemberUniqueNumber() : "", "text"));

        // 금일 취업 실적
        memberData.put("memberTodayEmployment", createFieldMap("금일 일반 취업",
                checkMemberDTO.getMemberTodayEmployment(), "number"));
        memberData.put("memberTodayPlacement", createFieldMap("금일 알선 취업",
                checkMemberDTO.getMemberTodayPlacement(), "number"));

        // 금주 취업 실적
        memberData.put("memberToWeekEmployment", createFieldMap("금주 일반 취업",
                checkMemberDTO.getMemberToWeekEmployment(), "number"));
        memberData.put("memberToWeekPlacement", createFieldMap("금주 알선 취업",
                checkMemberDTO.getMemberToWeekPlacement(), "number"));

        // 금월 취업 실적
        memberData.put("memberToMonthEmployment", createFieldMap("금월 일반 취업",
                checkMemberDTO.getMemberToMonthEmployment(), "number"));
        memberData.put("memberToMonthPlacement", createFieldMap("금월 알선 취업",
                checkMemberDTO.getMemberToMonthPlacement(), "number"));

        // 금년 취업 실적
        memberData.put("memberToYearEmployment", createFieldMap("금년 일반 취업",
                checkMemberDTO.getMemberToYearEmployment(), "number"));
        memberData.put("memberToYearPlacement", createFieldMap("금년 알선 취업",
                checkMemberDTO.getMemberToYearPlacement(), "number"));

        // 업데이트일자
        memberData.put("endUpdateStatus", createFieldMap("업데이트일자",
                checkMemberDTO.getEndUpdateStatus() != null ? checkMemberDTO.getEndUpdateStatus() : "", "date"));

        // 입사일
        memberData.put("memberJoinedDate", createFieldMap("입사일",
                checkMemberDTO.getMemberJoinedDate() != null ? checkMemberDTO.getMemberJoinedDate() : "", "date"));

        // 발령일
        memberData.put("memberAssignedDate", createFieldMap("발령일",
                checkMemberDTO.getMemberAssignedDate() != null ? checkMemberDTO.getMemberAssignedDate() : "", "date"));

        // 근속구분
        memberData.put("memberContinuous", createFieldMap("근속구분",
                checkMemberDTO.getMemberContinuous() != null ? checkMemberDTO.getMemberContinuous() : "", "text"));

        return memberData;
    }

    @NotNull
    private static Map<String, Object> createFieldMap(String name, Object value, String type) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("name", name);
        fieldMap.put("val", value);
        fieldMap.put("type", type);
        return fieldMap;
    }

    @NotNull
    private static Map<String, Object> createErrorResponse(String message, String status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("result", "false");
        response.put("status", status);
        response.put("data", new HashMap<>());
        return response;
    }
}