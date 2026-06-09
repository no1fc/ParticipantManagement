package com.jobmoa.app.CounselMain.biz.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * {@link MemberService} 구현체.
 * MemberDAO를 통해 회원 인증 및 관리를 처리한다.
 */
@Service("loginService")
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDAO loginDAO;

    @Override
    public MemberDTO selectOne(MemberDTO loginDTO) {
        return loginDAO.selectOne(loginDTO);
    }

    @Override
    public List<MemberDTO> selectAll(MemberDTO loginDTO) {
        return loginDAO.selectAll(loginDTO);
    }

    @Override
    public boolean insert(MemberDTO loginDTO) {
        return loginDAO.insert(loginDTO);
    }

    @Override
    public int selectCount(MemberDTO loginDTO) {
        return loginDAO.selectCount(loginDTO);
    }

    @Override
    public List<Map<String, Object>> selectActiveBranchList() {
        return loginDAO.selectActiveBranchList();
    }

    @Override
    public boolean update(MemberDTO loginDTO) {
        return loginDAO.update(loginDTO);
    }

    @Override
    public boolean delete(MemberDTO loginDTO) {
        return loginDAO.delete(loginDTO);
    }
}
