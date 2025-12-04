package com.zsq.springaialibabagraph.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.zsq.springaialibabagraph.node.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;



@Slf4j
@Configuration
public class GraphConfig {

    private final ChatClient.Builder chatClientBuilder;

    public GraphConfig(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }


    @Bean("quickStartGraph")
    public CompiledGraph quickStartGraph() throws GraphStateException {
        //定义状态图
        StateGraph stateGraph = new StateGraph(
                "quickStartGraph",
                () -> Map.of(
                        "input1", new ReplaceStrategy(),
                        "input2", new ReplaceStrategy())
        );

        //定义节点(状态图中默认包含了开始节点和结束结点，不需要单独定义)
        stateGraph.addNode(
                "node1",
                AsyncNodeAction.node_async(
                        state ->{
                            getInfo(state);
                            return Map.of("input1", 1, "input2", 1);
                        }
                )
        );
        stateGraph.addNode(
                "node2",
                AsyncNodeAction.node_async(
                        state ->{
                            getInfo(state);
                            return Map.of("input1", 2, "input2", 2);
                        }
                )
        );
        //定义边
        stateGraph.addEdge(StateGraph.START, "node1");
        stateGraph.addEdge("node1", "node2");
        stateGraph.addEdge("node2", StateGraph.END);

        //编译图
        return stateGraph.compile();

    }

    private static void getInfo(OverAllState state) {
        log.info("state :{}", state);
    }


    @Bean("simpleGraph")
    public CompiledGraph simpleGraph() throws GraphStateException {
        //创建状态图
        StateGraph stateGraph = new StateGraph(
                "simpleGraph",
                () -> Map.of(
                        "word", new ReplaceStrategy(),
                        "sentence", new ReplaceStrategy(),
                        "translation", new ReplaceStrategy())
        );

        //添加节点
        stateGraph.addNode("SentenceConstructionNode",
                AsyncNodeAction.node_async(new SentenceConstructionNode(chatClientBuilder)));
        stateGraph.addNode("TranslationNode",
                AsyncNodeAction.node_async(new TranslationNode(chatClientBuilder)));

        //添加边
        stateGraph.addEdge(StateGraph.START, "SentenceConstructionNode");
        stateGraph.addEdge("SentenceConstructionNode", "TranslationNode");
        stateGraph.addEdge("TranslationNode", StateGraph.END);

        //编译状态图 放入容器
        return stateGraph.compile();
    }

    @Bean("conditionalGraph")
    public CompiledGraph conditionalGraph() throws GraphStateException{
        //创建状态图
        StateGraph stateGraph = new StateGraph(
                "conditionalGraph",
                () -> Map.of(
                        "topic", new ReplaceStrategy(),
                        "joke", new ReplaceStrategy(),
                        "evaluation", new ReplaceStrategy()
                )
        );

        //添加结点
        stateGraph.addNode("GenerateJokeNode",
                AsyncNodeAction.node_async(new GenerateJokeNode(chatClientBuilder))
                );
        stateGraph.addNode("EvaluateJokeNode",
                AsyncNodeAction.node_async(new EvaluateJokeNode(chatClientBuilder))
                );
        stateGraph.addNode("EnhanceJokeQualityNode",
                AsyncNodeAction.node_async(new EnhanceJokeQualityNode(chatClientBuilder))
                );

        //添加边
        stateGraph.addEdge(StateGraph.START, "GenerateJokeNode");
        stateGraph.addEdge("GenerateJokeNode", "EvaluateJokeNode");
        stateGraph.addConditionalEdges(
                "EvaluateJokeNode",
                AsyncEdgeAction.edge_async(
                        state -> {
                            String evaluation = state.value("evaluation","不优秀");
                            if (evaluation.equals("优秀")) {
                                return StateGraph.END;
                            } else {
                                return "EnhanceJokeQualityNode";
                            }
                        }
                ),
                Map.of(
                        "EnhanceJokeQualityNode", "EnhanceJokeQualityNode",
                        StateGraph.END, StateGraph.END
                )
        );
        stateGraph.addEdge("EnhanceJokeQualityNode", StateGraph.END);
        //编译图
        return stateGraph.compile();
    }
}
