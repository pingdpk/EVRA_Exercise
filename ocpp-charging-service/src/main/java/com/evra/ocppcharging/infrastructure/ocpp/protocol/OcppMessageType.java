package com.evra.ocppcharging.infrastructure.ocpp.protocol;

public enum OcppMessageType {
    CALL(2),
    CALL_RESULT(3),
    CALL_ERROR(4);

    private final int code;

    OcppMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OcppMessageType fromCode(int code) {
        for (OcppMessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("unsupported OCPP message type: " + code);
    }
}
