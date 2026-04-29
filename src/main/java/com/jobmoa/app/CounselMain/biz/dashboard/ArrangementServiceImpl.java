package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("Arrangement")
public class ArrangementServiceImpl implements ArrangementService {

    @Autowired
    private ArrangementDAO arrangementDAO;

    @Override
    public List<ArrangementDTO> selectAll(ArrangementDTO arrangementDTO) {
        return arrangementDAO.selectAll(arrangementDTO);
    }

    @Override
    public ArrangementDTO selectOne(ArrangementDTO arrangementDTO) {
        return arrangementDAO.selectOne(arrangementDTO);
    }

    @Override
    public boolean insert(ArrangementDTO arrangementDTO) {
        return false;
    }

    @Override
    public boolean update(ArrangementDTO arrangementDTO) {
        return false;
    }

    @Override
    public boolean delete(ArrangementDTO arrangementDTO) {
        return false;
    }
}
