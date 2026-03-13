package com.evra.ocppcharging.query.repository;

import com.evra.ocppcharging.query.model.ChargerStatsView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryChargerStatsRepository implements ChargerStatsRepository {

    private final ConcurrentMap<String, ChargerStatsView> chargers = new ConcurrentHashMap<>();

    @Override
    public ChargerStatsView save(ChargerStatsView chargerStatsView) {
        chargers.put(chargerStatsView.getChargerId(), chargerStatsView);
        return chargerStatsView;
    }

    @Override
    public Optional<ChargerStatsView> findByChargerId(String chargerId) {
        return Optional.ofNullable(chargers.get(chargerId));
    }

    @Override
    public List<ChargerStatsView> findAll() {
        return chargers.values().stream()
                .sorted(java.util.Comparator.comparing(ChargerStatsView::getChargerId))
                .toList();
    }
}
