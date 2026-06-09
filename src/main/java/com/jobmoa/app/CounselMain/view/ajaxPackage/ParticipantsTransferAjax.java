package com.jobmoa.app.CounselMain.view.ajaxPackage;

import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 참여자 이관(전담자 변경)을 위한 비동기 REST 컨트롤러.
 * <p>이관 대상 참여자 목록 조회와 이관 처리 기능을 제공한다.</p>
 */
@Slf4j
@RestController
public class ParticipantsTransferAjax {

    @Autowired
    private ParticipantServiceImpl participantService;

    @Autowired
    private ChangeJson changeJson;  // 컴포넌트로 주입받기

    /**
     * 이관 대상 참여자 목록을 조회한다.
     *
     * @param participantDTO 검색 조건 DTO
     * @return 이관 대상 참여자 응답 목록 (구직번호, 이름, 참여형태, 생년월일, 성별)
     */
    // Java
    @GetMapping(value = "/transferGetAjax.login", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParticipantResponse> participantsTransfer(ParticipantDTO participantDTO) {
        participantDTO.setParticipantCondition("transferSelect");
        List<ParticipantDTO> list = participantService.selectAll(participantDTO);
        return list.stream().map(dto -> new ParticipantResponse(
                dto.getParticipantJobNo(),
                dto.getParticipantUserName(),
                dto.getParticipantPartic(),
                dto.getParticipantDob() == null ? "" : dto.getParticipantDob(),
                dto.getParticipantGender()
        )).toList();
    }

    /**
     * 이관 대상 참여자 응답 레코드.
     *
     * @param jobno     구직번호
     * @param userName  참여자명
     * @param particName 참여형태명
     * @param dob       생년월일
     * @param gender    성별
     */
    public record ParticipantResponse(
            int jobno,
            String userName,
            String particName,
            String dob,
            String gender
    ) {}


    /**
     * 참여자의 전담자를 변경(이관)한다.
     *
     * @param participantDTO 이관 대상 참여자 아이디 목록이 담긴 DTO
     * @return 이관 성공 여부
     */
    @PostMapping("/transferPostAjax.login")
    public boolean ParticipantsTransferUpdate(@RequestBody ParticipantDTO participantDTO){
        log.info("transferPostAjax.login : [{}]",participantDTO.getParticipantIDs());
        participantDTO.setParticipantCondition("transferUpdate");
        boolean flag = this.participantService.update(participantDTO);
        return flag;
    }
}
