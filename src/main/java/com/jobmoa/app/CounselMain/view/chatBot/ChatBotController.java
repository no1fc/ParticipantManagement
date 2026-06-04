package com.jobmoa.app.CounselMain.view.chatBot;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 테스트용 챗봇 페이지 컨트롤러.
 * <p>
 * HTML 기반 챗봇 UI 페이지를 제공하고, 간단한 키워드 기반 응답 로직을 포함한다.
 * </p>
 */
@Controller
public class ChatBotController {

    /**
     * 챗봇 UI 페이지로 이동한다.
     *
     * @return {@code "chatBot/TestChatBot"} JSP 뷰
     */
    @GetMapping("/chat")
    public String getChatPage() {
        // HTML 기반 챗 UI 페이지 반환
        return "chatBot/TestChatBot";
    }

    /**
     * 사용자의 채팅 입력을 받아 응답을 생성한다.
     *
     * @param userInput 사용자가 입력한 메시지
     * @return 챗봇 응답 문자열
     */
    @PostMapping("/ask")
    @ResponseBody
    public String handleChatInput(@RequestBody String userInput) {
        // 사용자의 입력을 받아 챗봇의 응답 처리
        return generateResponse(userInput);
    }

    /**
     * 사용자 입력에 따라 간단한 키워드 기반 응답을 생성한다.
     *
     * @param userInput 사용자 입력 메시지
     * @return 키워드에 매칭되는 응답 문자열
     */
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