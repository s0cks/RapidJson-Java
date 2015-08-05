package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.io.JsonInputStream;
import io.github.s0cks.rapidjson.io.JsonOutputStream;
import io.github.s0cks.rapidjson.reflect.InstanceFactory;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;
import io.github.s0cks.rapidjson.reflect.TypeAdapterFactory;
import io.github.s0cks.rapidjson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

public final class RapidJson{
    private final InstanceFactory instanceFactory;

    public RapidJson(){
        this.instanceFactory = new InstanceFactory(this, new HashMap<Type, TypeAdapter>(), new LinkedList<TypeAdapterFactory>());
    }

    protected RapidJson(RapidJsonBuilder builder){
        this.instanceFactory = new InstanceFactory(this, builder.adapters, builder.factories);
    }

    public <T> T fromJson(String json, Class<T> tClass)
    throws JsonException{
        try {
            return this.instanceFactory.create(tClass, new JsonInputStream(new ByteArrayInputStream(json.getBytes())).parse());
        } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(String json, TypeToken<T> t)
    throws JsonException{
        try{
            return this.instanceFactory.create(t, new JsonInputStream(new ByteArrayInputStream(json.getBytes())).parse());
        } catch(IllegalAccessException | NoSuchFieldException | IOException e){
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(InputStream in, TypeToken<T> t)
    throws JsonException{
        try{
            return this.instanceFactory.create(t, new JsonInputStream(in).parse());
        } catch(IllegalAccessException | NoSuchFieldException | IOException e){
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(InputStream in, Class<T> tClass)
    throws JsonException{
        try{
            return this.instanceFactory.create(tClass, new JsonInputStream(in).parse());
        } catch(IllegalAccessException | NoSuchFieldException | IOException e){
            throw new RuntimeException(e);
        }
    }

    public String toJson(Object obj){
        try(StringOutputStream sos = new StringOutputStream();
            JsonOutputStream jos = new JsonOutputStream(sos)){

            this.instanceFactory.write(obj, jos);
            return sos.toString();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static final class StringOutputStream
    extends OutputStream{
        private String str = "";

        @Override
        public void write(int i)
        throws IOException {
            this.str += (char) i;
        }

        @Override
        public String toString(){
            return this.str;
        }
    }
}