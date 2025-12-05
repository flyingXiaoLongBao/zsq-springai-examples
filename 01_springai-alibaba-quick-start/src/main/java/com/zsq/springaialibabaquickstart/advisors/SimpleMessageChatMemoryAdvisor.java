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
        // 步骤1: 获取会话ID
        String conversationId = chatClientRequest.context().get("conversationId").toString();

        // 步骤2: 查询或创建该会话的历史消息
        List<Message> messages = chatMemory.get(conversationId);
        if (messages == null) {
            messages = new ArrayList<>();
            chatMemory.put(conversationId, messages);
        }

        // 步骤3: 将当前请求的消息添加到历史记录
        List<Message> requestMessages = chatClientRequest.prompt().getInstructions();
        messages.addAll(requestMessages);
        chatMemory.put(conversationId, messages);

        // 步骤4: 构建新的请求，使用完整的对话历史
        return chatClientRequest
                .mutate()
                .prompt(new Prompt(messages, chatClientRequest.prompt().getOptions()))
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        // 步骤1: 获取会话ID
        String conversationId = chatClientResponse.context().get("conversationId").toString();
        List<Message> hisMessages = chatMemory.get(conversationId);

        // 步骤2: 提取AI的回复
        AssistantMessage assistantMessage = chatClientResponse
                .chatResponse()
                .getResult()
                .getOutput();

        // 步骤3: 将AI回复添加到历史记录
        hisMessages.add(assistantMessage);
        chatMemory.put(conversationId, hisMessages);

        // 步骤4: 返回响应（不做修改）
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "SimpleMessageChatMemoryAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
