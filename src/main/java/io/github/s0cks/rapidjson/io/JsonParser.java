package io.github.s0cks.rapidjson.io;

import io.github.s0cks.rapidjson.JsonException;
import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class JsonParser{
    private final String json;
    private int ptr;
    private String name;
    private int col;
    private int row;

    public JsonParser(String json){
        this.json = json;
        this.ptr = 0;
    }

    public Value parse()
    throws JsonException {
        switch(this.peek()){
            case '{': return this.parseObject();
            case '[': {
                this.nextReal();
                return this.parseArray();
            }
            default: throw new JsonException("Invalid syntax @" + this.col + "," + this.row);
        }
    }

    private Value parseObject()
    throws JsonException {
        Map<String, Value> values = new HashMap<>();
        char c;
        it: while ((c = this.nextReal()) != '}') {
            switch (c) {
                case '"': {
                    this.name = this.parseName();
                    c = this.nextReal();
                    break;
                }
                case ',':
                case '{': {
                    continue it;
                }
                case ':':{
                    c = this.nextReal();
                    break;
                }
                default: {
                    break;
                }
            }

            if(c == ':'){
                c = this.nextReal();
            }

            if (c == '"') {
                values.put(this.name, this.parseString());
            } else if (isNumber(c)) {
                values.put(this.name, this.parseNumber(c));
            } else if (isBoolean(c)) {
                values.put(this.name, this.parseBoolean(c));
            } else if (isNull(c)) {
                values.put(this.name, this.parseNull());
            } else if(c == '['){
                values.put(this.name, this.parseArray());
            } else if(c == '\0'){
                throw new JsonException("Invalid Syntax [end of document]@" + this.col + "," + this.row);
            } else if(c == '}'){
                break;
            } else{
                throw new JsonException("Invalid syntax @ " + this.name + " with (" + c + ") @" + this.col + "," + this.row);
            }
        }

        return Values.of((this.name == null ? "<root>" : this.name), values);
    }

    private Value parseArray()
    throws JsonException{
        List<Value> values = new LinkedList<>();
        char c;
        while((c = this.nextReal()) != '\0'){
            if (c == '"') {
                values.add(this.parseString());
            } else if (isNumber(c)) {
                values.add(this.parseNumber(c));
            } else if (isBoolean(c)) {
                values.add(this.parseBoolean(c));
            } else if (isNull(c)) {
                values.add(this.parseNull());
            } else if (c == '{') {
                values.add(this.parseObject());
            } else if(c == '['){
                values.add(this.parseArray());
            } else if(c == ','){
                // Fallthrough
            } else if(c == ']'){
                break;
            } else {
                throw new JsonException("Invalid syntax " + this.name + ":" + c + "@" + this.col + "," + this.row);
            }
        }

        return Values.of((this.name == null ? "<root>" : this.name), values);
    }

    private Value parseNumber(char c){
        String buffer = c + "";
        while(this.isNumber(c = this.next()) || (c == '.' && !buffer.contains("."))){
            buffer += c;
        }
        System.out.println(buffer);
        return new Values.NumberValue(this.name, new FlexibleNumber(buffer));
    }

    private Value parseNull(){
        for(int i = 0; i < 4; i++){
            this.next();
        }
        return new Values.NullValue(this.name);
    }

    private Value parseBoolean(char c)
    throws JsonException {
        switch(c){
            case 't':{
                for(int i = 0; i < 3; i++){
                    this.next();
                }
                return new Values.BooleanValue(this.name, true);
            }
            case 'f':{
                for(int i = 0; i < 4; i++){
                    this.next();
                }
                return new Values.BooleanValue(this.name, false);
            }
            default:{
                throw new JsonException("Invalid syntax @" + this.col + "," + this.row);
            }
        }
    }

    private String parseName(){
        String buffer = "";
        char c;
        while((c = this.next()) != '"'){
            buffer += c;
        }
        return buffer;
    }

    private Value parseString(){
        String buffer = "";
        char c;
        while((c = this.next()) != '"'){
            switch(c){
                case '\\':{
                    switch(c = this.next()){
                        case '\\':{
                            buffer += '\\';
                            break;
                        }
                        case 't':{
                            buffer += '\t';
                            break;
                        }
                        case '/':{
                            break;
                        }
                        default:{
                            buffer += c;
                            break;
                        }
                    }
                }
                default:{
                    buffer += c;
                    break;
                }
            }
        }
        return new Values.StringValue(this.name, buffer);
    }

    private boolean isNumber(char c){
        return (c >= '0' && c <= '9');
    }

    private boolean isBoolean(char c){
        return c == 't' || c == 'f';
    }

    private boolean isNull(char c){
        return c == 'n';
    }

    private char nextReal(){
        char c;
        while((whitespace(c = this.next())));
        return c;
    }

    private boolean whitespace(char c){
        switch(c){
            case '\n':{
                this.row++;
                this.col = 0;
            }
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

    private char next(){
        char c = this.peek();
        if(c != '\0') {
            this.col++;
            this.ptr++;
        }
        return c;
    }

    private char peek(){
        return (this.ptr < this.json.length() ? this.json.charAt(this.ptr) : '\0');
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