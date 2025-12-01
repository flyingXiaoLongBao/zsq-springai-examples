package com.zsq.springaialibabarag.controller;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coffee")
public class CoffeeController {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public CoffeeController(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;

        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.7)
                .build();

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor= RetrievalAugmentationAdvisor.builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                .build();
        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                .build();

        chatClient = chatClientBuilder
                .defaultAdvisors(retrievalAugmentationAdvisor, messageChatMemoryAdvisor)
                .build();
    }

    @PostMapping("/import")
    public String importData(){
        try {
            // 读取classpath下的QA.csv文件
            ClassPathResource resource = new ClassPathResource("QA.csv");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());

            // 使用Apache Commons CSV解析CSV文件
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()  // 第一行作为标题
                    .setSkipHeaderRecord(true)  // 跳过标题行
                    .build()
                    .parse(reader);

            List<Document> documents = new ArrayList<>();

            // 遍历每一行记录
            for (CSVRecord record : csvParser) {
                // 获取问题和回答字段
                String question = record.get("问题");
                String answer = record.get("回答");

                // 将问题和回答组合成文档内容
                String content = "问题: " + question + "\n回答: " + answer;

                // 创建Document对象
                Document document = new Document(content);

                // 添加到文档列表
                documents.add(document);
            }

            // 关闭解析器
            csvParser.close();

            // 将文档存入向量数据库
            vectorStore.add(documents);

            return "成功导入 " + documents.size() + " 条记录到向量数据库";
        } catch (IOException e) {
            e.printStackTrace();
            return "导入失败: " + e.getMessage();
        }
    }

    /**
     * 新增的RAG问答接口，明确展示查询向量数据库的过程
     * @param question 用户的问题
     * @return AI基于检索到的信息生成的回答
     */
    @GetMapping("/rag-ask")
    public String ragAskQuestion(
            @RequestParam("question") String question,
            @RequestParam("conversationId") String conversationId) {
        return chatClient.prompt()
                .system("你是ZSQ咖啡的服务员，你需要回答用户的问题.")
                .user(question)
                .advisors(
                        advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();

    }

    @GetMapping("/fetcher")
    public String fetcher(@RequestParam("question") String question,
                          @RequestParam("conversationId") String conversationId) {
        return chatClient.prompt()
                .system("你是一个网页爬取专家，你可以运用工具爬取指定网页的内容并且进行总结")
                .user(question)
                .advisors(
                        advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .call()
                .content();

    }
}
