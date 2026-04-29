package com.jobmoa.app.CounselMain.biz.particcertif;

import java.util.List;

public interface ParticcertifService {
    ParticcertifDTO selectOne(ParticcertifDTO particcertifDTO);
    List<ParticcertifDTO> selectAll(ParticcertifDTO particcertifDTO);
    boolean insert(ParticcertifDTO particcertifDTO);
    boolean update(ParticcertifDTO particcertifDTO);
    boolean delete(ParticcertifDTO particcertifDTO);
}
