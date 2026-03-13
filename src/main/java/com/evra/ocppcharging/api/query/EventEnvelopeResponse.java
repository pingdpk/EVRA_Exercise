package com.evra.ocppcharging.api.query;

import com.evra.ocppcharging.infrastructure.eventstoer.EventEnvelope;

import java.time.Instant;

public class EventEnvelopeResponse {

    private final long sequence;
    private final String eventId;
    private final String eventType;
    private final String chargerId;
    private final String transactionId;
    private final Instant occurredAt;

    public EventEnvelopeResponse(long sequence, String eventId, String eventType, String chargerId, String transactionId, Instant occurredAt) {
        this.sequence = sequence;
        this.eventId = eventId;
        this.eventType = eventType;
        this.chargerId = chargerId;
        this.transactionId = transactionId;
        this.occurredAt = occurredAt;
    }

    public static EventEnvelopeResponse from(EventEnvelope envelope) {
        return new EventEnvelopeResponse(
                envelope.getSequence(),
                envelope.getEvent().eventId(),
                envelope.getEvent().eventType(),
                envelope.getEvent().chargerId(),
                envelope.getEvent().transactionId(),
                envelope.getEvent().occurredAt()
        );
    }

    public long getSequence() {
        return sequence;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getChargerId() {
        return chargerId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
