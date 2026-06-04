package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * ChatGPT 챗봇 페이지 컨트롤러.
 * <p>ChatGPT 어시스턴트 기반 챗봇 UI 페이지를 제공한다.</p>
 */
@Slf4j
@Controller
public class ChatController {

    @Autowired
    ChatBotFunction chatBotFunction;

    /**
     * ChatGPT 챗봇 페이지로 이동한다.
     *
     * @param model 뷰에 전달할 데이터 모델
     * @return {@code "chatBot/ChatBot"} JSP 뷰
     * @throws Exception 스레드 생성 실패 시
     */
    @GetMapping("/chatgpt")
    public String chatBot(Model model) throws Exception{
//        String threadId = ;
//        model.addAttribute("threadId", createThread());

        return "chatBot/ChatBot";
    }

    /**
     * OpenAI Assistants API에 새로운 스레드를 생성한다.
     *
     * @return 생성된 스레드 정보 JSON 문자열
     * @throws Exception API 호출 실패 시
     */
    private String createThread() throws Exception{
        // 요청 JSON 생성
        String requestBodyJson = "";

        return chatBotFunction.chatBotRequestJson(requestBodyJson,"/threads",true);
    }

}
