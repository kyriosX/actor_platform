package com.kyrioslab.serializer;

import akka.serialization.JSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by wizzard on 24.09.14.
 */
public class MyGSonSerializer extends JSerializer{

    private Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        return gson.fromJson(new String(bytes), manifest);
    }

    @Override
    public int identifier() {
        return 12062010;
    }

    @Override
    public byte[] toBinary(Object o) {
        return (o.getClass().getName() + "-" + gson.toJson(o)).getBytes();
    }

    @Override
    public boolean includeManifest() {
        return false;
    }
}
