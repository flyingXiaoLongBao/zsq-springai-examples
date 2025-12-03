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


    public GraphController(
            @Qualifier("quickStartGraph") CompiledGraph quickStartGraph,
            @Qualifier("simpleGraph") CompiledGraph simpleGraph) {
        this.quickStartGraph = quickStartGraph;
        this.simpleGraph = simpleGraph;
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
                "sentence:" + overAllStateOptional.get().value("sentence").get() + "\n" +
                "translation:" + overAllStateOptional.get().value("translation").get();

    }
}
