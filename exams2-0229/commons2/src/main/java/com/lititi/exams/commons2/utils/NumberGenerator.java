package com.lititi.exams.commons2.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NumberGenerator {

    private static final char[] char29 = new char[] {'Q', 'W', 'E', '8', 'A', 'S', 'D', 'X', '9', 'C', '7', 'P', '5',
        'K', '3', 'M', 'J', 'U', 'F', 'R', '4', 'V', 'Y', 'T', 'N', '6', 'B', 'G', 'H'};

    /**
     * 如果符合生成的number规范返回true,不是的话返回false
     */
    public static boolean matchNumber(String codeStr) {
        return (NumberUtils.isDigits(codeStr) && codeStr.length() == 19);
    }

    public static List<Long> getNumberListByCurrentTime(int count) {
        List<Long> numList = new ArrayList<Long>();
        int numListSize = 0;
        while (numListSize < count) {
            Long number = generateRandomNumber();
            if (numList.contains(number)) {
                continue;
            }
            numList.add(number);
            numListSize++;
        }
        return numList;
    }

    public static void main(String[] args) {
        // String[] chars36 = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
        // "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        //
        // StringBuffer shortBuffer = new StringBuffer();
        // String uuid = UUID.randomUUID().toString().replace("-", "");
        // System.out.println(uuid.length());
        // for (int i = 0; i < 8; i++) {
        // String str = uuid.substring(i * 4, i * 4 + 4);
        // int x = Integer.parseInt(str, 16);
        // // 如果是 chars62,则是x%62
        // shortBuffer.append(chars36[x % 36]);
        // }
        System.out.println(genRandomNum());

    }

    /**
     * 获取随机的8位数(数字,字母组成,低概率重复)
     */
    public static String genRandomNum() {

        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(char29[x % 29]);
        }
        return shortBuffer.toString();
    }

    /**
     * 通过hash UUID来生成一个token数字，数据量巨大(大于2^50时冲突几率为万分之一)时考虑更换逻辑
     * @return 生成的token
     */
    public static long generateRandomToken(){
        String stringToken = UUID.randomUUID().toString();
        long token = 0;
        for(int i = 0;i < 32;i++){
            token = token  * 31 + stringToken.charAt(i);
        }
        return token;
    }

    /**
     * 通过当前时间生成随机数，如果在一毫秒内有大量调用，则会出现重复数字
     * 可以通过增大capacity来降低重复率，扩大时注意long数据的范围
     * @return 生产的随机数
     */
    public static long generateRandomNumber() {
        // 生成六位随机数，
        long capacity = 100000;
        long currentTime = System.currentTimeMillis() * capacity;
        long random = (long)((Math.random() * 9 + 1) * capacity);
        return currentTime + random;
    }
}
