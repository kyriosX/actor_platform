package com.kyrioslab.slave;

import akka.actor.*;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;
import com.kyrioslab.messages.SlaveAgentInitializationMessage;
import com.kyrioslab.messages.SlaveAgentRegistrationMessage;
import com.kyrioslab.messages.WorkClassDescriptionMessage;
import com.kyrioslab.messages.works.Work;
import com.kyrioslab.serializer.WorkClassLoader;

public class SlaveAgent extends UntypedActor {

    private ActorRef master;
    public static WorkClassLoader workClassLoader = new WorkClassLoader(ClassLoader.getSystemClassLoader());
    private ActorRef router = getContext().actorOf(new RoundRobinPool(3).withResizer(
            new DefaultResizer(5, 10)).props(Props.create(WorkerAgent.class)), "router1");

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("Received message: " + message);
        if (message instanceof SlaveAgentInitializationMessage) {
            initialize((SlaveAgentInitializationMessage) message);
        } else if (message instanceof WorkClassDescriptionMessage) {
            workClassLoader.loadClassFromMsg((WorkClassDescriptionMessage) message);
        } else if (message instanceof Work) {
            router.tell(message, getSender());
        }
    }

    private void initialize(SlaveAgentInitializationMessage message) {
        String masterPath = ((SlaveAgentInitializationMessage) message).getMasterPath();
        master = getContext().actorFor(masterPath);
        if (master == null
                || master instanceof DeadLetterActorRef
                || master instanceof EmptyLocalActorRef) {
            System.out.println("Cannot retrieve ActoRef for master with path: " + masterPath);
        } else {
            System.out.println("Registering at master: " + master);
            master.tell(new SlaveAgentRegistrationMessage(), getSelf());
            System.out.println("Registered.");
        }

    }
}



