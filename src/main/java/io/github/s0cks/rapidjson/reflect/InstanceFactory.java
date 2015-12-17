package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.JsonException;
import io.github.s0cks.rapidjson.SerializedName;
import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.io.JsonOutputStream;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class InstanceFactory{
    private final Map<Type, TypeAdapter> adapters;
    private final List<TypeAdapterFactory> factories;

    public InstanceFactory(Map<Type, TypeAdapter> adapters, List<TypeAdapterFactory> factories){
        this.adapters = adapters;
        this.adapters.put(Types.TYPE_BOOLEAN, TypeAdapters.BOOLEAN_ADAPTER);
        this.adapters.put(Types.TYPE_BYTE, TypeAdapters.BYTE_ADAPTER);
        this.adapters.put(Types.TYPE_DOUBLE, TypeAdapters.DOUBLE_ADAPTER);
        this.adapters.put(Types.TYPE_FLOAT, TypeAdapters.FLOAT_ADAPTER);
        this.adapters.put(Types.TYPE_INTEGER, TypeAdapters.INTEGER_ADAPTER);
        this.adapters.put(Types.TYPE_LONG, TypeAdapters.LONG_ADAPTER);
        this.adapters.put(Types.TYPE_SHORT, TypeAdapters.SHORT_ADAPTER);
        this.adapters.put(Types.TYPE_STRING, TypeAdapters.STRING_ADAPTER);
        this.factories = factories;
        this.factories.add(TypeAdapterFactories.COLLECTION_FACTORY);
        this.factories.add(TypeAdapterFactories.ENUM_FACTORY);
    }

    public TypeAdapter<?> getAdapter(Type t){
        return this.getAdapter(new TypeToken(t));
    }

    private String getFieldName(Field f){
        return f.isAnnotationPresent(SerializedName.class) ? f.getAnnotation(SerializedName.class).value() : f.getName();
    }

    @SuppressWarnings("unchecked")
    public <T> void write(T t, JsonOutputStream json)
    throws IOException, IllegalAccessException {
        TypeToken tToken = TypeToken.of(t.getClass());
        TypeAdapter<T> tAdapter = this.getAdapter(tToken);
        if(tAdapter != null){
            tAdapter.serialize(t, json);
        }

        if(Types.array(tToken.type)){
            json.newArray();

            json.endArray();
        } else{
            json.newObject();

            for(int i = 0; i < t.getClass().getDeclaredFields().length; i++){
                Field field = t.getClass().getDeclaredFields()[i];
                if(!Modifier.isStatic(field.getModifiers())){
                    if(!field.isAccessible()){
                        field.setAccessible(true);
                    }

                    TypeToken fToken = TypeToken.of(field);
                    Type fType = Types.unbox(fToken.type);
                    if(isGeneric(fType)){
                        this.writePrimitiveField(fType, t, field, json);
                    }

                    if(i < t.getClass().getDeclaredFields().length - 1){
                        json.next();
                    }
                }
            }

            json.endObject();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> TypeAdapter<T> getAdapter(TypeToken<T> token){
        if(this.adapters.containsKey(token.rawType)){
            return this.adapters.get(token.rawType);
        }

        for(TypeAdapterFactory factory : this.factories){
            if(factory.can(token)){
                return factory.create(this, token);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(TypeToken<T> t, Value value)
    throws IllegalAccessException, JsonException, NoSuchFieldException{
        Type type = Types.unbox(t.type);
        TypeAdapter<T> adapter = this.getAdapter(t);
        if(adapter != null){
            return adapter.deserialize((Class<T>) t.rawType, value);
        }

        if(Types.array(t.type) || Types.array(t.rawType)){
            Value[] values = value.asArray();
            Object array = Array.newInstance(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values.length);
            for(int i = 0; i < values.length; i++){
                Array.set(array, i, this.create(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values[i]));
            }
            return (T) array;
        }

        ObjectConstructor<T> tObjectConstructor = ObjectConstructorFactory.get(t);
        T instance = tObjectConstructor.construct();
        for(Field field : t.rawType.getDeclaredFields()){
            if(!Modifier.isStatic(field.getModifiers())){
                if(!field.isAccessible()) {
                    field.setAccessible(true);
                }

                TypeToken token = TypeToken.of(field);
                Type ftype = Types.unbox(token.type);
                int mods = field.getModifiers();

                if(Modifier.isFinal(mods)){
                    Field modField = t.rawType.getClass().getDeclaredField("modifiers");
                    modField.setAccessible(true);
                    modField.setInt(field, mods & ~Modifier.FINAL);
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                    modField.set(field, mods);
                } else{
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                }
            }
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> tClass, Value value)
    throws IllegalAccessException, NoSuchFieldException {
        TypeToken t = TypeToken.of(tClass);
        Type type = Types.unbox(t.type);
        TypeAdapter<T> adapter = this.getAdapter(t);
        if(adapter != null){
            return adapter.deserialize((Class<T>) t.rawType, value);
        }

        if(Types.array(t.type) || Types.array(t.rawType)){
            Value[] values = value.asArray();
            Object array = Array.newInstance(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values.length);
            for(int i = 0; i < values.length; i++){
                Array.set(array, i, this.create(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values[i]));
            }
            return (T) array;
        }

        ObjectConstructor<T> tObjectConstructor = ObjectConstructorFactory.get(TypeToken.of(tClass));
        T instance = tObjectConstructor.construct();
        for(Field field : tClass.getDeclaredFields()){
            if(!Modifier.isStatic(field.getModifiers())){
                if(!field.isAccessible()) {
                    field.setAccessible(true);
                }

                TypeToken token = TypeToken.of(field);
                Type ftype = Types.unbox(token.type);
                int mods = field.getModifiers();

                if(Modifier.isFinal(mods)){
                    Field modField = field.getClass().getDeclaredField("modifiers");
                    modField.setAccessible(true);
                    modField.setInt(field, mods & ~Modifier.FINAL);
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                    modField.set(field, mods);
                } else{
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                }
            }
        }

        return instance;
    }

    private <T> void set(T instance, Type raw, Field f, Value value)
    throws IllegalAccessException {
        if(raw.equals(Types.TYPE_BOOLEAN)){
            f.setBoolean(instance, value.asBoolean());
        } else if(raw.equals(Types.TYPE_BYTE)){
            f.setByte(instance, value.asByte());
        } else if(raw.equals(Types.TYPE_DOUBLE)){
            f.setDouble(instance, value.asDouble());
        } else if(raw.equals(Types.TYPE_FLOAT)){
            f.setFloat(instance, value.asFloat());
        } else if(raw.equals(Types.TYPE_INTEGER)){
            f.setInt(instance, value.asInt());
        } else if(raw.equals(Types.TYPE_LONG)){
            f.setLong(instance, value.asLong());
        } else if(raw.equals(Types.TYPE_SHORT)){
            f.setShort(instance, value.asShort());
        } else if(raw.equals(Types.TYPE_STRING)){
            f.set(instance, value.asString());
        } else{
            throw new RuntimeException("Invalid primitive type: " + raw);
        }
    }

    private void writePrimitiveField(Type raw, Object instance, Field f, JsonOutputStream jos)
    throws IOException, IllegalAccessException{
        if(raw.equals(Types.TYPE_BOOLEAN)){
            jos.name(this.getFieldName(f))
               .value(f.getBoolean(instance));
        } else if(raw.equals(Types.TYPE_BYTE)){
            jos.name(this.getFieldName(f))
                .value(f.getByte(instance));
        } else if(raw.equals(Types.TYPE_DOUBLE)){
            jos.name(this.getFieldName(f))
                .value(f.getDouble(instance));
        } else if(raw.equals(Types.TYPE_FLOAT)){
            jos.name(this.getFieldName(f))
                .value(f.getFloat(instance));
        } else if(raw.equals(Types.TYPE_INTEGER)){
            jos.name(this.getFieldName(f))
                .value(f.getInt(instance));
        } else if(raw.equals(Types.TYPE_LONG)){
            jos.name(this.getFieldName(f))
                .value(f.getLong(instance));
        } else if(raw.equals(Types.TYPE_SHORT)){
            jos.name(this.getFieldName(f))
                .value(f.getShort(instance));
        } else if(raw.equals(Types.TYPE_STRING)){
            jos.name(this.getFieldName(f))
                .value((String) f.get(instance));
        }
    }

    private boolean isGeneric(Type raw){
        return raw.equals(Types.TYPE_BOOLEAN)
            || raw.equals(Types.TYPE_BYTE)
            || raw.equals(Types.TYPE_DOUBLE)
            || raw.equals(Types.TYPE_FLOAT)
            || raw.equals(Types.TYPE_INTEGER)
            || raw.equals(Types.TYPE_LONG)
            || raw.equals(Types.TYPE_SHORT)
            || raw.equals(Types.TYPE_STRING);
    }
}