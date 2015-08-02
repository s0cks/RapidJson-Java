package io.github.s0cks.rapidjson.io;

import io.github.s0cks.rapidjson.Value;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

        JsonParser parser = new JsonParser(builder.toString());
        Value v = parser.parse();
        System.out.println(v);
    }
}