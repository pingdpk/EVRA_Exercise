package com.evra.ocppcharging.query.projection;

import com.evra.ocppcharging.domain.event.*;
import com.evra.ocppcharging.domain.model.ChargerStatus;
import com.evra.ocppcharging.domain.model.SessionStatus;
import com.evra.ocppcharging.query.model.ChargerStatsView;
import com.evra.ocppcharging.query.model.SessionView;
import com.evra.ocppcharging.query.repository.ChargerStatsRepository;
import com.evra.ocppcharging.query.repository.SessionViewRepository;
import org.springframework.stereotype.Component;

@Component
public class ChargingProjection {

    private final SessionViewRepository sessionViewRepository;
    private final ChargerStatsRepository chargerStatsRepository;

    public ChargingProjection(SessionViewRepository sessionViewRepository,
                              ChargerStatsRepository chargerStatsRepository) {
        this.sessionViewRepository = sessionViewRepository;
        this.chargerStatsRepository = chargerStatsRepository;
    }

    public void project(DomainEvent event) {
        if (event instanceof SessionStartedEvent started) {
            SessionView sessionView = new SessionView(started.transactionId(), started.chargerId());
            sessionView.setMeterStart(started.getMeterStart());
            sessionView.setMeterEnd(started.getMeterStart());
            sessionView.setStartTime(started.occurredAt());
            sessionView.setStatus(SessionStatus.ACTIVE);
            sessionView.incrementEventCount();
            sessionViewRepository.save(sessionView);

            ChargerStatsView charger = findOrCreateCharger(started.chargerId());
            charger.setOnline(true);
            charger.setStatus(ChargerStatus.CHARGING);
            charger.setActiveTransactionId(started.transactionId());
            charger.setLastSeenAt(started.occurredAt());
            chargerStatsRepository.save(charger);
            return;
        }

        if (event instanceof MeterValueRecordedEvent recorded) {
            sessionViewRepository.findByTransactionId(recorded.transactionId()).ifPresent(session -> {
                session.setMeterEnd(recorded.getMeterValue());
                session.setTotalEnergyConsumedWh(session.getTotalEnergyConsumedWh() + recorded.getDeltaWh());
                session.incrementEventCount();
                sessionViewRepository.save(session);
            });

            ChargerStatsView charger = findOrCreateCharger(recorded.chargerId());
            charger.addEnergy(recorded.getDeltaWh());
            charger.setLastSeenAt(recorded.occurredAt());
            chargerStatsRepository.save(charger);
            return;
        }

        if (event instanceof SessionCompletedEvent completed) {
            sessionViewRepository.findByTransactionId(completed.transactionId()).ifPresent(session -> {
                session.setMeterEnd(completed.getMeterStop());
                session.setTotalEnergyConsumedWh(completed.getTotalEnergyConsumedWh());
                session.setEndTime(completed.occurredAt());
                session.setStatus(SessionStatus.COMPLETED);
                session.incrementEventCount();
                sessionViewRepository.save(session);
            });

            ChargerStatsView charger = findOrCreateCharger(completed.chargerId());
            charger.setStatus(ChargerStatus.AVAILABLE);
            charger.setActiveTransactionId(null);
            charger.setLastSeenAt(completed.occurredAt());
            charger.incrementCompletedSessions();
            chargerStatsRepository.save(charger);
            return;
        }

        if (event instanceof AnomalyDetectedEvent anomaly) {
            sessionViewRepository.findByTransactionId(anomaly.transactionId()).ifPresent(session -> {
                session.getAnomalies().add(anomaly.getReason());
                session.incrementEventCount();
                sessionViewRepository.save(session);
            });

            ChargerStatsView charger = findOrCreateCharger(anomaly.chargerId());
            charger.incrementAnomalyCount();
            charger.setLastSeenAt(anomaly.occurredAt());
            chargerStatsRepository.save(charger);
            return;
        }

        if (event instanceof ChargerStatusChangedEvent statusChanged) {
            ChargerStatsView charger = findOrCreateCharger(statusChanged.chargerId());
            charger.setStatus(statusChanged.getChargerStatus());
            charger.setOnline(true);
            charger.setLastSeenAt(statusChanged.occurredAt());
            chargerStatsRepository.save(charger);
            return;
        }

        if (event instanceof ChargerBootedEvent booted) {
            ChargerStatsView charger = findOrCreateCharger(booted.chargerId());
            charger.setOnline(true);
            charger.setStatus(ChargerStatus.AVAILABLE);
            charger.setLastSeenAt(booted.occurredAt());
            chargerStatsRepository.save(charger);
            return;
        }

        if (event instanceof AuthorizationAcceptedEvent authorizationAcceptedEvent) {
            ChargerStatsView charger = findOrCreateCharger(authorizationAcceptedEvent.chargerId());
            charger.setOnline(true);
            charger.setLastSeenAt(authorizationAcceptedEvent.occurredAt());
            chargerStatsRepository.save(charger);
        }
    }

    private ChargerStatsView findOrCreateCharger(String chargerId) {
        return chargerStatsRepository.findByChargerId(chargerId).orElseGet(() -> new ChargerStatsView(chargerId));
    }
}
