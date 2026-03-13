package com.evra.ocppcharging.query.repository;

import com.evra.ocppcharging.query.model.ChargerStatsView;

import java.util.List;
import java.util.Optional;

public interface ChargerStatsRepository {
    ChargerStatsView save(ChargerStatsView chargerStatsView);
    Optional<ChargerStatsView> findByChargerId(String chargerId);
    List<ChargerStatsView> findAll();
}
