package com.jobmoa.app.CounselMain.biz.hr;

import lombok.Data;

/**
 * HR(입퇴사자관리) - 사이트 접속·권한 관리 DTO.
 * <p>대상 테이블: {@code J_직원_사이트접속} (중앙 프로비저닝), 이름·사이트명은 {@code J_직원}·{@code J_사이트} 조인.</p>
 */
@Data
public class HrSiteAccessDTO {

    /** 접속PK (PK, IDENTITY) */
    private Integer accessPk;
    /** 직원아이디 (FK→J_직원.아이디) */
    private String userId;
    /** 이름 (J_직원 조인) */
    private String userName;
    /** 사이트코드 (FK→J_사이트.사이트코드) */
    private String siteCode;
    /** 사이트명 (J_사이트 조인) */
    private String siteName;
    /** 부서코드 (NULL=전 부서 공통 기본행, 값=겸직 부서별) */
    private String deptCode;
    /** 접속허용 (bit) */
    private Boolean accessAllowed;
    /** 사이트내권한 (NULL ⇒ J_직원_재직.권한 상속) */
    private String siteRole;
    /** 부여일 */
    private String grantDate;

    // ===== 검색 조건 =====
    private String searchUserId;
    private String searchSiteCode;
}
