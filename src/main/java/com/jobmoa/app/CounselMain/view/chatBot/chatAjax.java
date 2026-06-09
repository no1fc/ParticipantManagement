package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChatGPT 대화 요청을 처리하는 비동기 REST 컨트롤러.
 * <p>사용자의 채팅 메시지를 받아 ChatGPT 응답을 반환한다.</p>
 */
@Slf4j
@RestController
public class chatAjax {

    @Autowired
    private ChatBotService chatBotService;//chatGPT 요청 객체

    /**
     * 사용자의 채팅 메시지를 ChatGPT에 전달하고 응답을 반환한다.
     *
     * @param request 사용자 메시지, 스레드 ID, 어시스턴트 ID가 담긴 요청 객체
     * @return ChatGPT의 응답 메시지를 담은 ResponseEntity
     * @throws Exception API 호출 실패 시
     */
    @PostMapping("/api")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) throws Exception {
        log.info("userMessage : [{}]",request);
        ResponseEntity<String> response = chatBotService.getChatGPTResponse(request);
        return response;
    }
}
