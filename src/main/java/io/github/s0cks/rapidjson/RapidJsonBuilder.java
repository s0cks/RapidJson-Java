package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.reflect.TypeAdapter;
import io.github.s0cks.rapidjson.reflect.TypeAdapterFactory;
import io.github.s0cks.rapidjson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class RapidJsonBuilder{
    protected final Map<Type, TypeAdapter> adapters = new HashMap<>();
    protected final List<TypeAdapterFactory> factories = new LinkedList<>();

    public RapidJsonBuilder registerTypeAdapter(Type t, TypeAdapter tad){
        this.adapters.put(t, tad);
        return this;
    }

    public RapidJsonBuilder registerTypeAdpater(Class<?> c, TypeAdapter tad){
        this.adapters.put(TypeToken.of(c).type, tad);
        return this;
    }

    public RapidJsonBuilder registerTypeAdapterFactory(TypeAdapterFactory fact){
        this.factories.add(fact);
        return this;
    }

    public RapidJson build(){
        return new RapidJson(this);
    }
}