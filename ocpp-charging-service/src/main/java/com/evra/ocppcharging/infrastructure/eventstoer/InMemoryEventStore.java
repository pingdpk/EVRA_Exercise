package com.evra.ocppcharging.infrastructure.eventstoer;

import com.evra.ocppcharging.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryEventStore implements EventStore {

    private final ConcurrentMap<String, List<EventEnvelope>> streams = new ConcurrentHashMap<>();
    private final List<EventEnvelope> globalEvents = java.util.Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public EventEnvelope append(String streamKey, DomainEvent event) {
        EventEnvelope envelope = new EventEnvelope(sequence.incrementAndGet(), event);
        streams.computeIfAbsent(streamKey, ignored -> java.util.Collections.synchronizedList(new ArrayList<>()))
                .add(envelope);
        globalEvents.add(envelope);
        return envelope;
    }

    @Override
    public List<DomainEvent> load(String streamKey) {
        return loadEnvelopes(streamKey).stream().map(EventEnvelope::getEvent).toList();
    }

    @Override
    public List<EventEnvelope> loadEnvelopes(String streamKey) {
        List<EventEnvelope> events = streams.getOrDefault(streamKey, List.of());
        synchronized (events) {
            return List.copyOf(events);
        }
    }

    @Override
    public List<EventEnvelope> loadAll() {
        synchronized (globalEvents) {
            return List.copyOf(globalEvents);
        }
    }
}
