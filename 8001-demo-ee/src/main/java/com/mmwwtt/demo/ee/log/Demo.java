package com.mmwwtt.demo.ee.log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demo {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    @Test
    public void doSomething() {
        logger.info("Doing something...");
    }
}
