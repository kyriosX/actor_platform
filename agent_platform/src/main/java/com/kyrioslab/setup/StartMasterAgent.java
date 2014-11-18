package com.kyrioslab.setup;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.kyrioslab.master.MasterAgent;
import com.kyrioslab.messages.works.Work;
import com.kyrioslab.messages.works.impl.EchoWork;
import com.kyrioslab.messages.works.impl.SearchWork;
import com.kyrioslab.messages.works.impl.SpyBrowserWork;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class StartMasterAgent {

    private static final String HALT_COMMAND = "halt";
    private static final String APP_PROPS = "application.properties";
    private static final String ACTOR_SYSTEM_NAME = "MasterSystem";
    private static final String MASTER_AGENT_NAME = "master_agent";

    private static Properties props = new Properties();

    private static Scanner reader = new Scanner(System.in);

    private static ActorRef masterAgent;

    public static void main(String[] args) {
        loadAppProps(APP_PROPS);
        ActorSystem masterSystem = ActorSystem.create(ACTOR_SYSTEM_NAME,
                ConfigFactory.load(("master_system")));

        System.out.println("Created master system. Initializing master agent");
        masterAgent = masterSystem.actorOf(Props.create(MasterAgent.class),
                MASTER_AGENT_NAME);

        if (masterAgent != null) {
            System.out.println("Master agent successfully initialized." +
                    "\n Waiting slaves...");
            String command = "";
            while (!command.toLowerCase().equals(HALT_COMMAND)) {
                command = reader.nextLine();
                handleCommand(command);
            }
        } else {
            System.out.println("Initialization failed.");
        }
    }

    private static void handleCommand(String command) {
        Work w = null;
        switch (command.toLowerCase()) {
            case "echo":
                w = new EchoWork();
                break;
            case "search":
                String q = command.substring(command.trim().indexOf(" ") + 1);
                w = new SearchWork();
                ((SearchWork) w).setQuery(q);
                break;
            case "spy":
                w = new SpyBrowserWork();
                break;
            default:
                System.out.println("Command '" + command + "' not recognized.");
                break;
        }
        masterAgent.tell(w, null);
    }

    private static void loadAppProps(String propFileName) {
        try (InputStream inputStream = StartMasterAgent.class
                .getClassLoader().getResourceAsStream(propFileName)) {
            props.load(inputStream);
        } catch (IOException e) {
            System.out.println("Cannot load application properties: " + propFileName + " " + e);
        }
    }
}
