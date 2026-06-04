package com.jobmoa.app.CounselMain.biz.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 대시보드 데이터 접근 객체.
 * <p>참여자 현황, 상담 통계 등 대시보드 화면에 필요한 데이터 조회를 담당한다.
 * DTO 내 조건(condition) 값에 따라 동적으로 MyBatis 매퍼 ID가 결정된다.</p>
 * <p>MyBatis 매퍼 네임스페이스 "DashboardDAO." 를 사용한다.</p>
 */
@Slf4j
@Repository
public class DashboardDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "DashboardDAO.";

    /**
     * 조건에 맞는 대시보드 데이터 목록을 조회한다.
     * <p>날짜 파라미터가 null인 경우 MSSQL 데이터 형식 호환성 오류가 발생할 수 있다.</p>
     *
     * @param dashboardDTO dashboardCondition(조회 조건) 및 날짜 범위가 설정된 DTO
     * @return 대시보드 데이터 목록
     */
    public List<DashboardDTO> selectAll(DashboardDTO dashboardDTO) {
        log.info("DashboardDTO DashboardDAO selectAll condition : [{}]", dashboardDTO.getDashboardCondition());

        // 데이터 형식 호환성 오류 방지를 위해 날짜 파라미터가 비어있는지 확인 로깅 추가
        if (dashboardDTO.getDashBoardStartDate() == null || dashboardDTO.getDashBoardEndDate() == null) {
            log.warn("Warning: Date parameters in DashboardDTO are null. This might cause SQLServerException (date vs varbinary).");
        }

        List<DashboardDTO> datas = sqlSession.selectList(ns+ dashboardDTO.getDashboardCondition(), dashboardDTO);
        return datas;
    }

    /**
     * 조건에 맞는 대시보드 데이터 단건을 조회한다.
     *
     * @param dashboardDTO dashboardCondition(조회 조건)이 설정된 DTO
     * @return 대시보드 단건 데이터, 없으면 null
     */
    public DashboardDTO selectOne(DashboardDTO dashboardDTO) {
        log.info("DashboardDTO DashboardDAO selectOne condition : [{}]", dashboardDTO.getDashboardCondition());
        DashboardDTO data = sqlSession.selectOne(ns+ dashboardDTO.getDashboardCondition(), dashboardDTO);

        return data;
    }

    /**
     * 대시보드 데이터를 등록한다. (미구현)
     *
     * @param dashboardDTO 등록할 대시보드 정보
     * @return 항상 false (미구현)
     */
    public boolean insert(DashboardDTO dashboardDTO) {
        return false;
    }

    /**
     * 대시보드 데이터를 수정한다. (미구현)
     *
     * @param dashboardDTO 수정할 대시보드 정보
     * @return 항상 false (미구현)
     */
    public boolean update(DashboardDTO dashboardDTO) {
        return false;
    }

    /**
     * 대시보드 데이터를 삭제한다. (미구현)
     *
     * @param dashboardDTO 삭제할 대시보드 정보
     * @return 항상 false (미구현)
     */
    public boolean delete(DashboardDTO dashboardDTO) {
        return false;
    }


}
