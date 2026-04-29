package com.jobmoa.app.jobPlacement.view.starbucks;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementDTO;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class StarbucksController {

    @Autowired
    private JobPlacementService jobPlacementService;

    @GetMapping("/Starbucks")
    public String starbucksPage(Model model, HttpSession session, JobPlacementDTO jobPlacementDTO, PaginationBean paginationBean) {
        log.info("starbucksPage Start");

        int page = jobPlacementDTO.getPage() <= 0 ? 1 : jobPlacementDTO.getPage();
        int pageRows = jobPlacementDTO.getPageRows() <= 0 ? 10 : jobPlacementDTO.getPageRows();
        int limitButton = 10;

        jobPlacementDTO.setCondition("selectStarbucksCount");
        JobPlacementDTO totalCountDTO = jobPlacementService.selectOne(jobPlacementDTO);

        if (totalCountDTO != null) {
            int totalCount = totalCountDTO.getTotalCount();
            log.info("starbucksPage totalCount : [{}]", totalCount);

            paginationBean.paginationProject(page, pageRows, limitButton, totalCount);

            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            String sessionUniqueNumber = "";
            if (loginBean != null) {
                sessionUniqueNumber = loginBean.getMemberUniqueNumber();
            }

            jobPlacementDTO.setStartPage(paginationBean.getStartPage());
            jobPlacementDTO.setEndPage(paginationBean.getEndPage());
            jobPlacementDTO.setPageRows(pageRows);
            jobPlacementDTO.setCondition("selectStarbucksAll");

            List<JobPlacementDTO> starbucksDatas = jobPlacementService.selectAll(jobPlacementDTO);

            for (JobPlacementDTO p : starbucksDatas) {
                String originalName = p.getParticipant();
                String originalAddress = p.getAddress();
                int originalAge = p.getAge();
                boolean isMine = Objects.equals(p.getUniqueNumber(), sessionUniqueNumber);
                if (!isMine) {
                    hideInfo(p, originalName, originalAddress, originalAge);
                }
            }

            model.addAttribute("starbucksDatas", starbucksDatas);
        }

        model.addAttribute("page", page);
        model.addAttribute("startButton", paginationBean.getStartButton());
        model.addAttribute("endButton", paginationBean.getEndButton());
        model.addAttribute("totalButton", paginationBean.getTotalButton());

        log.info("starbucksPage End");
        return "starbucksView/participant-list";
    }

    private void hideInfo(JobPlacementDTO data, String originalName, String originalAddress, int age) {
        if (originalName != null && !originalName.isEmpty()) {
            String maskedName = originalName.charAt(0) + originalName.substring(1).replaceAll(".", "O");
            data.setParticipant(maskedName);
        }

        if (originalAddress != null && !originalAddress.isEmpty()) {
            String makeAddress = originalAddress.length() > 11 ? originalAddress.substring(0, 11) : originalAddress;
            data.setAddress(makeAddress + "...");
        }

        if (age != 0) {
            if (age >= 1 && age <= 39) {
                data.setAgeRangeContent("청년");
            } else if (age >= 40 && age <= 59) {
                data.setAgeRangeContent("중년");
            } else if (age >= 60 && age <= 79) {
                data.setAgeRangeContent("장년");
            } else {
                data.setAgeRangeContent("비공개");
            }
        }
    }
}