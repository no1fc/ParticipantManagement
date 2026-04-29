package com.jobmoa.app.CounselMain.biz.mailSend;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MailServiceImpl implements MailService {


    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public boolean send(String from, String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);

            helper.setTo(to);

            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);

            return true;
        } catch (MessagingException e) {
            log.error("To send Mail Error : [{}]", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean send(String from, List<String> to, String subject, String content) {
        if (to == null || to.isEmpty()) {
            log.error("List to send Mail Error : [to list is empty]");
            return false;
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);

            // 여러 수신자를 배열로 설정 (올바른 방법)
            String[] toArray = to.toArray(new String[0]);
            helper.setTo(toArray);

            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("메일 전송 성공 : [{}] 명에게 전송 완료", to.size());
            return true;
        } catch (MessagingException e) {
            log.error("List To send Mail Error : [{}]", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean bccSend(String from, List<String> bcc, String subject, String content) {
        if (bcc == null || bcc.isEmpty()) {
            log.error("List bcc send Mail Error : [to list is empty]");
            return false;
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);

            // BCC로 설정 (수신자들이 서로 보이지 않음)
            String[] toArray = bcc.toArray(new String[0]);
            helper.setBcc(toArray);

            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("메일 전송 성공: [{}] 명에게 BCC로 전송", bcc.size());
            return true;

        } catch (MessagingException e) {
            log.error("BCC Mail send Error : [{}]", e.getMessage());
            return false;
        }

    }
}
