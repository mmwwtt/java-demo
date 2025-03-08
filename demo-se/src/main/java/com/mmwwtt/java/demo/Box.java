package com.mmwwtt.java.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class Box {
    public Double length;
    public Double width;
    public Double height;
    public Double weight;

    public Integer lengthIn;
    public Integer widthIn;
    public Integer heightIn;
    public Integer weightLB;

    public Integer firstLen;
    public Integer secondLen;
    public Integer thirdLen;

    public Integer realWeight;
    public Integer girth;

    public static String OUT_SPACE = "OUT_SPACE";
    public static String OVERSIZE = "OVERSIZE";
    public static String AHS_SIZE = "AHS-SIZE";
    public static String AHS_WEIGHT = "AHS-WEIGHT";
    public static Integer V_WEIGHT_BASE = 250;

    public Box(double length, double width, double height, double weight) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.heightIn = (int) Math.ceil(height / 2.54);
        this.widthIn = (int) Math.ceil(width / 2.54);
        this.lengthIn = (int) Math.ceil(length / 2.54);
        this.weightLB = (int) Math.ceil(weight / 0.454);

        List<Integer> list = Arrays.asList(lengthIn, widthIn, heightIn);
        Collections.sort(list, Collections.reverseOrder());
        this.firstLen = list.get(0);
        this.secondLen = list.get(1);
        this.thirdLen = list.get(2);

        this.realWeight = getRealWeight();
        this.girth = getGirth();
    }

    //计算围长
    public Integer getGirth() {
        return (int) Math.ceil(firstLen + (secondLen + thirdLen) * 2);
    }

    //计算体积重
    public Integer getVWeight() {
        return (int) Math.ceil(firstLen * secondLen * thirdLen / V_WEIGHT_BASE);
    }

    //计算实重
    public Integer getRealWeight() {
        Integer vWeight = getVWeight();
        return weightLB > vWeight ? weightLB : vWeight;
    }

    public Boolean isOutSpace() {
        if (realWeight > 150 || firstLen > 108 || girth > 165) {
            return true;
        }
        return false;
    }

    public Boolean isOverSize() {
        if ((girth > 130 && girth <= 165) || (firstLen >= 96 && firstLen < 108)) {
            return true;
        }
        return false;
    }

    public Boolean isAhsWeight() {
        if (realWeight > 50 && realWeight <= 150) {
            return true;
        }
        return false;
    }

    public Boolean isAhsSize() {
        if (girth > 105 || (firstLen >= 48 && firstLen < 108) || secondLen >= 30) {
            return true;
        }
        return false;
    }

    public List<String> getResult() {
        List<String> result = new ArrayList<>();
        if (isOutSpace()) {
            result.add(OUT_SPACE);
            return result;
        }
        if (isOverSize()) {
            result.add(OVERSIZE);
            return result;
        }
        if (isAhsWeight()) {
            result.add(AHS_WEIGHT);
        }
        if (isAhsSize()) {
            result.add(AHS_SIZE);
        }
        return result;
    }
}
@Slf4j
class Demo {
    public static void main(String[] args) {
        Box box = new Box(68, 70, 60, 23);
        log.info(box.getResult().stream().collect(Collectors.joining(" , ")));

    }
}