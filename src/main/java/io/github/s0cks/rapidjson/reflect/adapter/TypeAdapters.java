package io.github.s0cks.rapidjson.reflect.adapter;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;

public final class TypeAdapters{
    public static final TypeAdapter<Boolean> BOOLEAN_ADAPTER = new TypeAdapter<Boolean>() {
        @Override
        public Boolean deserialize(Class<Boolean> booleanClass, Value v) {
            return v.asBoolean();
        }

        @Override
        public Value serialize(Boolean value) {
            return new Values.BooleanValue(null, value);
        }
    };

    public static final TypeAdapter<Integer> INTEGER_ADAPTER = new TypeAdapter<Integer>() {
        @Override
        public Integer deserialize(Class<Integer> integerClass, Value v) {
            return v.asInt();
        }

        @Override
        public Value serialize(Integer value) {
            return new Values.NumberValue(null, value);
        }
    };

    public static final TypeAdapter<String> STRING_ADAPTER = new TypeAdapter<String>() {
        @Override
        public String deserialize(Class<String> stringClass, Value v) {
            return v.asString();
        }

        @Override
        public Value serialize(String value) {
            return new Values.StringValue(null, value);
        }
    };

    public static final TypeAdapter<Short> SHORT_ADAPTER = new TypeAdapter<Short>() {
        @Override
        public Short deserialize(Class<Short> shortClass, Value v) {
            return v.asShort();
        }

        @Override
        public Value serialize(Short value) {
            return new Values.NumberValue(null, value);
        }
    };

    public static final TypeAdapter<Double> DOUBLE_ADAPTER = new TypeAdapter<Double>() {
        @Override
        public Double deserialize(Class<Double> doubleClass, Value v) {
            return v.asDouble();
        }

        @Override
        public Value serialize(Double value) {
            return new Values.NumberValue(null, value);
        }
    };

    public static final TypeAdapter<Byte> BYTE_ADAPTER = new TypeAdapter<Byte>() {
        @Override
        public Byte deserialize(Class<Byte> byteClass, Value v) {
            return v.asByte();
        }

        @Override
        public Value serialize(Byte value) {
            return new Values.NumberValue(null, value);
        }
    };

    public static final TypeAdapter<Long> LONG_ADAPTER = new TypeAdapter<Long>() {
        @Override
        public Long deserialize(Class<Long> longClass, Value v) {
            return v.asLong();
        }

        @Override
        public Value serialize(Long value) {
            return new Values.NumberValue(null, value);
        }
    };

    public static final TypeAdapter<Float> FLOAT_ADAPTER = new TypeAdapter<Float>() {
        @Override
        public Float deserialize(Class<Float> floatClass, Value v) {
            return v.asFloat();
        }

        @Override
        public Value serialize(Float value) {
            return new Values.NumberValue(null, value);
        }
    };
}