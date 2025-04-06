package com.mmwwtt.demo.se.集合;


import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class CollectionsDemo {
    Map<String, String> map = new HashMap<>();
    Set<String> set = new HashSet<>();
    List<String> list = new ArrayList<>();


    @Test
    public void 将集合转为不可修改() {

        //将集合转为不可修改
        map = Collections.unmodifiableMap(map);
        set = Collections.unmodifiableSet(set);
        list = Collections.unmodifiableList(list);
        //jdk10 新增写法 长度不能改变
        list = List.of("hello", "world");

        //将集合转为线程安全
        map = Collections.synchronizedMap(map);
        set = Collections.synchronizedSet(set);
        list = Collections.synchronizedList(list);

        //返回空集合
        List<Integer> list1 = Collections.emptyList();

        //集合顺序倒置
        Collections.reverse(list);

        //交换list中两个元素
        Collections.swap(list, 0, 1);

        //对list进行排序（默认字典序/整形升序）
        Collections.sort(list);

        /**
         * 正数交换，第一个元素在后面；
         * 返回0,不交换
         * 负数不交换，第一个元素在前面；
         */
        List<BaseInfoVO> baseInfoList = BaseInfoVO.getPresetList();
        Collections.sort(baseInfoList, (BaseInfoVO a, BaseInfoVO b) -> {
            if (a.getHeight() != b.getHeight()) {
                return a.getHeight() > b.getHeight() ? -1 : 1;
            } else if (!a.getName().equals(b.getName())) {
                return a.getName().compareTo(b.getName());
            } else {
                return 0;
            }
        });
    }
}
