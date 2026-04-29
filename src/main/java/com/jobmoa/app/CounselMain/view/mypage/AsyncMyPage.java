package com.jobmoa.app.CounselMain.view.mypage;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/checkPassword.api", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> checkPassword(@RequestBody MemberDTO memberDTO, HttpSession session){
        log.info("Start checkPassword.api");

        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        String userID = loginBean.getMemberUserID();

        try {
            if(memberDTO == null || memberDTO.getMemberUserPW().trim().isEmpty()){
                log.info("checkPassword.api memberUserPW is null or empty");
                throw new NullPointerException("memberUserPW is null or empty");
            }

            memberDTO.setMemberUserID(userID);
            memberDTO.setMemberCondition("OneMemberDataSelect");
            MemberDTO checkMemberDTO = memberService.selectOne(memberDTO);

            if(checkMemberDTO != null) {
                Map<String, Object> response = getResponse(checkMemberDTO, "비밀번호 확인완료");
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .body(response);
            } else {
                Map<String, Object> response = createErrorResponse("비밀번호가 일치하지 않습니다.", "401");
                return ResponseEntity.status(401)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .body(response);
            }

        } catch (NullPointerException e){
            log.error("checkPassword.api NullPointerException : [{}]",e.getMessage());
            Map<String, Object> response = createErrorResponse(e.getMessage(), "400");
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        } catch (Exception e){
            log.error("checkPassword.api Exception : [{}]",e.getMessage());
            Map<String, Object> response = createErrorResponse("서버 오류가 발생했습니다.", "500");
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
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

        // 변경비밀번호 (빈 값)
        memberData.put("memberUserChangePW", createFieldMap("비밀번호 변경", "", "password"));
        memberData.put("memberUserChangePWOK", createFieldMap("비밀번호 변경 확인", "", "password"));

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