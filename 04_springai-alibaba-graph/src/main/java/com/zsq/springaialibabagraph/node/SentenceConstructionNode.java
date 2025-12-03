package com.zsq.springaialibabagraph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class SentenceConstructionNode implements NodeAction {
    private final ChatClient chatClient;

    public SentenceConstructionNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state){
        //从state中获取要造句的的单词
        String word = state.value("word", "");
        //模型调用
        PromptTemplate promptTemplate = new PromptTemplate("你是一个英语造句专家，能够基于给定的单词进行造句。" +
                "要求只返回最终造好的句子，不要返回其他信息。 给定的单词:{word}");

        promptTemplate.add("word",word);

        String content = chatClient
                .prompt()
                .user(promptTemplate.render())
                .call()
                .content();

        //把句子存入state
        return Map.of("sentence", content);
    }
}
