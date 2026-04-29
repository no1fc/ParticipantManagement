package com.jobmoa.app.CounselMain.view.chatBot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
public class ChatController {

    @Autowired
    ChatBotFunction chatBotFunction;

    @GetMapping("/chatgpt")
    public String chatBot(Model model) throws Exception{
//        String threadId = ;
//        log.info("createThread : [{}]",threadId);
//        model.addAttribute("threadId", createThread());

        return "chatBot/ChatBot";
    }

    private String createThread() throws Exception{
        // 요청 JSON 생성
        String requestBodyJson = "";

        return chatBotFunction.chatBotRequestJson(requestBodyJson,"/threads",true);
    }

}
