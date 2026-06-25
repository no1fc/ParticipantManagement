package com.jobmoa.app.CounselMain.biz.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link HrDepartmentService} 구현체. {@code J_부서} CRUD(소프트삭제)를 처리한다.
 */
@Slf4j
@Service
public class HrDepartmentServiceImpl implements HrDepartmentService {

    @Autowired
    private HrDepartmentDAO hrDepartmentDAO;

    @Override
    public List<HrDepartmentDTO> getDepartmentList(HrDepartmentDTO dto) {
        return hrDepartmentDAO.selectDepartmentList(dto);
    }

    @Override
    public HrDepartmentDTO getDepartmentOne(HrDepartmentDTO dto) {
        return hrDepartmentDAO.selectDepartmentOne(dto);
    }

    @Override
    public boolean isDeptCodeExists(HrDepartmentDTO dto) {
        return hrDepartmentDAO.selectDeptCodeExists(dto) > 0;
    }

    @Override
    public boolean addDepartment(HrDepartmentDTO dto) {
        return hrDepartmentDAO.insertDepartment(dto);
    }

    @Override
    public boolean modifyDepartment(HrDepartmentDTO dto) {
        return hrDepartmentDAO.updateDepartment(dto);
    }

    @Override
    public boolean removeDepartment(HrDepartmentDTO dto) {
        return hrDepartmentDAO.softDeleteDepartment(dto);
    }
}
