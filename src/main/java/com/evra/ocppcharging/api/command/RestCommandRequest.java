package com.evra.ocppcharging.api.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class RestCommandRequest {

    @JsonProperty("charger_id")
    @NotBlank
    private String chargerId;

    @JsonProperty("command_type")
    @NotBlank
    private String commandType; //lifeCycleType

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("meter_value")
    private Long meterValue;

    @NotNull
    private Instant timestamp;

    @JsonProperty("idempotency_key")
    @NotBlank
    private String idempotencyKey;

    private String status;

    @JsonProperty("authorization_id")
    private String authorizationId;

    @JsonProperty("connector_id")
    private Integer connectorId;

    public void toCommand() {
        //return null new LifeCyCles TODO
    }

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getMeterValue() {
        return meterValue;
    }

    public void setMeterValue(Long meterValue) {
        this.meterValue = meterValue;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
    }
}
