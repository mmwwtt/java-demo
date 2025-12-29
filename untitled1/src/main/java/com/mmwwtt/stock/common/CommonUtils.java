package com.mmwwtt.stock.common;

import static com.mmwwtt.stock.common.Constants.TOLERANCE;

public class CommonUtils {

    public static boolean isEquals(double num1, double num2) {
        return Math.abs(num1-num2) < TOLERANCE;
    }
}
