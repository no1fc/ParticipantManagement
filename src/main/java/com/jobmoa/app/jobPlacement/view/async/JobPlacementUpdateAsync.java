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

@Slf4j
@RestController
public class JobPlacementUpdateAsync {

    @Autowired
    JobPlacementService jobPlacementService;

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
