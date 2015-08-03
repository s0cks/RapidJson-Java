package io.github.s0cks.rapidjson.reflect;

public interface TypeAdapterFactory{
    public <T> boolean can(TypeToken<T> token);
    public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token);
}