package com.lititi.exams.commons2.exception;

/**
 * @author shangguan
 * @version ReturnEmptyResultException.java, v 0.1 2020/04/21 16:11
 * @company 杭州利提提
 */
public class ReturnEmptyResultException extends RuntimeException {

    private static final long serialVersionUID = 4658192327599362985L;

    public ReturnEmptyResultException(String message) {
        super(message);
    }

    public ReturnEmptyResultException() {}
}
