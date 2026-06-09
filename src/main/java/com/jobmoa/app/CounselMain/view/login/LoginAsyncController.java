package com.jobmoa.app.CounselMain.view.login;

import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인 관련 비동기 REST API 컨트롤러.
 * <p>비밀번호 변경 및 비밀번호 초기화 API를 제공한다.</p>
 */
@Slf4j
@RestController
public class LoginAsyncController {

    @Autowired
    MemberService memberService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    /**
     * 비밀번호를 변경한다.
     * <p>입력된 비밀번호와 확인 비밀번호의 일치 여부를 검증한 후 BCrypt 해싱하여 저장한다.</p>
     * @param memberDTO 사용자 ID, 비밀번호, 변경 비밀번호를 담은 DTO
     * @return 비밀번호 변경 결과 메시지를 담은 JSON 응답
     */
    @PostMapping("changePW.api")
    public ResponseEntity<?> changePW(@RequestBody MemberDTO memberDTO){
        String memberUserID = memberDTO.getMemberUserID();
        String memberUserPW = memberDTO.getMemberUserPW();
        String memberUserChangePW = memberDTO.getMemberUserChangePW();

        log.info("changePW.api changePW memberUserID : [{}]", memberUserID);

        if(memberUserID == null || memberUserPW == null || memberUserChangePW == null){
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"아이디, 비밀번호가 비어있습니다.\"}");

        }

        if(!memberUserPW.equals(memberUserChangePW)){
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"입력한 비밀번호와 확인용 비밀번호가 틀립니다.\"}");

        }

        // 비밀번호를 BCrypt 해싱 후 저장
        memberDTO.setMemberUserPW(passwordEncoder.encode(memberUserPW));
        memberDTO.setMemberCondition("changePassword");
        if(!memberService.update(memberDTO)){
            log.error("changePW.api update fail memberDTO : [{}]", memberDTO);
            log.info("changePW.api update fail : [false]");
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"비밀번호 오류로 인해 변경에 실패했습니다.\"}");

        }

        log.info("changePW.api update success : [true]");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"비밀번호가 변경되었습니다.\"}");

    }

    /**
     * 비밀번호를 초기화한다.
     * <p>관리자가 특정 사용자의 비밀번호를 빈 값으로 초기화하여,
     * 다음 로그인 시 비밀번호를 새로 설정하도록 유도한다.</p>
     * @param memberDTO 초기화 대상 사용자 ID를 담은 DTO
     * @return 비밀번호 초기화 결과 메시지를 담은 JSON 응답
     */
    @PostMapping("clearPassword.api")
    public ResponseEntity<?> clearPassword(@RequestBody MemberDTO memberDTO) {
        String memberUserID = memberDTO.getMemberUserID();
        log.info("clearPassword.api memberUserID : [{}]", memberUserID);

        if (memberUserID == null || memberUserID.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"아이디가 비어있습니다.\"}");
        }

        memberDTO.setMemberUserPW("");
        memberDTO.setMemberCondition("changePassword");
        if (!memberService.update(memberDTO)) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"비밀번호 초기화에 실패했습니다.\"}");
        }

        log.info("clearPassword.api success for user : [{}]", memberUserID);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\":\"비밀번호가 초기화되었습니다. 아이디만 입력하여 로그인해주세요.\"}");
    }

}
