package com.evra.ocppcharging.infrastructure.eventbus;

import com.evra.ocppcharging.domain.event.DomainEvent;
import com.evra.ocppcharging.query.projection.ChargingProjection;
import org.springframework.stereotype.Component;

@Component
public class SynchronousDomainEventPublisher implements DomainEventPublisher {

    private final ChargingProjection chargingProjection;

    public SynchronousDomainEventPublisher(ChargingProjection chargingProjection) {
        this.chargingProjection = chargingProjection;
    }

    @Override
    public void publish(DomainEvent event) {
        chargingProjection.project(event);
    }
}
