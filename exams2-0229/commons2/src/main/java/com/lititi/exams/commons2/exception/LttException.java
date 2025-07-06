package com.lititi.exams.commons2.exception;

import com.lititi.exams.commons2.enumeration.ExceptionCode;
import com.lititi.exams.commons2.object.CommonResultObject;

/**
 * 系统业务异常
 */
public class LttException extends RuntimeException {
    private static final long serialVersionUID = -7819187888527567089L;

    /** 错误消息 */
    protected String errorMsg;
    /** 错误代码 */
    protected ExceptionCode exceptionCode;

    protected CommonResultObject commonResultObject = new CommonResultObject();

    public LttException() {}

    public LttException(Throwable e) {
        super(e);
    }

    public LttException(String message) {
        super(message);
        commonResultObject.buildErrorResult(message);
        this.errorMsg = message;
    }

    /** 带错误代码的异常 */
    public LttException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.errorMsg = message;
        this.commonResultObject.buildErrorResult(message);
    }

    public LttException(ExceptionCode exceptionCode){
        this(exceptionCode, exceptionCode.getMessage());
    }

    public LttException(int errorCode,String errorMsg){
        this(ExceptionCode.getErrorCode(errorCode),errorMsg);
    }

    public LttException(String errorCode, String errorMsg) {
        this(Integer.parseInt(errorCode),errorMsg);
    }

    public LttException(String message, Throwable e) {
        super(message, e);
        this.errorMsg = message;
        this.commonResultObject.buildErrorResult(message);
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ExceptionCode getErrorCode() {
        return exceptionCode;
    }

    public void setErrorCode(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public CommonResultObject getCommonResultObject() {
        return commonResultObject;
    }

    public void setCommonResultObject(CommonResultObject commonResultObject) {
        this.commonResultObject = commonResultObject;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "LttException{" + "errorMsg='" + errorMsg + '\'' + ", exceptionCode=" + exceptionCode + '}';
    }

}
