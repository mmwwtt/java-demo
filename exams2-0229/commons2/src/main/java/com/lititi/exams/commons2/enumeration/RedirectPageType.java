package com.lititi.exams.commons2.enumeration;

public enum RedirectPageType {
    NORMAL(0, "正常"),
    LOGIN(1, "登录"),
    BIND_MOBILE_PHONE(2, "绑定手机号"),
    REGIST_MEMBER(3, "注册会员");

    private final int code;
    private final String label;

    RedirectPageType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
