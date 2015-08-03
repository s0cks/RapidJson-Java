package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.RapidJson;
import io.github.s0cks.rapidjson.RapidJsonBuilder;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InstanceFactoryTest {
    private static String json;

    static{
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.class.getResourceAsStream("/test.json")))){
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null){
                builder.append(line);
            }

            json = builder.toString();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static final RapidJson rapidJson = new RapidJsonBuilder()
            .build();

    public static void main(String... args)
    throws Exception{
        long start = System.nanoTime();
        List<Name> names = rapidJson.fromJson(json, new TypeToken<LinkedList<Name>>(){});
        System.out.println("Deserialization Took: " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - start)) + "ms");
        for(Name name : names){
            System.out.println(name);
        }
    }

    private enum Name{
        GEORGE, BOB, JIM;
    }

    private static final class Colors{
        @io.github.s0cks.rapidjson.Name("colorz") private final Color[] colors;
        private final boolean[] flags;
        private final String[] names;

        private Colors(Color[] colors, boolean[] flags, String[] names){
            this.colors = colors;
            this.flags = flags;
            this.names = names;
        }
    }

    private static final class TypeClass{
        private boolean dev = false;
        private int id = 100;
        private String name = "Hello World";
        private Color color = new Color(0x000);
    }
}