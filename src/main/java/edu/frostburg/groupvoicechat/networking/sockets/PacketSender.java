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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author Kevin Raoofi
 */
public class PacketSender {

    public void send(InetAddress address, int port, PacketStruct ps) throws IOException {
        DatagramSocket ds = new DatagramSocket();
        ds.connect(address, port);

        byte[] data = new byte[ps.getSize()];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.clear();
        ps.toByteBuffer(bb);

        ds.send(new DatagramPacket(data, data.length));
    }
}
