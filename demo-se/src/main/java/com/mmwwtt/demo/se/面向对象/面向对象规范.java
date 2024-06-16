package com.mmwwtt.demo.se.面向对象;


import com.mmwwtt.demo.se.公共类.A;
import com.mmwwtt.demo.se.公共类.B;
import org.junit.jupiter.api.Test;

public class 面向对象规范 {

    /**
     * 避免定义public 且无 final修饰的变量，应该将属性设置为private,避免他人直接依赖变量
     *
     * 子类重写父类方法时，要加上@Override注解，当参数不一致时会有提醒
     * java8种可以在接口种加上静态方法表示相关工厂/助手方法，（例如把Collections的方法放到Collection接口里面）
     *
     * 不要在父类构造方法种调用被子类重写的方法，当子类初始化时会调用父类构造方法，
     *  当构造方法种调用了子类覆写的方法，往往由于子类未初始化而出现异常
     */

    /**
     * 隐藏（hide）父子类之间
     * 一个类的属性，静态方法，内部类可以分别隐藏在其超类种可以访问到的具有相同名字的属性，静态变量，内部类种
     */
    @Test
    public void 隐藏() {
        A a = new B();
        B b = new B();
        //执行的是类A的message, 子类种的message被父类同名变量隐藏了
        System.out.println(a.message);
        System.out.println(b.message);
    }

    /**
     * 遮蔽（shadow） 类内部
     * 一个变量/方法/类可以分别遮蔽在类内部具有相同名字的变量/方法/类，如果一个实体被遮蔽了，将无法引用到他
     */
    public String href;
    @Test
    public void 遮蔽() {
        A href = new A();
        System.out.println(href);
    }

    /**
     * 遮掩(obscure)类内部
     * 一个变量可以遮掩具有相同名字的一个类，只要他们在同个范围内
     */
    public void 遮掩() {
        String System = "hello";
        //无法通过编译，System包被遮掩了
        //System.out.println();
    }


    public void add(int num1) {
        System.out.println("入参为int");
    }

    public void add(Integer num1) {
        System.out.println("入参为Integer");
    }
    /**
     * 重载方法种要避免由于基本数据类型和包装类型同名而出现的重载，在自动拆装箱场景下，边界很模糊
     * 不仔细看，容易出错
     */
    @Test
    public void 方法重载() {
        add(1);
        add(Integer.valueOf(1));
    }
}
