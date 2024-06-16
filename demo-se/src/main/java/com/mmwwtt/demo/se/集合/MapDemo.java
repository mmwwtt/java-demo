package com.mmwwtt.demo.se.集合;


import com.mmwwtt.demo.common.util.CommonUtils;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import org.junit.jupiter.api.Test;

import java.util.*;

public class MapDemo {
    @Test
    public void mapTypeDemo() {
        Map<String, Integer> map1 = new HashMap<>();

        //TreeMap 会根据键进行排序
        Map<String, Integer> map2 = new TreeMap<>();
        Map<BaseInfoVO, Integer> map = new TreeMap<>((a, b) -> {
            if (a.getAge() != b.getAge()) {
                return a.getAge() > b.getAge() ? -1 : 1;
            } else if (!a.getName().equals(b.getName())) {
                return a.getName().compareTo(b.getName());
            } else {
                return 0;
            }
        });

        //LinkedHashMap 会记录插入时的顺序
        Map<String, Integer> map3 = new LinkedHashMap<>();
        CommonUtils.println(map1, map2, map3);
    }

    @Test
    public void mapFunDemo() {
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
            System.out.println(str + " : " + map1.get(str));
        }
    }

    @Test
    public void keyIsObejctDemo() {

        BaseInfoVO baseInfo = BaseInfoVO.getPresetSingle1();
        Map<BaseInfoVO, Integer> map = new HashMap<>();
        map.put(baseInfo, 1);
        System.out.println(map.get(baseInfo));

        baseInfo.setAge(2);
        System.out.println(map.get(baseInfo));
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

        System.out.println(map.keySet());

        map.get(6);
        System.out.println(map.keySet());

    }

    @Test
    public void 获得键值对集合() {
        /**
         * keySet() 获得键集合
         * entrySet() 获得键值对 对象集合
         */
        Map<String, String> map = new HashMap<>();
        Set<String> keySet = map.keySet();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
    }
}
