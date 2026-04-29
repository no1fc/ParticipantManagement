package com.jobmoa.app.CounselMain.biz.dashboard;

import java.util.List;

public interface ArrangementService {
    List<ArrangementDTO> selectAll(ArrangementDTO arrangementDTO);
    ArrangementDTO selectOne(ArrangementDTO arrangementDTO);
    boolean insert(ArrangementDTO arrangementDTO);
    boolean update(ArrangementDTO arrangementDTO);
    boolean delete(ArrangementDTO arrangementDTO);
}
