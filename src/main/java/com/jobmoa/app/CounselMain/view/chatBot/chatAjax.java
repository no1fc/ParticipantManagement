package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class chatAjax {

    @Autowired
    private ChatBotService chatBotService;//chatGPT 요청 객체

    @PostMapping("/api")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) throws Exception {
        log.info("userMessage : [{}]",request);
        ResponseEntity<String> response = chatBotService.getChatGPTResponse(request);
        //log.info("chat response : [{}]",response);
        return response;
    }
}
