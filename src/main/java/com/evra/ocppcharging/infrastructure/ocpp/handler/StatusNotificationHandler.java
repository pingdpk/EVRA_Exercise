package com.evra.ocppcharging.infrastructure.ocpp.handler;

import com.evra.ocppcharging.infrastructure.idempotency.CommandResult;
import com.evra.ocppcharging.infrastructure.ocpp.mapper.OcppCommandMapper;
import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppCallFrame;
import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppFrames;
import com.evra.ocppcharging.services.command.ChargingCommandService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StatusNotificationHandler implements OcppActionHandler {

    private final ChargingCommandService chargingCommandService;
    private final OcppCommandMapper mapper;
    private final OcppFrames frames;

    public StatusNotificationHandler(ChargingCommandService chargingCommandService, OcppCommandMapper mapper, OcppFrames frames) {
        this.chargingCommandService = chargingCommandService;
        this.mapper = mapper;
        this.frames = frames;
    }

    @Override
    public String action() {
        return "StatusNotification";
    }

    @Override
    public String handle(String chargerId, OcppCallFrame frame) {
        CommandResult result = chargingCommandService.handle(mapper.statusNotification(chargerId, frame.getPayload(), frame.getUniqueId()));
        return frames.callResult(frame.getUniqueId(), Map.of("status", "Accepted", "message", result.getMessage()));
    }
}
