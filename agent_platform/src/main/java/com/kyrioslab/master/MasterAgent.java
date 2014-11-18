package com.kyrioslab.master;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Router;
import com.kyrioslab.messages.SlaveAgentRegistrationMessage;
import com.kyrioslab.messages.WorkClassDescriptionMessage;
import com.kyrioslab.messages.works.Work;
import com.kyrioslab.messages.works.impl.EchoWork;
import com.kyrioslab.messages.works.impl.SearchWork;
import com.kyrioslab.messages.works.impl.SpyBrowserWork;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class MasterAgent extends UntypedActor {

    private Router router = new Router(new BroadcastRoutingLogic());


    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof SlaveAgentRegistrationMessage) {
            registerSlave(getSender());
        } else if (message instanceof Work) {
            if (((Work) message).isDone()) {
                handleDoneWork((Work) message);
            } else {
                handleWork((Work) message);
            }
        }
    }

    private void handleWork(Work message) throws Exception {
        Class workClass = message.getClass();
        byte[] bytes = loadClass(workClass);

        if (bytes == null) {
            throw new Exception("Class not loaded to bytes: " + workClass);
        }

        router.route(new WorkClassDescriptionMessage(bytes,
                workClass.getName()), getSelf());

        System.out.println("Sending work: " + message);
        if (message instanceof EchoWork) {
            router.route(message, getSelf());
        } else if (message instanceof SearchWork) {
            //send helper class
            router.route(new WorkClassDescriptionMessage(
                            loadClass(SearchWork.Finder.class), SearchWork.Finder.class.getName()),
                    getSelf());
            router.route(message, getSelf());
        } else if (message instanceof SpyBrowserWork) {
            router.route(new WorkClassDescriptionMessage(
                            loadClass(SpyBrowserWork.PListener.class), SpyBrowserWork.PListener.class.getName()),
                    getSelf());
            router.route(message, getSelf());
        }
    }

    private void handleDoneWork(Work message) {
        System.out.println("Received result message: " + message);
        if (message instanceof EchoWork) {
            System.out.println(message.getResult());
        } else if (message instanceof SearchWork) {
            List<String> result = ((SearchWork) message).getResult();
            String host = ((SearchWork) message).getFromHost();
            System.out.println("\nHost '" + host + "' work result:");
            for (String p : result) {
                System.out.println(p);
            }
        } else if (message instanceof SpyBrowserWork) {
            Map<String, String> res = ((SpyBrowserWork) message).getResult();
            for (Map.Entry<String, String> p : res.entrySet()) {
                System.out.println("Host:\n" + p.getKey());
                System.out.println("Data:\n" + p.getValue());
            }
        }
    }

    private void registerSlave(ActorRef slave) {
        router = router.addRoutee(new ActorRefRoutee(slave));
        System.out.println("Registered slave, adress: " + slave);
    }

    private byte[] loadClass(Class c) {
        String name = c.getName().replaceAll("\\.", File.separator) + ".class";
        InputStream is = c.getClassLoader()
                .getResourceAsStream(name);
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            System.out.println("Exception while loading class bytes: " + c);
        }
        return null;
    }

}
