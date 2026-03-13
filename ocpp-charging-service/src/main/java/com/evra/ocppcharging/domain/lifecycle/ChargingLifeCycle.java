package com.evra.ocppcharging.domain.lifecycle;

import java.time.Instant;
import java.util.Objects;

public class ChargingLifeCycle {

    private final String chargerId;
    private final LifeCycleType lifeCycleType;
    private final String transactionId;
    private final Long meterValue;
    private final Instant timestamp;
    private final String idempotencyKey;
    private final String status;
    private final String authorizationId;
    private final Integer connectorId;

    public ChargingLifeCycle(String chargerId,
                             LifeCycleType lifeCycleType,
                             String transactionId,
                             Long meterValue,
                             Instant timestamp,
                             String idempotencyKey,
                             String status,
                             String authorizationId,
                             Integer connectorId) {
        this.chargerId = require(chargerId, "chargerId");
        this.lifeCycleType = Objects.requireNonNull(lifeCycleType, "lifeCycleType is required");
        this.transactionId = transactionId;
        this.meterValue = meterValue;
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp is required");
        this.idempotencyKey = require(idempotencyKey, "idempotencyKey");
        this.status = status;
        this.authorizationId = authorizationId;
        this.connectorId = connectorId;
    }

    private String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    public String getChargerId() {
        return chargerId;
    }

    public LifeCycleType getCommandType() {
        return lifeCycleType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getMeterValue() {
        return meterValue;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    public String lockKey() {
        return transactionId != null && !transactionId.isBlank() ? "txn:" + transactionId : "charger:" + chargerId;
    }
}
