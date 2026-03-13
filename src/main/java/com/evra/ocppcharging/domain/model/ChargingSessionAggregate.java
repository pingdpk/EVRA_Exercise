package com.evra.ocppcharging.domain.model;

import com.evra.ocppcharging.domain.event.*;
import com.evra.ocppcharging.domain.exception.InvalidSessionStateException;
import com.evra.ocppcharging.domain.exception.ValidationException;
import com.evra.ocppcharging.domain.lifecycle.ChargingLifeCycle;

import java.util.ArrayList;
import java.util.List;

public class ChargingSessionAggregate {

    private String chargerId;
    private String transactionId;
    private SessionStatus sessionStatus = SessionStatus.NONE;
    private Long meterStart;
    private Long currentMeterValue;
    private Long meterEnd;
    private long totalEnergyConsumedWh;
    private int eventCount;
    private final List<String> anomalies = new ArrayList<>();

    public static ChargingSessionAggregate rehydrate(List<DomainEvent> history) {
        ChargingSessionAggregate aggregate = new ChargingSessionAggregate();
        history.forEach(aggregate::apply);
        return aggregate;
    }

    public SessionStartedEvent start(ChargingLifeCycle command) {
        if (command.getTransactionId() == null || command.getTransactionId().isBlank()) {
            throw new ValidationException("transactionId is required for StartTransaction");
        }
        if (command.getMeterValue() == null) {
            throw new ValidationException("meterValue is required for StartTransaction");
        }
        if (sessionStatus == SessionStatus.ACTIVE) {
            throw new InvalidSessionStateException("session is already active");
        }
        return new SessionStartedEvent(command.getChargerId(), command.getTransactionId(), command.getTimestamp(), command.getMeterValue(), command.getConnectorId());
    }

    public MeterComputation recordMeterValue(ChargingLifeCycle command) {
        ensureActive(command.getTransactionId());
        if (command.getMeterValue() == null) {
            throw new ValidationException("meterValue is required for MeterValues");
        }

        long previous = currentMeterValue == null ? meterStart : currentMeterValue;
        long attempted = command.getMeterValue();

        if (attempted < previous) {
            return MeterComputation.rejected(new AnomalyDetectedEvent(
                    command.getChargerId(),
                    command.getTransactionId(),
                    command.getTimestamp(),
                    "meter value decreased",
                    previous,
                    attempted));
        }

        long delta = Math.max(0, attempted - previous);
        return MeterComputation.accepted(new MeterValueRecordedEvent(
                command.getChargerId(),
                command.getTransactionId(),
                command.getTimestamp(),
                attempted,
                delta));
    }

    public SessionCompletedEvent stop(ChargingLifeCycle command) {
        ensureActive(command.getTransactionId());
        if (command.getMeterValue() == null) {
            throw new ValidationException("meterValue is required for StopTransaction");
        }

        long previous = currentMeterValue == null ? meterStart : currentMeterValue;
        long attempted = command.getMeterValue();
        if (attempted < previous) {
            throw new ValidationException("stop meter value cannot be lower than previous meter value");
        }
        long total = totalEnergyConsumedWh + (attempted - previous);
        return new SessionCompletedEvent(command.getChargerId(), command.getTransactionId(), command.getTimestamp(), attempted, total);
    }

    public ChargerStatusChangedEvent statusChanged(ChargingLifeCycle command) {
        return new ChargerStatusChangedEvent(command.getChargerId(), command.getTimestamp(), ChargerStatus.fromExternal(command.getStatus()));
    }

    public void apply(DomainEvent event) {
        if (event instanceof SessionStartedEvent started) {
            this.chargerId = started.chargerId();
            this.transactionId = started.transactionId();
            this.sessionStatus = SessionStatus.ACTIVE;
            this.meterStart = started.getMeterStart();
            this.currentMeterValue = started.getMeterStart();
            this.meterEnd = null;
            this.totalEnergyConsumedWh = 0;
            this.eventCount++;
        } else if (event instanceof MeterValueRecordedEvent meter) {
            this.currentMeterValue = meter.getMeterValue();
            this.totalEnergyConsumedWh += meter.getDeltaWh();
            this.eventCount++;
        } else if (event instanceof SessionCompletedEvent completed) {
            this.currentMeterValue = completed.getMeterStop();
            this.meterEnd = completed.getMeterStop();
            this.totalEnergyConsumedWh = completed.getTotalEnergyConsumedWh();
            this.sessionStatus = SessionStatus.COMPLETED;
            this.eventCount++;
        } else if (event instanceof AnomalyDetectedEvent anomaly) {
            this.anomalies.add(anomaly.getReason());
            this.eventCount++;
        } else if (event instanceof ChargerStatusChangedEvent) {
            this.eventCount++;
        }
    }

    private void ensureActive(String requestedTransactionId) {
        if (sessionStatus != SessionStatus.ACTIVE) {
            throw new InvalidSessionStateException("no active session for transaction " + requestedTransactionId);
        }
        if (transactionId != null && requestedTransactionId != null && !transactionId.equals(requestedTransactionId)) {
            throw new InvalidSessionStateException("transaction mismatch: expected " + transactionId + " but got " + requestedTransactionId);
        }
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public String getChargerId() {
        return chargerId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getMeterStart() {
        return meterStart;
    }

    public Long getCurrentMeterValue() {
        return currentMeterValue;
    }

    public Long getMeterEnd() {
        return meterEnd;
    }

    public long getTotalEnergyConsumedWh() {
        return totalEnergyConsumedWh;
    }

    public int getEventCount() {
        return eventCount;
    }

    public List<String> getAnomalies() {
        return List.copyOf(anomalies);
    }

    public static final class MeterComputation {
        private final boolean accepted;
        private final MeterValueRecordedEvent recordedEvent;
        private final AnomalyDetectedEvent anomalyEvent;

        private MeterComputation(boolean accepted, MeterValueRecordedEvent recordedEvent, AnomalyDetectedEvent anomalyEvent) {
            this.accepted = accepted;
            this.recordedEvent = recordedEvent;
            this.anomalyEvent = anomalyEvent;
        }

        public static MeterComputation accepted(MeterValueRecordedEvent event) {
            return new MeterComputation(true, event, null);
        }

        public static MeterComputation rejected(AnomalyDetectedEvent event) {
            return new MeterComputation(false, null, event);
        }

        public boolean isAccepted() {
            return accepted;
        }

        public MeterValueRecordedEvent getRecordedEvent() {
            return recordedEvent;
        }

        public AnomalyDetectedEvent getAnomalyEvent() {
            return anomalyEvent;
        }
    }
}
