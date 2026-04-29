package com.jobmoa.app.CounselMain.biz.participantBasic;

import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifDTO;
import com.jobmoa.app.CounselMain.biz.particcertif.ParticcertifServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselDAO;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselDTO;
import com.jobmoa.app.CounselMain.biz.participantCounsel.CounselService;
import com.jobmoa.app.CounselMain.biz.participantEducation.EducationDTO;
import com.jobmoa.app.CounselMain.biz.participantEducation.EducationServiceImpl;
import com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentDAO;
import com.jobmoa.app.CounselMain.biz.participantEmployment.EmploymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("basicService")
public class BasicServiceImpl implements BasicService {

    @Autowired
    private BasicDAO basicDAO;

    @Autowired
    private CounselDAO counselDAO;

    @Autowired
    private EmploymentDAO employmentDAO;

    @Autowired
    private ParticcertifServiceImpl particcertifService;

    @Autowired
    private EducationServiceImpl educationService;

    @Override
    public boolean insert(BasicDTO basicDTO) {
        log.info("basic insert SQL basicDTO : [{}]",basicDTO);
        return basicDAO.insert(basicDTO);
    }


    //신규 참여자 등록 insert
    public boolean insert(BasicDTO basicDTO, CounselDTO counselDTO,
                          EmploymentDTO employmentDTO, ParticcertifDTO particcertifDTO, EducationDTO educationDTO) {
        boolean flag = false;
        if(basicDAO.insert(basicDTO)){
            //기본정보가 문제없이 저장되었다면 JOBNO를 받아 변수에 추가한다.
            basicDTO.setBasicCondition("basicSelectJOBNO");
            int jobno = basicDAO.selectOne(basicDTO).getBasicJobNo();
            //JOBNO를 상담정보, 취업정보, 자격증정보에 각각 추가한다.
            counselDTO.setCounselJobNo(jobno);
            counselDTO.setCounselCondition("counselUpdate");

            employmentDTO.setEmploymentJobNo(jobno);
            employmentDTO.setEmploymentCondition("employmentUpdate");

            particcertifDTO.setParticcertifJobNo(jobno);
            particcertifDTO.setParticcertifCondition("particcertifInsert");

            educationDTO.setEducationJobNo(jobno);
            educationDTO.setEducationCondition("educationInsert");
            //3가지 정보들을 DB 저장한다.
            if(!counselDAO.update(counselDTO)){
                log.error("구직 번호 [{}] 상담 정보 등록 실패", jobno);
                throw new RuntimeException("구직 번호 ["+jobno+"] 상담 정보 등록 실패");
            }
            if(!employmentDAO.update(employmentDTO)){
                log.error("구직 번호 [{}] 취업 정보 등록 실패", jobno);
                throw new RuntimeException("구직 번호 ["+jobno+"] 취업 정보 등록 실패");
            }

            if(particcertifDTO.getParticcertifCertifs() != null){
                if(!particcertifService.insert(particcertifDTO)){
                    log.error("구직 번호 [{}] 자격증 정보 등록 실패", jobno);
                    throw new RuntimeException("구직 번호 ["+jobno+"] 자격증 정보 등록 실패");
                }
            }
            if(educationDTO.getEducations() != null){
                if(!educationService.insert(educationDTO)){
                    log.error("구직 번호 [{}] 직업훈련 정보 등록 실패", jobno);
                    throw new RuntimeException("구직 번호 ["+jobno+"] 직업훈련 정보 등록 실패");
                }
            }
            String counselPlacement = counselDTO.getCounselPlacement();
            if(Objects.equals(counselPlacement, "희망")){
                counselDTO.setCounselCondition("counselPlacementInsert");
                if(!counselDAO.insert(counselDTO)){
                    log.error("구직 번호 [{}] 알선 상세 정보 등록 실패", jobno);
                    throw new RuntimeException("구직 번호 ["+jobno+"] 알선 상세 정보 등록 실패");
                }

                // 키워드 등록
                counselDTO.setCounselCondition("counselKeywordInsert");
                if (!counselDAO.insert(counselDTO)){
                    log.error("구직 번호 [{}] 키워드 등록 실패", jobno);
                    throw new RuntimeException("구직 번호 ["+jobno+"] 키워드 등록 실패");
                }
            }

            // 다중 희망직무 등록 (J_참여자관리_희망직무 테이블에 순위별 저장)
            // 기존 J_참여자관리의 희망직무/직무_카테고리 컬럼은 제거되어 이쪽에 통합 저장됨
            if (counselDTO.getWishJobList() != null && !counselDTO.getWishJobList().isEmpty()) {
                counselDTO.setCounselCondition("counselWishJobInsert");
                if (!counselDAO.insert(counselDTO)) {
                    log.error("구직 번호 [{}] 희망직무 등록 실패", jobno);
                    throw new RuntimeException("구직 번호 ["+jobno+"] 희망직무 등록 실패");
                }
            }

            flag = true;
        }
        return flag;
    }

    @Override
    public boolean update(BasicDTO basicDTO) {
//        log.info("basic update SQL basicDTO : [{}]",basicDTO);
        if(basicDTO == null || basicDTO.getBasicCondition() == null) {
            return false;
        }
        return basicDAO.update(basicDTO);
    }

    public boolean update(BasicDTO basicDTO, CounselDTO counselDTO,
                          EmploymentDTO employmentDTO, ParticcertifDTO particcertifDTO, EducationDTO educationDTO) {

        //기본정보 업데이트
        boolean flag = basicDAO.update(basicDTO);
        log.info("기본정보 업데이트 상태 : [{}]",flag);
        //상담정보 업데이트
        flag = flag && counselDAO.update(counselDTO);
        log.info("상담정보 업데이트 상태 : [{}]",flag);
        //취업정보 업데이트
        flag = flag && employmentDAO.update(employmentDTO);
        log.info("취업정보 업데이트 상태 : [{}]",flag);
        //자격증 업데이트
        flag = flag && particcertifService.insert(particcertifDTO);
        log.info("자격증 업데이트 상태 : [{}]",flag);
        //직업훈련 업데이트
        flag = flag && educationService.insert(educationDTO);
        log.info("직업훈련 업데이트 상태 : [{}]",flag);
        //알선 상세 정보 업데이트
        String counselPlacement = counselDTO.getCounselPlacement();
        if(Objects.equals(counselPlacement, "희망")){
            counselDTO.setCounselCondition("counselPlacementUpdate");
            if(!counselDAO.update(counselDTO)){
                counselDTO.setCounselCondition("counselPlacementInsert");
                flag = flag && counselDAO.insert(counselDTO);
            }
            log.info("알선 상세 정보 업데이트 상태 : [{}]",flag);

            // 키워드 등록
            counselDTO.setCounselCondition("counselKeywordDelete");
            counselDAO.delete(counselDTO);

            counselDTO.setCounselCondition("counselKeywordInsert");
            if (!counselDAO.insert(counselDTO)){
                flag = false;
            }
        }

        // 다중 희망직무 업데이트 (J_참여자관리_희망직무 테이블, delete-then-insert 패턴)
        // 키워드 처리 방식과 동일: 구직번호 기준으로 전체 삭제 후 새 리스트 일괄 등록
        counselDTO.setCounselCondition("counselWishJobDelete");
        counselDAO.delete(counselDTO);
        if (counselDTO.getWishJobList() != null && !counselDTO.getWishJobList().isEmpty()) {
            counselDTO.setCounselCondition("counselWishJobInsert");
            if (!counselDAO.insert(counselDTO)) {
                flag = false;
            }
        }

        if (!flag){
            throw new RuntimeException("참여자 정보 업데이트 실패");
        }

        return flag;
    }

    @Override
    public boolean delete(BasicDTO basicDTO) {

        return false;
    }

    @Override
    public BasicDTO selectOne(BasicDTO basicDTO) {
        BasicDTO data = null;
        // 기본정보 DTO 가 null 이거나 기본정보의 condition 값이 null 이라면 null을 반환한다.
        if (basicDTO == null || basicDTO.getBasicCondition() == null){
            return data;
        }
//        log.info("basic selectOne SQL basicDTO : [{}]",basicDTO);
        //기본 정보 DTO가 있고 condition 값이 있다면 select 를 진행
        data = basicDAO.selectOne(basicDTO);
        return data;
    }

    @Override
    public List<BasicDTO> selectAll(BasicDTO basicDTO) {
        return List.of();
    }
}
