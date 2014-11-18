package com.kyrioslab.slave.setup;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.kyrioslab.messages.SlaveAgentInitializationMessage;
import com.kyrioslab.slave.SlaveAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wizzard on 23.09.14.
 */
public class StartSlaveAgent {

    private static final String COMMON_CONF = "common.conf";
    private static final String APP_CONF = "application.properties";
    private static final String MASTER_PATH_PROP = "system.master.path";
    private static final String HOSTNAME_PROP = "akka.remote.netty.tcp.hostname";
    private static final int RECONNECTION_TIMEOUT = 10000;

    public static void main(String[] args) {
        Properties props = loadProps(APP_CONF);
        Config config = ConfigFactory.load("slave_agent.conf");

        if (args.length >= 2) {
            setMasterPath(args[0], props);
            config = setHost(args[1], config);
        }

        ActorSystem slaveSystem = ActorSystem.create("SlaveSystem",
                config);
        System.out.println("Created slave system.");
        System.out.println("Registering slave agent.");
        String masterPath = props.getProperty(MASTER_PATH_PROP);
        ActorRef slaveAgent = slaveSystem.actorOf(Props.create(SlaveAgent.class), "slave_agent");

        Thread.currentThread().setContextClassLoader(SlaveAgent.workClassLoader);
        slaveAgent.tell(new SlaveAgentInitializationMessage(masterPath), null);
    }

    private static Properties loadProps(String propFileName) {
        Properties properties = new Properties();
        try (InputStream inputStream = StartSlaveAgent.class.getClassLoader().getResourceAsStream(propFileName)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            System.out.println("Cannot load application properties: " + propFileName + " " + e);
        }
        return properties;
    }

    private static void setMasterPath(String path, Properties props) {
        if (path == null) {
            return;
        }
        props.setProperty(MASTER_PATH_PROP, path);
    }

    private static Config setHost(String host, Config config) {
        if (host == null) {
            return config;
        }
        return config.withValue(HOSTNAME_PROP, ConfigValueFactory.fromAnyRef(host));
    }
}
