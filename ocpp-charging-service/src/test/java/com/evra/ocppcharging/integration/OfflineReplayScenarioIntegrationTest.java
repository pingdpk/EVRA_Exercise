package com.evra.ocppcharging.integration;

import com.evra.ocppcharging.domain.lifecycle.ChargingLifeCycle;
import com.evra.ocppcharging.domain.lifecycle.LifeCycleType;
import com.evra.ocppcharging.services.command.ChargingCommandService;
import com.evra.ocppcharging.services.query.ChargingQueryService;
import com.evra.ocppcharging.query.model.SessionView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OfflineReplayScenarioIntegrationTest {

    @Autowired
    private ChargingCommandService chargingCommandService;

    @Autowired
    private ChargingQueryService chargingQueryService;

    @Test
    void shouldHandleOfflineReplayWithoutInflatingTotals() {
        String chargerId = "CHG-101";
        String transactionId = "TX-555";

        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.START_TRANSACTION, transactionId, 1200L,
                Instant.parse("2026-02-25T10:00:00Z"), "start-1", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1250L,
                Instant.parse("2026-02-25T10:05:00Z"), "m1", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1300L,
                Instant.parse("2026-02-25T10:10:00Z"), "m2", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1350L,
                Instant.parse("2026-02-25T10:15:00Z"), "m3", null, null, 1));

        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1250L,
                Instant.parse("2026-02-25T10:05:00Z"), "m1", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1300L,
                Instant.parse("2026-02-25T10:10:00Z"), "m2", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1350L,
                Instant.parse("2026-02-25T10:15:00Z"), "m3", null, null, 1));

        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1400L,
                Instant.parse("2026-02-25T10:20:00Z"), "m4", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.METER_VALUES, transactionId, 1500L,
                Instant.parse("2026-02-25T10:25:00Z"), "m5", null, null, 1));
        chargingCommandService.handle(new ChargingLifeCycle(chargerId, LifeCycleType.STOP_TRANSACTION, transactionId, 1550L,
                Instant.parse("2026-02-25T10:30:00Z"), "stop-1", null, null, 1));

        SessionView session = chargingQueryService.getSession(transactionId);

        assertEquals(350L, session.getTotalEnergyConsumedWh());
        assertEquals(7, chargingQueryService.getSessionEvents(transactionId).size());
    }
}
