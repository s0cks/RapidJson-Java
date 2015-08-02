package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.io.IO;
import io.github.s0cks.rapidjson.io.JsonParser;
import io.github.s0cks.rapidjson.reflect.InstanceFactory;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;

public final class RapidJson{
    private final InstanceFactory instanceFactory;

    public RapidJson(){
        this.instanceFactory = new InstanceFactory(new HashMap<Type, TypeAdapter>());
    }

    protected RapidJson(RapidJsonBuilder builder){
        this.instanceFactory = new InstanceFactory(builder.adapters);
    }

    public <T> T fromJson(String json, Class<T> tClass)
    throws JsonException{
        try {
            return this.instanceFactory.create(tClass, new JsonParser(json).parse());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(InputStream in, Class<T> tClass)
    throws JsonException{
        return fromJson(IO.consume(in), tClass);
    }

    public String toJson(Object obj)
    throws JsonException{
        Value v = new Values.ObjectValue("<root>");
        try {
            this.instanceFactory.emit(obj, v);
        } catch (IllegalAccessException e) {
            throw new JsonException(e.getMessage());
        }
        return v.toString();
    }
}