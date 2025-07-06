package com.lititi.exams.commons2.result;

/**
 * JSON格式类 Created by bin on 2017/2/20.
 */
public enum JsonCodeEnum {
    SUCCESS(1, "成功"), FAIL(-1, "失败"),

    PARAMETER_ERROR(101, "参数有误"),
    CLIENT_TIME_ERROR(102, "请求时间有误"),
    CLIENT_OVER_TIME(103, "请求时间超时"),
    CHECK_CODE_ERROR(104, "校验码错误"),
    DATA_NOT_FOUND(105, "未找到对应数据"),
    DATA_DEALED(106, "对应数据已处理"),

    PARAMETER_INVALID(204, "参数不合法"),
    EXISTING(205, "已存在"),
    UNREGISTERED(206, "未注册"),
    NOT_FIND(207, "未找到"),
    OVERTIME(208, "已超时或过期"),
    EMPTY(209, "空数据"),
    CONFLICT(211, "冲突"),
    BALANCE_NOT_ENOUGH(212, "余额不足"),
    NOT_AUTHORIZATION(213, "没有权限"),
    STATUS_DISABLED(214, "状态不可用"),
    BASIC_INFO_NOT_COMPLETED(215, "基础信息未完善"),
    LOGIN_EXPIRED(216, "该账号已在其他地方登录，请重新登录"),
    NOT_LOGIN(216, "请先登录"),
    INVALID_LOGIN(216, "登录信息已过期，请重新登录"),

    PASSAGEWAY_CARD_PAY(217, "请先绑定交易卡"),
    PAY_PWD(218, "请先设置支付密码"),
    PASSAGEWAY_CARD_W(219, "请先绑定提现卡"),
    NEED_ID_AUTH(220, "请先完成实名认证"),
    NEED_TD_REGISTER(221, "请先通过通道入网"),
    REGISTERED(222, "已注册,请下载或打开app登录"),
    ;

    JsonCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 代码
     */
    private Integer code;
    /**
     * 消息
     */
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "";
    }

    public static Integer successCode() {
        return JsonCodeEnum.SUCCESS.getCode();
    }

    public static Integer failCode() {
        return JsonCodeEnum.FAIL.getCode();
    }

    /**
     * 判断是否是正确返回
     */
    public static boolean isSuccess(JsonCodeEnum jsonCode) {
        return jsonCode != null && jsonCode.equals(JsonCodeEnum.SUCCESS);
    }
}
