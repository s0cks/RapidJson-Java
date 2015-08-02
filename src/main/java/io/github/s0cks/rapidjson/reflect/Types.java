package io.github.s0cks.rapidjson.reflect;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

public final class Types{
    public static final Type TYPE_STRING = new TypeToken<String>(){}.rawType;
    public static final Type TYPE_BYTE = new TypeToken<Byte>(){}.rawType;
    public static final Type TYPE_SHORT = new TypeToken<Short>(){}.rawType;
    public static final Type TYPE_INTEGER = new TypeToken<Integer>(){}.rawType;
    public static final Type TYPE_LONG = new TypeToken<Long>(){}.rawType;
    public static final Type TYPE_FLOAT = new TypeToken<Float>(){}.rawType;
    public static final Type TYPE_DOUBLE = new TypeToken<Double>(){}.rawType;
    public static final Type TYPE_BOOLEAN = new TypeToken<Boolean>(){}.rawType;

    public static Type unbox(Type t){
        if(t.equals(boolean.class)){
            return TYPE_BOOLEAN;
        } else if(t.equals(short.class)){
            return TYPE_SHORT;
        } else if(t.equals(byte.class)){
            return TYPE_BYTE;
        } else if(t.equals(int.class)){
            return TYPE_INTEGER;
        } else if(t.equals(float.class)){
            return TYPE_FLOAT;
        } else if(t.equals(double.class)){
            return TYPE_DOUBLE;
        } else{
            return t;
        }
    }

    public static boolean array(Type t){
        return t instanceof GenericArrayType
            || (t instanceof Class && ((Class<?>) t).isArray());
    }

    public static Type canonicalize(Type t){
        if(t instanceof Class){
            Class<?> c = (Class<?>) t;
            return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
        } else if(t instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) t;
            return new ParameterizedTypeImpl(pt.getOwnerType(), pt.getRawType(), pt.getActualTypeArguments());
        } else if(t instanceof GenericArrayType){
            return new GenericArrayTypeImpl(((GenericArrayType) t).getGenericComponentType());
        } else if(t instanceof WildcardType){
            WildcardType wt = (WildcardType) t;
            return new WildcardTypeImpl(wt.getUpperBounds(), wt.getLowerBounds());
        } else{
            return t;
        }
    }

    public static Class<?> getRawType(Type t){
        if(t instanceof Class<?>){
            return (Class<?>) t;
        } else if(t instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) t;
            Type rawType = pt.getRawType();
            if(!(rawType instanceof Class<?>)){
                throw new IllegalArgumentException("rawType not instanceof Class");
            }

            return (Class<?>) rawType;
        } else if(t instanceof GenericArrayType){
            Type compType = ((GenericArrayType) t).getGenericComponentType();
            return Array.newInstance(getRawType(compType), 0).getClass();
        } else if(t instanceof TypeVariable){
            return Object.class;
        } else if(t instanceof WildcardType){
            return getRawType(((WildcardType) t).getUpperBounds()[0]);
        } else{
            String className = t == null ? "null" : t.getClass().getName();
            throw new IllegalArgumentException("type <" + t + "> is of type <" + className + ">");
        }
    }

    private static boolean equal(Object a, Object b){
        return a == b || (a != null && a.equals(b));
    }

    public static boolean equals(Type a, Type b){
        if(a == b){
            return true;
        } else if(a instanceof Class){
            return a.equals(b);
        } else if(a instanceof ParameterizedType){
            if(!(b instanceof ParameterizedType)){
                return false;
            }

            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            return equal(pa.getOwnerType(), pb.getOwnerType())
                && pa.getRawType().equals(pb.getRawType())
                && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
        } else if(a instanceof GenericArrayType){
            if(!(b instanceof GenericArrayType)){
                return false;
            }

            GenericArrayType ga = (GenericArrayType) a;
            GenericArrayType gb = (GenericArrayType) b;
            return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
        } else if(a instanceof TypeVariable){
            if(!(b instanceof TypeVariable)){
                return false;
            }

            TypeVariable<?> va = (TypeVariable<?>) a;
            TypeVariable<?> vb = (TypeVariable<?>) b;
            return va.getGenericDeclaration() == vb.getGenericDeclaration()
                && va.getName().equals(vb.getName());
        } else{
            return false;
        }
    }

    public static String typeToString(Type t){
        return t instanceof Class<?> ? ((Class<?>) t).getName() : t.toString();
    }

    private static final class GenericArrayTypeImpl
    implements GenericArrayType,
               Serializable{
        private final Type compType;

        private GenericArrayTypeImpl(Type compType){
            this.compType = compType;
        }

        @Override
        public Type getGenericComponentType() {
            return this.compType;
        }

        @Override
        public boolean equals(Object o){
            return o instanceof GenericArrayType && Types.equals(this, (GenericArrayType) o);
        }

        @Override
        public int hashCode(){
            return this.compType.hashCode();
        }

        @Override
        public String toString(){
            return Types.typeToString(this.compType) + "[]";
        }
    }

    private static final class WildcardTypeImpl
    implements WildcardType,
               Serializable{
        private final Type upper, lower;

        private WildcardTypeImpl(Type[] upper, Type[] lower) {
            if(lower.length == 1){
                if(!(upper[0] == Object.class)
                || lower[0] == null){
                    throw new IllegalStateException("");
                }

                this.lower = canonicalize(lower[0]);
                this.upper = Object.class;
            } else{
                if(upper[0] == null){
                    throw new IllegalStateException("");
                }
                this.lower = null;
                this.upper = canonicalize(upper[0]);
            }
        }

        @Override
        public Type[] getUpperBounds() {
            return new Type[]{ this.upper };
        }

        @Override
        public Type[] getLowerBounds() {
            return new Type[]{ this.lower };
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof WildcardType
                && Types.equals(this, (WildcardType) obj);
        }

        @Override
        public int hashCode(){
            return (this.lower != null ? 31 + this.lower.hashCode() : 1)
                ^ (31 + this.upper.hashCode());
        }

        @Override
        public String toString(){
            if(lower != null){
                return "? super " + typeToString(this.lower);
            } else if(upper == Object.class){
                return "?";
            } else{
                return "? extends " + typeToString(this.upper);
            }
        }
    }

    private static final class ParameterizedTypeImpl
    implements ParameterizedType,
               Serializable{
        private final Type owner;
        private final Type raw;
        private final Type[] args;

        private ParameterizedTypeImpl(Type owner, Type raw, Type... args) {
            if(raw instanceof Class){
                Class<?> c = (Class<?>) raw;
                boolean staticOrTopLevel = Modifier.isStatic(c.getModifiers())
                        || c.getEnclosingClass() == null;
                if(!(owner == null || !staticOrTopLevel)){
                    throw new IllegalStateException("staticOrTopLevel?=" + staticOrTopLevel + "; owner=" + (owner == null));
                }
            }

            this.owner = owner == null ? null : canonicalize(owner);
            this.raw = canonicalize(raw);
            this.args = new Type[args.length];
            for(int i = 0; i < args.length; i++){
                if(args[i] == null){
                    throw new IllegalStateException("");
                }

                this.args[i] = canonicalize(args[i]);
            }
        }

        @Override
        public Type[] getActualTypeArguments(){
            return this.args.clone();
        }

        @Override
        public Type getRawType(){
            return this.raw;
        }

        @Override
        public Type getOwnerType(){
            return this.owner;
        }

        @Override
        public boolean equals(Object o){
            return o instanceof ParameterizedType
                && Types.equals(this, (ParameterizedType) o);
        }

        @Override
        public int hashCode(){
            return Arrays.hashCode(this.args)
                ^ this.raw.hashCode()
                ^ (this.owner == null ? 0 : this.owner.hashCode());
        }
    }
}