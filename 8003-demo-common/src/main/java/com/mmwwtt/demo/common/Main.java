package com.mmwwtt.demo.common;


import static com.mmwwtt.demo.common.util.CommonUtils.compressJson;

public class Main {
    public static void main(String args[]) throws Exception {
        String json = "{\n" +
                "\t\"bool\": {\n" +
                "\t\t\"must\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"match\": {\n" +
                "\t\t\t\t\t\"name\": \"?欢\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}";
        String compressedJson = compressJson(json);
        System.out.println(compressedJson);

    }

}
