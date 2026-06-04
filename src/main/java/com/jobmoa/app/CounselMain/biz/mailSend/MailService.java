package com.jobmoa.app.CounselMain.biz.mailSend;

import java.util.List;

/**
 * 메일 발송 서비스 인터페이스.
 * 단일/다중 수신자 메일 발송 및 BCC 발송 기능을 정의한다.
 */
public interface MailService {

    /**
     * 단일 수신자에게 메일을 발송한다.
     * @param from 발신자 이메일 주소
     * @param to 수신자 이메일 주소
     * @param subject 메일 제목
     * @param content 메일 본문 (HTML 지원)
     * @return 발송 성공 여부
     */
    boolean send(String from, String to, String subject, String content);

    /**
     * 다수 수신자에게 메일을 발송한다.
     * @param from 발신자 이메일 주소
     * @param to 수신자 이메일 주소 목록
     * @param subject 메일 제목
     * @param content 메일 본문 (HTML 지원)
     * @return 발송 성공 여부
     */
    boolean send(String from, List<String> to, String subject, String content);

    /**
     * BCC(숨은참조)로 다수 수신자에게 메일을 발송한다.
     * @param from 발신자 이메일 주소
     * @param bcc 숨은참조 수신자 이메일 주소 목록
     * @param subject 메일 제목
     * @param content 메일 본문 (HTML 지원)
     * @return 발송 성공 여부
     */
    boolean bccSend(String from, List<String> bcc, String subject, String content);
}
