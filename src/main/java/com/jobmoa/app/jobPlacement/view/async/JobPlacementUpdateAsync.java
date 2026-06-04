package com.jobmoa.app.jobPlacement.view.async;

import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifDTO;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementDTO;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 알선 정보 비동기 업데이트 REST API 컨트롤러.
 * 참여자의 알선 관련 정보(자격증 포함)를 비동기 방식으로 업데이트한다.
 */
@Slf4j
@RestController
public class JobPlacementUpdateAsync {

    @Autowired
    JobPlacementService jobPlacementService;

    /**
     * 참여자의 알선 정보를 비동기적으로 업데이트한다.
     * 자격증 데이터가 포함된 경우 특수문자를 구분자로 분리하여 개별 자격증으로 저장한다.
     *
     * @param jobPlacementDTO 업데이트할 알선 데이터 (자격증 문자열 포함 가능)
     * @return 업데이트 성공 여부 (true/false)
     */
    @PostMapping("/jobPlacement/jobPlacementAsync")
    public ResponseEntity<Boolean> updateJobPlacement(@RequestBody JobPlacementDTO jobPlacementDTO) {
        ResponseEntity<Boolean> response = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        //특수문자 콤마 변화를 위한 정규식
        String regex = "[{}\\[\\]/?.,;:|)*~`!^\\-_+<>@#$%&\\\\=('\"]+";

        try{
            String certificate = jobPlacementDTO.getCertificate();

            //여러 자격증을 한번에 저장해야하기에 각 배열로 변환 후 값을 저장
            if(certificate != null) {
                //모든 특수문자를 기준으로 문자로 전달받은 여러 자격증을 배열로 변환한다.
                String[] certificates = certificate.split(regex);
                //빈문자 제거 후 배열 새로 저장
                certificates=Arrays.stream(certificates)
                        .filter(cert -> cert != null && !cert.trim().isEmpty())
                        .map(String::trim).toArray(String[]::new);
                jobPlacementDTO.setCertificates(certificates);
            }

            log.info("JobPlacementUpdateAsync updateJobPlacement Start");
            jobPlacementDTO.setCondition("updateJobPlacementAsync");
            boolean outputDataFlag = jobPlacementService.update(jobPlacementDTO);

            if(!outputDataFlag) {
                response = ResponseEntity.status(204).headers(headers).body(false);
                log.info("JobPlacementUpdateAsync updateJobPlacement Fail");
            }
            else {
                response = ResponseEntity.status(200).headers(headers).body(true);
                log.info("JobPlacementUpdateAsync updateJobPlacement Success");
            }

            log.info("JobPlacementUpdateAsync updateJobPlacement End");
            return response;
        }
        catch(Exception e){
            response = ResponseEntity.status(500).headers(headers).body(false);
            log.error("JobPlacementUpdateAsync updateJobPlacement Error : {}",e.getMessage());
            return response;
        }

    }
}
