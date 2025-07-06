package com.lititi.exams.commons2.exception;

/**
 * @author Haifeng
 * @version ReturnMessageException.java, v 0.1 2020/04/21 16:11
 * @company 杭州利提提
 */
public class ReturnMessageException extends RuntimeException {

    private static final long serialVersionUID = -4055805895986102100L;

    public ReturnMessageException(String message) {
        super(message);
    }

    public ReturnMessageException() {}
}
