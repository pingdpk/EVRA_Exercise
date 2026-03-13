package com.evra.ocppcharging.infrastructure.ocpp.mapper;

import com.evra.ocppcharging.domain.lifecycle.ChargingLifeCycle;
import com.evra.ocppcharging.domain.lifecycle.LifeCycleType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OcppCommandMapper {

    public ChargingLifeCycle bootNotification(String chargerId, JsonNode payload, String uniqueMessageId) {
        Integer connectorId = payload.hasNonNull("connectorId") ? payload.get("connectorId").asInt() : null;
        Instant timestam = parseTimestamp(payload.get("timestamp"));
        return new ChargingLifeCycle(chargerId, LifeCycleType.BOOT_NOTIFICATION, null, null, timestam,
                deriveKey(chargerId, uniqueMessageId, "BootNotification"),
                null, null, connectorId);
    }

    public ChargingLifeCycle authorize(String chargerId, JsonNode payload, String uniqueMessageId) {
        String idTag = textOrNull(payload.get("idTag"));
        Instant timestam = parseTimestamp(payload.get("timestamp"));
        return new ChargingLifeCycle(chargerId, LifeCycleType.AUTHORIZE, null, null, timestam,
                deriveKey(chargerId, uniqueMessageId, "Authorize"),
                null, idTag, payload.hasNonNull("connectorId") ? payload.get("connectorId").asInt() : null);
    }

    public ChargingLifeCycle statusNotification(String chargerId, JsonNode payload, String uniqueMessageId) {
        Instant timestamp = parseTimestamp(payload.get("timestamp"));
        return new ChargingLifeCycle(chargerId, LifeCycleType.STATUS_NOTIFICATION, null, null, timestamp,
                deriveKey(chargerId, uniqueMessageId, "StatusNotification"),
                textOrNull(payload.get("status")),
                null,
                payload.hasNonNull("connectorId") ? payload.get("connectorId").asInt() : null);
    }

    public ChargingLifeCycle startTransaction(String chargerId, JsonNode payload, String uniqueMessageId) {
        String transactionId = textOrNull(payload.get("transactionId"));
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }
        Integer connectorId = payload.hasNonNull("connectorId") ? payload.get("connectorId").asInt() : null;
        return new ChargingLifeCycle(
                chargerId,
                LifeCycleType.START_TRANSACTION,
                transactionId,
                requiredLong(payload, "meterStart"),
                parseTimestamp(payload.get("timestamp")),
                deriveKey(chargerId, uniqueMessageId, "StartTransaction"),
                null,
                textOrNull(payload.get("idTag")),
                connectorId
        );
    }

    public ChargingLifeCycle meterValues(String chargerId, JsonNode payload, String uniqueMessageId) {
        return new ChargingLifeCycle(
                chargerId,
                LifeCycleType.METER_VALUES,
                requiredText(payload, "transactionId"),
                requiredLong(payload, "meterValue"),
                parseTimestamp(payload.get("timestamp")),
                deriveKey(chargerId, uniqueMessageId, "MeterValues"),
                null,
                null,
                payload.hasNonNull("connectorId") ? payload.get("connectorId").asInt() : null
        );
    }

    public ChargingLifeCycle stopTransaction(String chargerId, JsonNode payload, String uniqueMessageId) {
        return new ChargingLifeCycle(
                chargerId,
                LifeCycleType.STOP_TRANSACTION,
                requiredText(payload, "transactionId"),
                requiredLong(payload, "meterStop"),
                parseTimestamp(payload.get("timestamp")),
                deriveKey(chargerId, uniqueMessageId, "StopTransaction"),
                null,
                textOrNull(payload.get("idTag")),
                payload.hasNonNull("connectorId") ? payload.get("connectorId").asInt() : null
        );
    }

    private String deriveKey(String chargerId, String uniqueMessageId, String action) {
        return "ocpp-" + chargerId + "-" + action + "-" + uniqueMessageId;
    }

    private Instant parseTimestamp(JsonNode timestampNode) {
        if (timestampNode == null || timestampNode.isNull() || timestampNode.asText().isBlank()) {
            return Instant.now();
        }
        return Instant.parse(timestampNode.asText());
    }

    private String textOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }

    private String requiredText(JsonNode payload, String field) {
        JsonNode node = payload.get(field);
        if (node == null || node.isNull() || node.asText().isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return node.asText();
    }

    private long requiredLong(JsonNode payload, String field) {
        JsonNode node = payload.get(field);
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return node.asLong();
    }
}
