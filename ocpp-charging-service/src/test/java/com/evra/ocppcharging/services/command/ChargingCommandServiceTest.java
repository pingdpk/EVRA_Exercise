package com.evra.ocppcharging.services.command;

import com.evra.ocppcharging.domain.lifecycle.ChargingLifeCycle;
import com.evra.ocppcharging.domain.lifecycle.LifeCycleType;
import com.evra.ocppcharging.infrastructure.eventbus.SynchronousDomainEventPublisher;
import com.evra.ocppcharging.infrastructure.eventstoer.InMemoryEventStore;
import com.evra.ocppcharging.infrastructure.idempotency.CommandResult;
import com.evra.ocppcharging.infrastructure.idempotency.InMemoryIdempotencyStore;
import com.evra.ocppcharging.infrastructure.locking.InMemoryLockManager;
import com.evra.ocppcharging.query.projection.ChargingProjection;
import com.evra.ocppcharging.query.repository.InMemoryChargerStatsRepository;
import com.evra.ocppcharging.query.repository.InMemorySessionViewRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChargingCommandServiceTest {

    @Test
    void shouldReturnSameResponseForDuplicateIdempotencyKey() {
        InMemoryEventStore eventStore = new InMemoryEventStore();
        ChargingProjection projection = new ChargingProjection(new InMemorySessionViewRepository(), new InMemoryChargerStatsRepository());
        ChargingCommandService service = new ChargingCommandService(
                eventStore,
                new InMemoryIdempotencyStore(),
                new InMemoryLockManager(),
                new SynchronousDomainEventPublisher(projection)
        );

        ChargingLifeCycle start = new ChargingLifeCycle("CHG-1", LifeCycleType.START_TRANSACTION, "TX-1", 100L,
                Instant.parse("2026-02-25T10:00:00Z"), "same-key", null, null, 1);

        CommandResult first = service.handle(start);
        CommandResult second = service.handle(start);

        assertEquals(first.getEventId(), second.getEventId());
        assertEquals(1, eventStore.loadEnvelopes("session:TX-1").size());
    }
}
