package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.reflect.TypeAdapter;
import io.github.s0cks.rapidjson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class RapidJsonBuilder{
    protected final Map<Type, TypeAdapter> adapters = new HashMap<>();

    public RapidJsonBuilder registerTypeAdapter(Type t, TypeAdapter tad){
        this.adapters.put(t, tad);
        return this;
    }

    public RapidJsonBuilder registerTypeAdpater(Class<?> c, TypeAdapter tad){
        this.adapters.put(TypeToken.of(c).type, tad);
        return this;
    }

    public RapidJson build(){
        return new RapidJson(this);
    }
}