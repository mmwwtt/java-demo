package com.mmwwtt.demo.se.common;

import lombok.Data;

/**
 * 泛型声明
 * @param <T>
 */
@Data
public class Message<T> {

    public T data;

}
