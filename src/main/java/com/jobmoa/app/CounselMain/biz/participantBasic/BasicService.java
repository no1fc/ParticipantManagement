package com.jobmoa.app.CounselMain.biz.participantBasic;

import java.util.List;

public interface BasicService {
    boolean insert(BasicDTO basicDTO);
    boolean update(BasicDTO basicDTO);
    boolean delete(BasicDTO basicDTO);
    BasicDTO selectOne(BasicDTO basicDTO);
    List<BasicDTO> selectAll(BasicDTO basicDTO);
}
