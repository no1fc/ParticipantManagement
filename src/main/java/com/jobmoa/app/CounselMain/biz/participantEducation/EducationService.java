package com.jobmoa.app.CounselMain.biz.participantEducation;

import java.util.List;

public interface EducationService {
    EducationDTO selectOne(EducationDTO educationDTO);
    List<EducationDTO> selectAll(EducationDTO educationDTO);
    boolean insert(EducationDTO educationDTO);
    boolean update(EducationDTO educationDTO);
    boolean delete(EducationDTO educationDTO);
}
