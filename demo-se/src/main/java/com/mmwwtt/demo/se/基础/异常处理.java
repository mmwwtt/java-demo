package com.mmwwtt.demo.se.基础;

import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 业务中，异常捕获后，首先要将异常记录到日志中（原有异常不能丢，要用做异常原因分析），然后再抛出一个新的与业务代码对应的异常。
 */
@Slf4j
public class 异常处理 {



    @Test
    public void 捕获异常() {
        try {
            throw new Exception("返回的异常信息");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Test
    public void 抛出异常() throws Exception {
        try {
            throw new Exception("返回的异常信息");
        } finally {

        }
    }

    public BaseInfoVO fun1() {
        BaseInfoVO baseInfoVO = BaseInfoVO.getPresetSingle1();
        try {
            baseInfoVO.setAge(18);
            return baseInfoVO;
        } finally {
            baseInfoVO.setAge(22);
        }
    }

    public int fun2() {
        int num = 100;
        try {
            num = 200;
            return num;
        } finally {
            num = 300;
        }
    }

    /**
     * try中会把return的变量地址缓存起来
     * finally对return的变量地址做的修改，不会影响 try中的return，但是可以修改变量中的内容
     */
    @Test
    public void finally中对return变量做修改() {
        log.info("{}",fun1());
        log.info("{}",fun2());
    }


    /**
     * 不会抛出异常，因为在抛出异常前执行了continue,跳出了当层循环
     * 建议：不要在finally块中使用return,break,continue语句，会导致非正常结束
     * @throws Exception
     */
    @Test
    public void finally中使用continue() throws Exception {
        for (int i = 0; i < 2; i++) {
            try {
                throw new Exception("异常信息");
            } catch (Exception e) {
                throw e;
            } finally {
                continue;
            }
        }
    }

    /**
     * 异常捕获后要做出处理或者向上层抛出，不能用空的catch块捕获异常
     */
    @Test
    public void 不能通过空的catch来忽略异常() {
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {

        }
    }


    /**
     * 抛出的异常中可能会包含某些敏感信息，如果直接记录到日志或反馈给用户，会导致信息泄露
     * 如：文件路径不存在时抛出异常，恶意用户可以通过是否抛出异常来遍历构建文件路径，达到文件结构探测目的
     *
     * 解决方式：当异常中存在敏感信息时，抛出统一的异常(不包含敏感信息)
     * @throws Exception
     */
    @Test
    public void 包含敏感信息的异常() throws Exception {

        //黑名单判断：在指定路径开头外时抛出异常
        //白名单判断：在指定文件路径的集合外时抛出异常
        String path = "user/test.txt";
        if (!path.startsWith("user")) {
            throw new Exception("Invalid file...");
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new Exception("Invalid file...");
        }
    }


    /**
     * 不要捕获可通过预检查进行处理的RuntimeException,如NullPointException等
     * 运行时异常表示逻辑处理有问题
     */
    @Test
    public void 不要捕获运行时异常() {
        String str = "hello";
        if (str == null) {
            return;
        }
        //捕获运行时异常是不规范的
        try {
            str.equals("hello");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    /**
     * 如果知道可能抛出的异常，需要先捕获准确的异常类，根据不同的异常做出不同的处理
     * 如果都没捕获到，最后再捕获基类异常(Exception e)
     */
    @Test
    public void 不要直接捕获基类异常() {
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {

        }
    }
}
