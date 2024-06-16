package com.mmwwtt.demo.se.泛型;

/**
 * 泛型声明
 * @param <T>
 */
public class Message<T> {

    public T data;

    public  <T> T getDate(T data) {
        return data;
    }

}
