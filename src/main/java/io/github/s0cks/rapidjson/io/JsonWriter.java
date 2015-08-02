package io.github.s0cks.rapidjson.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public final class JsonWriter
implements Closeable {
    private final OutputStream os;

    public JsonWriter(OutputStream os){
        this.os = os;
    }

    public void write(Object obj){

    }

    @Override
    public void close()
    throws IOException {
        this.os.close();
    }
}