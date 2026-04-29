package com.jobmoa.app.CounselMain.view.mailSend;

import com.jobmoa.app.CounselMain.biz.login.MemberDTO;
import com.jobmoa.app.CounselMain.biz.login.MemberService;
import com.jobmoa.app.CounselMain.biz.mailSend.MailDTO;
import com.jobmoa.app.CounselMain.biz.mailSend.MailService;
import com.jobmoa.app.CounselMain.biz.redis.RedisService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
public class AsyncMailSend {

    @Autowired
    private MailService mailService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RedisService redisService;

    //메일 전달하는 메인 Email
    private static final String SEND_EMAIL="webmaster@jobmoa.com";
    //도메인 주소
    private static final String DOMAIN_EMAIL_PATH = "@jobmoa.com";
    //인증키 횟수 Redis키
    private static final String AUTH_CODE_COUNT_KEY = "auth_code:";
    //인증키 보관 Redis키
    private static final String AUTH_CODE_EXPIRE_TIME_KEY = "auth_code_expire_time:";
    //인증키 최대 보관 시간 (30분/단위 분)
    private static final long AUTH_TIME_MINUTES = 30;
    // expireTime = 요청 시간(단위 초)
    int EXPIRE_TIME = 1800;
    // maxAuthCount 인증 번호 요청 최대 횟수
    int MAX_AUTH_COUNT = 5;

/*    @GetMapping("/test.api")
    public ResponseEntity<String> test() {
        log.info("API test endpoint called");
        return ResponseEntity.ok("API is working!");
    }*/


    @PostMapping("/checkEmail.api")
    private ResponseEntity<?> asyncCheckEmail(@RequestBody MailDTO mailDTO, MemberDTO memberDTO){
        if(mailDTO == null || mailDTO.getFrom() == null){
            return ResponseEntity.badRequest().body("email is null");
        }

        String email = mailDTO.getFrom();
        String userID = email.split("@").length > 0 ? email.split("@")[0] : "";

        memberDTO.setMemberCondition("checkEmail");
        memberDTO.setMemberUserID(userID);
        memberDTO = this.memberService.selectOne(memberDTO);

        if(memberDTO != null){
            return ResponseEntity.ok(true);
        }
        else{
            return ResponseEntity.status(400).body("email is not exist");
        }
    }

    @PostMapping("/pwChangeSendEmail.api")
    private ResponseEntity<?> asyncChangePasswordSendEmail(@RequestBody MailDTO mailDTO) {
        if(mailDTO == null || mailDTO.getUserId() == null){
            return ResponseEntity.badRequest().body("email is null");
        }
        String responseJson ="";
        String userId = mailDTO.getUserId();
        String email = userId.replaceAll(" ", "") + DOMAIN_EMAIL_PATH;

        StringBuilder sb = new StringBuilder();
        //메일 제목
        String subject = "비밀번호 변경 인증번호입니다.";
        //메일 내용
        String message = "비밀번호 변경 인증번호는 다음과 같습니다.";

        try{
            //헥사코드로 인증코드 생성
            String joinCode = md5Hex(email);
            sb.append(message).append("<br>")
                    .append("인증번호 : ").append(joinCode).append("<br>")
                    .append("생성된 인증번호의 유효시간은 5분입니다.");

            log.info("Email message : [{}]", sb);

            //Redis 인증키 횟수 저장 (만료 5분 설정) (테스트 20초)
            String redisKey = AUTH_CODE_COUNT_KEY + email;
            //인증키 만료 시간 Key
            String expireTimeKey = AUTH_CODE_EXPIRE_TIME_KEY + AUTH_TIME_MINUTES + "m"+email;
            //인증 최대 횟수 및 시작 설정
            this.redisService.setStringWithExpire(redisKey, EXPIRE_TIME, MAX_AUTH_COUNT);
            //인증 번호 저장 및 인증 번호 삭제 시간 지정
            this.redisService.setStringWithExpire(expireTimeKey,joinCode, AUTH_TIME_MINUTES, TimeUnit.MINUTES);
            
            //Auth 인증키 설정완료 후 메일전달
            boolean flag = mailService.send(SEND_EMAIL,email,subject,sb.toString());

            //실패 여부 확인용
//            boolean flag = true;

//            log.info("Redis Key : [{}]", redisKey);
//            log.info("Redis Value : [{}]", this.redisService.getString(redisKey));
//            log.info("Redis AuthCode Key : [{}]", expireTimeKey);
//            log.info("Redis AuthCode Value : [{}]", this.redisService.getString(expireTimeKey));

            String responseText = email+" 로 메일 발송되었습니다.";
            if(!flag){
                responseText = email+"로 메일 전송을 실패했습니다.";
            }

            responseJson = "{\"flag\":\""+flag+"\",\"responseText\":\""+responseText+"\"}";
            return ResponseEntity.status(200).body(responseJson);
        }
        catch(NoSuchAlgorithmException e){
            log.error("인증코드 생성 실패 : [{}]",e.getMessage());
            responseJson = "{\"flag\":\""+ false +"\",\"responseText\":\"인증번호 생성 실패\"}";
            return ResponseEntity.status(202).body(responseJson);
        }
        catch(MailException e){
            log.error("메일 발송 실패 오류 : [{}]",e.getMessage());
            responseJson = "{\"flag\":\""+ false +"\",\"responseText\":\"서버 오류 이메일 발송 실패\"}";
            return ResponseEntity.status(409).body(responseJson);
        }
        catch (TimeoutException e) {
            log.error("인증 횟수 초과 : [{}]",e.getMessage());
            responseJson = "{\"flag\":\""+ false +"\",\"responseText\":\""+e.getMessage()+"\"}";
            return ResponseEntity.status(500).body(responseJson);
        }

    }

    //인증번호 확인 api
    @PostMapping("/checkAuthCode.api")
    private ResponseEntity<?> asyncCheckAuthCode(@RequestBody MailDTO mailDTO) {
        log.info("mailDTO : [{}]", mailDTO);
        if(mailDTO == null || mailDTO.getUserId() == null || mailDTO.getAuthCode() == null ||
                mailDTO.getUserId().trim().isEmpty() || mailDTO.getAuthCode().trim().isEmpty()){
            return ResponseEntity.badRequest().body("ID or authCode is ERROR :\n아이디 혹은 인증번호가 비어 있습니다.");
        }
        else if(mailDTO.getAuthCode().length() != 6){
            return ResponseEntity.badRequest().body("authCode is ERROR :\n인증번호는 6자리입니다.");
        }
        // 반환 내용
        String responseJson = "";
        String userId = mailDTO.getUserId();
        String email = userId.replaceAll(" ", "") + DOMAIN_EMAIL_PATH;

        try {
            //인증키 만료 시간 Key
            String expireTimeKey = AUTH_CODE_EXPIRE_TIME_KEY + AUTH_TIME_MINUTES + "m" + email;
            //Redis 인증키 호출
            String redisKey = this.redisService.getString(expireTimeKey);

//            log.info("checkAuthCode.api Redis AuthCode Key : [{}]", expireTimeKey);
//            log.info("checkAuthCode.api Redis AuthCode Value : [{}]", redisKey);

            //인증키가 없다면
            if (redisKey == null) {
                //오류를 발생
                throw new Exception("Redis Key is null");
            }

//            log.info("AuthCode : [{}]", mailDTO.getAuthCode());
//            log.info("AuthCode Value : [{}]", this.redisService.getString(expireTimeKey));

            if(!redisKey.equals(mailDTO.getAuthCode())){
                responseJson = "{\"flag\":\""+false+"\",\"responseText\":\"인증실패\"}";
                return ResponseEntity.status(204).body(responseJson);
            }
            //인증 완료 후 인증키 삭제
            this.redisService.delete(expireTimeKey);

            responseJson = "{\"flag\":\""+true+"\",\"responseText\":\"인증성공\"}";


            return ResponseEntity.status(200).body(responseJson);
        }
        catch (IllegalArgumentException e){
            log.error("IllegalArgumentException : [{}]",e.getMessage());
            responseJson = "{\"flag\":\""+false+"\",\"responseText\":\""+e.getMessage()+"\"}";
            return ResponseEntity.status(500).body(responseJson);
        }
        catch (Exception e) {
            log.error("Redis Key is null : [{}]",e.getMessage());
            responseJson = "{\"flag\":\""+false+"\",\"responseText\":\""+e.getMessage()+"\"}";
            return ResponseEntity.status(500).body(responseJson);
        }

    }

    //인증번호 생성
    private String md5Hex(String str) throws NoSuchAlgorithmException {
        //랜덤 함수 실행
        Random random = new Random();
        // 입력된 값에 랜덤 숫자를 추가
        str += random.nextInt(10000);

        //MD5 인스턴스 생성
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        //해쉬 값 업데이트 추가
        md.update(str.getBytes());
        //Hex String 생성
        String[] arrayString = Hex.encodeHexString(md.digest()).split("");
        StringBuilder responseString = new StringBuilder();
        for(int i=0; i<6; i++){
            responseString.append(arrayString[random.nextInt(arrayString.length)]);
        }

        return responseString.toString().toUpperCase();
    }

}
