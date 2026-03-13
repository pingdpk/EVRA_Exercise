package com.evra.ocppcharging.infrastructure.eventstoer;

import com.evra.ocppcharging.domain.event.DomainEvent;

import java.util.List;

public interface EventStore {

    EventEnvelope append(String streamKey, DomainEvent event);

    List<DomainEvent> load(String streamKey);

    List<EventEnvelope> loadEnvelopes(String streamKey);

    List<EventEnvelope> loadAll();
}
