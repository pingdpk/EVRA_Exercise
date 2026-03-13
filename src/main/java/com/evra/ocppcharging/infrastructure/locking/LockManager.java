package com.evra.ocppcharging.infrastructure.locking;

import java.util.concurrent.Callable;

public interface LockManager {
    <T> T executeWithLock(String key, Callable<T> action);
}
