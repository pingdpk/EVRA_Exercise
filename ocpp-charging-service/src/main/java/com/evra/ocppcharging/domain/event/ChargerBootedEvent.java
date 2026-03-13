package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public class ChargerBootedEvent extends AbstractDomainEvent {

    private final Integer connectorId;

    public ChargerBootedEvent(String chargerId, Instant occurredAt, Integer connectorId) {
        super(chargerId, null, occurredAt);
        this.connectorId = connectorId;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    @Override
    public String eventType() {
        return "ChargerBooted";
    }
}
