package com.gdut.ai.config;
import com.gdut.ai.service.ChatMessageService;
import com.gdut.ai.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler(), "/ws")
                .addInterceptors(new HttpHandshakeInterceptor())
                .setAllowedOrigins("*"); // 根据需要调整允许的源
    }

    @Bean
    public MyWebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler(chatMessageService, groupMemberService);
    }
}


