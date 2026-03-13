package com.evra.ocppcharging.infrastructure.ocpp.handler;

import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppCallFrame;
import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppFrames;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OcppActionDispatcher {

    private final Map<String, OcppActionHandler> handlers;
    private final OcppFrames frames;

    public OcppActionDispatcher(List<OcppActionHandler> handlers, OcppFrames frames) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(OcppActionHandler::action, Function.identity()));
        this.frames = frames;
    }

    public String dispatch(String chargerId, OcppCallFrame frame) {
        OcppActionHandler handler = handlers.get(frame.getAction());
        if (handler == null) {
            return frames.callError(frame.getUniqueId(), "NotImplemented", "Unsupported OCPP action: " + frame.getAction(), Map.of());
        }
        return handler.handle(chargerId, frame);
    }
}
