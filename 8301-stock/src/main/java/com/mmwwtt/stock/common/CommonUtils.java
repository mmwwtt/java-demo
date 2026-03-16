package com.mmwwtt.stock.common;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.mmwwtt.stock.common.Constants.TOLERANCE;

public class CommonUtils {

    public static boolean isEquals(double num1, double num2) {
        return Math.abs(num1 - num2) < TOLERANCE;
    }


    public static double subtract(double num1, double num2) {
        return num1 - num2;
    }

    public static double add(double num1, double num2) {
        return num1 + num2;
    }

    public static double multiply(double num1, double num2) {
        return num1 * num2;
    }


    public static double divide(double num1, double num2) {
        if (num2 == 0) {
            return 0;
        }
        return num1 / num2;
    }

    public static Boolean moreThan(double num1, double num2) {
        return num1 > num2 + TOLERANCE;
    }

    public static Boolean moreThan(double... nums) {
        double obj = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (!moreThan(obj, nums[i])) {
                return false;
            }
            obj = nums[i];
        }
        return true;
    }

    public static Boolean lessThan(double num1, double num2) {
        return num1 + TOLERANCE < num2;
    }


    /**
     * num  是否在 范围中
     */
    public static Boolean isInRange(double num, double leftRange, double rightRange) {
        return moreThan(num, leftRange) && lessThan(num, rightRange);
    }


    public static double max(double... nums) {
        double res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            res = Math.max(res, nums[i]);
        }
        return res;
    }

    public static double max(List<Double> list) {
        double res = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            res = Math.max(res, list.get(i));
        }
        return res;
    }

    public static double min(double... nums) {
        double res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            res = Math.min(res, nums[i]);
        }
        return res;
    }

    public static double min(List<Double> list) {
        double res = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            res = Math.min(res, list.get(i));
        }
        return res;
    }


    public static double sum(double... nums) {
        double res = 0;
        for (double num : nums) {
            res += num;
        }
        return res;
    }

    public static double sum(List<Double> list) {
        double res = 0;
        for (double num : list) {
            res += num;
        }
        return res;
    }


    private static BigDecimal toBigDecimal(Object obj) {
        if (obj instanceof Long) {
            return new BigDecimal((Long) obj);
        } else if (obj instanceof Integer) {
            return new BigDecimal((Integer) obj);
        } else if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        } else if (obj instanceof Double) {
            return BigDecimal.valueOf((Double) obj);
        } else if (obj instanceof Float) {
            return BigDecimal.valueOf((Float) obj);
        } else if (obj instanceof String) {
            return new BigDecimal((String) obj);
        }
        return null;
    }

    public static String getTimeStr() {
        return LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }


    public static String getDateStr() {
        return LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }


    /**
     * 计算品均值
     */
    public static double getAverage(List<Double> list) {
        return divide(sum(list), list.size());
    }

    public static double getAverage(double[] list) {
        return divide(sum(list), list.length);
    }

    /**
     * 计算中位数
     */
    public static double getMiddle(List<Double> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        List<Double> sortList = list.stream()
                .sorted(Comparator.comparing(Double::doubleValue))
                .toList();
        return sortList.get(sortList.size() / 2);
    }

    public static double getMiddle(double[] list) {
        if (Objects.isNull(list) || list.length == 0) {
            return 0;
        }
        Arrays.sort(list);
        return list[list.length / 2];
    }

    /**
     * 计算涨幅 第一个比第二个涨了多少
     */
    public static double getRise(double price1, double price2) {
        return divide(subtract(price1, price2), price2);
    }
}
