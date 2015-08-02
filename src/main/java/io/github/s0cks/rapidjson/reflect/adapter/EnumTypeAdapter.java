package io.github.s0cks.rapidjson.reflect.adapter;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;

public final class EnumTypeAdapter<T extends Enum>
implements TypeAdapter<T>{
    @Override
    public T deserialize(Class<T> tClass, Value v) {
        String name = v.asString();
        try {
            return (T) T.valueOf(tClass, name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Value serialize(T value) {
        return new Values.StringValue("<enum>", value.name());
    }
}