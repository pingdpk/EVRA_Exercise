package com.evra.ocppcharging.infrastructure.ocpp.config;

import com.evra.ocppcharging.infrastructure.ocpp.websocket.OcppWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class OcppWebSocketConfig implements WebSocketConfigurer {

    private final OcppWebSocketHandler ocppWebSocketHandler;
    private final PathVariableHandshakeInterceptor pathVariableHandshakeInterceptor;

    public OcppWebSocketConfig(OcppWebSocketHandler ocppWebSocketHandler,
                               PathVariableHandshakeInterceptor pathVariableHandshakeInterceptor) {
        this.ocppWebSocketHandler = ocppWebSocketHandler;
        this.pathVariableHandshakeInterceptor = pathVariableHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ocppWebSocketHandler, "/ocpp/{chargerId}")
                .addInterceptors(pathVariableHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
