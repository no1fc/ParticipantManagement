package com.jobmoa.app.jobPlacement.view.jobPlacement;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.PaginationBean;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementDTO;
import com.jobmoa.app.jobPlacement.biz.jobPlacement.JobPlacementService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/jobPlacement")
public class JobPlacementController {

    @Autowired
    private JobPlacementService jobPlacementService;

    // 추후 참여자에게 작성을 할 수 있기에 주석 처리
/*    @GetMapping("/placementMain")
    public String jobPlacementMainPage(){
        log.info("jobPlacementMainPage 메서드 호출됨");

        String viewPage = "jobPlacementView/participant";
        log.info("jobPlacementMainPage 반환할 뷰 이름: {}", viewPage);

        return viewPage;
    }    */

    @GetMapping("/")
    public String jobPlacementIndexPage(){
        log.info("jobPlacementIndexPage 메서드 호출됨");

        String viewPage = "redirect:/jobPlacement/placementList";
        log.info("jobPlacementIndexPage 반환할 뷰 이름: {}", viewPage);

        return viewPage;
    }

    @GetMapping("/placementList")
    public String jobPlacementListPage(Model model, HttpSession session, JobPlacementDTO jobPlacementDTO, PaginationBean paginationBean) {
        log.info("jobPlacementListPage Start");

        //사용자가 선택한 페이지 수가 없다면 기본 페이지 1 고정
        int page = jobPlacementDTO.getPage() <= 0 ? 1 : jobPlacementDTO.getPage();
        log.info("jobPlacementListPage page : [{}]", page);

        //사용자가 볼 게시글의 개수 0이라면 10 고정
        int pageRows = jobPlacementDTO.getPageRows() <= 0 ? 10 : jobPlacementDTO.getPageRows();
        log.info("jobPlacementListPage pageRows : [{}]", pageRows);

        // 사용자에게 보여질 버튼 개수
        int limitButton = 10;

        boolean isFilter = false;
        //검색 필터 사용 여부 체크
        String searchKeyword = jobPlacementDTO.getSearchKeyword();
        String searchType = jobPlacementDTO.getSearchType();
        //나이대 필터
        int ageRangeFilter = jobPlacementDTO.getAgeRangeFilter();
        // 직무 카테고리 대분류
        String jobCategoryLargeFilter = jobPlacementDTO.getJobCategoryLargeFilter();
        // 직무 카테고리 중분류
        String jobCategoryMidFilter = jobPlacementDTO.getJobCategoryMidFilter();
        //성별
        String genderFilter = jobPlacementDTO.getGenderFilter();
        //주소
        String[] searchAddressFilter = jobPlacementDTO.getSearchAddressFilter();

        //나이 필터, 희망 연봉 필터, 성별 필터가 비어 있지 않다면 필터 사용으로 간주한다.
        if(genderFilter != null || searchKeyword != null || ageRangeFilter != 0 || jobCategoryLargeFilter != null || jobCategoryMidFilter != null || searchAddressFilter != null){
            if(searchKeyword != null){
                searchKeyword = searchKeyword.trim();
                searchKeyword = searchKeyword.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\s]", "");
                jobPlacementDTO.setSearchKeyword(searchKeyword);
            }

            isFilter = true;
        }
        jobPlacementDTO.setFilterFlag(isFilter);

        //글 개수 쿼리 컨디션
        jobPlacementDTO.setCondition("selectExternalCount");

        // 게시글 개수
        JobPlacementDTO jobPlacementTotalCountDTO = jobPlacementService.selectOne(jobPlacementDTO);

        if(jobPlacementTotalCountDTO != null){
            int totalCount = jobPlacementTotalCountDTO.getTotalCount();
            log.info("ParticipantManagement totalCount : [{}]", totalCount);

            paginationBean.paginationProject(page,pageRows,limitButton,totalCount);
            // -------------------------페이지네이션 끝------------------------------

            //데이터가 있다면 로그인 정보를 확인
            //세션 로그인 정보
            LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");
            String sessionUniqueNumber = "";
            // nullPoint 방지를 위해 설정
            if(loginBean != null){
                sessionUniqueNumber = loginBean.getMemberUniqueNumber();
            }


            // 한 페이지에 보여줄 데이터를 출력하기 위해 OFFSET PAGEROWS 를 추가
            jobPlacementDTO.setStartPage(paginationBean.getStartPage());
            jobPlacementDTO.setEndPage(paginationBean.getEndPage());
            jobPlacementDTO.setPageRows(pageRows);
            // DB(J_참여자관리)로부터 참여자 목록 조회
            jobPlacementDTO.setCondition("selectExternalAll");
//            log.info("jobPlacementListPage jobPlacementDTO : [{}]", jobPlacementDTO);
            List<JobPlacementDTO> jobPlacementDatas = jobPlacementService.selectAll(jobPlacementDTO);

            // ParticipantDTO에서 이름 설정
            for (JobPlacementDTO p : jobPlacementDatas) {
                String originalName = p.getParticipant(); // 참여자 이름
                String originalAddress = p.getAddress(); // 참여자 주소
                int originalAge = p.getAge();
                String uniqueNumber = p.getUniqueNumber(); // 상담사 고유번호
                boolean flag = Objects.equals(uniqueNumber, sessionUniqueNumber); // 세션에 있는 아이디와 비교
                if(!flag){
                    //이름, 주소 숨김 처리
                    hideInfo(p,originalName,originalAddress,originalAge);
                }
            }

            // JSP로 데이터 전달
            model.addAttribute("jobPlacementDatas", jobPlacementDatas);

        }
        // 현재 페이지
        model.addAttribute("page", page);
        // 시작 버튼
        model.addAttribute("startButton", paginationBean.getStartButton());
        // 끝 버튼
        model.addAttribute("endButton", paginationBean.getEndButton());
        // 실제 버튼 개수
        model.addAttribute("totalButton", paginationBean.getTotalButton());
        log.info("jobPlacementListPage End");

        log.info("jobPlacementListPage 메서드 호출됨");

        String viewPage = "jobPlacementView/company-list";
        log.info("jobPlacementListPage 반환할 뷰 이름: {}", viewPage);

        return viewPage;
    }

    @GetMapping("/placementDetail")
    public String jobPlacementDetailPage(Model model, HttpSession session, JobPlacementDTO jobPlacementDTO){
        log.info("jobPlacementDetailPage 메서드 호출됨");

        //전달할 기본 페이지
        String viewPage = "jobPlacementView/company-detail";

        jobPlacementDTO.setCondition("selectExternalDetail");

        JobPlacementDTO data = jobPlacementService.selectOne(jobPlacementDTO);

        //데이터가 없다면 오류를 반환하도록 조건 추가.
        if(data == null){
            String url = "/jobPlacement/placementList";
            String title = "구직번호를 확인해주세요.";
            String message = "잘못된 접근입니다.";
            String icon = "warning";
            viewPage = "views/info";
            InfoBean.info(model, url, icon, title, message);
            return viewPage;
        }

        //데이터가 있다면 로그인 정보를 확인
        //세션 로그인 정보
        LoginBean loginBean = (LoginBean) session.getAttribute("JOBMOA_LOGIN_DATA");

        //이름 및 주소를 변수로 생성
        String originalName = data.getParticipant();
        String originalAddress = data.getAddress();
        int originalAge = data.getAge();

        //로그인 정보가 없다면
        if(loginBean == null){
            //개인 정보 이름, 주소를 변환
            hideInfo(data, originalName, originalAddress, originalAge);
        }
        else{
            //로그인 정보가 있다면 상담사 아이디와 구직번호를 받아오고
            String userID = loginBean.getMemberUserID();
            String jobNumber = data.getJobNumber();

            //값을 추가하여 상담사를 검색
            jobPlacementDTO.setCounselorId(userID);
            jobPlacementDTO.setJobNumber(jobNumber);
            jobPlacementDTO.setCondition("selectExternalCounselor");
            JobPlacementDTO counselorData = jobPlacementService.selectOne(jobPlacementDTO);

            //데이터가 null이면 로그인 정보와 다른 상담사로 값을 숨김처리해서 전달.
            if(counselorData == null){
                hideInfo(data, originalName, originalAddress,originalAge);
            }
        }

        //최종 데이터 전달
        model.addAttribute("data", data);

        log.info("jobPlacementDetailPage 반환할 뷰 이름: {}", viewPage);
        return viewPage;
    }


    //개인정보 숨기기(이름)
    private void hideInfo(JobPlacementDTO data, String originalName, String originalAddress, int age){
        if (originalName != null && !originalName.isEmpty()) {
            // 첫 글자 제외하고 나머지를 "O"로 변환
            String maskedName = originalName.charAt(0) + originalName.substring(1).replaceAll(".", "O");
            data.setParticipant(maskedName); // 이름 업데이트
        }

        if (originalAddress != null && !originalAddress.isEmpty()) {
            //주소가 비어 있지 않으면 0번째 글자에서 11번째 글자까지 글을 자른다.
            //단 11번째 글자가 끝이 아닐 수 있으니 글자 길이를 확인 후 글자 자르기를 진행
            String makeAddress = originalAddress.length() > 11 ? originalAddress.substring(0, 11): originalAddress;
            data.setAddress(makeAddress + "...");
        }

        if(age != 0){
            if(age >= 1 && age <= 39){
                data.setAgeRangeContent("청년");
            }
            else if (age >= 40 && age <= 59){
                data.setAgeRangeContent("중년");
            }
            else if (age >= 60 && age <= 79){
                data.setAgeRangeContent("장년");
            }
            else{
                data.setAgeRangeContent("비공개");
            }
        }
    }

}

