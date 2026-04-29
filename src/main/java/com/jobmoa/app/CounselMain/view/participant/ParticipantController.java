package com.jobmoa.app.CounselMain.view.participant;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.CounselMain.biz.participantEducation.EducationDTO;
import com.jobmoa.app.CounselMain.biz.participantEducation.EducationServiceImpl;
import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifDTO;
import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifServiceImpl;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicDTO;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselDTO;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentDTO;
import com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentServiceImpl;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class ParticipantController {

    @Value("${kakao.javascript-key:}")
    private String kakaoJsKey;

    @Autowired
    private ParticipantServiceImpl participantService;

    @Autowired
    private BasicServiceImpl basicService;

    @Autowired
    private CounselServiceImpl counselService;

    @Autowired
    private EmploymentServiceImpl employmentService;

    @Autowired
    private ParticcertifServiceImpl particcertifService;

    @Autowired
    private EducationServiceImpl educationService;

    @Autowired
    private InfoBean infoBean;

    //Page 이동
    @GetMapping("participant.login")
    public String participantPageController(Model model, HttpSession session, ParticipantDTO participantDTO, PaginationBean paginationBean){
        log.info("-----------------------------------");
        log.info("Start participantController");

        //session 에 있는 로그인 데이터
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
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

        // 카카오톡 공유용 JavaScript 키
        model.addAttribute("kakaoJsKey", kakaoJsKey);

        log.info("End participantController");
        log.info("-----------------------------------");
        return "views/participantMain";
    }

    @GetMapping("newparticipant.login")
    public String newParticipantsController(){
        return "views/NewParticipantsPage";
    }


    @PostMapping("newparticipant.login")
    public String newParticipantsController(Model model, HttpSession session,
                                            BasicDTO basicDTO, CounselDTO counselDTO, EmploymentDTO employmentDTO,
                                            ParticcertifDTO particcertifDTO, EducationDTO educationDTO){
        log.info("-----------------------------------");
        log.info("Start newParticipantsInsertController");
        String url = "newparticipant.login";
        String icon = "success";
        String title = "참여자 등록";
        String message = "";

        //넘어올 정보는 기본정보, 상담정보, 취업정보, 자격증정보
        //session에서 로그인 정보를 불어온다.
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        //불러온 정보(ID,지점)을 변수에 추가한다.
        String loginId = loginBean.getMemberUserID();
        String loginBranch = loginBean.getMemberBranch();
        log.info("loginId, loginBranch: [{}], [{}]", loginId,loginBranch);
        // 각 로그인 정보는 기본정보에 담아준다.
        basicDTO.setBasicUserid(loginId);
        basicDTO.setBasicBranch(loginBranch);

        //기본정보는 참여자가 비어 있지 않다면 DAO로 바로 넘겨준다.
        if(basicDTO != null && basicDTO.getBasicPartic() != null){

            //기본정보의 저장에 문제가 있다면 False 를 반환 받아 나머지 정보는 저장되지 않게 한다.
            if(!basicService.insert(basicDTO,counselDTO,employmentDTO,particcertifDTO,educationDTO)){
//                log.error("basicService insert error [{}]",basicDTO);
                message = "기본정보 등록에 실패하였습니다.";
                icon = "error";
                log.error(message);
            }
        }

        InfoBean.info(model,url,icon,title,message);
        log.info("-----------------------------------");
        return "views/info";
    }

}
