package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrTenurePolicyService} 구현체. {@code J_근속산정정책} CRUD(소프트삭제)를 처리한다.
 */
@Slf4j
@Service
public class HrTenurePolicyServiceImpl implements HrTenurePolicyService {

    @Autowired
    private HrTenurePolicyDAO hrTenurePolicyDAO;

    @Override
    public List<HrTenurePolicyDTO> getTenurePolicyList(HrTenurePolicyDTO dto) {
        return hrTenurePolicyDAO.selectTenurePolicyList(dto);
    }

    @Override
    public HrTenurePolicyDTO getTenurePolicyOne(HrTenurePolicyDTO dto) {
        return hrTenurePolicyDAO.selectTenurePolicyOne(dto);
    }

    @Override
    public boolean isPolicyKeyExists(HrTenurePolicyDTO dto) {
        return hrTenurePolicyDAO.selectPolicyKeyExists(dto) > 0;
    }

    @Override
    public boolean addTenurePolicy(HrTenurePolicyDTO dto) {
        return hrTenurePolicyDAO.insertTenurePolicy(dto);
    }

    @Override
    public boolean modifyTenurePolicy(HrTenurePolicyDTO dto) {
        return hrTenurePolicyDAO.updateTenurePolicy(dto);
    }

    @Override
    public boolean removeTenurePolicy(HrTenurePolicyDTO dto) {
        return hrTenurePolicyDAO.softDeleteTenurePolicy(dto);
    }
}
