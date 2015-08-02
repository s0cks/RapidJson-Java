package io.github.s0cks.rapidjson;

public interface TypeSerializer<T>{
    public T serialize(Value v)
    throws JsonException;
}