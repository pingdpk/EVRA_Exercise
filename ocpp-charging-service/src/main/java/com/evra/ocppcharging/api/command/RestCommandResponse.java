package com.evra.ocppcharging.api.command;

import com.evra.ocppcharging.infrastructure.idempotency.CommandResult;

public class RestCommandResponse {

    private final String eventId;
    private final String status;
    private final String message;

    public RestCommandResponse(String eventId, String status, String message) {
        this.eventId = eventId;
        this.status = status;
        this.message = message;
    }

    public static RestCommandResponse from(CommandResult result) {
        return new RestCommandResponse(result.getEventId(), result.getStatus(), result.getMessage());
    }

    public String getEventId() {
        return eventId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
