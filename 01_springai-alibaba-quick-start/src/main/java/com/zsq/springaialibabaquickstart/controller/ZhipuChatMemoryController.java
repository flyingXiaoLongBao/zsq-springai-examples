package com.zsq.springaialibabaquickstart.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zhipu/chatClient/")
public class ZhipuChatMemoryController {
    private final ChatClient chatClient;

    public ZhipuChatMemoryController(ChatClient.Builder chatClientBuilder) {

        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();

        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(memoryAdvisor)
                .build();
    }

    @GetMapping("/chatMemoryAdvisor")
    public ChatResponse chatMemoryAdvisor(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "conversationId") String conversationId
    ) {
        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage("你是一个有用的AI助手"),
                        new UserMessage(query)
                ),
                ZhiPuAiChatOptions.builder()
                        .temperature(0.4)
                        .build()
        );

        return chatClient
                .prompt(prompt)
                .advisors(
                        advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .chatResponse();
    }

    
}
