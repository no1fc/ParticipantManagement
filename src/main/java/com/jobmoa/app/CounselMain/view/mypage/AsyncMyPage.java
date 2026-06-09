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

/**
 * 마이페이지 비동기 REST API 컨트롤러.
 * <p>비밀번호 확인, 연락처 수정, 일일보고 저장, 비밀번호 변경, 계정 정보 업데이트 등
 * 마이페이지에서 사용하는 비동기 API를 제공한다.</p>
 */
@Slf4j
@RestController
public class AsyncMyPage {

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 마이페이지 접근을 위한 비밀번호 확인을 수행한다.
     * <p>BCrypt 해시와 평문 비밀번호를 동시에 지원한다. 비밀번호 미설정 사용자에게는
     * 비밀번호 설정을 안내한다. 인증 성공 시 마이페이지 데이터를 함께 반환한다.</p>
     * @param memberDTO 입력된 비밀번호를 담은 DTO
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @return 비밀번호 확인 결과 및 회원 데이터를 담은 JSON 응답
     */
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

    /**
     * 회원 연락처 정보를 수정한다.
     * @param memberDTO 수정할 연락처 정보를 담은 DTO
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @return 수정 성공/실패 여부를 담은 JSON 응답
     */
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

    /**
     * 일일보고를 저장한다.
     * @param memberDTO 일일보고 데이터를 담은 DTO
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @return 저장 성공/실패 여부를 담은 JSON 응답
     */
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

    /**
     * 마이페이지에서 비밀번호를 변경한다.
     * <p>새 비밀번호를 BCrypt로 해싱하여 저장한다.</p>
     * @param memberDTO 새 비밀번호를 담은 DTO
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @return 변경 성공/실패 여부를 담은 JSON 응답
     */
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


    /**
     * 계정 정보(입사일, 발령일, 근속구분 등)를 업데이트한다.
     * <p>업데이트 성공 시 최신 회원 데이터를 다시 조회하여 반환한다.</p>
     * @param memberDTO 업데이트할 계정 정보를 담은 DTO
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @return 업데이트 결과 및 최신 회원 데이터를 담은 JSON 응답
     */
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



    /**
     * 회원 데이터를 포함한 성공 응답 Map을 생성한다.
     * @param checkMemberDTO 조회된 회원 정보 DTO
     * @param returnMessage 응답 메시지
     * @return 회원 데이터와 성공 상태를 포함한 응답 Map
     */
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

    /**
     * 회원 DTO를 프론트엔드 표시용 필드 Map 구조로 변환한다.
     * <p>각 필드는 {name, val, type} 형태의 Map으로 구성된다.</p>
     * @param checkMemberDTO 변환할 회원 정보 DTO
     * @return 필드별 메타데이터를 포함한 회원 데이터 Map
     */
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

    /**
     * 개별 필드의 메타데이터 Map을 생성한다.
     * @param name 필드 표시명
     * @param value 필드 값
     * @param type 필드 유형 (text, number, date, phone, email 등)
     * @return {name, val, type}을 포함한 필드 Map
     */
    @NotNull
    private static Map<String, Object> createFieldMap(String name, Object value, String type) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("name", name);
        fieldMap.put("val", value);
        fieldMap.put("type", type);
        return fieldMap;
    }

    /**
     * 오류 응답 Map을 생성한다.
     * @param message 오류 메시지
     * @param status HTTP 상태 코드 문자열
     * @return 오류 정보를 포함한 응답 Map
     */
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