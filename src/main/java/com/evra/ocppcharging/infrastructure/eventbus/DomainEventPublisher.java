package com.evra.ocppcharging.infrastructure.eventbus;

import com.evra.ocppcharging.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
