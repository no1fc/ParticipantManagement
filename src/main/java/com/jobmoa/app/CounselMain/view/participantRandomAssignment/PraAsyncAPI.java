package com.jobmoa.app.CounselMain.view.participantRandomAssignment;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.participantRandomAssignment.ParticipantRandomAssignmentDTO;
import com.jobmoa.app.CounselMain.biz.participantRandomAssignment.ParticipantRandomAssignmentService;
import com.jobmoa.app.CounselMain.biz.participantRandomAssignment.PraHistoryRequestDTO;
import com.jobmoa.app.CounselMain.biz.participantRandomAssignment.PraHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PraAsyncAPI {

    @Autowired
    private ParticipantRandomAssignmentService praService;

    @Autowired
    private PraHistoryService praHistoryService;

    @PostMapping("/api/pra.login")
    public ResponseEntity<?> praInsertAPI(@RequestBody List<ParticipantRandomAssignmentDTO> asyncList, HttpSession session){
        //입력 데이터 확인
         log.info("praInsertAPI : [{}]",asyncList);
        try{
            LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
            String branch = loginBean.getMemberBranch();
            boolean flag = false;
            //정보 업데이트
            for(ParticipantRandomAssignmentDTO dto : asyncList){
                dto.setCondition("praInsert");
                dto.setBranch(branch);
                dto.setCareer(dto.getHasCareer());
                flag = praService.insert(dto);
            }

            return ResponseEntity.ok(flag);
        }
        catch (Exception e){
            log.error("praInsertAPI Exception : [{}]",e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/api/pra.history")
    public ResponseEntity<?> praHistoryAPI(@RequestBody PraHistoryRequestDTO request, HttpSession session) {
        log.info("praHistoryAPI : [{}]", request);
        try {
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            String branch = loginBean.getMemberBranch();
            String writerId = loginBean.getMemberUserID();

            boolean flag = praHistoryService.insertHistory(request, branch, writerId);
            return ResponseEntity.ok(flag);
        } catch (Exception e) {
            log.error("praHistoryAPI Exception : [{}]", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
