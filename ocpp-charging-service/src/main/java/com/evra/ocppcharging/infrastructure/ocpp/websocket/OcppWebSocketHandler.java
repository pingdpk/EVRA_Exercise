package com.evra.ocppcharging.infrastructure.ocpp.websocket;

import com.evra.ocppcharging.infrastructure.ocpp.handler.OcppActionDispatcher;
import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppCallFrame;
import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppFrameParser;
import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppFrames;
import com.evra.ocppcharging.infrastructure.ocpp.session.ChargerConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
public class OcppWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(OcppWebSocketHandler.class);

    private final OcppFrameParser frameParser;
    private final OcppActionDispatcher actionDispatcher;
    private final OcppFrames frames;
    private final ChargerConnectionRegistry registry;

    public OcppWebSocketHandler(OcppFrameParser frameParser,
                                OcppActionDispatcher actionDispatcher,
                                OcppFrames frames,
                                ChargerConnectionRegistry registry) {
        this.frameParser = frameParser;
        this.actionDispatcher = actionDispatcher;
        this.frames = frames;
        this.registry = registry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String chargerId = chargerId(session);
        registry.register(chargerId, session);
        log.info("ocpp websocket connected chargerId={} sessionId={}", chargerId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String chargerId = chargerId(session);
        OcppCallFrame frame = null;
        try {
            frame = frameParser.parseCall(message.getPayload());
            String response = actionDispatcher.dispatch(chargerId, frame);
            session.sendMessage(new TextMessage(response));
        } catch (Exception exc) {
            Throwable root = rootCause(exc);
            String uniqueId = frame != null ? frame.getUniqueId() : "unknown";
            String errorMessage = root.getMessage() != null ? root.getMessage() : exc.getMessage();

            log.warn("failed to process OCPP frame chargerId={} uniqueId={} err={}",
                    chargerId, uniqueId, errorMessage, exc);

            session.sendMessage(new TextMessage(
                    frames.callError(uniqueId, "ProtocolError", errorMessage, Map.of())
            ));
        }
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable curr = throwable;
        while (curr.getCause() != null) {
            curr = curr.getCause();
        }
        return curr;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.remove(chargerId(session));
        log.info("ocpp websocket disconnected. chargerId={} ,sessionId={}, closeCode={}  ", chargerId(session), session.getId(), status.getCode());
    }

    private String chargerId(WebSocketSession session) {
        Object chargerId = session.getAttributes().get("chargerId");
        if (chargerId != null) {
            return chargerId.toString();
        }
        String path = session.getUri() != null ? session.getUri().getPath() : "";
        String[] segments = path.split("/");
        return segments.length == 0 ? "unknown" : segments[segments.length - 1];
    }
}
