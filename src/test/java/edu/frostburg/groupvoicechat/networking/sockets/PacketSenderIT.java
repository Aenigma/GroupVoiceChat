/*
 * Copyright 2015 Kevin Raoofi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.frostburg.groupvoicechat.networking.sockets;

import edu.frostburg.groupvoicechat.networking.PacketStruct;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Kevin Raoofi
 */
public class PacketSenderIT {

    private static final int PORT = 9876;
    DatagramChannel dc;
    ExecutorService es;

    @Before
    public void setUp() throws IOException {
        this.es = Executors.newSingleThreadExecutor();
        this.dc = DatagramChannel.open();
        this.dc.bind(new InetSocketAddress(PORT));

        this.es.submit(new Runnable() {
            @Override
            public void run() {
                ByteBuffer bb = ByteBuffer.allocate(2048);
                try {
                    System.out.println("Listening for connection...");
                    SocketAddress sa = dc.receive(bb);

                    System.out.println("Got a connection...");

                    bb.flip();
                    PacketStruct ps = PacketStruct.fromByteBuffer(bb);

                    System.out.println(ps);

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        this.es.shutdown();
        boolean terminated = this.es.awaitTermination(1, TimeUnit.SECONDS);
        this.es.shutdownNow();

        this.dc.close();

        if (!terminated) {
            throw new RuntimeException(
                    "ExecutorService could not shut down properly");
        }
    }

    /**
     * Test of send method, of class PacketSender.
     */
    @Test
    public void testSend() throws Exception {
        System.out.println("send");
        InetAddress address = Inet4Address.getLocalHost();

        PacketStruct ps = new PacketStruct();
        ps.packetId = 0;
        ps.packetType = 0;
        ps.payload = "Test Data".getBytes();
        ps.time = (System.currentTimeMillis() << 16) >>> 16;

        PacketSender instance = new PacketSender();
        instance.send(address, PORT, ps);
    }

}
