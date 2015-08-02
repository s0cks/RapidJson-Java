package io.github.s0cks.rapidjson.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeToken<T>{
    public final Class<? super T> rawType;
    public final Type type;

    private final int hashCode;

    public static TypeToken of(Field f){
        return new TypeToken(f.getType());
    }

    public static TypeToken of(Class<?> tClass){
        return new TypeToken(tClass);
    }

    @SuppressWarnings("unchecked")
    protected TypeToken(){
        this.type = getSuperclassTypeParameter(this.getClass());
        this.rawType = (Class<? super T>) Types.getRawType(this.type);
        this.hashCode = type.hashCode();
    }


    @SuppressWarnings("unchecked")
    protected TypeToken(Type t){
        this.type = Types.canonicalize(t);
        this.rawType = (Class<? super T>) Types.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass){
        Type superClass = subclass.getGenericSuperclass();
        if(superClass instanceof Class){
            throw new RuntimeException("Missing type parameter");
        }
        ParameterizedType pt = (ParameterizedType) superClass;
        return Types.canonicalize(pt.getActualTypeArguments()[0]);
    }

    @Override
    public final int hashCode(){
        return this.hashCode;
    }

    @Override
    public final boolean equals(Object o){
        return o instanceof TypeToken<?>
            && Types.equals(this.type, ((TypeToken<?>) o).type);
    }

    @Override
    public final String toString(){
        return Types.typeToString(this.type);
    }
}