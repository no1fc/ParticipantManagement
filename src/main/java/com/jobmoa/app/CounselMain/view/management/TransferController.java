package com.jobmoa.app.CounselMain.view.management;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberServiceImpl;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class TransferController {

    @Autowired
    private MemberServiceImpl memberService;

    @GetMapping("/transfer.login")
    public String transfer(Model model, HttpSession session, MemberDTO memberDTO){
        log.info("-------------> Start Transfer.login");
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        boolean branchRole = (boolean)session.getAttribute("IS_BRANCH_MANAGER");
        String url = "index.jsp";
        String icon = "error";
        String title = "접근 권한이 없습니다.";
        String message = "권한 확인 후 접속 부탁드립니다.";
        String branch = loginBean.getMemberBranch();
        log.info("branch : [{}]", branch);

        log.info("branchRole : [{}]", branchRole);
        if(!branchRole){
            InfoBean.info(model,url,icon,title,message);
            return "views/info";
        }

        memberDTO.setMemberCondition("branchUserID");
        memberDTO.setMemberBranch(branch);
        List<MemberDTO> userData = memberService.selectAll(memberDTO);

        if(userData == null){
            url = "branchParitic.login";
            icon = "error";
            title = "전담자 확인이 불가능합니다.";
            message = "";
            InfoBean.info(model,url,icon,title,message);
            return "views/info";
        }

        log.info("counselors : [{}]", userData);
        model.addAttribute("counselors", userData);
        log.info("-------------> End Transfer.login");
        return "views/transferPage";
    }
}
