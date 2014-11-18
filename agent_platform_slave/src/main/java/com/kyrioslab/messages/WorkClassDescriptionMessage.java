package com.kyrioslab.messages;

import java.io.Serializable;import java.lang.Class;import java.lang.String;

/**
 * Created by wizzard on 24.09.14.
 */
public class WorkClassDescriptionMessage implements Serializable {

    private byte[] bytes;

    private String name;

    public WorkClassDescriptionMessage() {}

    public WorkClassDescriptionMessage(byte[] bytes, String name) {
        this.bytes = bytes;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
