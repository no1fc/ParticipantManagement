package com.jobmoa.app.CounselMain.view.chatBot;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChatBotController {

    @GetMapping("/chat")
    public String getChatPage() {
        // HTML 기반 챗 UI 페이지 반환
        return "chatBot/TestChatBot";
    }

    @PostMapping("/ask")
    @ResponseBody
    public String handleChatInput(@RequestBody String userInput) {
        // 사용자의 입력을 받아 챗봇의 응답 처리
        return generateResponse(userInput);
    }

    private String generateResponse(String userInput) {
        // 간단한 로직 예제: 사용자의 입력에 따라 응답 생성
        if (userInput.toLowerCase().contains("안녕")) {
            return "안녕하세요! 무엇을 도와드릴까요?";
        } else if (userInput.toLowerCase().contains("시간")) {
            return "현재 시간은 " + java.time.LocalTime.now() + "입니다.";
        }

        return "무슨 말인지 이해하지 못했어요.";
    }
}