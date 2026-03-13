package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public class SessionCompletedEvent extends AbstractDomainEvent {

    private final long meterStop;
    private final long totalEnergyConsumedWh;

    public SessionCompletedEvent(String chargerId, String transactionId, Instant occurredAt, long meterStop, long totalEnergyConsumedWh) {
        super(chargerId, transactionId, occurredAt);
        this.meterStop = meterStop;
        this.totalEnergyConsumedWh = totalEnergyConsumedWh;
    }

    public long getMeterStop() {
        return meterStop;
    }

    public long getTotalEnergyConsumedWh() {
        return totalEnergyConsumedWh;
    }

    @Override
    public String eventType() {
        return "SessionCompleted";
    }
}
