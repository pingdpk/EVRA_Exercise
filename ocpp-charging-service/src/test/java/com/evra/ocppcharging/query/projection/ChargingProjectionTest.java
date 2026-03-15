package com.evra.ocppcharging.query.projection;

import com.evra.ocppcharging.domain.event.MeterValueRecordedEvent;
import com.evra.ocppcharging.domain.event.SessionStartedEvent;
import com.evra.ocppcharging.domain.model.SessionStatus;
import com.evra.ocppcharging.query.repository.InMemoryChargerStatsRepository;
import com.evra.ocppcharging.query.repository.InMemorySessionViewRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChargingProjectionTest {

    @Test
    void shouldProjectSessionAndChargerViews() {
        InMemorySessionViewRepository sessions = new InMemorySessionViewRepository();
        InMemoryChargerStatsRepository chargers = new InMemoryChargerStatsRepository();
        ChargingProjection projection = new ChargingProjection(sessions, chargers);

        projection.project(new SessionStartedEvent("CHG-1", "TX-1", Instant.parse("2026-02-25T10:00:00Z"), 100L, 1));
        projection.project(new MeterValueRecordedEvent("CHG-1", "TX-1", Instant.parse("2026-02-25T10:05:00Z"), 140L, 40L));

        assertEquals(SessionStatus.ACTIVE, sessions.findByTransactionId("TX-1").orElseThrow().getStatus());
        assertEquals(40L, sessions.findByTransactionId("TX-1").orElseThrow().getTotalEnergyConsumedWh());
        assertEquals(40L, chargers.findByChargerId("CHG-1").orElseThrow().getTotalEnergyConsumedWh());
    }
}
