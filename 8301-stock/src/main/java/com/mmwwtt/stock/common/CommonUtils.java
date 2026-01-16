package com.mmwwtt.stock.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.mmwwtt.stock.common.Constants.TOLERANCE;

public class CommonUtils {

    public static boolean isEquals(double num1, double num2) {
        return Math.abs(num1 - num2) < TOLERANCE;
    }

    public static BigDecimal multiply(BigDecimal num1, BigDecimal num2) {
        return num1.multiply(num2);
    }

    public static BigDecimal multiply(BigDecimal num1, String num2) {
        return multiply(num1, new BigDecimal(num2));
    }


    public static BigDecimal divide(BigDecimal num1, BigDecimal num2) {
        return num1.divide(num2, 5, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal num1, String num2) {
        return divide(num1, new BigDecimal(num2));
    }

    public static Boolean moreThan(BigDecimal num1, BigDecimal num2) {
        if (num1 == null || num2 == null) {
            return false;
        }
        return num1.compareTo(num2) > 0;
    }

    public static Boolean moreThan(BigDecimal num1, String num2) {
        return moreThan(num1, new BigDecimal(num2));
    }


    public static Boolean lessThan(BigDecimal num1, BigDecimal num2) {
        if (num1 == null || num2 == null) {
            return false;
        }
        return num1.compareTo(num2) < 0;
    }

    public static Boolean lessThan(BigDecimal num1, String num2) {
        return lessThan(num1, new BigDecimal(num2));
    }

    public static BigDecimal max(BigDecimal... nums) {
        BigDecimal res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            res = res.max(nums[i]);
        }
        return res;
    }

    public static BigDecimal min(BigDecimal... nums) {
        BigDecimal res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            res = res.min(nums[i]);
        }
        return res;
    }
}
