package com.kyrioslab.slave;

import akka.actor.UntypedActor;
import com.kyrioslab.messages.works.Work;

/**
 * Created by wizzard on 07.10.14.
 */
public class WorkerAgent extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("Received work: " + message);
        if (message instanceof Work) {
            ((Work) message).doWork();
            getSender().tell(message, getSelf());
        } else {
            System.out.println("Unknown work");
        }
    }

}
