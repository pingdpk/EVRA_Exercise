package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public class SessionStartedEvent extends AbstractDomainEvent {

    private final long meterStart;
    private final Integer connectorId;

    public SessionStartedEvent(String chargerId, String transactionId, Instant occurredAt, long meterStart, Integer connectorId) {
        super(chargerId, transactionId, occurredAt);
        this.meterStart = meterStart;
        this.connectorId = connectorId;
    }

    public long getMeterStart() {
        return meterStart;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    @Override
    public String eventType() {
        return "SessionStarted";
    }
}
