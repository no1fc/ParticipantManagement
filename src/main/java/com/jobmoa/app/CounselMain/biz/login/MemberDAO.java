package com.jobmoa.app.CounselMain.biz.login;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 회원(로그인) 데이터 접근 객체.
 * <p>회원 인증, 회원 정보 조회/등록/수정, 지점 목록 조회 등 로그인 관련 데이터를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "MemberDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class MemberDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "MemberDAO.";

    /**
     * 조건에 맞는 회원 목록을 조회한다.
     *
     * @param memberDTO memberCondition(조회 조건)이 설정된 DTO
     * @return 회원 목록
     */
    public List<MemberDTO> selectAll(MemberDTO memberDTO) {
        log.info("MemberCondition selectAll: [{}]",memberDTO.getMemberCondition());
//        log.info("memberDTO : [{}]",memberDTO);
        List<MemberDTO> datas = sqlSession.selectList(ns+memberDTO.getMemberCondition(), memberDTO);
        if(datas == null) datas = null;
//        log.info("datas : [{}]",datas);
//        return null;
        return datas;
    }

    /**
     * 조건에 맞는 회원 단건 정보를 조회한다.
     *
     * @param memberDTO memberCondition(조회 조건)이 설정된 DTO
     * @return 회원 단건 데이터, 없으면 null
     */
    public MemberDTO selectOne(MemberDTO memberDTO) {
        log.info("MemberCondition selectOne: [{}]",memberDTO.getMemberCondition());
        MemberDTO data = sqlSession.selectOne(ns+memberDTO.getMemberCondition(), memberDTO);
//        log.info("Login data : [{}]",data);
        return data;
    }


    /**
     * 신규 회원을 등록한다.
     *
     * @param memberDTO memberCondition(조건) 및 등록할 회원 정보가 설정된 DTO
     * @return 등록 성공 여부
     */
    public boolean insert(MemberDTO memberDTO) {
        log.info("MemberCondition insert: [{}]", memberDTO.getMemberCondition());
        return sqlSession.insert(ns + memberDTO.getMemberCondition(), memberDTO) > 0;
    }

    /**
     * 조건에 맞는 회원 수를 조회한다.
     *
     * @param memberDTO memberCondition(조회 조건)이 설정된 DTO
     * @return 조건에 해당하는 회원 수
     */
    public int selectCount(MemberDTO memberDTO) {
        log.info("MemberCondition selectCount: [{}]", memberDTO.getMemberCondition());
        return sqlSession.selectOne(ns + memberDTO.getMemberCondition(), memberDTO);
    }

    /**
     * 활성 상태의 지점 목록을 조회한다.
     *
     * @return 활성 지점 목록 (Map 형태)
     */
    public List<Map<String, Object>> selectActiveBranchList() {
        return sqlSession.selectList(ns + "selectActiveBranchList");
    }

    /**
     * 회원 정보를 수정한다.
     *
     * @param memberDTO memberCondition(조건) 및 수정할 회원 정보가 설정된 DTO
     * @return 수정 성공 여부
     */
    public boolean update(MemberDTO memberDTO) {
        log.info("MemberCondition update: [{}]",memberDTO.getMemberCondition());
        boolean flag = sqlSession.update(ns+memberDTO.getMemberCondition(), memberDTO) > 0;
        return flag;
    }

    /**
     * 회원을 삭제한다. (미구현)
     *
     * @param memberDTO 삭제할 회원 정보
     * @return 항상 false (미구현)
     */
    public boolean delete(MemberDTO memberDTO) {
        return false;
    }

}
