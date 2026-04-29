package com.jobmoa.app.CounselMain.biz.login;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class MemberDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "MemberDAO.";

    public List<MemberDTO> selectAll(MemberDTO memberDTO) {
        log.info("MemberCondition selectAll: [{}]",memberDTO.getMemberCondition());
//        log.info("memberDTO : [{}]",memberDTO);
        List<MemberDTO> datas = sqlSession.selectList(ns+memberDTO.getMemberCondition(), memberDTO);
        if(datas == null) datas = null;
//        log.info("datas : [{}]",datas);
//        return null;
        return datas;
    }

    public MemberDTO selectOne(MemberDTO memberDTO) {
        log.info("MemberCondition selectOne: [{}]",memberDTO.getMemberCondition());
        MemberDTO data = sqlSession.selectOne(ns+memberDTO.getMemberCondition(), memberDTO);
//        log.info("Login data : [{}]",data);
        return data;
    }


    public boolean insert(MemberDTO memberDTO) {
        return false;
    }

    public boolean update(MemberDTO memberDTO) {
        log.info("MemberCondition update: [{}]",memberDTO.getMemberCondition());
        boolean flag = sqlSession.update(ns+memberDTO.getMemberCondition(), memberDTO) > 0;
        return flag;
    }

    public boolean delete(MemberDTO memberDTO) {
        return false;
    }

}
