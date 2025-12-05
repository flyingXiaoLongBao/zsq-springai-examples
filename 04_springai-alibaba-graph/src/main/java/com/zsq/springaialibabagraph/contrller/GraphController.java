package com.zsq.springaialibabagraph.contrller;


import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/graph")
public class GraphController {
    private final CompiledGraph quickStartGraph;
    private final CompiledGraph simpleGraph;
    private final CompiledGraph conditionalGraph;
    private final CompiledGraph loopGraph;


    public GraphController(
            @Qualifier("quickStartGraph") CompiledGraph quickStartGraph,
            @Qualifier("simpleGraph") CompiledGraph simpleGraph,
            @Qualifier("conditionalGraph") CompiledGraph conditionalGraph,
            @Qualifier("loopGraph") CompiledGraph loopGraph
            )
    {
        this.quickStartGraph = quickStartGraph;
        this.simpleGraph = simpleGraph;
        this.conditionalGraph = conditionalGraph;
        this.loopGraph = loopGraph;
    }

    @GetMapping("/quickStartGraph")
    public String quickStartGraph() {
        Optional<OverAllState> overAllStateOptional = quickStartGraph.call(Map.of());
        log.info("overAllStateOptional: {}", overAllStateOptional);
        return "ok";
    }

    @GetMapping("/simpleGraph")
    public String simpleGraph(@RequestParam("word") String word){
        Optional<OverAllState> overAllStateOptional = simpleGraph.call(Map.of("word", word));

        return
                "word:" + word + "\n" +
                "sentence:" + overAllStateOptional.flatMap(allStateOptional -> allStateOptional.value("sentence")).get() + "\n" +
                "translation:" + overAllStateOptional.get().value("translation").get();

    }

    @GetMapping("/conditionalGraph")
    public String conditionalGraph(@RequestParam("topic") String topic){
        Optional<OverAllState> overAllStateOptional = conditionalGraph.call(Map.of("topic", topic));

        return
                "topic:" + topic + '\n' +
                        "evaluation:" + overAllStateOptional.get().value("evaluation").get() + '\n' +
                        "joke:" + overAllStateOptional.get().value("joke").get();
    }

    @GetMapping("/loopGraph")
    public String loopGraph(
            @RequestParam("topic") String topic,
            @RequestParam("maxLoopCount") int maxLoopCount,
            @RequestParam("threshold") int threshold
    ){
        Optional<OverAllState> overAllStateOptional = loopGraph.call(
                Map.of(
                        "topic", topic,
                        "maxLoopCount", maxLoopCount,
                        "threshold", threshold
                        )
        );

        return
                "topic:" + topic + '\n' +
                        "evaluation:" + overAllStateOptional.get().value("evaluation").get() + '\n' +
                        "loopCount:" + overAllStateOptional.get().value("loopCount").get() + '\n' +
                        "joke:" + overAllStateOptional.get().value("joke").get();
    }

}
