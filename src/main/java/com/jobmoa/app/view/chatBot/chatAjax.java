package com.jobmoa.app.view.chatBot;

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
    private ChatBotService chatBotService;

    @PostMapping("/api")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        log.info("userMessage : [{}]",request);
        try {
            // OpenAI API에 요청을 전달하고 응답받기
            String response = chatBotService.getChatGPTResponse(request.getUserMessage());
            log.info("response : [{}]",response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
