package com.mmwwtt.java.demo.se.集合;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Array数组 {

    @Test
    public void array数组常用方法() {
        //创建空的数组
        int[] array2 = new int[3];

        //初始化一维数组
        int[] array1 = {1,3,5};

        //二维数组初始化
        int[][] array3 = new int[][]{{2, 4}, {3, 5}};

        //获得数组长度
        System.out.println(array1.length);



        //通过System.arraycopy() 或者 Arrays.copyof() 进行数组复制 (深拷贝)
        int[] array4 = new int[array1.length];
        System.arraycopy(array1, 0, array4, 0, array1.length);

        //二维数组复制
        int[][] arr1 = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        int[][] arr2 = new int[arr1.length][arr1[0].length];
        for (int i = 0; i < arr1.length; i++) {
            System.arraycopy(arr1[i], 0, arr2[i], 0, arr1[0].length);
        }


    }
}
