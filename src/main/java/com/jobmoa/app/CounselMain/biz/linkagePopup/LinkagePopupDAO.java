package com.jobmoa.app.CounselMain.biz.linkagePopup;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 국취 연계실적 독려 팝업 데이터 접근 객체.
 * <p>설정 조회 + 연계 건수/인원 집계를 담당한다.
 * MyBatis 매퍼 네임스페이스 "LinkagePopupDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class LinkagePopupDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "LinkagePopupDAO.";

    /**
     * 팝업 설정(단일 행)을 조회한다.
     *
     * @return 설정 DTO, 없으면 null
     */
    public LinkagePopupSettingDTO selectSetting() {
        return sqlSession.selectOne(ns + "selectSetting");
    }

    /**
     * 본인(전담자) 연계 건수를 조회한다.
     *
     * @param account   전담자 계정(아이디)
     * @param startDate 집계 시작일 (yyyy-MM-dd)
     * @param endDate   집계 종료일 (yyyy-MM-dd)
     * @return 연계 건수
     */
    public int selectMyLinkageCount(String account, String startDate, String endDate) {
        Integer c = sqlSession.selectOne(ns + "selectMyLinkageCount",
                Map.of("account", account, "startDate", startDate, "endDate", endDate));
        return c == null ? 0 : c;
    }

    /**
     * 전체 국취 모집단의 연계 합계를 조회한다(테스트 지점 제외).
     *
     * @param startDate 집계 시작일
     * @param endDate   집계 종료일
     * @return 연계 합계
     */
    public int selectTotalLinkageCount(String startDate, String endDate) {
        Integer c = sqlSession.selectOne(ns + "selectTotalLinkageCount",
                Map.of("startDate", startDate, "endDate", endDate));
        return c == null ? 0 : c;
    }

    /**
     * 전체 국취 인원(평균 분모 N)을 조회한다.
     *
     * @return 활성 국취 인원 수
     */
    public int selectTotalStaffCount() {
        Integer c = sqlSession.selectOne(ns + "selectTotalStaffCount");
        return c == null ? 0 : c;
    }

    /**
     * 본인 지점의 국취 인원(목표 분모)을 조회한다.
     *
     * @param branch 지점명
     * @return 해당 지점 활성 국취 인원 수
     */
    public int selectBranchStaffCount(String branch) {
        Integer c = sqlSession.selectOne(ns + "selectBranchStaffCount", Map.of("branch", branch));
        return c == null ? 0 : c;
    }
}
