package com.evra.ocppcharging.query.repository;

import com.evra.ocppcharging.query.model.SessionView;

import java.util.List;
import java.util.Optional;

public interface SessionViewRepository {
    SessionView save(SessionView view);
    Optional<SessionView> findByTransactionId(String transactionId);
    List<SessionView> findAll();
}
