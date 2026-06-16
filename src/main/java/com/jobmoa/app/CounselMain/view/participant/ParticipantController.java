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

/**
 * 참여자 관리 페이지 컨트롤러.
 * <p>참여자 목록 조회(페이지네이션/검색 지원) 및 신규 참여자 등록 기능을 제공한다.
 * 신규 등록 시 기본정보, 상담정보, 취업정보, 자격증정보, 교육정보를 함께 저장한다.</p>
 */
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

    /**
     * 참여자 목록 페이지를 표시한다.
     * <p>로그인한 전담자의 참여자 목록을 페이지네이션 및 검색 조건과 함께 조회한다.
     * 마감 여부 필터, 카카오톡 공유 키 등을 JSP에 전달한다.</p>
     * @param model Spring MVC Model
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @param participantDTO 검색 조건 및 페이지 정보를 담은 DTO
     * @param paginationBean 페이지네이션 계산용 빈
     * @return 참여자 목록 JSP 뷰 이름 (views/participantMain)
     */
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

        // 맨 URL 최초 접속(필터/검색/정렬 파라미터 전무) → 기본 필터(진행중 + 1·2유형)로 정규화 redirect.
        // redirect 후 URL에 기본 파라미터가 박혀 폼 체크박스·필터태그·SQL이 일관 동작한다.
        boolean noFilterParams =
                participantDTO.getEndDateOption() == null
                && (participantDTO.getEndDateOptionList() == null || participantDTO.getEndDateOptionList().isEmpty())
                && participantDTO.getParticipantInItCons() == null
                && (participantDTO.getSearch() == null || participantDTO.getSearch().isEmpty())
                && (participantDTO.getSearchTypeList() == null || participantDTO.getSearchTypeList().isEmpty())
                && (participantDTO.getParticipantPartTypeList() == null || participantDTO.getParticipantPartTypeList().isEmpty())
                && (participantDTO.getWishJobSearchList() == null || participantDTO.getWishJobSearchList().isEmpty())
                && (participantDTO.getColumn() == null || participantDTO.getColumn().isEmpty());
        if (noFilterParams) {
            return "redirect:/participant.login?page=1&endDateOptionList=false&participantPartTypeList=1&participantPartTypeList=2";
        }

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

    /**
     * 신규 참여자 등록 페이지를 표시한다.
     * @return 신규 참여자 등록 JSP 뷰 이름 (views/NewParticipantsPage)
     */
    @GetMapping("newparticipant.login")
    public String newParticipantsController(){
        return "views/NewParticipantsPage";
    }


    /**
     * 신규 참여자를 등록한다.
     * <p>기본정보, 상담정보, 취업정보, 자격증정보, 교육정보를 함께 저장한다.
     * 세션의 로그인 정보에서 전담자 ID와 지점을 가져와 기본정보에 설정한다.</p>
     * @param model Spring MVC Model
     * @param session HTTP 세션 (로그인 정보 확인용)
     * @param basicDTO 참여자 기본정보 DTO
     * @param counselDTO 상담정보 DTO
     * @param employmentDTO 취업정보 DTO
     * @param particcertifDTO 자격증정보 DTO
     * @param educationDTO 교육정보 DTO
     * @return 알림 페이지 (views/info) - 등록 결과 표시 후 신규 등록 페이지로 이동
     */
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
