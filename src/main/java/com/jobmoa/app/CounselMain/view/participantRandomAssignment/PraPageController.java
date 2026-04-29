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

@Slf4j
@Controller
public class PraPageController {

    @Autowired
    private ParticipantRandomAssignmentService praService;

    /**
     *
     * @param praDTO 데이터 전송 객체
     * @param model Back -> Front
     * @return {JSON} '상담사ID' : {name:"상담사 성명", total:총 진행인원, year:25년 진행인원, youth: 청년진행인원,
     *       middleAged:중장년진행인원, specialGroup:특정계층진행인원, current: 배정받은 인원 Default(0), max: 배정 가능 최대 인원 Default(100) }
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
//        log.info("praMainPage select ALL Datas : [{}]",praDatas);
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
//            log.info("praMainPage responseAssignData : [{}]",item);
        }
        responseAssignData.append("}");

//        log.info("praMainPage responseAssignData : [{}]",responseAssignData);

        model.addAttribute("assignData", responseAssignData);
        return "views/participantRandomAssignmentMain";
    }

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
