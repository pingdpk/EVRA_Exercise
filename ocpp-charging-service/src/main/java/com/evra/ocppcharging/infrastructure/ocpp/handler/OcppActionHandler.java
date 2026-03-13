package com.evra.ocppcharging.infrastructure.ocpp.handler;

import com.evra.ocppcharging.infrastructure.ocpp.protocol.OcppCallFrame;

public interface OcppActionHandler {

    String action();

    String handle(String chargerId, OcppCallFrame frame);
}
