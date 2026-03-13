package com.evra.ocppcharging.infrastructure.idempotency;

import java.util.Optional;

public interface IdempotencyStore {
    Optional<CommandResult> find(String key);
    void put(String key, CommandResult result);
}
