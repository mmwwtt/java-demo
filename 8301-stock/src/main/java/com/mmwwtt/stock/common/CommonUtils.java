package com.mmwwtt.stock.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mmwwtt.stock.common.Constants.TOLERANCE;

public class CommonUtils {

    public static boolean isEquals(double num1, double num2) {
        return Math.abs(num1 - num2) < TOLERANCE;
    }

    public static Boolean isEquals(BigDecimal num1, BigDecimal num2) {
        return num1.compareTo(num2) ==0;
    }

    public static BigDecimal subtract(Object num1, Object num2) {
        BigDecimal tmp1 = toBigDecimal(num1);
        BigDecimal tmp2 = toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return null;
        }
        return tmp1.subtract(tmp2);
    }

    public static BigDecimal add(Object num1, Object num2) {
        BigDecimal tmp1 = Objects.isNull(num1) ? BigDecimal.ZERO : toBigDecimal(num1);
        BigDecimal tmp2 = Objects.isNull(num2) ? BigDecimal.ZERO : toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return null;
        }
        return tmp1.add(tmp2);
    }

    public static BigDecimal multiply(Object num1, Object num2) {
        BigDecimal tmp1 = toBigDecimal(num1);
        BigDecimal tmp2 = toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return null;
        }
        return tmp1.multiply(tmp2);
    }


    public static BigDecimal divide(Object num1, Object num2) {
        BigDecimal tmp1 = toBigDecimal(num1);
        BigDecimal tmp2 = toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return null;
        }
        if (tmp2.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return tmp1.divide(tmp2, 4, RoundingMode.HALF_UP);
    }

    public static Boolean moreThan(Object num1, Object num2) {
        BigDecimal tmp1 = toBigDecimal(num1);
        BigDecimal tmp2 = toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return false;
        }
        return tmp1.compareTo(tmp2) > 0;
    }

    public static Boolean moreThan(Object... nums) {
        Object obj = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (!moreThan(obj, nums[i])) {
                return false;
            }
            obj = nums[i];
        }
        return true;
    }


    /**
     * num  是否在 范围中
     */
    public static Boolean isInRange(Object num, Object leftRange, Object rightRange) {
        BigDecimal tmp = toBigDecimal(num);
        BigDecimal left = toBigDecimal(leftRange);
        BigDecimal right = toBigDecimal(rightRange);
        if (tmp == null || left == null || right == null) {
            return false;
        }
        return left.compareTo(tmp) <= 0 && tmp.compareTo(right) <= 0;
    }

    public static Boolean isInRangeNotEquals(Object num, Object leftRange, Object rightRange) {
        BigDecimal tmp = toBigDecimal(num);
        BigDecimal left = toBigDecimal(leftRange);
        BigDecimal right = toBigDecimal(rightRange);
        if (tmp == null || left == null || right == null) {
            return false;
        }
        return left.compareTo(tmp) < 0 && tmp.compareTo(right) < 0;
    }


    public static Boolean bigDecimalEquals(Object num1, Object num2) {
        BigDecimal tmp1 = toBigDecimal(num1);
        BigDecimal tmp2 = toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return false;
        }
        return tmp1.compareTo(tmp2) == 0;
    }

    public static Boolean lessThan(Object num1, Object num2) {
        BigDecimal tmp1 = toBigDecimal(num1);
        BigDecimal tmp2 = toBigDecimal(num2);
        if (tmp1 == null || tmp2 == null) {
            return false;
        }
        return tmp1.compareTo(tmp2) < 0;
    }

    public static BigDecimal max(Object... nums) {
        List<BigDecimal> list = Arrays.stream(nums).map(CommonUtils::toBigDecimal).toList();
        return max(list);
    }

    public static BigDecimal max(List<BigDecimal> list) {
        BigDecimal res = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            res = res.max(list.get(i));
        }
        return res;
    }

    public static BigDecimal min(BigDecimal... nums) {
        List<BigDecimal> list = Arrays.stream(nums).map(CommonUtils::toBigDecimal).toList();
        return min(list);
    }

    public static BigDecimal min(List<BigDecimal> list) {
        BigDecimal res = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            res = res.min(list.get(i));
        }
        return res;
    }


    public static BigDecimal sum(Object... nums) {
        List<BigDecimal> list = Arrays.stream(nums).map(CommonUtils::toBigDecimal).toList();
        return sum(list);
    }

    public static BigDecimal sumAbs(Object... nums) {
        List<BigDecimal> list = Arrays.stream(nums).map(item -> toBigDecimal(item).abs()).toList();
        return sum(list);
    }

    public static BigDecimal sum(List<BigDecimal> list) {
        BigDecimal res = new BigDecimal("0");
        for (BigDecimal bigDecimal : list) {
            res = res.add(bigDecimal);
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
}
