package com.jobmoa.app.CounselMain.biz.particcertif;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ParticcertifServiceImpl implements ParticcertifService {
    @Autowired
    private ParticcertifDAO particcertifDAO;

    @Override
    public ParticcertifDTO selectOne(ParticcertifDTO particcertifDTO) {
        //log.info("particcertifDTO selectOne : [{}]",particcertifDTO);
        log.info("ParticcertifServiceImpl selectOne Start Log");
        ParticcertifDTO data = null;
        if(particcertifDTO != null || particcertifDTO.getParticcertifCondition() != null) {
            log.info("ParticcertifServiceImpl selectOne ParticcertifDTO Not Null Log");
            data = particcertifDAO.selectOne(particcertifDTO);
        }
        log.info("ParticcertifServiceImpl selectOne End Log");
        return data;
    }

    @Override
    public List<ParticcertifDTO> selectAll(ParticcertifDTO particcertifDTO) {
        //log.info("particcertifDTO selectAll : [{}]",particcertifDTO);
        log.info("ParticcertifServiceImpl selectAll Start Log");
        List<ParticcertifDTO> data = null;
        if(particcertifDTO != null || particcertifDTO.getParticcertifCondition() != null) {
            log.info("ParticcertifServiceImpl selectAll ParticcertifDTO Not Null Log");
            data = particcertifDAO.selectAll(particcertifDTO);
        }
        log.info("ParticcertifServiceImpl selectAll End Log");
        return data;
    }

    @Override
    public boolean insert(ParticcertifDTO particcertifDTO) {
        log.info("ParticcertifServiceImpl insert Start Log");

        if (particcertifDTO == null) {
            log.error("ParticcertifDTO is null");
            return false;
        }
        if (particcertifDTO.getParticcertifJobNo() <= 0) {
            log.error("Invalid particcertifJobNo: {}", particcertifDTO.getParticcertifJobNo());
            return false;
        }

        // 1) 요소 정제: null/빈 문자열 삭제
        String[] certs = particcertifDTO.getParticcertifCertifs();
        if (certs != null) {
            certs = Arrays.stream(certs)
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .map(String::trim)
                    .toArray(String[]::new);
            particcertifDTO.setParticcertifCertifs(certs);
        }

        // 2) 기존 데이터 삭제(업데이트 시 덮어쓰기 정책)
        particcertifDAO.delete(particcertifDTO);

        // 3) 배열이 비어있으면 새 삽입 생략 → INSERT ... VALUES 빈 쿼리 방지
        if (particcertifDTO.getParticcertifCertifs() == null || particcertifDTO.getParticcertifCertifs().length == 0) {
            log.info("No certificates to insert. Skipping insert.");
            log.info("ParticcertifServiceImpl insert End Log");
            return true;
        }

        // 4) 정상 삽입
        boolean result = particcertifDAO.insert(particcertifDTO);
        log.info("ParticcertifServiceImpl insert result: {}", result);
        log.info("ParticcertifServiceImpl insert End Log");
        return result;
    }


    @Override
    public boolean update(ParticcertifDTO particcertifDTO) {
        log.info("ParticcertifServiceImpl update Start Log");
        //log.info("particcertifDTO update : [{}]",particcertifDTO);
        log.info("ParticcertifServiceImpl update End Log");
        return particcertifDAO.update(particcertifDTO);
    }

    @Override
    public boolean delete(ParticcertifDTO particcertifDTO) {
        log.info("ParticcertifServiceImpl delete Start Log");
        //log.info("particcertifDTO delete : [{}]",particcertifDTO);
        log.info("ParticcertifServiceImpl delete End Log");
        return particcertifDAO.delete(particcertifDTO);
//        return false;
    }
}
