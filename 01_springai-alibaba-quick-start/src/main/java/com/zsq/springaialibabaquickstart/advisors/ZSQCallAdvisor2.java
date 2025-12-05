package com.zsq.springaialibabaquickstart.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j
public class ZSQCallAdvisor2 implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        log.info("SGCallAdvisor2 请求");
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        log.info("SGCallAdvisor2 响应");
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "ZSQCallAdvisor2";
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
