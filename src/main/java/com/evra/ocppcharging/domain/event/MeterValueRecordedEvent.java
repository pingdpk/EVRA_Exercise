package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public class MeterValueRecordedEvent extends AbstractDomainEvent {

    private final long meterValue;
    private final long deltaWh;

    public MeterValueRecordedEvent(String chargerId, String transactionId, Instant occurredAt, long meterValue, long deltaWh) {
        super(chargerId, transactionId, occurredAt);
        this.meterValue = meterValue;
        this.deltaWh = deltaWh;
    }

    public long getMeterValue() {
        return meterValue;
    }

    public long getDeltaWh() {
        return deltaWh;
    }

    @Override
    public String eventType() {
        return "MeterValueRecorded";
    }
}
