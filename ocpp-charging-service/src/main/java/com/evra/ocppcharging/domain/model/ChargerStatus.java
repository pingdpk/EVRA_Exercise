package com.evra.ocppcharging.domain.model;

public enum ChargerStatus {
    AVAILABLE,
    CHARGING,
    FAULTED,
    UNAVAILABLE,
    PREPARING,
    SUSPENDEDEV,
    SUSPENDEDEVSE,
    FINISHING,
    RESERVED,
    UNKNOWN;

    public static ChargerStatus fromExternal(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        String normalized = value.trim().replace(" ", "").replace("-", "").toUpperCase();
        for (ChargerStatus candidate : values()) {
            if (candidate.name().equals(normalized)) {
                return candidate;
            }
        }
        return UNKNOWN;
    }
}
