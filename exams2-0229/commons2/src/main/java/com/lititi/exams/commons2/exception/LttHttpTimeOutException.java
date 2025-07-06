package com.lititi.exams.commons2.exception;

/**
 * 
 * @company 杭州利提提科技有限公司
 * @author Kevin
 * @version LttHttpTimeOutException.java, v 0.1 May 30, 2019 3:18:00 PM
 */
public class LttHttpTimeOutException extends LttException {

    private static final long serialVersionUID = 1L;

    public LttHttpTimeOutException(String message) {
        this.errorMsg = message;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

}
