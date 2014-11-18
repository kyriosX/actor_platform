package com.kyrioslab.serializer;

import com.kyrioslab.messages.WorkClassDescriptionMessage;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by wizzard on 24.09.14.
 */
public class WorkClassLoader extends ClassLoader {

    public WorkClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("com.kyrioslab.messages.works.impl")) {
            return findLoadedClass(name);
        }
        return super.loadClass(name);
    }

    public void loadClassFromMsg(WorkClassDescriptionMessage msg) {
        if (findLoadedClass(msg.getName()) == null) {
            resolveClass(defineClass(msg.getName(),
                    msg.getBytes(),
                    0,
                    msg.getBytes().length));
        }
    }
}
