package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;

import java.lang.reflect.Type;
import java.util.List;

final class TypeAdapterFactories{
    private static final class EnumTypeAdapter<T extends Enum<T>>
    implements TypeAdapter<T>{
        @Override
        public T deserialize(Class<T> tClass, Value v) {
            return T.valueOf(tClass, v.asString());
        }

        @Override
        public Value serialize(T value) {
            return new Values.StringValue(value.name());
        }
    }

    public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> boolean can(TypeToken<T> token) {
            return Enum.class.isAssignableFrom(token.rawType) || token.rawType != Enum.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token) {
            Class<? super T> rawType = token.rawType;
            if(!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class){
                return null;
            }
            return (TypeAdapter<T>) new EnumTypeAdapter();
        }
    };

    public static final TypeAdapterFactory LIST_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> boolean can(TypeToken<T> token) {
            return List.class.isAssignableFrom(token.rawType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token) {
            Class<? super T> rawType = token.rawType;
            if(!List.class.isAssignableFrom(rawType) || rawType == List.class){
                return null;
            }
            Type elemType = Types.getCollectionElementType(token.type, rawType);
            TypeAdapter<?> adapter = factory.getAdapter(elemType);
            ObjectConstructor<T> constructor = ObjectConstructorFactory.get(token);
            return (TypeAdapter<T>) new ListTypeAdapter<>(elemType, adapter, constructor);
        }
    };

    private static final class ListTypeAdapter<T>
    implements TypeAdapter<List<T>>{
        private final Type elemType;
        private final TypeAdapter adapter;
        private final ObjectConstructor<?> constructor;

        private ListTypeAdapter(Type elemType, TypeAdapter adapter, ObjectConstructor<?> constructor){
            this.elemType = elemType;
            this.adapter = adapter;
            this.constructor = constructor;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<T> deserialize(Class<List<T>> listClass, Value v) {
            List<T> tList = (List<T>) this.constructor.construct();

            for(Value value : v.asArray()){
                tList.add((T) this.adapter.deserialize((Class) this.elemType, value));
            }

            return tList;
        }

        @Override
        public Value serialize(List<T> value) {
            return null;
        }
    }
}