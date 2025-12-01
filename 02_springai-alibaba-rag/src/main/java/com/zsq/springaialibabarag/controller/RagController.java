package com.zsq.springaialibabarag.controller;


import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final VectorStore vectorStore;

    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("/importData")
    public String importData(@RequestParam("data") String data) {
        vectorStore.add(
                List.of(
                        Document.builder()
                                .text(data)
                                .build()
                )
        );
        return "success";
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam("query") String query){
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(3)
                .similarityThreshold(0.6)
                .query(query)
                .build();
        return  vectorStore.similaritySearch(searchRequest);
    }
}
