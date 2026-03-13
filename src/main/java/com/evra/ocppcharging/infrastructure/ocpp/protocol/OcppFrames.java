package com.evra.ocppcharging.infrastructure.ocpp.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OcppFrames {

    private final ObjectMapper objectMapper;

    public OcppFrames(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String callResult(String uniqueId, Object payload) {
        JsonNode payloadNode = objectMapper.valueToTree(payload);
        return toJson(List.of(OcppMessageType.CALL_RESULT.getCode(), uniqueId, payloadNode));
    }

    public String callError(String uniqueId, String errorCode, String errorDescription, Object errorDetails) {
        JsonNode details = objectMapper.valueToTree(errorDetails == null ? Map.of() : errorDetails);
        return toJson(List.of(OcppMessageType.CALL_ERROR.getCode(), uniqueId, errorCode, errorDescription, details));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exce) {
            throw new IllegalStateException("Failed to serialize OCPP frame", exce);
        }
    }
}
