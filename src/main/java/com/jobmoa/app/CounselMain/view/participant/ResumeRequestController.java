package com.jobmoa.app.CounselMain.view.participant;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantService;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 이력서 요청 관리 컨트롤러.
 * <p>참여자의 이력서 요청 목록을 페이지네이션과 함께 조회하여 표시한다.
 * /resumeRequest 경로 하위에 매핑된다.</p>
 */
@Slf4j
@Controller
@RequestMapping("/resumeRequest")
public class ResumeRequestController {

    @Autowired
    private ParticipantService participantService;

    /**
     * 이력서 요청 목록 페이지를 표시한다.
     * <p>로그인 여부를 확인하고, 사용자 권한에 따라 이력서 요청 목록을
     * 페이지네이션 적용하여 조회한다. 상태값의 공백을 제거하여 JSP에 전달한다.</p>
     * @param model Spring MVC Model
     * @param session HTTP 세션 (로그인 및 권한 정보 확인용)
     * @param participantDTO 검색 조건 및 페이지 정보를 담은 DTO
     * @param paginationBean 페이지네이션 계산용 빈
     * @return 이력서 요청 목록 JSP 뷰 이름 (views/resumeRequestListPage) 또는 알림 페이지
     */
    @GetMapping("/list.login")
    public String resumeRequestListPage(Model model, HttpSession session, ParticipantDTO participantDTO, PaginationBean paginationBean) {
        log.info("resumeRequestListPage Start");

        // 세션 로그인 정보 확인
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
        if (loginBean == null) {
            String url = "/login.do";
            String title = "로그인이 필요합니다.";
            String message = "이력서 요청 목록을 보려면 로그인해주세요.";
            String icon = "warning";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }

        // 사용자 권한 정보 설정
        participantDTO.setParticipantUserid(loginBean.getMemberUserID());
        participantDTO.setParticipantBranch(loginBean.getMemberBranch());
//        participantDTO.setParticipantBranchManagement(loginBean.isMemberBranchManagement());
//        participantDTO.setParticipantManagement(loginBean.isMemberManagement());

        // 사용자가 선택한 페이지 수가 없다면 기본 페이지 1 고정
        int page = participantDTO.getPage() <= 0 ? 1 : participantDTO.getPage();
        log.info("resumeRequestListPage page : [{}]", page);

        // 사용자가 볼 게시글의 개수 0이라면 10 고정
        int pageRows = participantDTO.getPageRows() <= 0 ? 10 : participantDTO.getPageRows();
        log.info("resumeRequestListPage pageRows : [{}]", pageRows);

        // 사용자에게 보여질 버튼 개수
        int limitButton = 10;

        // 이력서 요청 목록 개수 조회
        participantDTO.setParticipantCondition("resumeRequestListCount");
        ParticipantDTO totalCountDTO = participantService.selectOne(participantDTO);
        int totalCount = (totalCountDTO != null) ? totalCountDTO.getTotalCount() : 0;

        paginationBean.paginationProject(page, pageRows, limitButton, totalCount);

        // 페이징 적용하여 데이터 조회
        participantDTO.setStartPage(paginationBean.getStartPage());
        participantDTO.setEndPage(paginationBean.getEndPage());
        participantDTO.setPageRows(pageRows);

        // 이력서 요청 목록 조회
        participantDTO.setParticipantCondition("resumeRequestListSelect");
        List<ParticipantDTO> resumeRequestList = participantService.selectAll(participantDTO);

        //공백 제거
        for (ParticipantDTO dto : resumeRequestList) {
            dto.setStatus(dto.getStatus().trim());
        }

        // JSP로 데이터 전달
        model.addAttribute("resumeRequestList", resumeRequestList);
        model.addAttribute("page", page);
        model.addAttribute("startButton", paginationBean.getStartButton());
        model.addAttribute("endButton", paginationBean.getEndButton());
        model.addAttribute("totalButton", paginationBean.getTotalButton());

        log.info("resumeRequestListPage End");
        return "views/resumeRequestListPage";
    }
}