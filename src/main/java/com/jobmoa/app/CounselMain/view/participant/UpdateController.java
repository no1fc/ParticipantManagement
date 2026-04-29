package com.jobmoa.app.CounselMain.view.participant;

import com.jobmoa.app.CounselMain.biz.bean.LoginBean;
import com.jobmoa.app.CounselMain.biz.bean.SearchBean;
import com.jobmoa.app.CounselMain.biz.participantEducation.EducationDTO;
import com.jobmoa.app.CounselMain.biz.participantEducation.EducationServiceImpl;
import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifDTO;
import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicDTO;
import com.jobmoa.app.CounselMain.biz.participantBasic.BasicServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselDTO;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantCounsel.WishJobDTO;
import com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentDTO;
import com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentServiceImpl;
import com.jobmoa.app.CounselMain.view.function.ChangeJson;
import com.jobmoa.app.CounselMain.view.function.InfoBean;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class UpdateController {
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
    private ChangeJson changeJson;

    /*
    //Page Moves
    @GetMapping("/updatebasic.login")
    public String updateBasicPage(Model model, HttpSession session, BasicDTO basicDTO, ParticcertifDTO particcertifDTO){
        //session내에 있는 로그인 정보를 불러온다.
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        boolean branchAdminFlag = (Boolean)session.getAttribute("IS_BRANCH_MANAGER");
        boolean adminFlag = (Boolean)session.getAttribute("IS_MANAGER");
        String loginId = loginBean.getMemberUserID();
        //각 정보를 조회

        //기본 정보 조회
        // 로그인 정보에 있는 사용자 아이디를 추가
        basicDTO.setBasicUserid(loginId);
        basicDTO.setBasicBranchManagement(branchAdminFlag);
        basicDTO.setBasicManagement(adminFlag);
        //condition을 추가하여 sql문을 확인할 수 있도록한다.
        basicDTO.setBasicCondition("basicSelectPKONE");
        log.info("loginBean : [{}]", loginBean); // login 정보 로그
        // 구직번호와 맞는 기본정보 하나를 받아온다.
        basicDTO = basicService.selectOne(basicDTO);
//        log.info("basicDTO : [{}]", basicDTO);

        //받아온 기본 정보의 jobno(구직번호)를 particcertif에 추가한다.
        int jobno = basicDTO.getBasicJobNo();
        //검색할 DB를 확인하기 위해 condition 값을 추가.
        particcertifDTO.setParticcertifCondition("particcertifSelectALLParticOne");
        particcertifDTO.setParticcertifJobNo(jobno);

        //구직번호를 추가한 자격증 데이터를 불러오고
        List<ParticcertifDTO> datas = particcertifService.selectAll(particcertifDTO);
//        log.info("datas : [{}]", datas);
//        log.info("particcertifDTO : [{}]", particcertifDTO);

        //자격증을 JSON배열로 변경하여 전달
        String particcertifArr = changeJson.convertListToJsonArray(datas, item -> {
            ParticcertifDTO dto = (ParticcertifDTO) item;  // 객체 캐스팅
            return "{\"particcertifPartNo\":\"" + dto.getParticcertifPartNo() + "\","
                    + "\"particcertif\":\"" + dto.getParticcertifCertif() + "\"}";
        });

        //기본 정보와 자격증 정보를 전달한다.
        model.addAttribute("basic", basicDTO);
        model.addAttribute("particcertifs", particcertifArr);

        return "views/UpdateBasicPage";
    }

    @GetMapping("/updatecounsel.login")
    public String updateCounselPage(Model model, CounselDTO counselDTO, EducationDTO educationDTO){
        //받아온 기본 정보의 jobno(구직번호)를 education에 추가한다.
        int jobno = counselDTO.getCounselJobNo();

        //상담 정보를 불러온다.
        counselDTO.setCounselCondition("counselSelectOne");
        counselDTO = counselService.selectOne(counselDTO);
//        log.info("counselDTO : [{}]", counselDTO);

        //검색할 DB를 확인하기 위해 condition 값을 추가.
        educationDTO.setEducationCondition("educationSelectALLOne");
        educationDTO.setEducationJobNo(jobno);

        //구직번호를 추가한 자격증 데이터를 불러오고
        List<EducationDTO> datas = educationService.selectAll(educationDTO);
//        log.info("education datas : [{}]", datas);
//        log.info("update Counsel educationDTO : [{}]", educationDTO);

        //직업훈련정보를 JSON배열로 변경하여 전달
        String educationArr = changeJson.convertListToJsonArray(datas, item -> {
            EducationDTO dto = (EducationDTO) item;  // 객체 캐스팅
            return "{\"educationNo\":\"" + dto.getEducationNo() + "\","
                    + "\"education\":\"" + dto.getEducation() + "\"}";
        });

        //불러온 상담 정보를 전달한다.
        model.addAttribute("educations",educationArr);
        model.addAttribute("counsel", counselDTO);
        return "views/UpdateCounselPage";
    }

    @GetMapping("/updateemployment.login")
    public String updateEmploymentPage(Model model, EmploymentDTO employmentDTO, CounselDTO counselDTO){
        int jobNo = employmentDTO.getEmploymentJobNo();
        //취업 정보를 불러온다.
        employmentDTO.setEmploymentCondition("employmentSelectOne");
        employmentDTO = employmentService.selectOne(employmentDTO);
//        log.info("updateEmploymentPage employmentDTO : [{}]", employmentDTO);

        //상담정보에서 진행단계를 불러오기 위해 counselDTO 에서 jobno(구직번호)로 검색
        counselDTO.setCounselCondition("counselSelectOneEmployment");
        counselDTO.setCounselJobNo(jobNo);
        counselDTO = counselService.selectOne(counselDTO);
        if(counselDTO == null){
            String url = "updatebasic.login";
            String icon = "back";
            String title = "상담정보 확인 불가";
            String message = "상담정보를 먼저 입력해주세요.";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }

        //DTO 확인용 로그
//        log.info("updateEmploymentPage counselDTO : [{}]", counselDTO);
        //만약 counselDTO 가 null 이라면 "" 공백
        //아니라면 counselProgress 를 반환
        String counselProgress = counselDTO == null ? "" : counselDTO.getCounselProgress();

        //취업 정보를 전달한다.
        model.addAttribute("employment", employmentDTO);
        //상담 정보의 참여 유형도 전달
        model.addAttribute("counselProgress", counselProgress);
        return "views/UpdateEmploymentPage";
    }


    //update Mappings
    @PostMapping("/updatebasic.login")
    public String update(Model model, HttpSession session, BasicDTO basicDTO, ParticcertifDTO particcertifDTO, SearchBean searchBean){
        //업데이트 여부에 따라 페이지가 다르기 때문에 각 변수를 선언
        String url = "participant.login?"+searchBean;
        String icon = "";
        String title = "";
        String message = "";
        int jobNo = getJobNo(basicDTO.getBasicJobNo(), basicDTO, session);

        //구직번호가 없다면 오류를 반환하고 조회페이지로 반환
        log.info("기본정보 jobNo : [{}]", jobNo);
        if(jobNo <= 0){

            icon = "error";
            title = "구직번호를 찾을 수 없습니다.";
            message = "";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }

        basicDTO.setBasicCondition("basicUpdate");
        boolean basicFlag = basicService.update(basicDTO);
        log.info("basicUpdate basicFlag : [{}]", basicFlag);

        //자격증 확인용 flag
        boolean particcertifFlag = false;

        //삭제전 구직번호를 추가해준다.
        particcertifDTO.setParticcertifJobNo(jobNo);
        particcertifDTO.setParticcertifCondition("particcertifInsert");
        //자격증을 추가하기전에 삭제를 진행한다.(service 파트에서 이뤄질 예정)
        particcertifFlag = particcertifService.insert(particcertifDTO);

        log.info("basicUpdate particcertifFlag : [{}]", particcertifFlag);

        icon = "success";
        title = "변경 완료";
        message = "수정이 완료되었습니다.";

        if(!basicFlag){
            url = "updatebasic.login?basicJobNo="+jobNo+"&"+searchBean;
            icon = "error";
            title = "기본정보 업데이트 실패";
            message = "기본정보 등록중 문제가 발생했습니다.";
        }
        else if(!particcertifFlag){
            url = "updatebasic.login?basicJobNo="+jobNo+"&"+searchBean;
            icon = "error";
            title = "자격증 추가 실패";
            message = "자격증 등록중 문제가 발생했습니다.";
        }

        //확인용 로그
        InfoBean.info(model, url, icon, title, message);

        return "views/info";
    }

    @PostMapping("/updatecounsel.login")
    public String update(Model model, int page, HttpSession session, BasicDTO basicDTO, CounselDTO counselDTO, EducationDTO educationDTO, SearchBean searchBean){
        //info 페이지로 넘길 변수 선언
        String url = "participant.login?"+searchBean;
        String icon = "";
        String title = "";
        String message = "";

        boolean flag = false;

        //확인을 위해 상담정보의 구직번호를 따로 받는다.
        int counselJobNo = counselDTO.getCounselJobNo();
        //상담번호가 0이 아니라면 구직번호 여부를 확인한다.
        if(counselJobNo > 0){
            log.info("상담정보 구직번호 : [{}]",counselJobNo);
            //받은 로그인 정보를 토대로 기본정보에서 구직번호가 있는지 확인한다.
            //상담정보의 구직번호와 검색한 구직번호가 0보다 크다면 업데이트를 진행
            int jobNo = getJobNo(counselJobNo, basicDTO, session);

            //구직번호가 없다면 오류를 반환하고 조회페이지로 반환
            log.info("상담정보 jobNo : [{}]", jobNo);
            if(jobNo <= 0){
                icon = "error";
                title = "구직번호를 찾을 수 없습니다.";
                message = "";
                InfoBean.info(model, url, icon, title, message);
                return "views/info";
            }

            //상담정보 업데이트로 데이터를 전달하고
            counselDTO.setCounselCondition("counselUpdate");
            flag = counselService.update(counselDTO);

            //자격증 확인용 flag
            boolean educationFlag = false;

            //삭제전 구직번호를 추가해준다.
            educationDTO.setEducationJobNo(jobNo);

            //자격증을 추가하기전에 삭제를 진행한다.(service 파트에서 이뤄질 예정)
            educationFlag = educationService.insert(educationDTO);

            log.info("counselUpdate educationFlag : [{}]", educationFlag);

            icon = "success";
            title = "상담정보 변경 완료";

            if(!flag){
                url = "updatecounsel.login?counselJobNo="+jobNo+"&"+searchBean;
                icon = "error";
                title = "상담정보 변경 실패";
            }
            else if (!educationFlag){
                url = "updatecounsel.login?counselJobNo="+jobNo+"&"+searchBean;
                icon = "error";
                title = "직업훈련 변경 실패";
                message = "직업훈련 등록중 문제가 발생했습니다.";
            }
        }
//        //만약 상담번호가 0이라면 신규 상담으로 확인하여 상담정보에 추가한다.
//        else if(counselJobNo <= 0){
//            icon = "success";
//            title = "상담정보 추가 완료";
//            counselDTO.setCounselCondition("counselInsert");
//            if(!counselService.insert(counselDTO)){
//                url += "updatecounsel.login?counselJobNo="+counselJobNo;
//                icon = "error";
//                title = "상담정보 추가 실패";
//            }
//
//            //직업훈련에 구직번호 추가하고
//            educationDTO.setEducationJobNo(counselJobNo);
//            //직업훈련을 삭제하고 추가를 진행한다.(service 에서 삭제 예정)
//            educationService.insert(educationDTO);
//        }

        InfoBean.info(model, url, icon, title, message);

        return "views/info";
    }

    @PostMapping("/updateemployment.login")
    public String update(Model model, int page, HttpSession session, BasicDTO basicDTO, EmploymentDTO employmentDTO, CounselDTO counselDTO, SearchBean searchBean){
        //info 페이지로 넘길 변수 선언
        String url = "participant.login?"+searchBean;
        String icon = "";
        String title = "";
        String message = "";

        boolean flag = false;
        log.info("updateemployment employmentDTO : [{}]", employmentDTO);
        int employmentJobNo = employmentDTO.getEmploymentJobNo();
        int jobNo = getJobNo(employmentJobNo, basicDTO, session);

        //취업번호가 0보다 크다면 업데이트
        if(employmentJobNo > 0){
            //검색된 구직번호가 0보다 크다면 취업정보의 업데이트를 진행한다.
            if(jobNo > 0){
                employmentDTO.setEmploymentCondition("employmentUpdate");
                flag = employmentService.update(employmentDTO);
            }

            icon = "success";
            title = "취업정보 수정 완료";

            if(!flag){
                url = "updateemployment.login?employmentJobNo="+jobNo+"&"+searchBean;
                icon = "error";
                title = "취업정보 수정 실패";
            }
            counselDTO.setCounselJobNo(jobNo);
            counselDTO.setCounselCondition("counselUpdateProgress");
            counselService.update(counselDTO);
        }
        //취업번호가 0보다 작거나 같다면 신규 등록
        else if(employmentJobNo <= 0){
            //취업번호가 없다면 신규 등록으로 추가를 진행한다.
            url = "participant.login?" + searchBean;
            icon = "success";
            title = "취업정보 추가 완료";
            employmentDTO.setEmploymentCondition("employmentInsert");
            if(!employmentService.insert(employmentDTO)){
                url = "updateemployment.login?employmentJobNo="+employmentJobNo;
                icon = "error";
                title = "취업정보 추가 실패";
            }
        }

        InfoBean.info(model, url, icon, title, message);

        return "views/info";
    }
*/
    //------------------------한 페이지 참여자 업데이트 시작----------------------------------
    @GetMapping("/participantUpdate.login")
    public String updateParticipantsPage(Model model, HttpSession session, BasicDTO basicDTO, EmploymentDTO employmentDTO,
                                         CounselDTO counselDTO, EducationDTO educationDTO, ParticcertifDTO particcertifDTO, SearchBean searchBean,
                                         boolean branchManagementPageFlag) {
        log.info("Start updateParticipantsPage log");
        //참여자 선택시 모든 정보를 확인할 페이지
        //구직번호, 전담자 정보를 변수로 저장
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        String loginId = loginBean.getMemberUserID();
        boolean branchAdminFlag = (Boolean)session.getAttribute("IS_BRANCH_MANAGER");
        boolean adminFlag = (Boolean)session.getAttribute("IS_MANAGER");
        //각 정보를 조회

        //기본 정보 조회
        basicDTO.setBasicUserid(loginId);
        basicDTO.setBasicBranchManagement(branchAdminFlag);
        basicDTO.setBasicManagement(adminFlag);
        basicDTO.setBasicCondition("basicSelectPKONE");
        basicDTO = basicService.selectOne(basicDTO);
//        log.info("조회된 기본정로 basicDTO : [{}]", basicDTO);

        //기본 정보 데이터가 이때 조회 되지 않는 참여자 혹은 전담자라면
        //조회 불가 메시지를 띄운 후 참여자 조회 페이지로 전달
        if(basicDTO == null){
            String url = "participant.login?"+searchBean;
            String icon = "error";
            String title = "참여자 조회 불가";
            String message = "참여자가 없거나 권한이 없는 참여자입니다.";
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }


        //자격증 정보
        particcertifDTO.setParticcertifJobNo(basicDTO.getBasicJobNo());
        particcertifDTO.setParticcertifCondition("particcertifSelectALLParticOne");
        List<ParticcertifDTO> particcertifList = particcertifService.selectAll(particcertifDTO);


        //상담 정보 조회
        counselDTO.setCounselJobNo(basicDTO.getBasicJobNo());
        counselDTO.setCounselCondition("counselSelectOne");
        counselDTO = counselService.selectOne(counselDTO);
//        List<CounselDTO> counselList = counselService.selectAll(counselDTO);


        //직업훈련 정보
        educationDTO.setEducationJobNo(basicDTO.getBasicJobNo());
        educationDTO.setEducationCondition("educationSelectALLOne");
        List<EducationDTO> educationList = educationService.selectAll(educationDTO);

        //취업 정보 조회
        employmentDTO.setEmploymentJobNo(basicDTO.getBasicJobNo());
        employmentDTO.setEmploymentCondition("employmentSelectOne");
        employmentDTO = employmentService.selectOne(employmentDTO);

        //자격증을 JSON배열로 변경하여 전달
        String particcertifArr = changeJson.convertListToJsonArray(particcertifList, item -> {
            ParticcertifDTO dto = (ParticcertifDTO) item;  // 객체 캐스팅
            return "{\"particcertifPartNo\":\"" + dto.getParticcertifPartNo() + "\","
                    + "\"particcertif\":\"" + dto.getParticcertifCertif() + "\"}";
        });

        //직업훈련정보를 JSON배열로 변경하여 전달
        String educationArr = changeJson.convertListToJsonArray(educationList, item -> {
            EducationDTO dto = (EducationDTO) item;  // 객체 캐스팅
            return "{\"educationNo\":\"" + dto.getEducationNo() + "\","
                    + "\"education\":\"" + dto.getEducation() + "\"}";
        });

        // 희망직무 목록을 JSON 배열로 변환하여 전달
        String wishJobArr = "[]";
        if (counselDTO != null && counselDTO.getWishJobList() != null && !counselDTO.getWishJobList().isEmpty()) {
            wishJobArr = changeJson.convertListToJsonArray(counselDTO.getWishJobList(), item -> {
                WishJobDTO dto = (WishJobDTO) item;
                return "{\"wishRank\":" + dto.getWishRank() + ","
                        + "\"categoryLarge\":\"" + (dto.getCategoryLarge() != null ? dto.getCategoryLarge() : "") + "\","
                        + "\"categoryMid\":\"" + (dto.getCategoryMid() != null ? dto.getCategoryMid() : "") + "\","
                        + "\"jobWant\":\"" + (dto.getJobWant() != null ? dto.getJobWant() : "") + "\"}";
            });
        }

        //조회된 내용을 정리하여 페이지로 전달.
        log.info("basicDTO : [{}]", basicDTO);
        log.info("basicDTO.getBasicEducation() : [{}]", basicDTO.getBasicEducation());
        model.addAttribute("basic", basicDTO);
        model.addAttribute("counsel", counselDTO);
        log.info("counselDTO : [{}]", counselDTO);
        model.addAttribute("employment", employmentDTO);
        model.addAttribute("educations", educationArr);
        model.addAttribute("particcertifs", particcertifArr);
        model.addAttribute("wishJobs", wishJobArr);
        // 관리자 페이지 전환을 위해 전달
        model.addAttribute("branchManagementPageFlag", branchManagementPageFlag);

        return "views/UpdateParticipantsPage";
    }


    @PostMapping("/participantUpdate.login")
    public String update(Model model, HttpSession session, BasicDTO basicDTO, EmploymentDTO employmentDTO,
                         CounselDTO counselDTO, EducationDTO educationDTO, ParticcertifDTO particcertifDTO, SearchBean searchBean,
                         boolean branchManagementPageFlag){
        String url = "participant.login?"+searchBean;
        String icon = "success";
        String title = "참여자 정보 업데이트 완료";
        String message = "";

        //구직번호 변수를 추가해둔다.
        int jobNo = basicDTO.getBasicJobNo();

        //session에 있는 로그인 정보를 가져온다.
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        //지점 관리자 여부 확인
        boolean branchAdminFlag = (Boolean)session.getAttribute("IS_BRANCH_MANAGER");
        //관리자 권한 여부 확인
        boolean adminFlag = (Boolean)session.getAttribute("IS_MANAGER");

        //로그인 정보에서 아이디를 가져온다.
        String loginId = loginBean.getMemberUserID();

        //기본정보 업데이트
        basicDTO.setBasicCondition("basicUpdate");
        //지점 관리 페이지에서 전달된 수정요청일때만 실행 지점 관리자 권한이 있어가 지점 관리자 권한이 있다면 condition을 basicManagerUpdate로 변경
        if(branchManagementPageFlag && (branchAdminFlag || adminFlag)){
            basicDTO.setBasicCondition("basicManagerUpdate");
            url = "branchParitic.login?"+searchBean;
        }
        //기본정보 DTO에 가져온 아이디를 추가한다.
        basicDTO.setBasicUserid(loginId);
        //상담정보, 취업정보, 자격증정보, 직업훈련정보에 구직번호를 추가한다.
        //상담정보 업데이트
        counselDTO.setCounselCondition("counselUpdate");
        counselDTO.setCounselJobNo(jobNo);
        //취업정보 업데이트
        employmentDTO.setEmploymentCondition("employmentUpdate");
        employmentDTO.setEmploymentJobNo(jobNo);
        particcertifDTO.setParticcertifJobNo(jobNo);
        educationDTO.setEducationJobNo(jobNo);
        try{
            if(!basicService.update(basicDTO,counselDTO,employmentDTO,particcertifDTO,educationDTO)){
                url="participantUpdate.login?basicJobNo="+jobNo +"&"+searchBean;
                icon="error";
                title="참여자 업데이트 실패";
                message="참여자 번호 : "+jobNo;
            }
        }catch (Exception e){
            url="participantUpdate.login?basicJobNo="+jobNo +"&"+searchBean;
            icon="error";
            title="참여자 업데이트 실패";
            message="참여자 번호 : "+jobNo;
            log.error("update error : [{}]", e.getMessage());
            InfoBean.info(model, url, icon, title, message);
            return "views/info";
        }

        // 업데이트가 완료된 후 취소자 여부 확인
        String cancelFlag = counselDTO.getCounselProgress();
        if (cancelFlag.equals("취소")) {
            // 취소자라면 백업 및 삭제를 진행
            // (트랙젝션을 위해 Service에서 백업 후 삭제를 진행)
            // 취소자 여부 확인 후 Service에서 처리하도록 변경
        }

        //update 완료 여부를 확인해 info page로 정보를 전달한다.
        InfoBean.info(model, url, icon, title, message);
        return "views/info";
    }
    //------------------------한 페이지 참여자 업데이트 끝----------------------------------


    private int getJobNo(int jobNo, BasicDTO basicDTO, HttpSession session){
        LoginBean loginBean = (LoginBean)session.getAttribute("JOBMOA_LOGIN_DATA");
        String loginId = loginBean.getMemberUserID();
        log.info("getJobNo loginId : [{}]", loginId);
        //받은 로그인 정보를 토대로 기본정보에서 구직번호가 있는지 확인한다.
        basicDTO.setBasicCondition("basicSelectOneJOBNO");
        basicDTO.setBasicJobNo(jobNo);
        basicDTO.setBasicUserid(loginId);
        basicDTO = basicService.selectOne(basicDTO);

        if(basicDTO != null){
            jobNo = basicDTO.getBasicJobNo();
        }
        log.info("getJobNo jobNo : [{}]", jobNo);
        return jobNo;
    }


}
