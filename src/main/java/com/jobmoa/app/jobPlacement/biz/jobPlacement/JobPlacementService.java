package com.jobmoa.app.jobPlacement.biz.jobPlacement;

import java.util.List;

public interface JobPlacementService {
    JobPlacementDTO selectOne(JobPlacementDTO jobPlacementDTO);
    List<JobPlacementDTO> selectAll(JobPlacementDTO jobPlacementDTO);
    boolean insert(JobPlacementDTO jobPlacementDTO);
    boolean update(JobPlacementDTO jobPlacementDTO);
    boolean delete(JobPlacementDTO jobPlacementDTO);
}