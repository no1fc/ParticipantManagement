package com.jobmoa.app.CounselMain.view.management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 지점 사용자 관리 페이지 컨트롤러.
 * <p>지점 소속 사용자(상담사) 관리 페이지로의 이동을 처리한다.</p>
 */
@Slf4j
@Controller
public class UserManagementController {

    /**
     * 지점 사용자 관리 페이지를 표시한다.
     * @return 지점 사용자 관리 JSP 뷰 이름 (views/BranchUserManagement)
     */
    @GetMapping("/UserManagement.login")
    public String BranchUserManagement(){

        return "views/BranchUserManagement";
    }
}
