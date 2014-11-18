package com.kyrioslab.messages;

import java.io.Serializable;


public class SlaveAgentInitializationMessage implements Serializable {

    private String masterPath;

    public SlaveAgentInitializationMessage() {}

    public SlaveAgentInitializationMessage(String masterPath) {
        this.masterPath = masterPath;
    }

    public String getMasterPath() {
        return masterPath;
    }

    public void setMasterPath(String masterPath) {
        this.masterPath = masterPath;
    }
}
