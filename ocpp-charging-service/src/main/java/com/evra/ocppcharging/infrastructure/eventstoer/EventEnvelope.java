package com.evra.ocppcharging.infrastructure.eventstoer;

import com.evra.ocppcharging.domain.event.DomainEvent;

public class EventEnvelope {

    private final long sequence;
    private final DomainEvent event;

    public EventEnvelope(long sequence, DomainEvent event) {
        this.sequence = sequence;
        this.event = event;
    }

    public long getSequence() {
        return sequence;
    }

    public DomainEvent getEvent() {
        return event;
    }
}
