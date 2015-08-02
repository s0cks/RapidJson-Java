package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.RapidJson;
import io.github.s0cks.rapidjson.RapidJsonBuilder;
import io.github.s0cks.rapidjson.reflect.adapter.ColorTypeAdapter;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                                                          .build();

    public static void main(String... args)
    throws Exception{
    }

    private static final class Colors{
        private final Color[] colors;
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