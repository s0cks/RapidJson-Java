package io.github.s0cks.rapidjson.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class JsonOutputStream
implements Closeable {
    private final OutputStream out;

    public JsonOutputStream(OutputStream out){
        if(out == null) throw new NullPointerException("out == null");
        this.out = out;
    }

    public JsonOutputStream newObject()
    throws IOException{
        this.write('{');
        return this;
    }

    public JsonOutputStream endObject()
    throws IOException {
        this.write('}');
        return this;
    }

    public JsonOutputStream newArray()
    throws IOException {
        this.write('[');
        return this;
    }

    public JsonOutputStream endArray()
    throws IOException {
        this.write(']');
        return this;
    }

    public JsonOutputStream name(String name)
    throws IOException {
        this.write('\"');
        this.write(name);
        this.write('\"');
        this.write(':');
        return this;
    }

    public JsonOutputStream value(String str)
    throws IOException {
        this.write('\"');
        this.write(str);
        this.write('\"');
        return this;
    }

    public JsonOutputStream value(int i)
    throws IOException {
        this.write(String.valueOf(i));
        return this;
    }

    public JsonOutputStream value(double d)
    throws IOException {
        this.write(String.valueOf(d));
        return this;
    }

    public JsonOutputStream value(float f)
    throws IOException {
        this.write(String.valueOf(f));
        return this;
    }

    public JsonOutputStream value(char c)
    throws IOException {
        this.write(c);
        return this;
    }

    public JsonOutputStream value(short s)
    throws IOException {
        this.write(String.valueOf(s));
        return this;
    }

    public JsonOutputStream value(long l)
    throws IOException {
        this.write(String.valueOf(l));
        return this;
    }

    public JsonOutputStream value(byte b)
    throws IOException {
        this.write(String.valueOf(b));
        return this;
    }

    public JsonOutputStream value(boolean b)
    throws IOException{
        this.write(String.valueOf(b));
        return this;
    }

    public JsonOutputStream next()
    throws IOException {
        this.write(',');
        return this;
    }

    private void write(char c)
    throws IOException{
        this.out.write((byte) c);
    }

    private void write(String chars)
    throws IOException{
        this.out.write(chars.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close()
    throws IOException {
        this.out.close();
    }
}