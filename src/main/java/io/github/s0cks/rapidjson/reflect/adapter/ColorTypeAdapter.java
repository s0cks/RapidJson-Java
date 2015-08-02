package io.github.s0cks.rapidjson.reflect.adapter;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;

import java.awt.Color;

public final class ColorTypeAdapter
implements TypeAdapter<Color>{
    @Override
    public Color deserialize(Class<Color> colorClass, Value v) {
        return Color.decode(v.asString());
    }

    @Override
    public Value serialize(Color value) {
        return new Values.StringValue("name", "#" + value.getRGB());
    }
}