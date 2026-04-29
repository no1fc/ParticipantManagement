package com.jobmoa.app.jobPlacement.biz.jobPlacement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("jobPlacementService")
public class JobPlacementServiceImpl implements JobPlacementService{
    @Autowired
    private JobPlacementDAO jobPlacementDAO;

    @Override
    public JobPlacementDTO selectOne(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        JobPlacementDTO data = jobPlacementDAO.selectOne(jobPlacementDTO);
        if ("selectExternalDetail".equals(condition) && data != null && data.getJobNumber() != null) {
            JobPlacementDTO param = new JobPlacementDTO();
            param.setJobNumber(data.getJobNumber());
            param.setCondition("selectDesiredJobList");
            List<JobPlacementDTO> jobList = jobPlacementDAO.selectDesiredJobList(param);
            data.setDesiredJobList(jobList);
        }
        return data;
    }

    @Override
    public List<JobPlacementDTO> selectAll(JobPlacementDTO jobPlacementDTO) {
        String condition = jobPlacementDTO.getCondition();
        List<JobPlacementDTO> list = jobPlacementDAO.selectAll(jobPlacementDTO);
        if ("selectExternalAll".equals(condition) || "selectStarbucksAll".equals(condition)) {
            for (JobPlacementDTO dto : list) {
                JobPlacementDTO param = new JobPlacementDTO();
                param.setJobNumber(dto.getJobNumber());
                param.setCondition("selectDesiredJobList");
                List<JobPlacementDTO> jobList = jobPlacementDAO.selectDesiredJobList(param);
                dto.setDesiredJobList(jobList);
            }
        }
        return list;
    }

    @Override
    public boolean insert(JobPlacementDTO jobPlacementDTO) {
        return jobPlacementDAO.insert(jobPlacementDTO);
    }

    @Override
    public boolean update(JobPlacementDTO jobPlacementDTO) {

        String condition = jobPlacementDTO.getCondition();
        log.info("JobPlacementDTO update condition : [{}]",condition);

        if(condition == null) {
            return false;
        }

        //자격증 배열
        String[] certificates = jobPlacementDTO.getCertificates();
        //알선 상세정보
        String placementDetail = jobPlacementDTO.getPlacementDetail();
        //구직번호
        String jobNumber = jobPlacementDTO.getJobNumber();
        log.info("JobPlacementDTO update certificates : [{}]", (Object) certificates);
        log.info("JobPlacementDTO update placementDetail : [{}]",placementDetail);


        boolean certificateFlag = false;

        // 자격증 배열이 비어 있지 않다면
        if(certificates != null && certificates.length > 0){
            //자격증 삭제를 진행
            jobPlacementDTO.setCondition("certificateDelete");
            jobPlacementDAO.delete(jobPlacementDTO);
            //자격증 추가
            jobPlacementDTO.setCondition("certificateInsert");
            certificateFlag = jobPlacementDAO.insert(jobPlacementDTO) ;
//            log.info("JobPlacementDTO delete success certificateFlag : [{}]",certificateFlag);
        }
        else{
            certificateFlag = jobPlacementDAO.delete(jobPlacementDTO);
        }

        //알선 상세 정보 수정
        if (!placementDetail.trim().isEmpty()) {
            jobPlacementDTO.setCondition("selectPlacementDetail");
            JobPlacementDTO data = jobPlacementDAO.selectOne(jobPlacementDTO);
            if(data == null) {
                jobPlacementDTO.setCondition("insertPlacementDetail");
                certificateFlag = certificateFlag && jobPlacementDAO.insert(jobPlacementDTO);
//                log.info("JobPlacementDTO insert success certificateFlag : [{}]",certificateFlag);
            }
            else{
                data.setCondition("updatePlacementDetail");
                data.setPlacementDetail(placementDetail);
                data.setJobNumber(jobNumber);
                certificateFlag = certificateFlag && jobPlacementDAO.update(data);
//                log.info("JobPlacementDTO update success certificateFlag : [{}]",certificateFlag);
            }
        }
        else{
            jobPlacementDTO.setCondition("deletePlacementDetail");
            certificateFlag = certificateFlag && jobPlacementDAO.delete(jobPlacementDTO);
//            log.info("JobPlacementDTO delete success certificateFlag : [{}]",certificateFlag);
        }

        if(condition.equals("updateJobPlacementAsync")) {
            jobPlacementDTO.setCondition("updateJobPlacementAsync");
            certificateFlag = certificateFlag && jobPlacementDAO.update(jobPlacementDTO);

            // 희망직무 DELETE + INSERT
            List<JobPlacementDTO> desiredJobList = jobPlacementDTO.getDesiredJobList();
            if (desiredJobList != null) {
                jobPlacementDTO.setCondition("desiredJobDelete");
                jobPlacementDAO.delete(jobPlacementDTO);
                if (!desiredJobList.isEmpty()) {
                    jobPlacementDTO.setCondition("desiredJobInsert");
                    certificateFlag = certificateFlag && jobPlacementDAO.insert(jobPlacementDTO);
                }
            }
        }
        else{
            certificateFlag = false;
        }

        log.info("JobPlacementDTO certificateFlag : [{}]",certificateFlag);

        if(!certificateFlag) {
            //삭제 및 저장이 되지 않았다면 오류를 반환하여 트랜젝션을 실행시킨다.
            throw new RuntimeException("Failed to delete certificate data");
        }

        return true;
    }

    @Override
    public boolean delete(JobPlacementDTO jobPlacementDTO) {
        return jobPlacementDAO.delete(jobPlacementDTO);
    }
}
