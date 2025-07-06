package com.lititi.exams.commons2.enumeration;

public enum RedisDB {

    SESSION(0, "session"), OTHER(1, "other"),
    ;
    /**
     * value同时作为db索引
     **/
    private final int value;
    private final String label;

    RedisDB(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static RedisDB getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RedisDB status : RedisDB.values()) {
            if (code.byteValue() == status.value()) {
                return status;
            }
        }
        return null;
    }

    public Integer value() {
        return value;
    }

    public String label() {
        return label;
    }

}
