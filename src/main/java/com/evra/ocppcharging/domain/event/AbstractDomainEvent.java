package com.evra.ocppcharging.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractDomainEvent implements DomainEvent {

    private final String eventId;
    private final String chargerId;
    private final String transactionId;
    private final Instant occurredAt;

    protected AbstractDomainEvent(String chargerId, String transactionId, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.chargerId = Objects.requireNonNull(chargerId, "chargerId is required");
        this.transactionId = transactionId;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt is required");
    }

    @Override
    public String eventId() {
        return eventId;
    }

    @Override
    public String chargerId() {
        return chargerId;
    }

    @Override
    public String transactionId() {
        return transactionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
