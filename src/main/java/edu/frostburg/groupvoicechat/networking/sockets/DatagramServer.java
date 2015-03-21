/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.frostburg.groupvoicechat.networking.sockets;

import edu.frostburg.groupvoicechat.networking.PacketContext;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;
import edu.frostburg.groupvoicechat.networking.events.EventType;
import edu.frostburg.groupvoicechat.networking.events.EventWrapper;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author russell
 */
public class DatagramServer implements Runnable {

    static final int BUFFER_SIZE = 1 << 20;
    static final int PORT_NUMBER = 6000;

    private final DatagramSocket serverSock;
    private final EventRouter<EventRouterState> er;
    private final byte[] receive;
    private final byte[] sent;

    public DatagramServer(EventRouter<EventRouterState> er) throws SocketException {
        this.er = er;
        receive = new byte[BUFFER_SIZE];
        sent = new byte[BUFFER_SIZE];
        serverSock = new DatagramSocket(PORT_NUMBER);

    }

    void makeServer() {

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
            try {
                serverSock.receive(receivePacket);
            } catch (IOException ex) {
                Logger.getLogger(DatagramServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] packetData = receivePacket.getData();
            
            InetAddress IpAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            ByteBuffer bb = ByteBuffer.wrap(packetData);

            PacketStruct ps = new PacketStruct();
            ps.packetId = bb.getLong();
            ps.time = bb.getLong();
            ps.packetType = bb.get();
            ps.payload = new byte[bb.remaining()];
            bb.get(ps.payload);

            PacketContext pc = new PacketContext(ps, er);
            
            pc.setAddress(IpAddress);
            pc.setPort(port);
            
            EventWrapper<PacketContext> eventWrapper = new EventWrapper<>(EventType.PACKET);
            eventWrapper.setContext(pc);

            er.addEvent(eventWrapper);

        }
    }

    @Override
    public void run() {
        makeServer();
    }

}
