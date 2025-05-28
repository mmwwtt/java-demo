package com.mmwwtt.demo.se.设计模式23种;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * 访问者模式
 *  优：要多个访问者只要新增一个访问者类即可，符号开闭原则
 *  缺：要多一个被访问元素，需要在所有访问者中添加访问该元素的操作，违反开闭原则
 *
 */
@Slf4j
public class 结构_访问者模式Test {

    @Test
    @DisplayName("测试访问者模式")
    public void test() {
        Struct struct = new Struct();
        struct.addElement(new ConcreteElementA());
        struct.addElement(new ConcreteElementB());
        struct.acceptVisitor(new ConcreteVisitorA());
        struct.acceptVisitor(new ConcreteVisitorB());
    }

    /**
     * 对象结构，存储具体元素，访问具体元素
     */
    class Struct {
        private List<Element> elementList = new ArrayList<>();

        public void addElement(Element element) {
            elementList.add(element);
        }

        public void remove(Element element) {
            elementList.remove(element);
        }
        public void acceptVisitor(Visitor visitor) {
            for (Element element : elementList) {
                element.accept(visitor);
            }
        }
    }

    /**
     * 抽象元素
     */
    interface Element {
        void accept(Visitor visitor);
    }

    /**
     * 具体元素A
     */
    class ConcreteElementA implements Element {
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * 具体元素B
     */
    class ConcreteElementB implements Element {
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * 抽象访问者
     */
    interface Visitor {
        void visit(ConcreteElementA elementA);
        void visit(ConcreteElementB elementB);
    }

    /**
     * 具体访问者A
     */
    class ConcreteVisitorA implements Visitor{
        public void visit(ConcreteElementA elementA) {
            log.info("访问者A  访问了   elementA");
        }
        public void visit(ConcreteElementB elementB) {
            log.info("访问者A  访问了   elementB");
        }
    }

    class ConcreteVisitorB implements Visitor{
        public void visit(ConcreteElementA elementA) {
            log.info("访问者B  访问了   elementA");
        }
        public void visit(ConcreteElementB elementB) {
            log.info("访问者B  访问了   elementB");
        }
    }
}