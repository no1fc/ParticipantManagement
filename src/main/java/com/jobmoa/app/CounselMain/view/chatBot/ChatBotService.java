package com.jobmoa.app.CounselMain.view.chatBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatBotService {

    @Autowired
    ChatBotFunction chatBotFunction;

    static final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<String> getChatGPTResponse(ChatRequest chatRequest) throws Exception {

        //원래 있던(4o,3-5) ChatGPT 를 사용하려면 아래 함수를 사용
        //onModelChat(chatRequest);

        createMassage(chatRequest);
        chatRequest.setRunId(creatRun(chatRequest));
        ResponseEntity<String> response = retrieveRun(chatRequest);
        //log.info("getChatGPTResponse response : [{}]",response.getBody());
        return response;
    }

    //기본 챗봇 실행 함수
//    private String onModelChat(ChatRequest chatRequest) throws Exception {
//        String userMessage = chatRequest.getUserMessage();
//        // 요청 JSON 생성
//        String requestBodyJson = """
//            {
//                "model":"gpt-4o",
//                "messages":[
//                    {"role": "user", "content": "%s"}
//                  ]
//            }
//            """.formatted(userMessage);
//
//        String requestHttp = """
//                /chat/completions
//                """;
//
//        log.info(" getChatGPTResponse requestBodyJson : [{}]",requestBodyJson);
//        return chatBotFunction.chatBotRequestJson(requestBodyJson,requestHttp);
//    }

    //어시스턴스에 메시지를 추가할 함수
    private void createMassage(ChatRequest chatRequest) throws Exception {
        String userMessage = chatRequest.getUserMessage();
        String threadId = chatRequest.getThreadId();

        // 요청 JSON 생성
        String requestBodyJson = """
            {
              "role": "user",
              "content": "%s"
            }
            """.formatted(userMessage);

        String requestHttp = """
                /threads/%s/messages
                """.formatted(threadId);

        //log.info("createMassage threadId : [{}]",threadId);
        //log.info(" createMassage requestBodyJson : [{}]",requestBodyJson);
        chatBotFunction.chatBotRequestJson(requestBodyJson,requestHttp,true);
    }

    //chatGPT에게 본문 내용과 사용할 챗봇 어시스턴스를 준비해달라고 요청할 함수
    private String creatRun(ChatRequest chatRequest) throws Exception {
        String threadId = chatRequest.getThreadId();
        String assistantsId = chatRequest.getAssistantsId();

        // 요청 JSON 생성
        String requestBodyJson = """
        {
            "assistant_id":"%s"
        }
        """.formatted(assistantsId);

        String requestHttp = """
                /threads/%s/runs
                """.formatted(threadId);
        //log.info(" creatRun requestBodyJson : [{}]",requestBodyJson);

        // OpenAI API에 요청을 전달하고 응답받기
        String response = chatBotFunction.chatBotRequestJson(requestBodyJson,requestHttp,true);
        // OpenAI API에 요청을 전달하고 응답받기
        //받은 데이터를 objectMapper JSON 배열로 반환
        JsonNode root = objectMapper.readTree(response);
        //반환받은 데이터의 id 값만 출력
        response = root.get("id").asText();
        //log.info("creatRun response : [{}]",response);
        return response;
    }

    private ResponseEntity<String> retrieveRun(ChatRequest chatRequest){
        String runId = chatRequest.getRunId();
        String threadId = chatRequest.getThreadId();

        // 요청 JSON 생성
        String requestBodyJson = "";

        String requestHttp = """
                /threads/%s/runs/%s
                """.formatted(threadId,runId);

        // OpenAI API에 요청을 전달하고 응답받기
        try {
            String response;
            JsonNode jsonNode;
            while(true){
                response = chatBotFunction.chatBotRequestJson(requestBodyJson,requestHttp,false);
                // OpenAI API에 요청을 전달하고 응답받기
                //받은 데이터를 objectMapper JSON 배열로 반환
                jsonNode = objectMapper.readTree(response);
                //반환받은 데이터의 choices 0(첫번째) 배열을 가져오고 해당 배열중 message 값에 본문만 출력
//            response = jsonNode.get("choices").get(0).get("message").get("content").asText();
                //chatGPT 실행 여부를 확인한다.
                response = jsonNode.get("status").asText();
                //log.info("retrieveRun try { response : [{}]",response);
                //실행 여부가
                //cancelled,
                //requires_action,
                //cancelling,
                // failed,
                //completed,
                //incomplete,
                // expired 라면 멈춤
                log.info("retrieveRun /threads/%s/runs/%s status : [{}]",response);
                if(response.equals("completed") || response.equals("expired") || response.equals("cancelling")
                || response.equals("failed") || response.equals("cancelled") || response.equals("requires_action")
                || response.equals("incomplete")) {
                    break;
                }
                Thread.sleep(4000);
            }

            //메시지 목록을 물러 온다.
            requestHttp = """
            /threads/%s/messages?order=desc
            """.formatted(threadId);
            response = chatBotFunction.chatBotRequestJson(requestBodyJson,requestHttp,false);
            jsonNode = objectMapper.readTree(response);
            //log.info("retrieveRun jsonNode : [{}]",jsonNode);
            //불러온 메시지 목록에서 GPT 답변만 출력한다.
            response = jsonNode.get("data").get(0).get("content").get(0).get("text").get("value").asText();
            //log.info("retrieveRun if( response : [{}]",response);
            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("retrieveRun Fail : "+e.getMessage());
        }
    }
}
