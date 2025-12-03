package com.zsq.springaialibabamcpserver.config;


import com.zsq.springaialibabamcpserver.tool.TimeTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {
    @Bean
    public ToolCallbackProvider ToolCallbackProvider(TimeTools tools){
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
