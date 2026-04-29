package com.jobmoa.app.CounselMain.biz.participant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@EnableScheduling
@Service("participant")
public class ParticipantServiceImpl implements ParticipantService {

    @Autowired
    private ParticipantDAO participantDAO;

    @Override
    public List<ParticipantDTO> selectAll(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantService selectOne : [{}]",participantDTO);
        if(participantDTO == null || participantDTO.getParticipantCondition() == null) {
            log.error("selectAll participantDTO null OR participantCondition null");
            return null;
        }

        sanitizeSearchTerm(participantDTO);

        return participantDAO.selectAll(participantDTO);
    }

    private void sanitizeSearchTerm(ParticipantDTO participantDTO) {
        String search = participantDTO.getSearch();

        if(search != null && !search.isEmpty()){
            search = search.replaceAll("[!@#$%^&*]", "");

            participantDTO.setSearch(search);
        }
    }


    @Override
    public ParticipantDTO selectOne(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantService selectOne : [{}]",participantDTO);
        if(participantDTO == null || participantDTO.getParticipantCondition() == null) {
            log.error("selectOne participantDTO null OR participantCondition null");
            return null;
        }
        return participantDAO.selectOne(participantDTO);
    }

    @Override
    public boolean insert(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantService insert : [{}]",participantDTO);
        return participantDAO.insert(participantDTO);
    }

    @Override
    public boolean update(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantService update : [{}]",participantDTO);
        return participantDAO.update(participantDTO);
    }

    @Override
    @Transactional
    public boolean delete(ParticipantDTO participantDTO) {
        //log.info("ParticipantDTO ParticipantService delete : [{}]",participantDTO);
        //반환용 boolean flag 변수
        boolean flag = false;

        //기본정보 condition 이 null이 아니고 기본정보 dto 가 null이 아니라면
        if(participantDTO == null || participantDTO.getParticipantCondition() == null) {
            log.error("delete participantDTO not null OR participantCondition null");
            return false;
        }
        log.info("delete participantDTO not null OR participantCondition not null");

        //기본정보 삭제를 진행하고
        if(participantDAO.delete(participantDTO)){
            //각 삭제 쿼리를 실행할 condition
            String[] conditions = {"Particcertif","Education"};
            //삭제가 되었다면 반복문을 용해 각 정보를 삭제한다.
            for(String condition : conditions){
                //각 condition으로 검색해 값이 있으면 삭제 진행
                log.info("delete participantDTO condition : [{}]",condition);
                if(selectCount(participantDTO,condition)){
                    //정보에 맞는 삭제 쿼리를 실행하기 위해 condition 을 추가하고
                    log.info("Start delete participantDTO condition : [{}]",condition);
                    participantDTO.setParticipantCondition("participant"+condition+"Delete");
                    //삭제를 진행한다.
                    if(!participantDAO.delete(participantDTO)){
                        log.error("delete participantDTO condition : [{}] \n return false",condition);
                        //만약 삭제가 되지 않았다면 오류를 반환하여 트랜젝션을 실행시킨다.
                        throw new RuntimeException("Failed to delete participant data");
                    }
                    log.info("End delete participantDTO condition : [{}]",condition);
                }
            }
            //문제없이 끝나면 true 로 전달한다.
            flag = true;
        }
        log.info("End delete participantDTO flag : [{}]",flag);
        return flag;
    }

    private boolean selectCount(ParticipantDTO participantDTO, String condition){
        //값이 넘어올때 jobno는 있으니 condition 만 추가하여 값을 전달한다.
        participantDTO.setParticipantCondition("selectOne"+condition);
        //자격증 개수를 받고
        int count = participantDAO.selectOne(participantDTO).getParticipantCount();
        //자격증이 0 보다 크면 true, 0이거나 작으면 false 반환
        return count > 0;
    }

    // 매일 00시 00분 (오전 12시)에 Spring 스케줄러를 통해 데이터를 확인한다.
    // 확인을 진행할때 일주일이 지난 취소자를 제거 및 백업 데이터로 이전한다.
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void canselParticipantBackup(){
        log.info("=== 일주일 경과 취소자 백업 및 삭제 배치 시작 ===");

        // 1. 파라미터 대신 메서드 내부에서 DTO 인스턴스 생성
        ParticipantDTO participantDTO = new ParticipantDTO();

        // 2. 7일이 지난 취소자 데이터를 백업 테이블로 복사
        participantDTO.setParticipantCondition("participantBackupOldCancelled");
        boolean isBackedUp = participantDAO.insert(participantDTO);

        log.info("백업 실행 결과 (isBackedUp) : [{}]", isBackedUp);

        // 3. 백업이 성공적으로 수행되었다면 원본 테이블에서 영구 삭제 진행
        if(isBackedUp) {
            participantDTO.setParticipantCondition("participantDeleteOldCancelled");
            boolean isDeleted = participantDAO.delete(participantDTO);
            log.info("원본 삭제 실행 결과 (isDeleted) : [{}]", isDeleted);
        } else {
            log.info("백업할 대상 데이터가 없거나 백업에 실패하여 삭제 로직을 건너뜁니다.");
        }

        log.info("=== 일주일 경과 취소자 백업 및 삭제 배치 종료 ===");
    }
}
