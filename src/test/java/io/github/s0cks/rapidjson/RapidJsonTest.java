package io.github.s0cks.rapidjson;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RapidJsonTest {
    @Test
    public void testToJson()
    throws Exception {
        RapidJson json = new RapidJson();
        long start = System.nanoTime();
        System.out.println(json.toJson(new TestClass()));
        System.out.println("Serialization Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    private static final class TestClass{
        private int test = 100;
        private String name = "Hello World";
    }
}