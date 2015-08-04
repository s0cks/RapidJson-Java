package io.github.s0cks.rapidjson.io;

import io.github.s0cks.rapidjson.JsonException;
import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class JsonInputStream
implements Closeable {
    private final InputStream in;
    private char peek = '\0';
    private String name;
    private String buffer;

    public JsonInputStream(InputStream in){
        if(in == null) throw new NullPointerException("input == null");
        this.in = in;
    }

    public Value parse()
    throws IOException, JsonException {
        switch(this.peek()){
            case '{': return this.parseObject();
            case '[':{
                this.nextReal();
                return this.parseArray();
            }
            default: throw new JsonException("Invalid syntax");
        }
    }

    private Value parseObject()
    throws IOException, JsonException{
        Map<String, Value> values = new HashMap<>();
        char c;
        it: while((c = this.nextReal()) != '}'){
            switch(c){
                case '"':{
                    this.name = this.parseName();
                    c = this.nextReal();
                    break;
                }
                case ',':
                case '{':{
                    continue it;
                }
                case ':':{
                    c = this.nextReal();
                    break;
                }
                default:{
                    break;
                }
            }

            if(c == ':'){
                c = this.nextReal();
            }

            if(c == '"'){
                values.put(this.name, this.parseString());
            } else if(c >= '0' && c <= '9'){
                values.put(this.name, this.parseNumber(c));
            } else if(c == 't' || c == 'f'){
                values.put(this.name, this.parseBoolean(c));
            } else if(c == 'n'){
                values.put(this.name, this.parseNull());
            } else if(c == '['){
                values.put(this.name, this.parseArray());
            } else if(c == '{'){
                values.put(this.name, this.parseObject());
            } else if(c == '\0'){
                throw new JsonException("End of stream");
            } else if(c == '}'){
                break;
            } else{
                throw new JsonException("Invalid syntax (" + this.name + "): " + c);
            }
        }

        return new Values.ObjectValue(values);
    }

    private Value parseArray()
    throws IOException, JsonException{
        List<Value> values = new LinkedList<>();
        char c;
        while((c = this.nextReal()) != '\0'){
            if(c == '"'){
                values.add(this.parseString());
            } else if(c >= '0' && c <= '9'){
                values.add(this.parseNumber(c));
            } else if(c == 't' || c == 'f'){
                values.add(this.parseBoolean(c));
            } else if(c == 'n'){
                values.add(this.parseNull());
            } else if(c == '{'){
                values.add(this.parseObject());
            } else if(c == '['){
                values.add(this.parseArray());
            } else if(c == ','){
                // Fallthrough
            } else if(c == ']'){
                break;
            } else{
                throw new JsonException("Invalid syntax: " + c);
            }
        }

        return new Values.ArrayValue(values.toArray(new Value[values.size()]));
    }

    private Value parseNumber(char c)
    throws IOException{
        this.buffer = c + "";
        while(((c = this.next()) >= '0' && c <= '9') || (c == '.' && !this.buffer.contains("."))){
            this.buffer += c;
        }
        return new Values.NumberValue(new FlexibleNumber(this.buffer));
    }

    private Value parseNull()
    throws IOException{
        this.skip(4);
        return Values.NullValue.NULL;
    }

    private Value parseBoolean(char c)
    throws IOException, JsonException {
        switch(c){
            case 't':{
                this.skip(3);
                return Values.BooleanValue.TRUE;
            }
            case 'f':{
                this.skip(4);
                return Values.BooleanValue.FALSE;
            }
            default:{
                throw new JsonException("Invalid syntax: " + c);
            }
        }
    }

    private String parseName()
    throws IOException{
        this.buffer = "";
        char c;
        while((c = this.next()) != '"'){
            this.buffer += c;
        }
        return this.buffer;
    }

    private Value parseString()
    throws IOException{
        this.buffer = "";
        char c;
        while((c = this.next()) != '"'){
            switch(c){
                case '\\':{
                    switch(c = this.next()){
                        case '\\':{
                            this.buffer += '\\';
                            break;
                        }
                        case 't':{
                            this.buffer += '\t';
                            break;
                        }
                        case '/':{
                            break;
                        }
                        default:{
                            this.buffer += c;
                            break;
                        }
                    }
                }
                default:{
                    this.buffer += c;
                    break;
                }
            }
        }

        return new Values.StringValue(this.buffer);
    }

    private void skip(int amount)
    throws IOException{
        for(int i = 0; i < amount; i++){
            this.next();
        }
    }

    private char nextReal()
    throws IOException{
        char c;
        while(this.isWhitespace(c = this.next()));
        return c;
    }

    private boolean isWhitespace(char c){
        switch(c){
            case '\n':
            case '\t':
            case ' ':
            case '\r':{
                return true;
            }
            default:{
                return false;
            }
        }
    }

    private char next()
    throws IOException{
        char ret;
        if(this.peek == '\0'){
            ret = (char) this.in.read();
        } else{
            ret = this.peek;
            this.peek = '\0';
        }
        if(ret == '\0'){
            throw new IllegalStateException("End of stream");
        }
        return ret;
    }

    private char peek()
    throws IOException{
        if(this.peek != '\0') {
            throw new IllegalStateException("Already peeking");
        }

        this.peek = (char) this.in.read();
        return this.peek;
    }

    @Override
    public void close()
    throws IOException {
        this.in.close();
    }

    private static final class FlexibleNumber
    extends Number{
        private final String data;

        private FlexibleNumber(String data){
            this.data = data;
        }

        @Override
        public int intValue() {
            return Integer.valueOf(this.data);
        }

        @Override
        public long longValue() {
            return Long.valueOf(this.data);
        }

        @Override
        public float floatValue() {
            return Float.valueOf(this.data);
        }

        @Override
        public double doubleValue() {
            return Double.valueOf(this.data);
        }

        @Override
        public String toString(){
            return data;
        }
    }
}