package com.jobmoa.app.CounselMain.view.participantRandomAssignment;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.dashboard.DashboardDTO;
import com.jobmoa.app.CounselMain.biz.participantRandomAssignment.ParticipantRandomAssignmentDTO;
import com.jobmoa.app.CounselMain.biz.participantRandomAssignment.ParticipantRandomAssignmentService;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 참여자 랜덤 배정 페이지 컨트롤러.
 * 상담사별 배정 현황을 조회하여 랜덤 배정 화면을 렌더링한다.
 */
@Slf4j
@Controller
public class PraPageController {

    @Autowired
    private ParticipantRandomAssignmentService praService;

    /**
     * 참여자 랜덤 배정 메인 페이지를 표시한다.
     * 지점 내 상담사별 배정 현황 데이터를 JSON 형태로 구성하여 뷰에 전달한다.
     * 지점장, PRA 관리자, 총관리자 권한이 필요하다.
     *
     * @param praDTO  배정 조회 조건 DTO
     * @param model   뷰에 전달할 모델 객체
     * @param session HTTP 세션 (권한 및 로그인 정보 조회용)
     * @return JSP 뷰 이름 ("views/participantRandomAssignmentMain") 또는 권한 없을 시 안내 페이지
     */
    @GetMapping("/pra.login")
    public String praMainPage(ParticipantRandomAssignmentDTO praDTO, Model model, HttpSession session){
        log.info("praMainPage On");
        boolean managerRole = (boolean)session.getAttribute("IS_MANAGER");
        boolean branchRole = (boolean)session.getAttribute("IS_BRANCH_MANAGER");
        boolean praRole = (boolean)session.getAttribute("IS_PRA_MANAGER");
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        String branch = loginBean.getMemberBranch();

        if(!branchRole && !praRole && !managerRole){
            log.info("praMainPage role false");
            String url = "index.jsp";
            String icon = "error";
            String title = "접속 권한이 없습니다.";
            String message = "문의 후 이용 부탁드립니다.";
            model.addAttribute("url", url);
            model.addAttribute("icon", icon);
            model.addAttribute("title", title);
            model.addAttribute("message", message);

            return "views/info";
        }

        log.info("Calendar.YEAR : [{}]", Calendar.YEAR);
        log.info("Calendar.getInstance().get(Calendar.YEAR) : [{}]", Calendar.getInstance().get(Calendar.YEAR));

        log.info("praMainPage select ALL Start");
        praDTO.setCondition("selectAssign"); //검색할 Condition
        praDTO.setBranch(branch); // 지점
        praDTO.setYearDate(Calendar.getInstance().get(Calendar.YEAR));
        List<ParticipantRandomAssignmentDTO> praDatas = praService.selectAll(praDTO);
        log.info("praMainPage select ALL End");
        /*
            배열 객체 사용을 위해 {} 배열로 변환
         */
        StringBuilder responseAssignData = new StringBuilder();
        responseAssignData.append("{");
        for(ParticipantRandomAssignmentDTO dto : praDatas){
            //한 문장에 너무 길어져 메서드 시그니처로 전환
            String item = getString(dto);
            responseAssignData.append(item);
            if(praDatas.indexOf(dto) != praDatas.size()-1){
                responseAssignData.append(",");
            }
        }
        responseAssignData.append("}");


        model.addAttribute("assignData", responseAssignData);
        return "views/participantRandomAssignmentMain";
    }

    /**
     * 상담사 배정 정보를 JavaScript 객체 형태의 문자열로 변환한다.
     *
     * @param dto 상담사별 배정 현황 데이터
     * @return JavaScript 객체 리터럴 형태의 문자열
     */
    @NotNull
    private static String getString(ParticipantRandomAssignmentDTO dto) {
        String defaultValue = "\"%s\": {name: \"%s\", employmentDate: \"%s\", total:%d, assignmentAllocationDay:%d, assignmentAllocationWeek: %d, assignmentAllocationTwoWeek: %d, assignmentAllocationMonth: %d, type1:%d, type2:%d, man:%d, woman:%d, year2025:%d, youth:%d, middleAged:%d, specialGroup:%d, current: 0, max:120, assignmentHeadcountWeight:%.1f}";
        return String.format(defaultValue,
                dto.getCounselorID(),
                dto.getCounselor(),
                dto.getCounselorEmploymentDate(),
                dto.getAssignmentTotal(),
                dto.getAssignmentAllocationDay(),
                dto.getAssignmentAllocationWeek(),
                dto.getAssignmentAllocationTwoWeek(),
                dto.getAssignmentAllocationMonth(),
                dto.getAssignmentType1(),
                dto.getAssignmentType2(),
                dto.getAssignmentGenderMan(),
                dto.getAssignmentGenderWoman(),
                dto.getAssignmentYear(),
                dto.getAssignmentYouth(),
                dto.getAssignmentMiddleAged(),
                dto.getAssignmentSpecialGroup(),
                dto.getAssignmentHeadcountWeight()
        );
    }

}
