package com.jobmoa.app.CounselMain.view.management;


import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantDTO;
import com.jobmoa.app.CounselMain.biz.participant.ParticipantServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class ParticipantManagement {

    @Autowired
    private ParticipantServiceImpl participantService; // 서비스 클래스 의존성 주입

    //participant_management_page.jsp
    @GetMapping("/jobseekers")
    public String viewParticipants(Model model, ParticipantDTO participantDTO, PaginationBean paginationBean) {
        log.info("viewParticipants Start");

        // 사용자가 선택한 페이지 수가 없다면 기본 페이지 1 고정
        int page = participantDTO.getPage() <= 0 ? 1 : participantDTO.getPage();
        log.info("ParticipantManagement page : [{}]", page);

        //사용자가 볼 게시글의 개수 0이라면 10 고정
        int pageRows = participantDTO.getPageRows() <= 0 ? 10 : participantDTO.getPageRows();
        log.info("ParticipantManagement pageRows : [{}]", pageRows);

        // 사용자에게 보여질 버튼 개수
        int limitButton = 10;

        //글 개수 쿼리 컨디션
        participantDTO.setParticipantCondition("selectExternalCount");

        // 게시글 개수
        ParticipantDTO participantTotalCountDTO = participantService.selectOne(participantDTO);

//        log.info("ParticipantManagement participantTotalCountDTO : [{}]", participantTotalCountDTO);

        if(participantTotalCountDTO != null){
            int totalCount = participantTotalCountDTO.getTotalCount();
            log.info("ParticipantManagement totalCount : [{}]", totalCount);

            paginationBean.paginationProject(page,pageRows,limitButton,totalCount);
            // -------------------------페이지네이션 끝------------------------------
            // 한 페이지에 보여줄 데이터를 출력하기 위해 OFFSET PAGEROWS 를 추가
            participantDTO.setStartPage(paginationBean.getStartPage());
            participantDTO.setEndPage(paginationBean.getEndPage());
            participantDTO.setPageRows(pageRows);


            // DB(J_참여자관리)로부터 참여자 목록 조회
            participantDTO.setParticipantCondition("selectExternal");
            List<ParticipantDTO> participantList = participantService.selectAll(participantDTO);

            // ParticipantDTO에서 이름 설정
            for (ParticipantDTO p : participantList) {
                String originalName = p.getParticipantPartic(); // 참여자 이름 가져오기
                if (originalName != null && !originalName.isEmpty()) {
                    // 첫 글자 제외하고 나머지를 "O"로 변환
                    String maskedName = originalName.charAt(0) + originalName.substring(1).replaceAll(".", "O");
                    p.setParticipantPartic(maskedName); // 이름 업데이트
                }
            }

            // JSP로 데이터 전달
            model.addAttribute("participantList", participantList);

        }
        // 현재 페이지
        model.addAttribute("page", page);
        // 시작 버튼
        model.addAttribute("startButton", paginationBean.getStartButton());
        // 끝 버튼
        model.addAttribute("endButton", paginationBean.getEndButton());
        // 실제 버튼 개수
        model.addAttribute("totalButton", paginationBean.getTotalButton());
        log.info("viewParticipants End");
        return "external/participant_management_page"; // JSP 파일 이름 (participantView.jsp)
    }
}

