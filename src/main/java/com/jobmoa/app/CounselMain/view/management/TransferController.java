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

/**
 * 참여자 전담자 이관 페이지 컨트롤러.
 * <p>지점 관리자 이상의 권한을 가진 사용자만 접근 가능하며,
 * 해당 지점의 상담사 목록을 조회하여 참여자 이관 페이지에 전달한다.</p>
 */
@Slf4j
@Controller
public class TransferController {

    @Autowired
    private MemberServiceImpl memberService;

    /**
     * 참여자 전담자 이관 페이지를 표시한다.
     * <p>지점 관리자 권한이 없으면 접근 권한 오류 페이지로 이동한다.
     * 권한이 있으면 해당 지점의 상담사 목록을 조회하여 전달한다.</p>
     * @param model Spring MVC Model
     * @param session HTTP 세션 (로그인 및 권한 정보 확인용)
     * @param memberDTO 상담사 조회용 DTO
     * @return 이관 페이지(views/transferPage) 또는 알림 페이지(views/info)
     */
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
