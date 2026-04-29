package com.jobmoa.app.CounselMain.biz.login;

import java.util.List;

public interface MemberService {
    MemberDTO selectOne(MemberDTO loginDTO);
    List<MemberDTO> selectAll(MemberDTO loginDTO);
    boolean insert(MemberDTO loginDTO);
    boolean update(MemberDTO loginDTO);
    boolean delete(MemberDTO loginDTO);
}
