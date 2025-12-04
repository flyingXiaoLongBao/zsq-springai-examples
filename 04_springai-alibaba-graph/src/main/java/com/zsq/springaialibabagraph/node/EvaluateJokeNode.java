package com.zsq.springaialibabagraph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class EvaluateJokeNode implements NodeAction {

    private final ChatClient chatClient;

    public EvaluateJokeNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        //从状态中获取生成的笑话
        String joke = state.value("joke", "");

        //设置提示词模板
        PromptTemplate promptTemplate = new PromptTemplate(
                "你是一个笑话评分专家，能够对笑话进行评分，基于效果的搞笑程度给出0到10分的打分。你很挑剔只有真正好笑的笑话才能6分及以上\n" +
                        "六分及以上的笑话为优秀，反之为不优秀\n" +
                        "要求结果只返回最后的评价(优秀或者不优秀)，不要其他内容。" +
                        "要评分的笑话：:{joke}"
        );
        promptTemplate.add("joke", joke);
        //模型调用
        String content = chatClient
                .prompt()
                .user(promptTemplate.render())
                .call()
                .content();

        return Map.of("evaluation", content);
    }
}
