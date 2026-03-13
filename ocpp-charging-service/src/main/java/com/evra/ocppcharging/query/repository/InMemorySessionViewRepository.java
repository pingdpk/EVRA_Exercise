package com.evra.ocppcharging.query.repository;

import com.evra.ocppcharging.query.model.SessionView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemorySessionViewRepository implements SessionViewRepository {

    private final ConcurrentMap<String, SessionView> sessions = new ConcurrentHashMap<>();

    @Override
    public SessionView save(SessionView view) {
        sessions.put(view.getTransactionId(), view);
        return view;
    }

    @Override
    public Optional<SessionView> findByTransactionId(String transactionId) {
        return Optional.ofNullable(sessions.get(transactionId));
    }

    @Override
    public List<SessionView> findAll() {
        return sessions.values().stream()
                .sorted(java.util.Comparator.comparing(SessionView::getTransactionId))
                .toList();
    }
}
