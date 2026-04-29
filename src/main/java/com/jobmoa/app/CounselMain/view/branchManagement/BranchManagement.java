package com.jobmoa.app.CounselMain.view.branchManagement;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class BranchManagement {

    @Autowired
    private ParticipantServiceImpl participantService;

    @GetMapping("/branchParitic.login")
    public String branchManagement(Model model, HttpSession session, ParticipantDTO participantDTO, PaginationBean paginationBean){
        log.info("-----------------------------------");
        log.info("Start participantController");

        //session 에 있는 로그인 데이터
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        //지점 관리자 권한 여부
        boolean isBranchAdmin = (boolean)session.getAttribute("IS_BRANCH_MANAGER");
        //관리자 권한 여부
        boolean isManager = (boolean)session.getAttribute("IS_MANAGER");

        //권한이 둘중 하나라도 false라면 dashboard 페이지로 전환
        if(!isBranchAdmin && !isManager){
            return "redirect:dashboard.login";
        }


        String loginId = loginBean.getMemberUserID();
        String loginBranch = loginBean.getMemberBranch();

        // -------------------------페이지네이션 시작------------------------------
        //참여자 정보 불러올때 사용하기 위해 전담자 아이디를 불러온다.
        participantDTO.setParticipantUserid(loginId);
        participantDTO.setParticipantBranch(loginBranch);
        log.info("Login ID : [{}]", loginId);

        //마감 여부가 없으면 기본 false로 진행
        String isClose = participantDTO.getEndDateOption() == null ? "false" : participantDTO.getEndDateOption();
        participantDTO.setEndDateOption(isClose);
        log.info("participantController isClose : [{}]", isClose);

        // 사용자가 선택한 페이지 수가 없다면 기본 페이지 1 고정
        int page = participantDTO.getPage() <= 0 ? 1 : participantDTO.getPage();
        log.info("participantController page : [{}]", page);

        //사용자가 볼 게시글의 개수 0이라면 10 고정
        int pageRows = participantDTO.getPageRows() <= 0 ? 100 : participantDTO.getPageRows();
        log.info("participantController pageRows : [{}]", pageRows);

        // 사용자에게 보여질 버튼 개수
        int limitButton = 10;
        String search = participantDTO.getSearch();

        participantDTO.setSearchPath("managerSearch");
        if(search != null && !search.isEmpty()){
            participantDTO.setSearchStatus(true);
        }
        String selectCondition = "selectAllParticipantBasic";
        String selectCountCondition = "selectCountParticipant";

        //글 개수 쿼리 컨디션
        participantDTO.setParticipantCondition(selectCountCondition);

        // 게시글 개수
        int totalCount = participantService.selectOne(participantDTO).getTotalCount();
        log.info("totalCount : [{}]", totalCount);

        paginationBean.paginationProject(page,pageRows,limitButton,totalCount);
        // -------------------------페이지네이션 끝------------------------------
        // 한 페이지에 보여줄 데이터를 출력하기 위해 OFFSET PAGEROWS 를 추가
        participantDTO.setStartPage(paginationBean.getStartPage());
        participantDTO.setEndPage(paginationBean.getEndPage());
        participantDTO.setPageRows(pageRows);

        //검색 Condition
        participantDTO.setParticipantCondition(selectCondition);

        // 위 정보를 토대로 게시글 받아 온다.
        List<ParticipantDTO> datas = participantService.selectAll(participantDTO);

        model.addAttribute("datas", datas);
        model.addAttribute("page", page);
        // 시작 버튼
        model.addAttribute("startButton", paginationBean.getStartButton());
        // 끝 버튼
        model.addAttribute("endButton", paginationBean.getEndButton());
        // 실제 버튼 개수
        model.addAttribute("totalButton", paginationBean.getTotalButton());
        // 검색된 참여자 개수
        model.addAttribute("totalCount", totalCount);
        // 지점 전체 참여자 페이지 확인용
        model.addAttribute("branchManagementPageFlag", true);

        log.info("End participantController");
        log.info("-----------------------------------");
        return "views/BranchPariticipant";
    }
}
