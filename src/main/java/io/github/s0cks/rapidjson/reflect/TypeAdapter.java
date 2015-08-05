package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.io.JsonOutputStream;

public interface TypeAdapter<T>{
    public T deserialize(Class<T> tClass, Value v);
    public void serialize(T value, JsonOutputStream jos);
}