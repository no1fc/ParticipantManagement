package com.jobmoa.app.CounselMain.biz.participantCounsel;

import java.util.List;

public interface CounselService {
    CounselDTO selectOne(CounselDTO counselDTO);
    List<CounselDTO> selectAll(CounselDTO counselDTO);
    boolean insert(CounselDTO counselDTO);
    boolean update(CounselDTO counselDTO);
    boolean delete(CounselDTO counselDTO);
}
