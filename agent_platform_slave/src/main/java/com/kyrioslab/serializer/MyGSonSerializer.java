package com.kyrioslab.serializer;

import akka.serialization.JSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kyrioslab.messages.works.Work;
import com.kyrioslab.slave.SlaveAgent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by wizzard on 24.09.14.
 */
public class MyGSonSerializer extends JSerializer {

    private Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest){
        try {
            String obj = new String(bytes);
            int delim = obj.indexOf('-');
            String name = obj.substring(0, delim);
            return gson.fromJson(obj.substring(delim + 1),
                    SlaveAgent.workClassLoader.loadClass(name));
        } catch (ClassNotFoundException e) {
            //ignoring
        }
        return null;
    }

    @Override
    public int identifier() {
        return 12062010;
    }

    @Override
    public byte[] toBinary(Object o) {
        return gson.toJson(o).getBytes();
    }

    @Override
    public boolean includeManifest() {
        return true;
    }
}
