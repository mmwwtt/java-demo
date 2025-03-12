package com.mmwwtt.demo.se.设计模式.设计模式23种.创建型.建造者模式;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class 建造者模式 {

    @Test
    public void test() {
        Builder builder = new ConcreteBUilder1();
        Director director = new Director();
        director.setBuilder(builder);
        Point point = director.createPoint();
        log.info("{}",point);
    }
}

@Data
@ToString
class Point {
    int x;
    int y;
}

/**
 * 抽象构造者
 */
@NoArgsConstructor
abstract class Builder {
    Point point = new Point();

    public abstract void buildX();

    public abstract void buildY();

    public Point buildPoint() {
        return point;
    }
}

/**
 * 具体构造者1
 */
class ConcreteBUilder1 extends Builder {

    @Override
    public void buildX() {
        point.x = 10;
    }

    @Override
    public void buildY() {
        point.y = 10;
    }
}

/**
 * 具体构造者2
 */
class ConcreteBUilder2 extends Builder {

    @Override
    public void buildX() {
        point.x = 100;
    }

    @Override
    public void buildY() {
        point.y = 100;
    }
}

/**
 * 指挥者
 */
@Data
class Director {
    Builder builder = null;

    public Point createPoint() {
        builder.buildX();
        builder.buildY();
        return builder.buildPoint();
    }
}