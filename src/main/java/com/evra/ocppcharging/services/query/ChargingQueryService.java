package com.evra.ocppcharging.services.query;

import com.evra.ocppcharging.infrastructure.eventstoer.EventEnvelope;
import com.evra.ocppcharging.infrastructure.eventstoer.EventStore;
import com.evra.ocppcharging.query.model.ChargerStatsView;
import com.evra.ocppcharging.query.model.SessionView;
import com.evra.ocppcharging.query.repository.ChargerStatsRepository;
import com.evra.ocppcharging.query.repository.SessionViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChargingQueryService {

    private final SessionViewRepository sessionViewRepository;
    private final ChargerStatsRepository chargerStatsRepository;
    private final EventStore eventStore;

    public ChargingQueryService(SessionViewRepository sessionViewRepository,
                                ChargerStatsRepository chargerStatsRepository,
                                EventStore eventStore) {
        this.sessionViewRepository = sessionViewRepository;
        this.chargerStatsRepository = chargerStatsRepository;
        this.eventStore = eventStore;
    }

    public List<SessionView> listSessions() {
        return sessionViewRepository.findAll();
    }

    public SessionView getSession(String transactionId) {
        return sessionViewRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new java.util.NoSuchElementException("session not found: " + transactionId));
    }

    public List<EventEnvelope> getSessionEvents(String transactionId) {
        return eventStore.loadEnvelopes("session:" + transactionId);
    }

    public List<ChargerStatsView> listChargers() {
        return chargerStatsRepository.findAll();
    }

    public ChargerStatsView getChargerStats(String chargerId) {
        return chargerStatsRepository.findByChargerId(chargerId)
                .orElseThrow(() -> new java.util.NoSuchElementException("charger not found: " + chargerId));
    }
}
