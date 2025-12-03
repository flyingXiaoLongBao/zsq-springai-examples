package com.zsq.springaialibabagraph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class TranslationNode implements NodeAction {
    private final ChatClient chatClient;

    public TranslationNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state){
        //从state中获取造句
        String sentence = state.value("sentence", "");
        //模型调用
        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语翻译专家，能够基于给定的英语句子进行中文翻译。" +
                "要求只返回最终翻译好的中文句子，不要返回其他信息。特别的，遇到人名wangzixin，必须翻译成王子欣。给定的英文句子:{sentence}");

        promptTemplate.add("sentence",sentence);

        String content = chatClient
                .prompt()
                .user(promptTemplate.render())
                .call()
                .content();

        //把翻译后的句子存入state
        return Map.of("translation", content);
    }
}
