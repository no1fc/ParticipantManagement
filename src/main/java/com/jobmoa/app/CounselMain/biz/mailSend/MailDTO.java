package com.jobmoa.app.CounselMain.biz.mailSend;

import lombok.Data;

/**
 * 메일 발송을 위한 데이터 전송 객체 (Data Transfer Object)
 * 이메일 발송 시 필요한 모든 정보를 담는 DTO 클래스
 */
@Data
public class MailDTO {

    /**
     * 사용자 아이디
     */
    private String userId;

    /**
     * 발신자 이메일 주소
     * 예: "sender@example.com"
     */
    private String from;

    /**
     * 수신자 이메일 주소
     * 단일 수신자인 경우 사용
     * 예: "recipient@example.com"
     */
    private String to;

    /**
     * 이메일 제목
     * 예: "비밀번호 변경 인증번호입니다."
     */
    private String subject;

    /**
     * 이메일 본문 내용
     * HTML 태그를 포함할 수 있음
     * 예: "인증번호는 <b>123456</b>입니다."
     */
    private String content;

    /**
     * 숨은 참조(BCC) 수신자 목록
     * 여러 명에게 발송하되 서로의 이메일 주소가 보이지 않도록 할 때 사용
     * 예: ["user1@example.com", "user2@example.com"]
     */
    private String[] bcc;

    /**
     * 참조(CC) 수신자 목록
     * 메일을 참고용으로 받아야 하는 사람들의 이메일 주소
     * 모든 수신자에게 CC 목록이 공개됨
     * 예: ["manager@example.com", "team@example.com"]
     */
    private String[] cc;

    /**
     * 첨부파일 경로 목록
     * 이메일에 첨부할 파일들의 경로를 저장
     * 예: ["/uploads/document.pdf", "/uploads/image.jpg"]
     */
    private String[] attachFiles;

    /**
     * 인증번호
     * 이메일 인증 시 사용되는 코드
     * 비밀번호 변경 등에서 활용
     * 예: "A1B2C3" (6자리 영숫자 조합)
     */
    private String authCode;
}

