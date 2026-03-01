package com.evra.ocppcharging.api.command;

public class RestCommandResponse {

    private final String eventId;
    private final String status;
    private final String message;

    public RestCommandResponse(String eventId, String status, String message) {
        this.eventId = eventId;
        this.status = status;
        this.message = message;
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
