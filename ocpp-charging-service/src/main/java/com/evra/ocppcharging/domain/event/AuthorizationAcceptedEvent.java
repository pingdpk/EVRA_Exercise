package com.evra.ocppcharging.domain.event;

import java.time.Instant;

public class AuthorizationAcceptedEvent extends AbstractDomainEvent {

    private final String authorizationId;

    public AuthorizationAcceptedEvent(String chargerId, Instant occurredAt, String authorizationId) {
        super(chargerId, null, occurredAt);
        this.authorizationId = authorizationId;
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    @Override
    public String eventType() {
        return "AuthorizationAccepted";
    }
}
