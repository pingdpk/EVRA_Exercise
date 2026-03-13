package com.evra.ocppcharging.infrastructure.ocpp.protocol;

import com.fasterxml.jackson.databind.JsonNode;

public class OcppCallFrame {

    private final String uniqueId;
    private final String action;
    private final JsonNode payload;

    public OcppCallFrame(String uniqueId, String action, JsonNode payload) {
        this.uniqueId = uniqueId;
        this.action = action;
        this.payload = payload;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getAction() {
        return action;
    }

    public JsonNode getPayload() {
        return payload;
    }
}
