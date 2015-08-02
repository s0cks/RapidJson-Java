package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.reflect.TypeToken;
import io.github.s0cks.rapidjson.reflect.Types;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Values{
    private static final Type MAP_TYPE = new TypeToken<HashMap<String, Value>>(){}.rawType;
    private static final Type LIST_TYPE = new TypeToken<LinkedList<Value>>(){}.rawType;

    public static Value of(Object instance, Field f){
        try {
            return Values.of(f.getName(), f.get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Value of(String name, Object obj){
        if(name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }

        if(obj == null){
            return new NullValue(name);
        } else if(obj instanceof Number){
            return new NumberValue(name, (Number) obj);
        } else if(obj instanceof String){
            return new StringValue(name, String.valueOf(obj));
        } else if(obj instanceof Boolean){
            return new BooleanValue(name, (Boolean) obj);
        } else if(Types.equals(LIST_TYPE, Types.getRawType(obj.getClass()))){
            List<Value> values = (List<Value>) obj;
            return new ArrayValue(name, values.toArray(new Value[values.size()]));
        } else if(Types.equals(MAP_TYPE, Types.getRawType(obj.getClass()))){
            return new ObjectValue(name, ((Map<String, Value>) obj));
        } else{
            throw new IllegalStateException("Unknown type: " + obj.getClass().getName());
        }
    }

    private static abstract class AbstractValue
    implements Value{
        protected final String name;

        protected AbstractValue(String name){
            this.name = name;
        }

        @Override
        public boolean isNull(){
            return false;
        }

        @Override
        public String name(){
            return this.name;
        }

        @Override
        public byte asByte() {
            throw new UnsupportedOperationException("Not of type byte");
        }

        @Override
        public short asShort() {
            throw new UnsupportedOperationException("Not of type short");
        }

        @Override
        public int asInt() {
            throw new UnsupportedOperationException("Not of type int");
        }

        @Override
        public long asLong() {
            throw new UnsupportedOperationException("Not of type long");
        }

        @Override
        public float asFloat() {
            throw new UnsupportedOperationException("Not of type float");
        }

        @Override
        public double asDouble() {
            throw new UnsupportedOperationException("Not of type double");
        }

        @Override
        public boolean asBoolean() {
            throw new UnsupportedOperationException("Not of type boolean");
        }

        @Override
        public char asChar() {
            throw new UnsupportedOperationException("Not of type char");
        }

        @Override
        public String asString() {
            throw new UnsupportedOperationException("Not of type String");
        }

        @Override
        public Value asObject() {
            throw new UnsupportedOperationException("Not of type Object");
        }

        @Override
        public Value[] asArray() {
            throw new UnsupportedOperationException("Not of type Array");
        }

        @Override
        public Value getValue(String name) {
            throw new UnsupportedOperationException("Not of type object");
        }

        @Override
        public String toString(){
            return this.write();
        }

        @Override
        public void setValue(String name, Value v){
            throw new UnsupportedOperationException("Not of type object");
        }

        @Override
        public void addValue(Value v){
            throw new UnsupportedOperationException("Not of type array");
        }
    }

    public static final class ObjectValue
    extends AbstractValue{
        private final Map<String, Value> values;

        public ObjectValue(String name, Map<String, Value> values){
            super(name);
            this.values = values;
        }

        public ObjectValue(String name){
            this(name, new HashMap<String, Value>());
        }

        @Override
        public Value getValue(String name) {
            return this.values.get(name);
        }

        @Override
        public String write(){
            StringBuilder builder = new StringBuilder()
                    .append("{");
            List<Map.Entry<String, Value>> entrySet = new LinkedList<>(this.values.entrySet());
            for(int i = 0; i < entrySet.size(); i++){
                Map.Entry<String, Value> entry = entrySet.get(i);
                builder.append("\"")
                        .append(entry.getKey())
                        .append("\"")
                        .append(":")
                        .append(entry.getValue().write());
                if(i < entrySet.size() - 1){
                    builder.append(",");
                }
            }
            return builder.append("}").toString();
        }

        @Override
        public void setValue(String name, Value value){
            this.values.put(name, value);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ObjectValue
                && ((ObjectValue) obj).values.equals(this.values);
        }
    }

    public static final class StringValue
    extends AbstractValue{
        private final String value;

        public StringValue(String name, String value){
            super(name);
            this.value = value;
        }

        @Override
        public String asString(){
            return this.value;
        }

        @Override
        public String write() {
            return "\"" + this.value + "\"";
        }

        @Override
        public char asChar(){
            return this.value.charAt(0);
        }

        @Override
        public boolean equals(Object obj){
            return (obj instanceof StringValue
                && ((StringValue) obj).value.equals(this.value))
                || (obj instanceof String
                && obj.equals(this.value));
        }
    }

    public static final class NumberValue
    extends AbstractValue{
        private final Number value;

        public NumberValue(String name, Number value){
            super(name);
            this.value = value;
        }

        @Override
        public byte asByte() {
            return this.value.byteValue();
        }

        @Override
        public double asDouble() {
            return this.value.doubleValue();
        }

        @Override
        public String write() {
            return this.value.toString();
        }

        @Override
        public float asFloat() {
            return this.value.floatValue();
        }

        @Override
        public short asShort(){
            return this.value.shortValue();
        }

        @Override
        public long asLong() {
            return this.value.longValue();
        }

        @Override
        public int asInt() {
            return this.value.intValue();
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof Number
                && obj.equals(this.value);
        }
    }

    public static final class ArrayValue
    extends AbstractValue{
        private Value[] values;

        public ArrayValue(String name, Value[] values){
            super(name);
            this.values = values;
        }

        @Override
        public String write() {
            StringBuilder builder = new StringBuilder()
                    .append("[");
            for(int i = 0; i < this.values.length; i++){
                if(this.values[i] != null){
                    builder.append(this.values[i].toString());
                } else{
                    builder.append("null");
                }

                if(i < this.values.length - 1){
                    builder.append(",");
                }
            }
            return builder.append("]").toString();
        }

        @Override
        public Value[] asArray() {
            return this.values;
        }

        @Override
        public void addValue(Value v){
            Value[] newArrays = new Value[this.values.length + 1];
            System.arraycopy(this.values, 0, newArrays, 0, this.values.length);
            newArrays[this.values.length] = v;
            this.values = newArrays;
        }
    }

    public static final class NullValue
    extends AbstractValue{
        public NullValue(String name){
            super(name);
        }

        @Override
        public boolean isNull(){
            return true;
        }

        @Override
        public String write() {
            return "null";
        }

        @Override
        public boolean equals(Object obj){
            return obj == null;
        }
    }

    public static final class BooleanValue
    extends AbstractValue{
        private final boolean value;

        public BooleanValue(String name, boolean value){
            super(name);
            this.value = value;
        }

        @Override
        public boolean asBoolean(){
            return this.value;
        }

        @Override
        public String write() {
            return String.valueOf(this.value);
        }

        @Override
        public boolean equals(Object obj){
            return obj instanceof Boolean
                && obj.equals(this.value);
        }
    }
}