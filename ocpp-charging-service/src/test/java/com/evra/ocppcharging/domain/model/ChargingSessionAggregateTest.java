package com.evra.ocppcharging.domain.model;

import com.evra.ocppcharging.domain.lifecycle.ChargingLifeCycle;
import com.evra.ocppcharging.domain.lifecycle.LifeCycleType;
import com.evra.ocppcharging.domain.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChargingSessionAggregateTest {

    @Test
    void shouldRebuildStateFromEventHistory() {
        ChargingSessionAggregate aggregate = new ChargingSessionAggregate();
        List<DomainEvent> history = new ArrayList<>();

        ChargingLifeCycle start = new ChargingLifeCycle("CHG-1", LifeCycleType.START_TRANSACTION, "TX-1", 100L,
                Instant.parse("2026-02-25T10:00:00Z"), "k1", null, null, 1);
        history.add(aggregate.start(start));
        history.forEach(aggregate::apply);

        ChargingLifeCycle meter = new ChargingLifeCycle("CHG-1", LifeCycleType.METER_VALUES, "TX-1", 125L,
                Instant.parse("2026-02-25T10:05:00Z"), "k2", null, null, 1);
        DomainEvent meterEvent = aggregate.recordMeterValue(meter).getRecordedEvent();
        history.add(meterEvent);

        ChargingSessionAggregate rebuilt = ChargingSessionAggregate.rehydrate(history);

        assertEquals(SessionStatus.ACTIVE, rebuilt.getSessionStatus());
        assertEquals(25L, rebuilt.getTotalEnergyConsumedWh());
        assertEquals(125L, rebuilt.getCurrentMeterValue());
        assertEquals(2, rebuilt.getEventCount());
    }

    @Test
    void shouldRejectDecreasingMeterValue() {
        ChargingSessionAggregate aggregate = new ChargingSessionAggregate();
        aggregate.apply(aggregate.start(new ChargingLifeCycle("CHG-1", LifeCycleType.START_TRANSACTION, "TX-1", 100L,
                Instant.parse("2026-02-25T10:00:00Z"), "k1", null, null, 1)));

        ChargingSessionAggregate.MeterComputation result = aggregate.recordMeterValue(
                new ChargingLifeCycle("CHG-1", LifeCycleType.METER_VALUES, "TX-1", 90L,
                        Instant.parse("2026-02-25T10:01:00Z"), "k2", null, null, 1)
        );

        assertFalse(result.isAccepted());
        assertNotNull(result.getAnomalyEvent());
        assertEquals("meter value decreased", result.getAnomalyEvent().getReason());
    }
}
