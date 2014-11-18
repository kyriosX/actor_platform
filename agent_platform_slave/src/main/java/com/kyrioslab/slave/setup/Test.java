package com.kyrioslab.slave.setup;

/**
 * Created by wizzard on 29.09.14.
 */
public class Test {
    public static void main( String [] args ) {
        StartSlaveAgent.main(new String[] {"akka.tcp://MasterSystem@127.0.0.1:2554/user/master_agent",
                "127.0.0.1"});
    }
}
