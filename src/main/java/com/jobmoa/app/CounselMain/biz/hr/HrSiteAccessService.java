package com.jobmoa.app.CounselMain.biz.hr;

import java.util.List;

/**
 * HR - 사이트 접속·권한 관리 서비스 인터페이스.
 */
public interface HrSiteAccessService {

    List<HrSiteAccessDTO> getSiteAccessList(HrSiteAccessDTO dto);

    HrSiteAccessDTO getSiteAccessOne(HrSiteAccessDTO dto);

    /** UNIQUE(직원,사이트,부서) 충돌 여부. */
    boolean isSiteAccessExists(HrSiteAccessDTO dto);

    boolean addSiteAccess(HrSiteAccessDTO dto);

    boolean modifySiteAccess(HrSiteAccessDTO dto);

    boolean removeSiteAccess(HrSiteAccessDTO dto);

    /** 사이트 드롭다운 목록. */
    List<HrSiteAccessDTO> getSiteList();
}
