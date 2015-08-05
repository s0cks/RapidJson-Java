package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.io.JsonOutputStream;

import java.io.IOException;

final class TypeAdapters{
    public static final TypeAdapter<Boolean> BOOLEAN_ADAPTER = new TypeAdapter<Boolean>() {
        @Override
        public Boolean deserialize(Class<Boolean> booleanClass, Value v) {
            return v.asBoolean();
        }

        @Override
        public void serialize(Boolean value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<Integer> INTEGER_ADAPTER = new TypeAdapter<Integer>() {
        @Override
        public Integer deserialize(Class<Integer> integerClass, Value v) {
            return v.asInt();
        }

        @Override
        public void serialize(Integer value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<String> STRING_ADAPTER = new TypeAdapter<String>() {
        @Override
        public String deserialize(Class<String> stringClass, Value v) {
            return v.asString();
        }

        @Override
        public void serialize(String value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<Short> SHORT_ADAPTER = new TypeAdapter<Short>() {
        @Override
        public Short deserialize(Class<Short> shortClass, Value v) {
            return v.asShort();
        }

        @Override
        public void serialize(Short value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<Double> DOUBLE_ADAPTER = new TypeAdapter<Double>() {
        @Override
        public Double deserialize(Class<Double> doubleClass, Value v) {
            return v.asDouble();
        }

        @Override
        public void serialize(Double value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<Byte> BYTE_ADAPTER = new TypeAdapter<Byte>() {
        @Override
        public Byte deserialize(Class<Byte> byteClass, Value v) {
            return v.asByte();
        }

        @Override
        public void serialize(Byte value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<Long> LONG_ADAPTER = new TypeAdapter<Long>() {
        @Override
        public Long deserialize(Class<Long> longClass, Value v) {
            return v.asLong();
        }

        @Override
        public void serialize(Long value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static final TypeAdapter<Float> FLOAT_ADAPTER = new TypeAdapter<Float>() {
        @Override
        public Float deserialize(Class<Float> floatClass, Value v) {
            return v.asFloat();
        }

        @Override
        public void serialize(Float value, JsonOutputStream jos) {
            try {
                jos.value(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };
}