package com.evra.ocppcharging.domain.event;

import com.evra.ocppcharging.domain.model.ChargerStatus;

import java.time.Instant;

public class ChargerStatusChangedEvent extends AbstractDomainEvent {

    private final ChargerStatus chargerStatus;

    public ChargerStatusChangedEvent(String chargerId, Instant occurredAt, ChargerStatus chargerStatus) {
        super(chargerId, null, occurredAt);
        this.chargerStatus = chargerStatus;
    }

    public ChargerStatus getChargerStatus() {
        return chargerStatus;
    }

    @Override
    public String eventType() {
        return "ChargerStatusChanged";
    }
}
