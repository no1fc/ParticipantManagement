package com.jobmoa.app.CounselMain.view.management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserManagementController {

    @GetMapping("/UserManagement.login")
    public String BranchUserManagement(){

        return "views/BranchUserManagement";
    }
}
