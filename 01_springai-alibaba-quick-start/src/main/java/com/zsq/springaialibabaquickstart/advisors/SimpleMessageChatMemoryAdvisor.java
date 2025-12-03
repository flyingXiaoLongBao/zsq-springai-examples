package com.zsq.springaialibabaquickstart.advisors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.*;

@Slf4j
public class SimpleMessageChatMemoryAdvisor implements BaseAdvisor {

    private static Map<String, List<Message>> chatMemory = new HashMap<>();

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        //通过会话id查询之前的对话记录
        String conversationId = chatClientRequest.context().get("conversationId").toString();
        //把这次请求的信息添加到对话记录中
        List<Message> messages = chatMemory.get(conversationId);
        if (messages == null) {
            messages = new ArrayList<>();
            chatMemory.put(conversationId, messages);
        }
        //把添加后记录的List<Message>放入请求中
        List<Message> requestMessages = chatClientRequest.prompt().getInstructions();
        messages.addAll(requestMessages);
        chatMemory.put(conversationId, messages);

        return chatClientRequest
                .mutate()
                .prompt(new Prompt(messages, chatClientRequest.prompt().getOptions()))
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        //通过会话id查询之前的对话记录
        String conversationId = chatClientResponse.context().get("conversationId").toString();
        List<Message> hisMessages = chatMemory.get(conversationId);

        AssistantMessage assistantMessage = chatClientResponse
                .chatResponse()
                .getResult()
                .getOutput();

        hisMessages.add(assistantMessage);
        chatMemory.put(conversationId, hisMessages);
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
