package com.evra.ocppcharging.infrastructure.idempotency;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryIdempotencyStore implements IdempotencyStore {

    private final ConcurrentMap<String, CommandResult> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<CommandResult> find(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public void put(String key, CommandResult result) {
        storage.putIfAbsent(key, result);
    }
}
