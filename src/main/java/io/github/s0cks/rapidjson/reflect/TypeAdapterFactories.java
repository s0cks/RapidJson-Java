package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.io.JsonOutputStream;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

final class TypeAdapterFactories{
    private static final class EnumTypeAdapter<T extends Enum<T>>
    implements TypeAdapter<T>{
        @Override
        public T deserialize(Class<T> tClass, Value v) {
            return T.valueOf(tClass, v.asString());
        }

        @Override
        public void serialize(T value, JsonOutputStream jos) {
            try {
                jos.value(value.name());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public static final TypeAdapterFactory COLLECTION_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> boolean can(TypeToken<T> token) {
            return Collection.class.isAssignableFrom(token.rawType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token) {
            Class<? super T> rawType = token.rawType;
            if(!Collection.class.isAssignableFrom(rawType) || rawType == Collection.class){
                return null;
            }
            Type elemType = Types.getCollectionElementType(token.type, rawType);
            TypeAdapter<?> adapter = factory.getAdapter(elemType);
            ObjectConstructor<T> constructor = ObjectConstructorFactory.get(token);
            return (TypeAdapter<T>) new ListTypeAdapter<>(elemType, adapter, constructor, factory);
        }
    };

    private static final class ListTypeAdapter<T>
    implements TypeAdapter<Collection<T>>{
        private final Type elemType;
        private final TypeAdapter adapter;
        private final ObjectConstructor<?> constructor;
        private final InstanceFactory factory;

        private ListTypeAdapter(Type elemType, TypeAdapter adapter, ObjectConstructor<?> constructor, InstanceFactory factory){
            this.elemType = elemType;
            this.adapter = adapter;
            this.constructor = constructor;
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Collection<T> deserialize(Class<Collection<T>> listClass, Value v) {
            Collection<T> tList = (Collection<T>) this.constructor.construct();

            for(Value value : v.asArray()){
                try{
                    if(this.adapter != null){
                        tList.add((T) this.adapter.deserialize((Class) this.elemType, value));
                    } else{
                        tList.add((T) this.factory.create((Class) this.elemType, value));
                    }
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            }

            return tList;
        }

        @Override
        public void serialize(Collection<T> value, JsonOutputStream jos) {
            try {
                jos.newArray();

                jos.endArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}