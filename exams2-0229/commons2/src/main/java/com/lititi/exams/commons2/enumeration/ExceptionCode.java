package com.lititi.exams.commons2.enumeration;

public enum ExceptionCode {
    NOT_FOUND(0, "404"),
    REDIRECT(1, "转发"),
    BUSINESS_ERROR(2, "系统异常,请联系管理员"),
    NOT_LOGIN(3, "未登录"),
    NOT_IDENTITY_AUTH(4, "身份未认证"),
    NOT_BIND_MOBILE_PHONE(5, "未绑定手机号"),
    METHOD_NOT_SUPPORTED(6, "方法不支持"),
    NOT_REGIST_MEMBER(7, "未注册会员"),
    DATABASE_CONNECTION_FAILURE(10, "数据库链接异常"),
    PARAMETER_MISSING_EXCEPTION(11, "参数缺失"),
    PARAMETER_TYPE_MISMATCH_EXCEPTION(12, "参数类型不匹配"),
    HTTP_MESSAGE_NOT_READABLE_EXCEPTION(13, "请求参数解析出错"),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION(14, "http媒体类型不支持"),
    NOT_BIND_INVITATION_CODE(15, "未绑定邀请码"),
    INCOMPLETE_INFO(16, "信息填写不完整"),
    INSUFFICIENT_AUTHORITY(17, "用户权限不足"),
    SERVER_FAILURE(99999, "服务器故障");

    private final int code;
    private final String message;

    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;

    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    /**
     * 通过statusCode获取ErrorCode
     *
     * @param code
     *            异常码
     * @return 返回和statusCode对应的ErrorCode，如果没有匹配的
     */
    public static ExceptionCode getErrorCode(int code) {
        for (ExceptionCode exceptionCode : values()) {
            if (exceptionCode.code == code) {
                return exceptionCode;
            }
        }
        return BUSINESS_ERROR;
    }
}
