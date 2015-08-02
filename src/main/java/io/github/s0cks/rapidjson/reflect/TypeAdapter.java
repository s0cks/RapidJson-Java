package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;

public interface TypeAdapter<T>{
    public T deserialize(Class<T> tClass, Value v);
    public Value serialize(T value);
}