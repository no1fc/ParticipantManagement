package com.jobmoa.app.CounselMain.biz.mailSend;

import java.util.List;

public interface MailService {

    /**
     * 
     * @param from 메일을 보낸 사람
     * @param to 메일을 받는 사람
     * @param subject 메일 제목
     * @param content 메일 내용
     */
    boolean send(String from, String to, String subject, String content);
    
    /**
     * 
     * @param from 메일을 보낸 사람
     * @param to List 여러명에게 메일을 보내야할때 사용
     * @param subject 메일 제목
     * @param content 메일 내용
     */
    boolean send(String from, List<String> to, String subject, String content);

    /**
     *
     * @param from 메일을 보낸 사람
     * @param bcc List 여러명에게 메일을 보내야할때 사용
     * @param subject 메일 제목
     * @param content 메일 내용
     */
    boolean bccSend(String from, List<String> bcc, String subject, String content);
}
