package com.mmwwtt.demo.se.公共类;

public class Point {
    public int x;
    public int y;
    public int value;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }

    public Point() {
    }

    public Point(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}
