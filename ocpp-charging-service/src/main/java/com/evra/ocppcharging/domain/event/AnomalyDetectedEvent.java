package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public class AnomalyDetectedEvent extends AbstractDomainEvent {

    private final String reason;
    private final long previousMeterValue;
    private final long attemptedMeterValue;

    public AnomalyDetectedEvent(String chargerId, String transactionId, Instant occurredAt, String reason, long previousMeterValue, long attemptedMeterValue) {
        super(chargerId, transactionId, occurredAt);
        this.reason = reason;
        this.previousMeterValue = previousMeterValue;
        this.attemptedMeterValue = attemptedMeterValue;
    }

    public String getReason() {
        return reason;
    }

    public long getPreviousMeterValue() {
        return previousMeterValue;
    }

    public long getAttemptedMeterValue() {
        return attemptedMeterValue;
    }

    @Override
    public String eventType() {
        return "AnomalyDetected";
    }
}
