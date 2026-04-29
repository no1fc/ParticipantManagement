package com.jobmoa.app.CounselMain.biz.participantEmployment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("employmentService")
public class EmploymentServiceImpl implements EmploymentService {

    @Autowired
    private EmploymentDAO employmentDAO;

    @Override
    public boolean insert(EmploymentDTO employmentDTO) {
//        log.info("employment insert data : [{}]",employmentDTO);
        log.info("EmploymentServiceImpl insert Start");
        log.info("EmploymentServiceImpl insert End");
        return employmentDAO.insert(employmentDTO);
    }

    @Override
    public boolean update(EmploymentDTO employmentDTO) {
//        log.info("employment update data : [{}]", employmentDTO);
        log.info("EmploymentServiceImpl update Start");
        log.info("EmploymentServiceImpl update End");
        return employmentDAO.update(employmentDTO);
    }

    @Override
    public boolean delete(EmploymentDTO employmentDTO) {
        return false;
    }

    @Override
    public EmploymentDTO selectOne(EmploymentDTO employmentDTO) {
//        log.info("employment selectOne data : [{}]",employmentDTO);
        log.info("EmploymentServiceImpl selectOne Start");
        EmploymentDTO data = null;
        //DTO 가 null이 아니고 condition 이 null 이 아니면 selecone 함수 실행
        if(employmentDTO != null && employmentDTO.getEmploymentCondition() != null) {
            log.info("EmploymentServiceImpl EmploymentDTO Not null Start SelectOne");
            data = employmentDAO.selectOne(employmentDTO);
            log.info("EmploymentServiceImpl EmploymentDTO Not null End SelectOne");
        }
//        log.info("employment selectOne data : [{}]",data);
        log.info("EmploymentServiceImpl selectOne End");
        return data;
    }

    @Override
    public List<EmploymentDTO> selectAll(EmploymentDTO employmentDTO) {
        return null;
    }
}
