package com.kyrioslab.messages.works.impl;

import com.kyrioslab.messages.works.Work;

/**
 * Created by wizzard on 23.09.14.
 */
public class EchoWork implements Work {

    private String result;
    private boolean done = false;

    @Override
    public void doWork() {
        result = "ECHO";
        done= true;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public EchoWork() {
    }
}
