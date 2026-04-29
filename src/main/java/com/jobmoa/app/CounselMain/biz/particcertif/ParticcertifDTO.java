package com.jobmoa.app.CounselMain.biz.particcertif;

import lombok.Data;

@Data
public class ParticcertifDTO {
    private int particcertifPartNo; //자격증 PK 번호
    private int[] particcertifPartNos; //여러 자격증 PK 번호
    private int particcertifJobNo; //참여자 PK 번호
    private String particcertifCertif; //자격증 명칭
    private String[] particcertifCertifs; //여러 자격증 명칭

    //DB 외 정보
    private String particcertifCondition;
}
