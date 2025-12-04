package com.zsq.springaialibabagraph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class EnhanceJokeQualityNode implements NodeAction {

    private final ChatClient chatClient;

    public EnhanceJokeQualityNode(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        //从状态中获取joke
        String joke = state.value("joke", "");

        //设置提示词
        PromptTemplate promptTemplate = new PromptTemplate(
                "你是一个笑话优化专家，你能够优化笑话，让它更加搞笑" +
                        "要求只返回优化后的笑话不要返回其他信息。要优化的笑话：{joke}"
        );
        promptTemplate.add("joke", joke);

        //模型调用
        String content = chatClient
                .prompt()
                .user(promptTemplate.render())
                .call()
                .content();

        return Map.of("joke", content);
    }
}
