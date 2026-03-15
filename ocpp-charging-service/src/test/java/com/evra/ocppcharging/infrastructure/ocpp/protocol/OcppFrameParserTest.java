package com.evra.ocppcharging.infrastructure.ocpp.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OcppFrameParserTest {

    @Test
    void shouldParseCallFrame() {
        OcppFrameParser parser = new OcppFrameParser(new ObjectMapper());
        OcppCallFrame frame = parser.parseCall("[2,\"msg-1\",\"BootNotification\",{\"chargePointModel\":\"X\",\"timestamp\":\"2026-02-25T10:00:00Z\"}]");

        assertEquals("msg-1", frame.getUniqueId());
        assertEquals("BootNotification", frame.getAction());
    }
}
