package com.jobmoa.app.CounselMain.biz.login;

import java.util.List;
import java.util.Map;

public interface MemberService {
    MemberDTO selectOne(MemberDTO loginDTO);
    List<MemberDTO> selectAll(MemberDTO loginDTO);
    boolean insert(MemberDTO loginDTO);
    int selectCount(MemberDTO loginDTO);
    List<Map<String, Object>> selectActiveBranchList();
    boolean update(MemberDTO loginDTO);
    boolean delete(MemberDTO loginDTO);
}
