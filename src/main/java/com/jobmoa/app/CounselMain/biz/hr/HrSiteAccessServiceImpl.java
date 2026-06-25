package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrSiteAccessService} 구현체. {@code J_직원_사이트접속} CRUD를 처리한다.
 * <p>{@code 사이트내권한=NULL ⇒ J_직원_재직.권한 상속} 규약을 따른다(빈 값은 NULL로 저장).</p>
 */
@Slf4j
@Service
public class HrSiteAccessServiceImpl implements HrSiteAccessService {

    @Autowired
    private HrSiteAccessDAO hrSiteAccessDAO;

    @Override
    public List<HrSiteAccessDTO> getSiteAccessList(HrSiteAccessDTO dto) {
        return hrSiteAccessDAO.selectSiteAccessList(dto);
    }

    @Override
    public HrSiteAccessDTO getSiteAccessOne(HrSiteAccessDTO dto) {
        return hrSiteAccessDAO.selectSiteAccessOne(dto);
    }

    @Override
    public boolean isSiteAccessExists(HrSiteAccessDTO dto) {
        return hrSiteAccessDAO.selectSiteAccessExists(dto) > 0;
    }

    @Override
    public boolean addSiteAccess(HrSiteAccessDTO dto) {
        normalizeBlanks(dto);
        return hrSiteAccessDAO.insertSiteAccess(dto);
    }

    @Override
    public boolean modifySiteAccess(HrSiteAccessDTO dto) {
        normalizeBlanks(dto);
        return hrSiteAccessDAO.updateSiteAccess(dto);
    }

    @Override
    public boolean removeSiteAccess(HrSiteAccessDTO dto) {
        return hrSiteAccessDAO.deleteSiteAccess(dto);
    }

    @Override
    public List<HrSiteAccessDTO> getSiteList() {
        return hrSiteAccessDAO.selectSiteList();
    }

    /** 빈 문자열 부서코드·사이트내권한을 NULL(기본행/상속)로 정규화한다. */
    private void normalizeBlanks(HrSiteAccessDTO dto) {
        if (dto.getDeptCode() != null && dto.getDeptCode().isBlank()) dto.setDeptCode(null);
        if (dto.getSiteRole() != null && dto.getSiteRole().isBlank()) dto.setSiteRole(null);
        if (dto.getAccessAllowed() == null) dto.setAccessAllowed(Boolean.TRUE);
    }
}
