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

/**
 * 참여자 랜덤 배정 비동기 REST API 컨트롤러.
 * 참여자 배정 등록 및 배정 이력 저장 기능을 제공한다.
 */
@Slf4j
@RestController
public class PraAsyncAPI {

    @Autowired
    private ParticipantRandomAssignmentService praService;

    @Autowired
    private PraHistoryService praHistoryService;

    /**
     * 참여자 랜덤 배정 정보를 일괄 등록한다.
     * 로그인한 사용자의 지점 정보를 기반으로 각 배정 데이터를 저장한다.
     *
     * @param asyncList 배정할 참여자 목록
     * @param session   HTTP 세션 (로그인 정보 조회용)
     * @return 배정 등록 성공 여부 (true/false) 또는 오류 메시지
     */
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

    /**
     * 참여자 랜덤 배정 이력을 저장한다.
     * 배정 작업의 이력 정보를 기록하여 추적 가능하도록 한다.
     *
     * @param request 배정 이력 요청 데이터
     * @param session HTTP 세션 (로그인 정보 조회용)
     * @return 이력 저장 성공 여부 (true/false) 또는 오류 메시지
     */
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
