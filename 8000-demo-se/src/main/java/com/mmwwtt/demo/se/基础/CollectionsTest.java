package com.mmwwtt.demo.se.基础;


import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class CollectionsTest {

    @Test
    @DisplayName("Arrays基本使用")
    public void test() {
        Integer[] array0 = {1, 2, 3};
        int[] array1 = {1, 3, 5};
        //array转list
        List<Integer> list2 = Arrays.asList(array0);

        //将数组排序(默认升序，用于基本数据类型)
        Arrays.sort(array1);

        //自定义数组排序(引用类型)
        Arrays.sort(array0, (num1, num2) -> {
            /*
              返回正数，第一个元素在后面；
              返回0,不交换
              返回负数，第一个元素在前面；
             */
            return num1 > num2 ? 1 : -1;
        });
        //对数组进行填充
        Arrays.fill(array1, 16);

        //对数组进行填充,[起始索引，结束索引)
        Arrays.fill(array1, 1, 2, 77);
    }

    @Test
    @DisplayName("Array基本使用")
    public void test1() {
        //创建空的数组
        int[] array2 = new int[3];

        //初始化一维数组
        int[] array1 = {1,3,5};

        //二维数组初始化
        int[][] array3 = {{2, 4}, {3, 5}};

        //获得数组长度
        log.info("{}",array1.length);

        //通过System.arraycopy() 或者 Arrays.copyof() 进行数组复制 (深拷贝)
        int[] array4 = new int[array1.length];
        System.arraycopy(array1, 0, array4, 0, array1.length);

        //二维数组复制
        int[][] arr1 = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] arr2 = new int[arr1.length][arr1[0].length];
        for (int i = 0; i < arr1.length; i++) {
            System.arraycopy(arr1[i], 0, arr2[i], 0, arr1[0].length);
        }
        System.out.println();
    }

    @Test
    @DisplayName("Collections基本使用")
    public void test2() {

        //集合转为线程安全
        Map<String, String> map = Collections.unmodifiableMap(new HashMap<>());
        Set<String> set = Collections.unmodifiableSet(new HashSet<>());
        List<String> list = Collections.unmodifiableList(new ArrayList<>());

        //jdk10 新增写法 长度不能改变
        list = List.of("hello", "world");

        //返回空集合
        List<Integer> list1 = Collections.emptyList();

        //集合顺序倒置
        Collections.reverse(list);

        //交换list中两个元素
        Collections.swap(list, 0, 1);

        //对list进行排序（默认字典序/整形升序）
        Collections.sort(list);

        /*
          正数交换，第一个元素在后面；
          返回0,不交换
          负数不交换，第一个元素在前面；
         */
        List<BaseInfoVO> baseInfoList = BaseInfoVO.getPresetList();
        baseInfoList.sort((BaseInfoVO a, BaseInfoVO b) -> {
            if (!Objects.equals(a.getHeight(), b.getHeight())) {
                return a.getHeight() > b.getHeight() ? -1 : 1;
            } else if (!a.getName().equals(b.getName())) {
                return a.getName().compareTo(b.getName());
            } else {
                return 0;
            }
        });
    }

    @Test
    @DisplayName("Integer基本使用")
    public void test3() {
        Integer num1 = Integer.parseInt("123");
        Integer num2 = Integer.valueOf("100");
    }

    @Test
    @DisplayName("List基本使用")
    public void test4() {
        //数组结构
        List<Integer> list = new ArrayList<>();

        //链表结构
        List<Integer> list0 = new LinkedList<>();

        //初始化方法1
        List<Integer> list1 = new ArrayList<>();

        //初始化方法2: 通过asList获得定长的list,长度不能改变
        List<Integer> list2 = new ArrayList<>(Arrays.asList(1,2,3));

        //初始化方法3: 将一个数组赋给另一个数组（深拷贝）
        List<Integer> list3 = new ArrayList<>(list1);

        //初始化方法4：返回的list长度不能修改
        List<Integer> list5 = Arrays.asList(1);

        //返回list大小
        list1.size();

        //添加元素
        list1.add(1);
        //指定下标添加元素
        list1.add(0, 18);

        //list添加另一个list
        list1.addAll(list2);

        //将list转为数组 0表示全部拷贝
        Integer[] arr = list2.toArray(new Integer[0]);
        Integer[] arr1 = list2.toArray(new Integer[list2.size()]);
        //jdk11后使用该形式， 不需要创建临时数组，且不用考虑长度
        Integer[] arr2 = list2.toArray(Integer[] ::new);
        //访问指定下标的元素
        list1.get(0);

        //移除指定下标的元素
        list1.remove(0);

        //清空元素
        list1.clear();

        //截取集合
        List<Integer> list4 = list2.subList(0,1);

    }

    @Test
    @DisplayName("Set-种类")
    public void test5() {

        //普通set
        Set<Integer> set1 = Set.of(1, 123, -124);

        //可以排序的set
        Set<BaseInfoVO> set2 = new TreeSet<>((a, b) -> {
            if (!Objects.equals(a.getHeight(), b.getHeight())) {
                return a.getHeight() > b.getHeight() ? -1 : 1;
            }
            return 0;
        });
        set2.add(new BaseInfoVO());
        set2.add(new BaseInfoVO());

        //记住插入顺序的set
        Set<Integer> set3 = new LinkedHashSet<>();
        set3.add(1);
        set3.add(123);
        set3.add(-123);
        log.info("set3: {}", set3);
    }

    @Test
    @DisplayName("Set基本使用")
    public void test6() {
        Set<Integer> set1 = new HashSet<>();

        //添加元素
        set1.add(1);

        //删除元素 的三种方式
        //方式1
        set1.remove(1);

        //方式2
        Iterator<Integer> iterator = set1.iterator();
        while (iterator.hasNext()) {
            Integer num = iterator.next();
            if (num.equals(1)) {
                iterator.remove();
            }
        }

        //方式3 推荐使用
        set1.removeIf(next -> next.equals(1));

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
            log.info("{}", num);
        }
        set1.stream().forEach(item ->{});

        //set转 数组 或者 list
        Integer[] list1 = new Integer[set1.size()];
        set1.toArray(list1);

        List<Integer> list2 = new ArrayList<>(set1);
    }

    @Test
    @DisplayName("Map-种类")
    public void test7() {
        Map<String, Integer> map1 = new HashMap<>();

        //TreeMap 会根据键进行排序
        Map<String, Integer> map2 = new TreeMap<>();
        Map<BaseInfoVO, Integer> map = new TreeMap<>((a, b) -> {
            if (!Objects.equals(a.getHeight(), b.getHeight())) {
                return a.getHeight() > b.getHeight() ? -1 : 1;
            }
            return 0;
        });

        //LinkedHashMap 会记录插入时的顺序
        Map<String, Integer> map3 = new LinkedHashMap<>();
        CommonUtils.println(map1, map2, map3);
    }

    @Test
    @DisplayName("Map-基本使用")
    public void test8() {
        Map<String, Integer> map1 = new HashMap<>();

        //插入键值对
        map1.put("name1", 1);

        //获取键对应的值,如果没有对应的键会报错，使用前需要先进行containsKey()判断
        map1.get("name1");

        //是否包含该键
        map1.containsKey("name1");

        //获得map的大小
        map1.size();

        //移除指定键的键值对
        map1.remove("name1");

        //遍历map,不能在遍历的时候进行插入和移除操作
        for (String str : map1.keySet()) {
            log.info("{} : {}", str, map1.get(str));
        }
        //遍历map,不能在遍历的时候进行插入和移除操作
        for (Map.Entry entry : map1.entrySet()) {
            log.info("{},{}",entry.getKey(), entry.getValue());
        }
        //forEach遍历map
        map1.forEach((k,v) -> {});

        //删除元素
        map1.entrySet().removeIf(entry -> entry.getValue() == 2);
    }

    @Test
    public void LinkedHashMap作为定长LRU缓存() {

        // 16表示初始map大小
        //0.75f表示容量达到 75% 后进行自动扩容
        // true表示启动得带顺序特性， 访问元素后，将元素放到最后面
        Map<Integer, Integer> map =
                new LinkedHashMap<Integer, Integer>(16, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                        //重写删除实例方法，当容量大于5时删除最老的数据
                        return size() > 5;
                    }
                };

        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }

        log.info("{}",map.keySet());

        map.get(6);
        log.info("{}",map.keySet());

    }
}
