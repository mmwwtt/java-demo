package com.lititi.exams.commons2.enumeration;

public enum NumberSignEnum {

    PLUS_SIGN(0, "正号"), MINUS_SIGN(1, "负号");

    private final Integer code;
    private final String comment;

    NumberSignEnum(Integer code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public Integer code() {
        return code;
    }

    public String comment() {
        return comment;
    }

    public static NumberSignEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NumberSignEnum flag : NumberSignEnum.values()) {
            if (flag.code.equals(code)) {
                return flag;
            }
        }
        return null;
    }
}
