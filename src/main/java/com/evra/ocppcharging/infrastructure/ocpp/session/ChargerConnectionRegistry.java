package com.evra.ocppcharging.infrastructure.ocpp.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChargerConnectionRegistry {

    private final ConcurrentMap<String, WebSocketSession> sessionsByCharger = new ConcurrentHashMap<>();

    public void register(String chargerId, WebSocketSession session) {
        sessionsByCharger.put(chargerId, session);
    }

    public void remove(String chargerId) {
        sessionsByCharger.remove(chargerId);
    }

    public Optional<WebSocketSession> findByChargerId(String chargerId) {
        return Optional.ofNullable(sessionsByCharger.get(chargerId));
    }
}
