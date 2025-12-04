package com.zsq.springaialibabagraph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class GenerateJokeNode implements NodeAction {
    private final ChatClient chatClient;

    public GenerateJokeNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //从状态图中获取到关键字
        String topic = state.value("topic", "");

        //设置提示词
        PromptTemplate promptTemplate = new PromptTemplate("你需要写一个关于指定主题的短笑话。要求返回的结果中只能包含笑话的内容" +
                "主题:{topic}");
        promptTemplate.add("topic",topic);
        String prompt = promptTemplate.render();

        //模型调用
        String content = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
        return Map.of("joke", content);
    }
}
