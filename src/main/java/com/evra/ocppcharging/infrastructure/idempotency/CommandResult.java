package com.evra.ocppcharging.infrastructure.idempotency;

public class CommandResult {

    private final String eventId;
    private final String status;
    private final String message;
    private final String transactionId;

    public CommandResult(String eventId, String status, String message, String transactionId) {
        this.eventId = eventId;
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }
}
