package com.mmwwtt.stock.common;

import org.apache.commons.collections4.CollectionUtils;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

import static com.mmwwtt.stock.common.Constants.TOLERANCE;

public class CommonUtils {

    /**
     * double  double
     */

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


    /**
     * Double  Double
     */
    public static Double subtract(Double num1, Double num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        return num1 - num2;
    }

    public static Double add(Double num1, Double num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        return num1 + num2;
    }

    public static Double multiply(Double num1, Double num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        return num1 * num2;
    }


    public static Double divide(Double num1, Double num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        if (num2 == 0) {
            return 0.0;
        }
        return divide(num1.doubleValue(), num2.doubleValue());
    }


    /**
     * double  Double
     */
    public static Double subtract(double num1, Double num2) {
        if (num2 == null) {
            return null;
        }
        return num1 - num2;
    }

    public static Double add(double num1, Double num2) {
        if (num2 == null) {
            return null;
        }
        return num1 + num2;
    }

    public static Double multiply(double num1, Double num2) {
        if (num2 == null) {
            return null;
        }
        return num1 * num2;
    }


    public static Double divide(double num1, Double num2) {
        if (num2 == null) {
            return null;
        }
        return divide(num1, num2.doubleValue());
    }


    /**
     * Double  double
     */
    public static Double subtract(Double num1, double num2) {
        if (num1 == null) {
            return null;
        }
        return num1 - num2;
    }

    public static Double add(Double num1, double num2) {
        if (num1 == null) {
            return null;
        }
        return num1 + num2;
    }

    public static Double multiply(Double num1, double num2) {
        if (num1 == null) {
            return null;
        }
        return num1 * num2;
    }


    public static Double divide(Double num1, double num2) {
        if (num1 == null) {
            return null;
        }
        return divide(num1.doubleValue(), num2);
    }


    /**
     * 判断相关
     *
     * @param num1
     * @param num2
     * @return
     */
    public static boolean isEquals(Double num1, Double num2) {
        if (Objects.isNull(num1) || Objects.isNull(num2)) {
            return false;
        }
        return Math.abs(num1 - num2) < TOLERANCE;
    }

    public static Boolean moreThan(Double num1, Double num2) {
        if (Objects.isNull(num1) || Objects.isNull(num2)) {
            return false;
        }
        return num1 > num2 + TOLERANCE;
    }

    public static Boolean moreThan(Double... nums) {
        if (Arrays.stream(nums).anyMatch(Objects::isNull)) {
            return false;
        }
        double obj = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (!moreThan(obj, nums[i])) {
                return false;
            }
            obj = nums[i];
        }
        return true;
    }

    public static Boolean lessThan(Double num1, Double num2) {
        if (Objects.isNull(num1) || Objects.isNull(num2)) {
            return false;
        }
        return num1 + TOLERANCE < num2;
    }


    /**
     * num  是否在 范围中
     */
    public static Boolean isInRange(Double num, Double leftRange, Double rightRange) {
        if (Objects.isNull(num) || Objects.isNull(leftRange) || Objects.isNull(rightRange)) {
            return false;
        }
        return moreThan(num, leftRange) && lessThan(num, rightRange);
    }


    /**
     * 计算相关都不能有null
     */
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

    public static String getTimeStr() {
        return LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }


    public static String getDateStr() {
        return LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }


    /**
     * 不能有null
     * 计算平均值
     */
    public static double getAverage(List<Double> list) {
        return divide(sum(list), list.size());
    }

    public static double getAverage(double[] list) {
        return divide(sum(list), list.length);
    }


    /**
     * 不能有null
     * 计算中位数，O(n) 平均时间复杂度，基于 QuickSelect 算法
     * 与 getMiddle 语义一致：奇数取中间，偶数取高位中位数
     */
    public static double getMiddle(List<Double> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        double[] arr = list.stream().mapToDouble(d -> d == null ? 0 : d).toArray();
        return getMiddle(arr);
    }

    /**
     * 不能有null
     * 计算中位数，O(n) 平均时间复杂度，基于 QuickSelect 算法
     * 不修改原数组
     */
    public static double getMiddle(double[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        double[] copy = Arrays.copyOf(arr, arr.length);
        int k = copy.length / 2;
        return quickSelect(copy, 0, copy.length - 1, k);
    }

    /**
     * QuickSelect：在 arr[left..right] 中找第 k 小元素（0-indexed）
     */
    private static double quickSelect(double[] arr, int left, int right, int k) {
        if (left == right) {
            return arr[left];
        }
        int pivotIdx = partition(arr, left, right);
        if (k == pivotIdx) {
            return arr[k];
        }
        if (k < pivotIdx) {
            return quickSelect(arr, left, pivotIdx - 1, k);
        }
        return quickSelect(arr, pivotIdx + 1, right, k);
    }

    private static int partition(double[] arr, int left, int right) {
        double pivot = arr[right];
        int i = left;
        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, right);
        return i;
    }

    private static void swap(double[] arr, int i, int j) {
        double tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * 不能有null
     * 计算涨幅 第一个比第二个涨了多少
     */
    public static double getRise(double price1, double price2) {
        return divide(subtract(price1, price2), price2);
    }

    /**
     * 不能有null
     * 返回 128 位紧凑 key，用于 md5ToLevelMap，不分配 String，适合几十万 key 的大 map
     */
    public static String getMd5Key(int[] arr) {
        try {
            // 1. 将 int[] 转换为 byte[] (每个 int 4字节)
            byte[] input = new byte[arr.length * 4];
            for (int i = 0; i < arr.length; i++) {
                input[i * 4] = (byte) (arr[i] >> 24);
                input[i * 4 + 1] = (byte) (arr[i] >> 16);
                input[i * 4 + 2] = (byte) (arr[i] >> 8);
                input[i * 4 + 3] = (byte) (arr[i]);
            }

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input);

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String fullMD5 = sb.toString();

            return fullMD5.substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 不能有null
     * 取两个 list的交集
     * 前提：两个list 都要升序,且无重复
     */
    public static int[] retainAll(int[] list1, int[] list2) {
        if (Objects.isNull(list1) || Objects.isNull(list2)) {
            return new int[0];
        }
        int[] tmpArr = new int[Math.min(list1.length, list2.length)];
        if (tmpArr.length == 0) {
            return tmpArr;
        }
        int j = 0;
        int arrIdx = 0;
        for (int num1 : list1) {
            while (num1 > list2[j]) {
                j++;
                if (j >= list2.length) {
                    return Arrays.copyOfRange(tmpArr, 0, arrIdx);
                }
            }
            if (num1 == list2[j]) {
                tmpArr[arrIdx] = num1;
                j++;
                arrIdx++;
                if (j >= list2.length) {
                    return Arrays.copyOfRange(tmpArr, 0, arrIdx);
                }
            }
        }
        return Arrays.copyOfRange(tmpArr, 0, arrIdx);
    }

    public static int[] retainAll(List<int[]> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new int[0];
        }
        boolean havaNall = list.stream().anyMatch(Objects::isNull);
        if (havaNall) {
            return new int[0];
        }

        list = list.stream().sorted(Comparator.comparing(item -> item.length)).toList();
        int[] res = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            res = retainAll(res, list.get(i));
        }
        return res;
    }

    /**
     * 计算两个list的重合度
     * 算法： 交集 / 并集
     */
    public static double getRepeatPerc(int[] list1, int[] list2) {
        if (Objects.isNull(list1) || Objects.isNull(list2)) {
            return 0d;
        }

        //交集
        int[] retainArr = retainAll(list1, list2);

        //并集
        Set<Integer> unionSet = new HashSet<>();
        for (int detailId : list1) {
            unionSet.add(detailId);
        }
        for (int detailId : list2) {
            unionSet.add(detailId);
        }
        return retainArr.length * 1.0 / unionSet.size();
    }
}
