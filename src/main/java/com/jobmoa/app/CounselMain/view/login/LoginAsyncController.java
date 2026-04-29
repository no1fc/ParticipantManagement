package com.jobmoa.app.CounselMain.view.login;

import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginAsyncController {

    @Autowired
    MemberService memberService;

    @PostMapping("changePW.api")
    public ResponseEntity<?> changePW(@RequestBody MemberDTO memberDTO){
        String memberUserID = memberDTO.getMemberUserID();
        String memberUserPW = memberDTO.getMemberUserPW();
        String memberUserChangePW = memberDTO.getMemberUserChangePW();

        log.info("changePW.api changePW memberUserID,memberUserPW,memberUserChangePW : [{},{},{}]",memberUserID, memberUserPW, memberUserChangePW);

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

}
