package com.jobmoa.app.jobPlacement.view.async;

import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementDTO;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementService;
import com.jobmoa.app.jobPlacement.view.webSocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class JobPlacementResumeEmailRequestAsync {

    @Autowired
    private JobPlacementService jobPlacementService;

    @Autowired
    private WebSocketService webSocketService;

    @PostMapping(value = "/jobPlacement/resumeRequest", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> resumeRequestAsync(@RequestBody JobPlacementDTO jobPlacementDTO) {
        log.info("JobPlacementResumeEmailRequestAsync resumeRequestAsync Start");
        log.info("JobPlacementResumeEmailRequestAsync resumeRequestAsync jobPlacementDTO : [{}]",jobPlacementDTO);
        jobPlacementDTO.setCondition("resumeRequestInsertAsync");

        String responseJson = "{\"statusData\":\"%s\",\"message\":\"%s\"}";

        try{
            boolean flag = jobPlacementService.insert(jobPlacementDTO);
            if(!flag) {
                responseJson = String.format(responseJson,"error","이력서 요청중 오류가 발생했습니다.");
                throw new SQLException(responseJson);
            }

            // 이력서 요청 성공 시 담당 상담사에게 WebSocket 알림 전송
            sendResumeRequestNotification(jobPlacementDTO);

            responseJson = String.format(responseJson,"success","이력서 요청이 완료되었습니다. 상담사 확인 후 메일 발송되실겁니다.");
            log.info("JobPlacementResumeEmailRequestAsync resumeRequestAsync responseJson : [{}]",responseJson);
            log.info("JobPlacementResumeEmailRequestAsync resumeRequestAsync End");
            return ResponseEntity.status(200).body(responseJson);
        }
        catch(SQLException e){
            log.error("JobPlacementResumeEmailRequestAsync resumeRequestAsync SQLException Error : {}",e.getMessage());
            responseJson = String.format(responseJson,"error","이력서 요청 실패. 서버 문제로 이력서 요청에 실패했습니다.");
            return ResponseEntity.status(400).body(responseJson);
        }
        catch(Exception e){
            log.error("JobPlacementResumeEmailRequestAsync resumeRequestAsync Exception Error : {}",e.getMessage());
            responseJson = String.format(responseJson,"error","이력서 요청 실패. 서버 요청에 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(responseJson);
        }
    }

    /**
     * 이력서 요청 완료 후 담당 상담사에게 WebSocket 알림 전송
     */
    private void sendResumeRequestNotification(JobPlacementDTO jobPlacementDTO) {
        try {
            // 담당 상담사 조회
            JobPlacementDTO param = new JobPlacementDTO();
            param.setJobNumber(jobPlacementDTO.getJobNumber());
            param.setCondition("selectCounselorByJobNumber");
            JobPlacementDTO counselorInfo = jobPlacementService.selectOne(param);

            if (counselorInfo == null || counselorInfo.getCounselorId() == null) {
                log.warn("[WebSocket] 이력서 요청 알림 전송 실패: 담당 상담사 정보 없음 (구직번호={})", jobPlacementDTO.getJobNumber());
                return;
            }

            Map<String, Object> notification = new HashMap<>();
            notification.put("jobSeekerNo", jobPlacementDTO.getJobNumber());
            notification.put("participantName", counselorInfo.getParticipant());
            notification.put("companyName", jobPlacementDTO.getCompanyName());
            notification.put("message", counselorInfo.getParticipant() + "님의 이력서를 " + jobPlacementDTO.getCompanyName() + "에서 요청했습니다.");

            String destination = "/topic/resume-request/" + counselorInfo.getCounselorId();
            webSocketService.sendObject(destination, notification);
            log.info("[WebSocket] 이력서 요청 알림 전송 counselor={}, jobNumber={}", counselorInfo.getCounselorId(), jobPlacementDTO.getJobNumber());
        } catch (Exception e) {
            log.warn("[WebSocket] 이력서 요청 알림 전송 실패: {}", e.getMessage());
        }
    }
}
