package io.github.s0cks.rapidjson.reflect;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

abstract class UnsafeAllocator{
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

            try{
                Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
                getConstructorId.setAccessible(true);
                final int id = (Integer) getConstructorId.invoke(null, Object.class);
                final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, int.class);
                newInstance.setAccessible(true);
                return instance = new UnsafeAllocator() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> T newInstance(Class<T> tClass)
                    throws Exception {
                        return (T) newInstance.invoke(null, tClass, id);
                    }
                };
            } catch(Exception e){
                // Fallthrough
            }

            try{
                final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
                newInstance.setAccessible(true);
                return instance = new UnsafeAllocator() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> T newInstance(Class<T> tClass)
                    throws Exception {
                        return (T) newInstance.invoke(null, tClass, Object.class);
                    }
                };
            } catch(Exception e){
                // Fallthrough
            }

            return instance = new UnsafeAllocator() {
                @Override
                public <T> T newInstance(Class<T> tClass)
                throws Exception {
                    throw new UnsupportedOperationException("Cannot allocate " + tClass);
                }
            };
        }

        return instance;
    }
}