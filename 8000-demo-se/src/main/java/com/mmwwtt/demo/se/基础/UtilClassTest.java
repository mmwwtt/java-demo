package com.mmwwtt.demo.se.基础;


import com.mmwwtt.demo.common.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class UtilClassTest {

    /**
     * double存在精度损失，当用到精确计算时，需要用到BigDecimal类
     */
    @Test
    @DisplayName("BigDecimal基本使用")
    public void test1() {
        //浮点型时，会先转成double，已经丢失了精度，需要使用字符串赋值
        BigDecimal num = new BigDecimal(123.123);
        log.info("{}",num);

        BigDecimal num1 = new BigDecimal("123.123");
        BigDecimal num2 = new BigDecimal("12345.12345678910");
        //加  scale(保留位数， 四舍五入类型）
        BigDecimal num3 = num1.add(num2).setScale(2, RoundingMode.HALF_UP);
        //减
        BigDecimal num4 = num1.subtract(num2);
        //乘
        BigDecimal num5 = num1.multiply(num2);

        //获得值
        log.info("{}",num5.doubleValue());
    }

    @Test
    @DisplayName("Integer基本使用")
    public void test2() {
        Integer num1 = 123;
        Integer num2 = Integer.valueOf("123");

        //Integer 转为 String
        String str = num1.toString();

        //Integer 比较值相等
        log.info("{}",num1.equals(1));

        //字符串转为16进制
        int num = Integer.parseInt("FF", 16);
        log.info("{}",num);
    }

    @Test
    @DisplayName("Math基本使用")
    public void test3() {
        //取最大值
        Math.max(1,2);

        //取最小值
        Math.min(1,2);

        //四舍五入
        Math.round(4.45);

        //向上取整
        Math.ceil(4.45);

        //向下取整
        Math.floor(4.45);

        //取绝对值
        Math.abs(-4.45);

        //取平方根
        Math.sqrt(5);

        //取立方根
        Math.cbrt(5);

        //计算a的b次方
        Math.pow(2,8);

        // Math.addExact() 当计算溢出时会抛出异常
        try {
            Math.addExact(Integer.MAX_VALUE, Integer.MAX_VALUE);
        } catch (ArithmeticException e) {
            log.info("error");
        }
    }


    @Test
    @DisplayName("Scanner基本使用")
    public  void test4() {
        Scanner scanner = new Scanner(System.in);
        String str = "";

        //输入一行字符串(包括空格)
        str = scanner.nextLine();
        log.info(str);

        //输入一行字符串，直到空格结束
        str = scanner.next();
        log.info(str);

        //输入一个数值
        //如果使用next()/nextInt()/nextDouble()等后还要使用nextLine()读取下一行，则需要先使用nextLine()将当前行的换行读取掉
        int num = scanner.nextInt();

        //判断是否有下一个输入
        while(scanner.hasNext()) {

        }
    }

    @Test
    @DisplayName("StringBuilder基本使用")
    public void test5() {
        StringBuilder s1 = new StringBuilder();

        //将不同类型进行字符串拼接
        s1.append("hello");
        s1.append(15);
        s1.append(false);

        //反转字符串
        s1.reverse();

        //将StringBuffer类转为String类，并返回
        s1.toString();

        //匹配字符串，有则返回第一次的索引，无则返回-1
        s1.indexOf("h");

        //返回指定下标下的字符
        s1.charAt(0);

        //返回字符串长度
        s1.length();

        //删除指定下标下的字符串,一个参数表示单个字符，两个参数表示起始和结束位置
        s1.deleteCharAt(0);

        //替换指定索引下的字符串
        s1.setCharAt(0,'a');
    }

    @Test
    @DisplayName("String-格式化字符串")
    public void test6() {
        double num1 = 3.1415916;
        int num2 = 1;
        //格式化字符串
        String str1 = String.format("%f", num1);

        //保留一位小数
        String str2 = String.format("%.1f", num1);

        //前位补0
        String str3 = String.format("%03d", num2);

        log.info(str1, str2, str3);
    }


    @Test
    @DisplayName("String基本使用")
    public void test7() {
        String str1 = "hello";
        String str2 = "world";

        //判断字符串是否相等,返回boolean
        str1.equals(str2);

        //返回指定索引下的字符，return char
        str1.charAt(0);

        //返回字符串中存在字串的索引，无则返回-1
        str1.indexOf("ll");

        //根据指定 字符串进行分割，'.'需要转为'\\.', '\'需要转为'\\\\'
        String[] words = str1.split(" ");

        // 用|隔开，可以按照多个条件进行分割
        String[] words1 = str1.split(" |l");

        //去除字符串头尾空格后，返回新串
        str1.trim();

        //截取字符串后，返回新串。若只有一个参数则表示起始到末尾。两个参数表示【起始-结束)位置
        str1.substring(0);

        //返回字符串长度
        str1.length();

        //将字符串转为小写后，返回新串
        //考虑到地区语言上存在差异，大小写转换时，必须加上Locale.ROOT/Locale.ENGLISH
        str1.toLowerCase(Locale.ROOT);

        //将字符串转为大写后，返回新船
        str1.toUpperCase(Locale.ROOT);

        //将字符串中的某个字符替换为新的字符后返回   字符串替换
        str1.replace(',','&');
        str1.replace(" "," ");

        //将字符串中的某个字符串替换为新的字符串后返回   正则替换
        str1.replaceAll("aa","bb");

        //将首个符合条件的字符串替换为新串
        str1.replaceFirst("a","b");

        //将String转为int
        Integer.parseInt(str1);

        //equals方法的参数可用为null
        str1.equals(null);

        //判断字符串是否以指定字符串开头
        str1.startsWith("hello");

        //字符串字典序比较
        str1.compareTo(str2);
    }

    @Test
    @DisplayName("String-正则")
    public void test8() {
        String str = "hello world";

        //用了正则，每次都会预编译，性能慢
        String str1 = str.replace("he", "hhee");


        Pattern pattern = Pattern.compile("he");
        Matcher matcher = pattern.matcher(str);
        String str2 = matcher.replaceAll("hhee");
        CommonUtils.println(str1,str2);
    }

    @Test
    @DisplayName("Stack基本使用")
    public void test9() {
        Stack<Integer> stack = new Stack<>();

        //入栈
        stack.push(1);

        //返回栈顶元素,如果栈为空调用peek()会报错，用之前要判断是否为空
        stack.peek();

        //删除栈顶元素，并返回
        stack.pop();

        //栈是否为空,return boolean
        stack.isEmpty();

        //返回栈长度
        stack.size();

        //判断栈是否为空
        stack.isEmpty();

        //返回是否包含元素
        stack.search(1);

        //添加元素
        stack.add(1);
        stack.add(1,1);

        //添加Collectioni接口的集合元素
        List<Integer> list = new ArrayList<>();
        list.add(1);
        stack.addAll(list);
    }

    @Test
    @DisplayName("Queue基本使用")
    public void test10() {
        Queue<Integer> queue1 = new LinkedList<>();
        Queue<Integer> queue2 = new LinkedList<>();

        //入队，
        queue1.add(1);
        //offer():队列已经满会抛出异常
        queue2.offer(2);

        //返回队首元素
        queue1.peek();

        //队首为空会抛出异常
        queue2.element();

        //将队首元素出队，并返回,
        queue2.poll();

        //remove():队列为空会返回异常
        queue1.remove();

        //返回队长
        queue1.size();

        // 判断队列是否为空
        queue1.isEmpty();

        //清空队列
        queue1.clear();
    }
}
