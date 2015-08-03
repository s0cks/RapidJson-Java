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
            return Values.of(f.get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Value of(Object obj){
        if(obj == null){
            return new NullValue();
        } else if(obj instanceof Number){
            return new NumberValue((Number) obj);
        } else if(obj instanceof String){
            return new StringValue(String.valueOf(obj));
        } else if(obj instanceof Boolean){
            return new BooleanValue((Boolean) obj);
        } else if(Types.equals(LIST_TYPE, Types.getRawType(obj.getClass()))){
            List<Value> values = (List<Value>) obj;
            return new ArrayValue(values.toArray(new Value[values.size()]));
        } else if(Types.equals(MAP_TYPE, Types.getRawType(obj.getClass()))){
            return new ObjectValue(((Map<String, Value>) obj));
        } else{
            throw new IllegalStateException("Unknown type: " + obj.getClass().getName());
        }
    }

    private static abstract class AbstractValue
    implements Value{
        @Override
        public boolean isNull(){
            return false;
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

        public ObjectValue(Map<String, Value> values){
            this.values = values;
        }

        public ObjectValue(){
            this(new HashMap<String, Value>());
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

        public StringValue(String value){
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

        public NumberValue(Number value){
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

        public ArrayValue(Value[] values){
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
        public static final NullValue NULL = new NullValue();

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
        public static final BooleanValue TRUE = new BooleanValue(true);
        public static final BooleanValue FALSE = new BooleanValue(false);

        private final boolean value;

        public BooleanValue(boolean value){
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