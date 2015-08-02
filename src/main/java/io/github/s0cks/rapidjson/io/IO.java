package io.github.s0cks.rapidjson.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class IO{
    private IO(){}

    public static String consume(InputStream in){
        StringBuilder builder = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line)
                        .append('\n');
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }

        return builder.toString();
    }
}