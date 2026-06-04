package com.jobmoa.app.CounselMain.biz.participantEducation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * {@link EducationService} 구현체.
 * DAO를 통해 참여자 교육 이력의 조회, 등록, 수정, 삭제를 처리한다.
 */
@Slf4j
@Service
public class EducationServiceImpl implements EducationService {
    @Autowired
    private EducationDAO educationDAO;

    @Override
    public EducationDTO selectOne(EducationDTO educationDTO) {
        EducationDTO data = null;
        if(educationDTO != null || educationDTO.getEducationCondition() != null) {
            log.info("selectOne educationDTO Not Null Start EducationDAO Start");
            data = educationDAO.selectOne(educationDTO);
        }
        return data;
    }

    @Override
    public List<EducationDTO> selectAll(EducationDTO educationDTO) {
        List<EducationDTO> data = null;
        if(educationDTO != null || educationDTO.getEducationCondition() != null) {
            log.info("selectAll educationDTO Not Null Start EducationDAO Start");
            data = educationDAO.selectAll(educationDTO);
        }
        return data;
    }

    @Override
    public boolean insert(EducationDTO educationDTO) {
        log.info("EducationServiceImpl insert Start");
        if (educationDTO == null) {
            log.error("educationDTO is null");
            return false;
        }
        if (educationDTO.getEducationJobNo() <= 0) {
            log.error("Invalid getEducationJobNo: {}", educationDTO.getEducationJobNo());
            return false;
        }

        // 1) 요소 정제: null/빈 문자열 삭제
        String[] educations = educationDTO.getEducations();
        if (educations != null) {
            educations = Arrays.stream(educations)
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .map(String::trim)
                    .toArray(String[]::new);
            educationDTO.setEducations(educations);
        }

        // 2) 기존 데이터 삭제(업데이트 시 덮어쓰기 정책)
        educationDAO.delete(educationDTO);

        // 3) 배열이 비어있으면 새 삽입 생략 → INSERT ... VALUES 빈 쿼리 방지
        if (educationDTO.getEducations() == null || educationDTO.getEducations().length == 0) {
            log.info("No EducationServiceImpl to insert. Skipping insert.");
            log.info("EducationServiceImpl insert End");
            return true;
        }

        // 4) 정상 삽입
        boolean result = educationDAO.insert(educationDTO);
        log.info("EducationServiceImpl insert result: {}", result);
        log.info("EducationServiceImpl insert End");
        return result;
    }


    @Override
    public boolean update(EducationDTO educationDTO) {
        log.info("EducationDTO update Start");
        log.info("EducationDTO update End");
        return educationDAO.update(educationDTO);
    }

    @Override
    public boolean delete(EducationDTO educationDTO) {
        log.info("EducationDTO delete Start");
        log.info("EducationDTO delete End");
//        return educationDAO.delete(educationDTO);
        return false;
    }
}
