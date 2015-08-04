package io.github.s0cks.rapidjson.io;

import io.github.s0cks.rapidjson.Value;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class JsonParserTest {
    @Test
    public void testParse()
    throws Exception {
        StringBuilder builder = new StringBuilder();
        String line;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.class.getResourceAsStream("/test.json")))){
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
        }

        long start = System.nanoTime();
        Value v = new JsonInputStream(System.class.getResourceAsStream("/test.json")).parse();
        System.out.println("New Parsing Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }
}