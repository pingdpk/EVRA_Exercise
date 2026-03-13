package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public interface DomainEvent {

    String eventId();

    String chargerId();

    String transactionId();

    Instant occurredAt();

    String eventType();
}
