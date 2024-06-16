package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.原型模式;

import com.mmwwtt.demo.se.公共类.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 通过一个对象实例确定创建对象的种类，通过拷贝原型来创建新的实例
 * 就是复制原对象
 */
public class 原型模式 {
    public static void main(String[] args) throws CloneNotSupportedException {
        ConcreteObject concreteObject = new ConcreteObject(new Point(1,2,3), 15);
        ConcreteObject concreteObjectCopy = concreteObject.clone();

        //修改对象中的引用类型的对象中的值，被拷贝的对象也会改变
        concreteObject.getPoint().setValue(999);

        System.out.println(concreteObject);
        System.out.println(concreteObjectCopy);


    }
}

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
class ConcreteObject implements Cloneable {
    Point point;
    int num;

    //clone对对象中的基本数据类型是深拷贝，对引用类型是浅拷贝
    @Override
    protected ConcreteObject clone() throws CloneNotSupportedException {
        return (ConcreteObject) super.clone();
    }
}
