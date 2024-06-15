package com.mmwwtt.java.demo.se.设计模式.SOLID五大设计原则.合成复用原则;

/**
 * 尽量使用合成复用原则，而不是继承
 * 合成聚合均是关联关系的特殊情况，
 * 能够直接黑箱复用组合类的其他属性和行为，因为组合对象内部细节是当前对象无法看见的，所以复用所需的依赖少
 */
public class 合成复用原则 {
}

class Legs {

}

/**
 * 组合：类中含有另一个类的实例化
 */
class Wolf {
    private Legs legs = new Legs();
}

/**
 * 聚合：类中含有另一个类作为参数
 */
class WolfGroup {
    public Wolf wolf1;
    public Wolf wolf2;
    public WolfGroup(Wolf wolf1, Wolf wolf2) {
        this.wolf1 = wolf1;
        this.wolf2 = wolf2;
    }
}