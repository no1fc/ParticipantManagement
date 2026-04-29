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

@Slf4j
@RestController
public class ParticipantsTransferAjax {

    @Autowired
    private ParticipantServiceImpl participantService;

    @Autowired
    private ChangeJson changeJson;  // 컴포넌트로 주입받기

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

    public record ParticipantResponse(
            int jobno,
            String userName,
            String particName,
            String dob,
            String gender
    ) {}


    @PostMapping("/transferPostAjax.login")
    public boolean ParticipantsTransferUpdate(@RequestBody ParticipantDTO participantDTO){
        log.info("transferPostAjax.login : [{}]",participantDTO.getParticipantIDs());
        participantDTO.setParticipantCondition("transferUpdate");
        boolean flag = this.participantService.update(participantDTO);
        return flag;
    }
}
