/**
 * Copyright (c) 2018, 杭州利提提. All rights reserved.
 */
package com.lititi.exams.commons2.utils;

import com.alibaba.fastjson.JSONObject;
import com.lititi.exams.commons2.constant.Constant;
import com.lititi.exams.commons2.enumeration.WhetherFlag;
import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.commons2.object.PageParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共工具类。 提供空判断、集合、字符串、类型转换、解析等公共方法。
 *
 * @author Kevin
 * @company 杭州利提提
 * @date 2018年3月22日 下午3:17:52
 * @since 1.0
 */

public class CommonUtil {

    /**
     * 统一日志处理
     */
    private static final LttLogger log = new LttLogger(CommonUtil.class);

    /**
     * 判断字符串中是否包含数字
     *
     * @param content
     * @return
     */
    public static boolean hasDigit(String content) {
        return content.replaceAll("\\d", "").length() != content.length();
    }

    public static boolean isChineseStr(String str) {
        String pattern = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static String transFirstLetter(String str, boolean isUpper) {
        String firstWord = str.substring(0, 1);
        String leftWords = str.substring(1);
        firstWord = (isUpper) ? firstWord.toUpperCase() : firstWord.toLowerCase();
        return firstWord + leftWords;
    }

    public static int[] parse2IntArray(Object[] arr) {
        if (arr == null || arr.length == 0) {
            return new int[] {};
        }
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = Integer.parseInt(arr[i].toString());
        }
        return result;
    }

    /**
     * 解析表达式，支持占位符
     *
     * @param exp
     *            ,如my name is {0},my age is {1}
     * @param values
     *            ,按顺序传入，values[0] = 'jack',values[1] = 20
     * @return .
     */
    public static String parseExpression(String exp, Object... values) {
        String expRet = exp;
        if (!ArrayUtils.isEmpty(values)) {
            for (int i = 0; i < values.length; i++) {
                String replace = values[i] == null ? "" : values[i].toString();
                expRet = expRet.replaceAll("\\{" + i + "\\}", replace);
            }
        }
        return expRet;
    }

    public static Properties loadProperties(String cpPath) throws IOException {
        try (InputStream is = CommonUtil.class.getResourceAsStream(cpPath)) {
            if (is == null) {
                return null;
            }
            Properties prop = new Properties();
            prop.load(is);
            return prop;
        }
    }

    public static String getPropertyValue(Properties prop, String key) {
        if (prop == null) {
            return null;
        }
        String value = prop.getProperty(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            value = new String(value.getBytes("ISO8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e.toString());
        }
        return value;
    }

    /**
     * 替换字符串中的"_",且单词手字母大写
     *
     * @return
     */
    public static String replace2JavaName(String s) {
        StringBuilder sb = new StringBuilder(s);
        int flag = -1;
        while ((flag = sb.indexOf("_")) != -1) {
            sb.replace(flag, flag + 2, String.valueOf(sb.charAt(flag + 1)).toUpperCase());
        }
        return sb.toString();
    }

    public static Object[] parse2ObjectArray(long[] items) {
        if (ArrayUtils.isEmpty(items)) {
            return new Object[] {};
        }
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i];
        }
        return result;
    }

    public static Object[] parse2ObjectArray(int[] items) {
        if (ArrayUtils.isEmpty(items)) {
            return new Object[] {};
        }
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i];
        }
        return result;
    }

    public static Object[] parse2ObjectArray(short[] items) {
        if (ArrayUtils.isEmpty(items)) {
            return new Object[] {};
        }
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i];
        }
        return result;
    }

    public static Object[] parse2ObjectArray(float[] items) {
        if (ArrayUtils.isEmpty(items)) {
            return new Object[] {};
        }
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i];
        }
        return result;
    }

    public static Object[] parse2ObjectArray(double[] items) {
        if (ArrayUtils.isEmpty(items)) {
            return new Object[] {};
        }
        Object[] result = new Object[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i];
        }
        return result;
    }

    public static String getPrefixBlank(int lay) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lay; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    /**
     * 将fieldName 转换成 field_name 这种格式的字符串
     *
     * @param javaFieldName
     * @return
     */
    public static String getFiledName(String javaFieldName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < javaFieldName.length(); i++) {
            char c = javaFieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(c);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 多个参数组成字符串
     */
    public static String concat(Object... args) {
        int n = 0;
        for (Object s : args) {
            if (s == null) {
                continue;
            }
            n += s.toString().length();
        }
        StringBuilder sb = new StringBuilder(n);
        for (Object s : args) {
            if (s == null) {
                continue;
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 从一串字符串中取出手机号
     *
     * @param num
     * @return
     */
    public static String getPhoneNumber(String num) {
        if (num == null || num.length() == 0) {
            return "";
        }
        Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[358]\\d{9})|(?:861[358]\\d{9}))(?!\\d)");
        Matcher matcher = pattern.matcher(num);
        StringBuilder bf = new StringBuilder(64);
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

    /**
     * 生成6位随机数
     *
     * @return
     */
    public static String createRandomNumber() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    /**
     * 检查是否符合身份证号码规则，待完善
     *
     * @param identity
     * @return
     */
    public static boolean idCardCheck(String identity) {
        if (StringUtils.isBlank(identity)) {
            return false;
        }
        identity = StringUtils.upperCase(identity);
        if (identity.matches("^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9Xx])$")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查是否符合手机号规则
     *
     * @param phoneNumber
     *            手机号码
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^1[3456789]\\d{9}$");
    }

    /**
     * 检查是否符合座机号规则
     */
    public static boolean isTelehoneNumber(String phoneNumber) {
        String regStr = "0\\d{2,6}-?\\d{5,10}";
        return phoneNumber != null && phoneNumber.matches(regStr);
    }

    /**
     * 隐藏身份证号
     *
     * @param data
     * @return
     */
    public static String hideIdNumber(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        data = StringUtils.trimToEmpty(data);
        StringBuilder sb = new StringBuilder();
        if (data.length() >= 2) {
            StringBuilder hideData = new StringBuilder();
            for (int index = 1; index < data.length(); index++) {
                hideData.append("*");
            }
            sb.append(data.charAt(0)).append(hideData).append(data.substring(data.length() - 1, data.length()));

        } else {
            sb.append(data);

        }
        return sb.toString();
    }

    public static String hideInfo(String data, int hiddenCharInfo) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        if (hiddenCharInfo <= 0) {
            hiddenCharInfo = 4;
        }
        data = StringUtils.trimToEmpty(data);
        StringBuilder sb = new StringBuilder();
        int dataLength = data.length();
        if (dataLength >= hiddenCharInfo) {
            String hiddenStar = StringUtils.leftPad("*", hiddenCharInfo, "*");
            int remainChar = dataLength - hiddenCharInfo;
            if (remainChar <= 0) {
                sb.append(hiddenStar);
            } else {
                remainChar = remainChar / 2;
                if (hiddenCharInfo > 1) {
                    sb.append(data.substring(0, remainChar)).append(hiddenStar)
                        .append(data.substring(hiddenCharInfo + remainChar, data.length()));
                } else {
                    hiddenStar = StringUtils.leftPad("*", Math.max(hiddenCharInfo, dataLength - 1), "*");
                    sb.append(data.charAt(0)).append(hiddenStar);
                }
            }
        } else {
            String hiddenStar = StringUtils.leftPad("*", dataLength, "*");
            sb.append(hiddenStar);
        }
        return sb.toString();
    }

    /**
     * @param data
     * @return
     */
    public static String hideInfo(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        data = StringUtils.trimToEmpty(data);
        StringBuilder sb = new StringBuilder();
        if (data.length() >= 4) {
            sb.append(data.substring(0, 3)).append("****").append(data.substring(data.length() - 4, data.length()));

        } else {
            sb.append(data).append("****").append(data);

        }
        return sb.toString();
    }

    /**
     * @param data
     * @return
     */
    public static String hideStringInfo(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        data = StringUtils.trimToEmpty(data);
        StringBuilder sb = new StringBuilder();

        if (data.length() > 15) {
            sb.append(data.substring(0, 5)).append("*****").append(data.substring(data.length() - 5, data.length()));

        } else if (data.length() >= 9) {
            sb.append(data.substring(0, 3)).append("***").append(data.substring(data.length() - 3, data.length()));

        } else if (data.length() > 3) {
            sb.append(data.substring(0, 3)).append("***");
        } else {
            sb.append(data);
        }
        return sb.toString();
    }

    public static String hideIp(String data) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        data = StringUtils.trimToEmpty(data);
        int length = data.length();
        StringBuilder sb = new StringBuilder();
        String[] dataArr = data.split("\\.");
        if (dataArr.length != 4) {
            return "";
        }
        int hideLength = length - dataArr[0].length() - dataArr[3].length() - 2;
        StringBuilder hideStr = new StringBuilder();
        for (int i = 0; i < hideLength; i++) {
            hideStr.append("*");
        }
        sb.append(dataArr[0]).append(".").append(hideStr).append("." + dataArr[3]);
        return sb.toString();
    }

    public static void main(String[] args) {

    }

    /**
     * 判断字符串中是都包含中文，默认为true
     *
     * @param str
     * @return
     */
    public static boolean isChineseCharacterIncluded(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count++;
            }
        }
        if (count == 0) {
            return false;
        }
        return true;
    }

    /**
     * 从n个数字中选择m个数字 eg:传入的列表为{1,2,3,4},m=3 result: {[1 ,2 ,3],[ 1, 2, 4],[ 1, 3, 4],[ 2, 3 ,4]}
     *
     * @param list
     * @param m
     * @return
     */
    public static List<Integer[]> combine(List<Integer> list, int m) {
        int n = list.size();
        if (m > n) {
            throw new LttException("错误！列表中只有" + n + "个元素。" + m + "大于" + n + "!!!");
        }

        List<Integer[]> result = new ArrayList<>();
        if (m == n) {
            Integer[] tem = new Integer[n];
            list.toArray(tem);
            result.add(tem);
            return result;
        }
        int[] bs = new int[n];
        for (int i = 0; i < n; i++) {
            bs[i] = 0;
        }
        // 初始化
        for (int i = 0; i < m; i++) {
            bs[i] = 1;
        }
        boolean flag = true;
        boolean tempFlag = false;
        int pos = 0;
        int sum = 0;
        // 首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
        do {
            sum = 0;
            pos = 0;
            tempFlag = true;
            result.add(getCombineResult(bs, list, m));

            for (int i = 0; i < n - 1; i++) {
                if (bs[i] == 1 && bs[i + 1] == 0) {
                    bs[i] = 0;
                    bs[i + 1] = 1;
                    pos = i;
                    break;
                }
            }
            // 将左边的1全部移动到数组的最左边

            for (int i = 0; i < pos; i++) {
                if (bs[i] == 1) {
                    sum++;
                }
            }
            for (int i = 0; i < pos; i++) {
                if (i < sum) {
                    bs[i] = 1;
                } else {
                    bs[i] = 0;
                }
            }

            // 检查是否所有的1都移动到了最右边
            for (int i = n - m; i < n; i++) {
                if (bs[i] == 0) {
                    tempFlag = false;
                    break;
                }
            }
            flag = !tempFlag;

        } while (flag);
        result.add(getCombineResult(bs, list, m));

        return result;
    }

    private static Integer[] getCombineResult(int[] bs, List<Integer> a, int m) {
        Integer[] result = new Integer[m];
        int pos = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == 1) {
                result[pos] = a.get(i);
                pos++;
            }
        }
        return result;
    }

    /**
     * 数组suffixArray，长度为k, 从总获取n个账号 ----》Cnk组合
     */

    private static void combination(List<String> inedxsResult, String indexTemp, int[] tempArray, int n) {
        if (n == 1) {
            for (int j : tempArray) {
                inedxsResult.add(indexTemp + j);
            }
        } else {
            for (int i = 0; i < tempArray.length - (n - 1); i++) {
                String ss = indexTemp + tempArray[i] + ",";
                // 建立从i开始的子数组,从子数组里面过滤出 n-1下标
                int[] subTempArray = new int[tempArray.length - i - 1];
                System.arraycopy(tempArray, i + 1, subTempArray, 0, subTempArray.length);
                combination(inedxsResult, ss, subTempArray, n - 1);
            }
        }
    }

    /**
     * 将16位16进制的字符转化为long，例如hex16=ffffffffffffffff,返回值为-1
     *
     * @param hex16
     * @return
     */
    public static long Hex16ToLong(String hex16) {
        if (StringUtils.isBlank(hex16)) {
            throw new LttException("数据非法：" + hex16);
        }
        // 去掉开头的0x
        if (hex16.startsWith("0x")) {
            hex16 = hex16.substring(2, hex16.length());
        }
        return new BigInteger(hex16, 16).longValue();
    }

    public static String hideEmailInfo(String mailForAuth) {
        if (StringUtils.isBlank(mailForAuth)) {
            return "";
        }
        mailForAuth = StringUtils.trimToEmpty(mailForAuth);
        StringBuilder sb = new StringBuilder();
        int loginNameLenth = mailForAuth.indexOf("@");
        if (loginNameLenth > 15) {
            sb.append(mailForAuth.substring(0, 5));
            for (int i = 0; i < loginNameLenth - 10; i++) {
                sb.append("*");
            }
            sb.append(mailForAuth.substring(loginNameLenth - 5, loginNameLenth));
        } else if (9 < loginNameLenth) {
            sb.append(mailForAuth.substring(0, 3));
            for (int i = 0; i < loginNameLenth - 6; i++) {
                sb.append("*");
            }
            sb.append(mailForAuth.substring(loginNameLenth - 3, loginNameLenth));
        } else if (loginNameLenth >= 4) {
            sb.append(mailForAuth.substring(0, 2));
            for (int i = 0; i < loginNameLenth - 4; i++) {
                sb.append("*");
            }
            sb.append(mailForAuth.substring(loginNameLenth - 2, loginNameLenth));
        } else {
            sb.append(mailForAuth.charAt(0));
            for (int i = 0; i < loginNameLenth - 1; i++) {
                sb.append("*");
            }
        }
        sb.append(mailForAuth.substring(loginNameLenth, mailForAuth.length()));
        return sb.toString();
    }

    public static String generateSerNo(long orgLoginId, long idInOrg, long createTime) {
        String orgNo = orgLoginId + "";
        if (orgNo.endsWith("0000")) {
            String orgLengthStr = StringUtils.substring(orgNo, 0, 1);
            if (!NumberUtils.isNumber(orgLengthStr)) {
                throw new LttException("机构号错误");
            }
            int orgLength = Integer.parseInt(orgLengthStr);
            orgNo = StringUtils.substring(orgNo, 0, orgLength + 1);
        } else {
            throw new LttException("机构号错误");
        }
        String createTimeStr = createTime + "";
        if (createTimeStr.length() < 3) {
            throw new LttException("createTime length unnormal");
        }
        return orgNo + idInOrg + StringUtils.substring(createTimeStr, createTimeStr.length() - 3);
    }

    /**
     * 根据orgLoginId+idInOrg+createTime后三位拼装的订单编号解析orgLoginId和idInOrg,返回一个数组，index 0=orgLoginId,index
     * 1=idInOrg,index=创建时间后3位
     *
     * @param orderNo
     * @return
     */
    public static Long[] parseOrgLoginIdAndIdInOrg(String orderNo) {
        Long[] orgLoginIdAndidInOrg = new Long[] {};
        if (!NumberUtils.isDigits(orderNo)) {
            return orgLoginIdAndidInOrg;
        }
        Long orgLoginId = null;
        Long idInOrg = null;
        Long createTime = null;
        int length = orderNo.length();
        // 目前我们系统中的机构可以分为三种方式190000，2660000,31240000
        // idForDisplay的组合方式：机构号+idInOrg+createTime后3位,例如31020000机构，有一个idInOrg=1234的订单，组合之后为：31021234235
        if (StringUtils.startsWith(orderNo, "1")) {
            if (length < 6) {
                return orgLoginIdAndidInOrg;
            }
            orgLoginId = Long.valueOf(StringUtils.substring(orderNo, 0, 2) + "0000");
            idInOrg = Long.valueOf(StringUtils.substring(orderNo, 2, length - 3));
            createTime = Long.valueOf(StringUtils.substring(orderNo, length - 3, length));
        } else if (StringUtils.startsWith(orderNo, "2")) {
            if (length < 7) {
                return orgLoginIdAndidInOrg;
            }
            orgLoginId = Long.valueOf(StringUtils.substring(orderNo, 0, 3) + "0000");
            idInOrg = Long.valueOf(StringUtils.substring(orderNo, 3, length - 3));
            createTime = Long.valueOf(StringUtils.substring(orderNo, length - 3, length));
        } else if (StringUtils.startsWith(orderNo, "3")) {
            if (length < 8) {
                return orgLoginIdAndidInOrg;
            }
            orgLoginId = Long.valueOf(StringUtils.substring(orderNo, 0, 4) + "0000");
            idInOrg = Long.valueOf(StringUtils.substring(orderNo, 4, length - 3));
            createTime = Long.valueOf(StringUtils.substring(orderNo, length - 3, length));
        } else {
            return orgLoginIdAndidInOrg;
        }
        return new Long[] {orgLoginId, idInOrg, createTime};
    }

    /**
     * 获取orgLoginId+idInOrg+createTime后3位组装的id
     *
     * @param orgLoginId
     * @param idInOrg
     * @param createTime
     * @return
     */
    public static String getAssembledId(Long orgLoginId, Long idInOrg, Long createTime) {
        if (orgLoginId == null || idInOrg == null || createTime == null) {
            return "";
        }
        String orgLoginIdStr = orgLoginId.toString();
        String createTimeStr = createTime.toString();

        String orgLengthStr = StringUtils.substring(orgLoginIdStr, 0, 1);
        int orgLength = Integer.parseInt(orgLengthStr);
        String orgNo = StringUtils.substring(orgLoginIdStr, 0, orgLength + 1);
        return orgNo + idInOrg
            + StringUtils.substring(createTimeStr, createTimeStr.length() - 3, createTimeStr.length());
    }

    public static Long extractOrgLoginIdFromLoginId(Long loginId) {
        Long orgLoginId = null;
        if (loginId == null) {
            return null;
        }
        String loginIdStr = loginId.toString();
        int length = loginIdStr.length();
        // 目前我们系统中的机构可以分为三种方式190000，2660000,31240000
        // idForDisplay的组合方式：机构号+idInOrg+createTime后3位,例如31020000机构，有一个idInOrg=1234的订单，组合之后为：31021234235
        if (StringUtils.startsWith(loginIdStr, "1")) {
            if (length < 6) {
                return orgLoginId;
            }
            orgLoginId = Long.valueOf(StringUtils.substring(loginIdStr, 0, 2) + "0000");
        } else if (StringUtils.startsWith(loginIdStr, "2")) {
            if (length < 7) {
                return orgLoginId;
            }
            orgLoginId = Long.valueOf(StringUtils.substring(loginIdStr, 0, 3) + "0000");
        } else if (StringUtils.startsWith(loginIdStr, "3")) {
            if (length < 8) {
                return orgLoginId;
            }
            orgLoginId = Long.valueOf(StringUtils.substring(loginIdStr, 0, 4) + "0000");
        } else {
            return orgLoginId;
        }
        return orgLoginId;
    }

    /**
     * 获取orgLoginId+idInOrg组装后的id
     *
     * @param orgLoginId
     * @param idInOrg
     * @return
     */
    public static Long getAssembledIdByOrgLoginIdAndIdInOrg(Long orgLoginId, Long idInOrg) {
        if (orgLoginId == null || idInOrg == null) {
            return null;
        }
        String orgLoginIdStr = orgLoginId.toString();
        return Long.valueOf(StringUtils.substring(orgLoginIdStr, 0, orgLoginIdStr.length() - 4) + idInOrg);
    }

    /**
     * 随机指定范围内N个不重复的数 在初始化的无重复待选数组中随机产生一个数放入结果中， 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换 然后从len-2里随机产生下一个随机数，如此类推
     *
     * @param max
     *            指定范围最大值
     * @param min
     *            指定范围最小值
     * @param n
     *            随机数个数
     * @return int[] 随机数结果集
     */
    public static int[] randomArray(int min, int max, int n) {
        int len = max - min + 1;

        if (max < min || n > len) {
            return null;
        }

        // 初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            // 待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            // 将随机到的数放入结果集
            result[i] = source[index];
            // 将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

    /**
     * 隐藏手机号码
     *
     * @param mobilePhone
     *            手机号
     * @return 示例 188****8888
     */
    public static String hideMobilePhone(String mobilePhone) {
        return isPhoneNumber(mobilePhone) ? mobilePhone.substring(0, 3) + "****" + mobilePhone.substring(7) : "";
    }

    // pageNo * pageSize 有可能溢出变为负数
    public static int countBeginIndex(int pageNo, int pageSize) {
        pageNo = (pageNo - 1) >= 0 ? pageNo : 1;
        int beginIndex = (pageNo - 1) * pageSize;
        return beginIndex;
    }

    public static String stringFormatThree(Long num1, Integer num2, Long num3) {
        if (num1 == null || num2 == null || num3 == null) {
            return null;
        }
        return String.format(Constant.THREE_KEY_FORMAT, num1, num2, num3);
    }

    public static JSONObject convertJsonObject(String basis) {
        if (StringUtils.isBlank(basis)) {
            return new JSONObject();
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(basis);
        } catch (Exception e) {
            log.error(basis + "转换JSONObject失败");
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    public static JSONObject convertJsonObjectWithNull(String basis) {
        if (StringUtils.isBlank(basis)) {
            return null;
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(basis);
        } catch (Exception e) {
            log.error(basis + "转换JSONObject失败");
            jsonObject = null;
        }
        return jsonObject;
    }

    public static Integer getIntByJsonObject(JSONObject jsonObject, String key) {
        String value = jsonObject.getString(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (NumberUtils.isDigits(value)) {
            Integer valueInt = Integer.parseInt(value);
            return valueInt;
        }
        return null;
    }

    public static Integer convertStrToInt(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (!NumberUtils.isDigits(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            log.error("字符串转换为int失败", e);
            return null;
        }
    }

    public static Long convertStrToLong(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (!NumberUtils.isDigits(str)) {
            return null;
        }
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            log.error("字符串转换为long失败", e);
            return null;
        }
    }

    /**
     * 
     * @param str
     * @param baseNum
     * @return
     */
    public static Integer getSeqByMode(String str, int baseNum) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (baseNum <= 0) {
            return null;
        }
        byte[] strByteArray = str.getBytes();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.error("获取加密错误", e);
            return null;
        }
        byte[] md5StrArray = md.digest(strByteArray);
        long sum = 0;
        for (byte b : md5StrArray) {
            sum += (b & 0x3F);
        }
        // log.info("str对应的sum为" + str + ",num" + sum);
        return (int)(sum % baseNum);
    }

    /**
     * terminalIMEI有多个值时取第一个terminalIMEI
     *
     * @param terminalIMEI
     * @return
     */
    public static String getOneTerminalIMEI(String terminalIMEI) {
        if (StringUtils.isBlank(terminalIMEI)) {
            return null;
        }
        String[] terminalIMEIArrary = terminalIMEI.split(",");
        return terminalIMEIArrary[0];
    }

    public static String getIpAddr(HttpServletRequest request) {
        System.out.println(request);
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            // ipAddress = request.getHeader("X-Forwarded-For");//有时候是大写，在于nginx.conf中的proxy_set_header如何配置了
            if (ipAddress == null || ipAddress.length() == 0 || Constant.UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Real-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || Constant.UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || Constant.UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || Constant.UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (Constant.LOCALHOST.equals(ipAddress) || Constant.LOCALHOST_IPV6.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        // 根据网卡取本机配置的IP
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(Constant.COMMA_SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    /**
     * 判断0元购商品的比例
     * 
     * @param storeReturnCommissionRatio
     * @return
     */
    public static boolean judgeZeroPurchaseProductRatio(long storeReturnCommissionRatio) {
        return storeReturnCommissionRatio >= Constant.ONE_MILLION;
    }

    public static String convertObjToStr(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }

    public static String convertObjToStr(Object o, String str) {
        return (o == null) ? str : o.toString();
    }

    /**
     * 判断字符串是否为URL
     *
     * @param urls
     *            需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
            + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";// 设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());// 对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();// 判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    public static boolean isUrl(String url) {
        if (StringUtils.isBlank(url) || url.length() < 7) {
            return false;
        }
        if ("http://".equals((url.substring(0, 7))) || ("https://".equals(url.substring(0, 8)))
            || "http://".equals((url.substring(1, 8))) || ("https://".equals(url.substring(1, 9)))) {
            return true;
        }
        return false;
    }

    public static <T> List<T> subListByPage(List<T> list, int pageIndex, int pageSize) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        int size = list.size();
        if (pageIndex < 0 || pageIndex >= size) {
            return null;
        }
        int toIndex = pageIndex + pageSize;
        toIndex = Math.min(toIndex, size);
        if (pageIndex > toIndex) {
            return null;
        }
        return list.subList(pageIndex, toIndex);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 比较两个无重复元素的list内容是否相同
     * 
     * @param list1
     * @param list2
     * @return
     */
    public static boolean isListEquals(List<Long> list1, List<Long> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }
        return list1.containsAll(list2) && list2.containsAll(list1);
    }

}
