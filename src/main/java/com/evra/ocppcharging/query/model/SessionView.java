package com.evra.ocppcharging.query.model;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SessionView {

    private final String transactionId;
    private final String chargerId;
    private long meterStart;
    private long meterEnd;
    private long totalEnergyConsumedWh;
    private Instant startTime;
    private Instant endTime;
    private final List<String> anomalies;
    private int eventCount;

    public SessionView(String transactionId, String chargerId) {
        this.transactionId = transactionId;
        this.chargerId = chargerId;
        this.anomalies = new ArrayList<>();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getChargerId() {
        return chargerId;
    }

    public long getMeterStart() {
        return meterStart;
    }

    public void setMeterStart(long meterStart) {
        this.meterStart = meterStart;
    }

    public long getMeterEnd() {
        return meterEnd;
    }

    public void setMeterEnd(long meterEnd) {
        this.meterEnd = meterEnd;
    }

    public long getTotalEnergyConsumedWh() {
        return totalEnergyConsumedWh;
    }

    public void setTotalEnergyConsumedWh(long totalEnergyConsumedWh) {
        this.totalEnergyConsumedWh = totalEnergyConsumedWh;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<String> getAnomalies() {
        return anomalies;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void incrementEventCount() {
        this.eventCount++;
    }

    public Long getDurationSeconds() {
        if (startTime == null || endTime == null) {
            return null;
        }
        return endTime.getEpochSecond() - startTime.getEpochSecond();
    }
}
