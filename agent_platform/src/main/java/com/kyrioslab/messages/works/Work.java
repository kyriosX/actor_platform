package com.kyrioslab.messages.works;

import java.io.Serializable;

/**
 * Created by wizzard on 23.09.14.
 */

public interface Work extends Serializable{

    public void doWork();

    public Object getResult();

    public boolean isDone();

}
