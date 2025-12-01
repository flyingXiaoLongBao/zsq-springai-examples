package com.zsq.springaialibabaquickstart.controller;


import com.zsq.springaialibabaquickstart.advisors.SimpleMessageChatMemoryAdvisor;
import com.zsq.springaialibabaquickstart.entity.Book;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/zhipu/chatClient/")
public class ZhipuChatClientController {
    private final ChatClient chatClient;

    public ZhipuChatClientController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/simple")
    public String simple(@RequestParam(name = "query") String query) {
        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage("你是一个有用的AI助手"),
                        new UserMessage(query)
                ),
                ZhiPuAiChatOptions.builder()
                        .temperature(0.4)
                        .build()
        );

        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/response")
    public ChatResponse response(@RequestParam(name = "query") String query) {
        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage("你是一个有用的AI助手"),
                        new UserMessage(query)
                ),
                ZhiPuAiChatOptions.builder()
                        .temperature(0.4)
                        .build()
        );

        return chatClient
                .prompt(prompt)
                .advisors(new SimpleMessageChatMemoryAdvisor())
                .call()
                .chatResponse();
    }

    @GetMapping("/bookResponse")
    public Book response() {
        return chatClient.prompt()
                .system("你是一个书籍生成器，但是只需要生成书籍的名称和作者")
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .call().entity(Book.class);
    }

    @GetMapping("/streamResponse")
    public Flux<String> streamResponse() {
        return chatClient.prompt()
                .system("你是一个书籍生成器，但是只需要生成书籍的名称和作者")
                .user("给我随机生成一本书，要求书名和作者都是中文")
                .stream()
                .content();
    }

    @GetMapping("/simpleMessageChatMemoryAdvisor")
    public ChatResponse simpleMessageChatMemoryAdvisor(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "conversationId") String conversationId
    ) {
        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage("你是一个有用的AI助手"),
                        new UserMessage(query)
                ),
                ZhiPuAiChatOptions.builder()
                        .temperature(0.4)
                        .build()
        );

        return chatClient
                .prompt(prompt)
                .advisors(
                        advisorSpec -> {
                             advisorSpec.param("conversationId", conversationId);
                        }
                )
                .advisors(new SimpleMessageChatMemoryAdvisor())
                .call()
                .chatResponse();
    }


}
