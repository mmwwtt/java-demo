package com.mmwwtt.demo.se.公共类;

public class B extends A {

    public String message = "B";

    public static void showMessage() {
        System.out.println("B类的静态方法");
    }

    public String getMessage() {
        return "B";
    }

}
