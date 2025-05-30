package com.mmwwtt.demo.ee.junit5;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Jumit5Test {
    @BeforeAll
    public static void setup() {
        System.out.println("before all");
    }

    @BeforeEach
    public void beforeEachTest() {
        System.out.println("Before each test");
    }

    @AfterEach
    public void afterEachTest() {
        System.out.println("After each test");
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("Teardown once for all tests");
    }

    @Test
    @DisplayName("测试equals")
    public void test() {
        String str1 = "hello";
        String str2 = "hello";
        boolean boolean1 = true;
        assertEquals(str1, str2);
        assertTrue(boolean1);
        assertNotNull(str1);
        assertThrows(Exception.class, () -> {
           int result = 10/0;
        },"Division by zero should throw ArithmeticException");

        //数组是否相等
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};
        assertArrayEquals(expected, actual, "Arrays should be equal");

        //集合是否相等
        List<Integer> expectedList = List.of(1, 2, 3);
        List<Integer> actualList = List.of(1, 2, 3);
        assertEquals(expectedList, actualList, "Collections should be equal");
    }
}
