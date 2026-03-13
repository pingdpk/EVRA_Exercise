package com.evra.ocppcharging.infrastructure.ocpp.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class OcppFrameParser {

    private final ObjectMapper objectMapper;

    public OcppFrameParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OcppCallFrame parseCall(String rawFrame) {
        try {
            JsonNode root = objectMapper.readTree(rawFrame);
            if (!root.isArray() || root.size() != 4) {
                throw new IllegalArgumentException("OCPP CALL frame must be [2, uniqueId, action, payload]");
            }

            int messageType = root.get(0).asInt();
            if (OcppMessageType.fromCode(messageType) != OcppMessageType.CALL) {
                throw new IllegalArgumentException("expected OCPP CALL frame");
            }

            return new OcppCallFrame(
                    root.get(1).asText(),
                    root.get(2).asText(),
                    root.get(3)
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException("invalid OCPP frame: " + exception.getMessage(), exception);
        }
    }
}
