package com.evra.ocppcharging.query.model;

import com.evra.ocppcharging.domain.model.ChargerStatus;

import java.time.Instant;

public class ChargerStatsView {

    private final String chargerId;
    private ChargerStatus status = ChargerStatus.UNKNOWN;
    private boolean online;
    private String activeTransactionId;
    private long totalEnergyConsumedWh;
    private int completedSessions;
    private int anomalyCount;
    private Instant lastSeenAt;

    public ChargerStatsView(String chargerId) {
        this.chargerId = chargerId;
    }

    public String getChargerId() {
        return chargerId;
    }

    public ChargerStatus getStatus() {
        return status;
    }

    public void setStatus(ChargerStatus status) {
        this.status = status;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getActiveTransactionId() {
        return activeTransactionId;
    }

    public void setActiveTransactionId(String activeTransactionId) {
        this.activeTransactionId = activeTransactionId;
    }

    public long getTotalEnergyConsumedWh() {
        return totalEnergyConsumedWh;
    }

    public void addEnergy(long deltaWh) {
        this.totalEnergyConsumedWh += deltaWh;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void incrementCompletedSessions() {
        this.completedSessions++;
    }

    public int getAnomalyCount() {
        return anomalyCount;
    }

    public void incrementAnomalyCount() {
        this.anomalyCount++;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
