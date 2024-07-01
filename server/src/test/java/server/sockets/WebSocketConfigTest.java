package server.sockets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketConfigTest {

    WebSocketConfig config;

    @BeforeEach
    void setup(){
        config = new WebSocketConfig();
    }

    @Test
    void registerStompEndpoints() {
        StompEndpointRegistry registry = Mockito.mock(StompEndpointRegistry.class);
        when(registry.addEndpoint(anyString())).thenReturn(Mockito.mock(StompWebSocketEndpointRegistration.class));
        config.registerStompEndpoints(registry);
        verify(registry).addEndpoint("/websocket");
    }

    @Test
    void configureMessageBroker() {
        MessageBrokerRegistry registry = Mockito.mock(MessageBrokerRegistry.class);
        config.configureMessageBroker(registry);
        verify(registry).enableSimpleBroker("/topic","/queue");
        verify(registry).setApplicationDestinationPrefixes("/app");

    }
}