package com.mmwwtt.demo.se.集合;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class SetDemo {

    @Test
    public void set种类() {

        //普通set
        Set<Integer> set1 = new HashSet<>();
        set1.add(1);
        set1.add(123);
        set1.add(-123);
        log.info("set3: " , set1);

//        //可以排序的set
//        Set<BaseInfoVO> set2 = new TreeSet<>((a, b) -> {
//            if (a.getAge() != b.getAge()) {
//                return a.getAge() > b.getAge() ? -1 : 1;
//            } else if (!a.getName().equals(b.getName())) {
//                return a.getName().compareTo(b.getName());
//            } else {
//                return 0;
//            }
//        });
//
//        set2.add(new BaseInfoVO());
//        set2.add(new BaseInfoVO());
//        log.info("set2: ", set2);
//
//        //记住插入顺序的set
//        Set<Integer> set3 = new LinkedHashSet<>();
//        set3.add(1);
//        set3.add(123);
//        set3.add(-123);
//        log.info("set3: {}", set3);
    }

    @Test
    public void set常用方法() {
        Set<Integer> set1 = new HashSet<>();

        //添加元素
        set1.add(1);

        //删除元素 的三种方式
        //方式1
        set1.remove(1);

        //方式2
        set1.removeIf(next -> next.equals(1));

        //方式3
        Iterator<Integer> iterator = set1.iterator();
        while (iterator.hasNext()) {
            Integer num = iterator.next();
            if (num.equals(1)) {
                iterator.remove();
            }
        }

        //是否包含元素
        if (set1.contains(1)) {
            log.info("包含1");
        }

        //获得set大小
        log.info("set1的size: " + set1.size());

        //清空set
        set1.clear();

        set1.add(1);
        set1.add(2);
        set1.add(3);
        //遍历set,不能在遍历的时候移除元素
        for (Integer num : set1) {
            log.info("{}",num);
        }

        //set转 数组 或者 list
        Integer[] list1 = new Integer[set1.size()];
        set1.toArray(list1);

        List<Integer> list2 = new ArrayList<>(set1);
    }

//    @Test
//    public void 内存泄露() {
//        Set<Point> set = new HashSet<>();
//        Point point = new Point(1, 1, 1);
//        set.add(point);
//        point.x = 1;
//        set.remove(point);
//        log.info(set);
//
//        //map,set中  当重写Point的hashcode和equals方法后 修改point会使其hashcode方法发生改变
//        //导致删除不了point对象，即对象无法删除，造成内存泄露
//    }
}
