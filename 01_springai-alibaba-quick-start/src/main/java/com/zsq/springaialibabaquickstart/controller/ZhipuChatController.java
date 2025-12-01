package com.zsq.springaialibabaquickstart.controller;


import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zhipu")
public class ZhipuChatController {

    private final ChatModel chatModel;

    public ZhipuChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/simple")
    public String simple(@RequestParam(name = "query") String query) {
        //调用模型
        return chatModel.call(query);
    }

    @GetMapping("/message")
    public String message(@RequestParam(name = "query") String query) {
        //调用模型
        return chatModel.call(
                new SystemMessage("你是一个幽默的AI助手，你的每句话都自带喜感。"),
                new UserMessage(query)
        );
    }

    @GetMapping("/chatOptions")
    public String chatOptions(@RequestParam(name = "query") String query) {
        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage("你是一个风趣幽默的AI助手"),
                        new UserMessage(query)
                ),
                ZhiPuAiChatOptions.builder()
                        .temperature(0.2)
                        .build()
        );

        ChatResponse chatResponse = chatModel.call(prompt);

        Generation responseResult = chatResponse.getResult();
        return responseResult.getOutput().getText();
    }
}
