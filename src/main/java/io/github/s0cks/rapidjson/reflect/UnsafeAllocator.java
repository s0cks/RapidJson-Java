package io.github.s0cks.rapidjson.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator{
    public abstract <T> T newInstance(Class<T> tClass)
    throws Exception;

    private static UnsafeAllocator instance;

    public static UnsafeAllocator instance(){
        if(instance == null){
            try{
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field f = unsafeClass.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                final Object unsafe = f.get(null);
                final Method allocator = unsafeClass.getDeclaredMethod("allocateInstance", Class.class);
                return instance = new UnsafeAllocator(){
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> T newInstance(Class<T> tClass)
                    throws Exception{
                        return (T) allocator.invoke(unsafe, tClass);
                    }
                };
            } catch(Exception e){
                // Fallthrough
            }
        }

        return instance;
    }
}