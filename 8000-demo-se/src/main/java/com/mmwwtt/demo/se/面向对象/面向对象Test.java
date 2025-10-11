package com.mmwwtt.demo.se.面向对象;


import com.mmwwtt.demo.se.common.Level_0;
import com.mmwwtt.demo.se.common.Level_0_1;
import com.mmwwtt.demo.se.common.Level_0_2;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 避免public且无final修饰的变量，属性设置为private,避免他人直接依赖
 * 不要在父类构造方法中调用被子类重写的方法，当子类初始化时会调用父类构造方法
 *  当构造方法中调用子类重写的方法，往往由于子类未初始化而出现异常
 */
@Slf4j
public class 面向对象Test {

    /**
     * 编译看左，运行看右
     * 多态是方法的多态，和成员变量没有关系
     * 方法会执行左边子类的方法，成员变量依旧是右边类的成员变量
     * private 修饰不会被子类继承
     */
    @Test
    @DisplayName("测试多态")
    public void test1() {
        Level_0 level01 = new Level_0_1();
        log.info(level01.getMessage());

        Level_0 level02 = new Level_0_2();
        log.info(level02.getMessage());
    }

    /**
     * 用类名来调用静态方法，不要实例调用
     */
    @Test
    @DisplayName("测试静态方法调用")
    public void test2() {
        Level_0.showMessage();
    }


    @Test
    @DisplayName("重用构造方法")
    public void test3() {
        Demo demo = new Demo();
        Demo demo0 = new Demo(1);
        Demo demo1 = new Demo(1,1);
        Demo demo2 = new Demo(1,1,1);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    class Demo {
        Integer x;
        Integer y;
        Integer value;

        public Demo(Integer x) {
            this(x, null);
        }

        public Demo(Integer x, Integer y) {
            this(x, y, null);
        }
    }

    public void add(int num1) {
        log.info("入参为int");
    }

    public void add(Integer num1) {
        log.info("入参为Integer");
    }
    /**
     * 重载要避免基本/包装类型的重载，在自动拆装箱场景下，边界很模糊
     * 不仔细看，容易出错
     */
    @Test
    @DisplayName("方法重载")
    public void test4() {
        add(1);
        add(Integer.valueOf(1));
    }
}
