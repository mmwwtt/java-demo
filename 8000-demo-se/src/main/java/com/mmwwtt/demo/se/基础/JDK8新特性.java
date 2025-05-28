package com.mmwwtt.demo.se.基础;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class JDK8新特性 {

    @Test
    @DisplayName("测试方法引用")
    public void test() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList();

        //普通方法引用(不带参数)
        list.forEach(BaseInfoVO::format);

    }

    /**
     * () -> {} 形式的函数
     * 更加灵活，无需进行函数声明
     */
    @Test
    @DisplayName("测试Lambda表达")
    public void test1() {
        List<Integer> list = Arrays.asList(3,6,4,10,88,22);
        list.sort((s1, s2) -> s1 - s2);
        assertNotNull(list);
    }

    @Test
    @DisplayName("测试LocalDate")
    public void test2() {
        LocalDate now1 = LocalDate.now();                            // 当前日期
        LocalDate now2 = LocalDate.now(ZoneId.of("Asia/Shanghai"));    // 当前日期 (指定时区)
        LocalDate now3 = LocalDate.now(Clock.systemDefaultZone());    // 当前日期 (指定时钟)
        LocalDate localDate = LocalDate.of(2023, 1, 1);                // 指定日期 2023-01-01


        LocalDate now = LocalDate.now();
        int year = now.getYear();						// 获取年份
        int month = now.getMonthValue();				// 获取月份（1-12）
        Month monthEnum = now.getMonth();				// 获取月份的枚举值
        int dayOfMonth = now.getDayOfMonth();			// 获取月份中的第几天（1-31）
        int dayOfYear = now.getDayOfYear();				// 获取一年中的第几天（1-366）
        DayOfWeek dayOfWeek = now.getDayOfWeek();		// 获取现在是星期几
        int lengthOfYear = now.lengthOfYear();			// 获得当年总天数
        int lengthOfMonth = now.lengthOfMonth();		// 获得当月总天数
        long epochDay = now.toEpochDay();				// 与时间纪元（1970年1月1日）相差的天数


        LocalDate localDate1 = now.plusDays(1);			// 给当前时间加一天
        LocalDate localDate2 = now.plusDays(1);			// 给当前时间加一周
        LocalDate localDate3 = now.plusMonths(1);		// 给当前时间加一月
        LocalDate localDate4 = now.plusYears(1);		// 给当前时间加一年
        LocalDate localDate5 = now.minusDays(1);		// 给当前时间减一天
        LocalDate localDate6 = now.minusWeeks(1);		// 给当前时间减一周
        LocalDate localDate7 = now.minusMonths(1);		// 给当前时间减一月
        LocalDate localDate8 = now.minusYears(1);		// 给当前时间减一年


        LocalDate localDate9 = now.withYear(2020);		// 修改日期对象年份为2020
        LocalDate localDate10 = now.withMonth(1);		// 修改日期对象月份为1
        LocalDate localDate11 = now.withDayOfMonth(1);	// 修改日期对象的日期(一月中的第几天)
        LocalDate localDate12 = now.withDayOfYear(1);	// 修改日期对象的日期(一年中的第几天)

        log.info("{},{},{},{}",now1, now2, now3, localDate);
    }

    @Test
    @DisplayName("测试Date和LocalDate转换")
    public void test3() {

        //Date转DateTime
        Date date = new Date();
        LocalDate dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //DateTime转String
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);

        log.info(formatStr, dateTime);
    }

    /**
     * Optional类：避免大量的是否为null判断，代码更加优雅
     * isPresent() 非null返回ture
     * Optional.empty():创建空的Optional对象
     * Optional.of(): 创建非空的Optional对象
     * Optional.ofNullable: 创建可能为空的Optional对象
     * orElse: 如果是null返回默认值
     * orElseGet：如果是null返回方法结果值
     * orElseThrow: 如果是null则抛出异常
     */
    @Test
    @DisplayName("测试Optinoal")
    public void test4() {
        Optional<String> empty = Optional.empty();
        log.info("{}",empty.isPresent());
        empty.get();

        Optional<String> notNull = Optional.of("hello");
        log.info("{}",notNull.isPresent());

        Optional<String> ableNull = Optional.ofNullable(null);
        log.info("{}",ableNull.isPresent());

        String str = ableNull.get();
        String str1 = ableNull.orElse("hell0");
        String str2 = ableNull.orElseGet(() -> "hello");
        String str3 = ableNull.orElseThrow();
    }


    private static final List<BaseInfo> list1 = BaseInfo.getPresetList();
    private static final List<BaseInfo> list2 = BaseInfo.getPresetList();

    @Test
    @DisplayName("测试Stream初始化集合")
    public void test5() {
        List<BaseInfo> list = Stream.of(
                        new BaseInfo(),
                        new BaseInfo())
                .collect(Collectors.toList());
        log.info("{}", list);
    }

    @Test
    @DisplayName("测试map属性转换")
    public void test6() {
        List<String> nameList1 = list1.stream().map(BaseInfo::getName)
                .collect(Collectors.toList());

        List<String> nameList2 = list1.stream()
                .map(item -> "hello: " + item.getName())
                .collect(Collectors.toList());
        log.info("{}\n{}", nameList1, nameList2);
    }

    @Test
    @DisplayName("测试mapToXX 映射成基本类型")
    public void test7() {
        double sum = list1.stream()
                .mapToDouble(BaseInfo::getHeight).sum();
        log.info("{}", sum);
    }


    @Test
    @DisplayName("测试boxed 基本类型转包装类型")
    public void test8() {
        int[] array = {1, 2, 3};
        List<Integer> list = Arrays.stream(array)
                .boxed()
                .toList();
        log.info("{}", list);
    }

    @Test
    @DisplayName("测试flatMap 合并两个list")
    public void test9() {
        List<BaseInfo> list = Stream.of(list1, list2)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        log.info("{}", list);
    }


    @Test
    @DisplayName("测试filter 保留为true的元素")
    public void test10() {
        List<BaseInfo> list = list1.stream()
                .filter(item -> "1".equals(item.getSexCode()))
                .toList();
        log.info("{}", list);
    }

    @Test
    @DisplayName("测试limit 截取元素")
    public void test11() {
        List<BaseInfo> list = list1.stream()
                .limit(2)
                .toList();
        log.info("{}", list);
    }

    @Test
    @DisplayName("测试skip 跳过元素")
    public void test12() {
        List<BaseInfo> list = list1.stream()
                .skip(1)
                .toList();
        log.info("{}", list);
    }


    @Test
    @DisplayName("测试distinct 去重")
    public void test14() {
        //对自定义对象去重时，要重写equals和hashCode方法/ 增加@Data注解
        //内部用hashSet去重
        List<String> list = Stream.of("hello", "world", "hello")
                .distinct()
                .toList();
        log.info("{}", list);
    }

    @Test
    @DisplayName("测试groupingBy 对集合进行分组")
    public void test15() {
        Map<String, List<BaseInfo>> map = list1.stream()
                .collect(Collectors.groupingBy(BaseInfo::getSexCode));
        log.info("{}", map);
    }

    /**
     * 多字段排序(默认降序)，
     * reversed()将降序反转为升序
     * sorted(Comparator.comparing(Point::getX)) 根据x进行排序
     * thenComparing(Point::y) 再根据y进行排序
     */
    @Test
    @DisplayName("测试sort排序")
    public void test16() {
        List<BaseInfo> list = list1.stream()
                .sorted(Comparator.comparing(BaseInfo::getName).reversed()
                        .thenComparing(BaseInfo::getSexCode).reversed())
                .toList();
        log.info("{}", list);
    }

    @Test
    @DisplayName("测试count计数")
    public void test17() {
        Long count = list1.stream()
                .filter(item -> "0".equals(item.getSexCode()))
                .count();
        log.info("{}", count);
    }

    @Test
    @DisplayName("测试joining 字符串拼接")
    public void test18() {
        String str = list1.stream()
                .map(item -> String.valueOf(item.getName()))
                .collect(Collectors.joining());
        log.info("{}", str);
    }

    /**
     * anyMatch:有一个为true 就返回true
     * allMatch:全部为true 才返回true
     * noneMatch: 全部为flase 才返回true
     */
    @Test
    @DisplayName("测试match 判断")
    public void test19() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList();
        boolean flag1 = list.stream().anyMatch(item -> item.getHeight() >= 10);
        boolean flag2 = list.stream().allMatch(item -> item.getHeight() >= 11);
        boolean flag3 = list.stream().noneMatch(item -> item.getHeight() >= 12);
        log.info("{},{},{}", flag1, flag2, flag3);
    }

    /**
     * list转map
     * Student::getName 建
     * Function.identity() 返回一个输出和输入一样的lambda表达式对象，等价于 item -> item
     * (key1, key2) -> key2 表示键相同的，后来的会覆盖之前的   不写如果出现该情况会报错
     */
    @Test
    @DisplayName("测试list和map转换")
    public void test20() {
        Map<String, BaseInfo> map1 = list1.stream()
                .collect(Collectors.toMap(BaseInfo::getName, Function.identity(),
                        (key1, key2) -> key2));
        Map<String, Long> map2 = list1.stream()
                .collect(Collectors.toMap(BaseInfo::getName, BaseInfo::getBaseInfoId,
                        (key1, key2) -> key2));
        // map转list
        List<BaseInfo> list = map1.values()
                .stream().toList();
        log.info("{},{}", map1,map2,list);
    }
}
