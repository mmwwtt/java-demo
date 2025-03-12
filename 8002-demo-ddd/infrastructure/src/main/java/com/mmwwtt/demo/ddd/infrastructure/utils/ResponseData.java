package com.mmwwtt.demo.ddd.infrastructure.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Response data.
 *
 * @param <T> the type parameter
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData<T> {
    private String status;
    private String error;
    private T data;

    /**
     * Success response data.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the response data
     */
    public static <T> ResponseData success(T data) {
        return new ResponseData(Constants.SUCCESS, null, data);
    }

    /**
     * Error response data.
     *
     * @param <T>   the type parameter
     * @param error the error
     * @return the response data
     */
    public static <T> ResponseData error(String error) {
        return new ResponseData(Constants.ERROR, error, null);
    }
}
