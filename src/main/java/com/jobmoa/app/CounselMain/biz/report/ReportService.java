package com.jobmoa.app.CounselMain.biz.report;

import java.util.List;

public interface ReportService {
    ReportDTO selectOne(ReportDTO reportDTO);
    List<ReportDTO> selectAll(ReportDTO reportDTO);
    boolean insert(ReportDTO reportDTO);
    boolean update(ReportDTO reportDTO);
    boolean delete(ReportDTO reportDTO);
}
