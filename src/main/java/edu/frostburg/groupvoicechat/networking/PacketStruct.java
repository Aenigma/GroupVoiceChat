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
package edu.frostburg.groupvoicechat.networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class contains data which represents the payload data in an actual
 * packet. It contains several mandatory fields as well as
 *
 * @author Kevin Raoofi
 */
public final class PacketStruct {

    public static final byte PACKET_TYPE_CONNECTION = 0;
    public static final byte PACKET_TYPE_STATUS = 1;
    public static final byte PACKET_TYPE_TEXT = 2;
    public static final byte PACKET_TYPE_AUDIO = 3;
    public static final byte PACKET_TYPE_ERROR = 127;

    public long packetId;
    public long time;
    public byte packetType;
    /**
     * Whilst each host can tell which host has sent it the packet, this is
     * reserved for the original sender. A client could populate this field.
     * However, a server could verify or modify it to reflect the client's
     * hostname.
     */
    /*
     * TODO: this is silly; without the port # clients are no longer made
     * unique. It would be more sensible to have a number assigned by the server
     * for this. But gotta meet deadlines!
     */
    public InetAddress senderAddress;

    public byte[] payload;

    /**
     *
     * @param bb
     * @return
     * @throws IllegalStateException if IP in bytebuffer is of illegal length
     */
    public static PacketStruct fromByteBuffer(ByteBuffer bb) throws
            IllegalStateException {

        /*
         * FIXME: this should not assume that the bytebuffer has everything
         *
         * A better appraoch would be to have this functionality removed to
         * another class whose sole purpose is to extract multiple packetstructs
         * form a bytebuffer.
         *
         */
        final PacketStruct ps = new PacketStruct();

        ps.packetId = bb.getLong();
        ps.time = bb.getLong();
        ps.packetType = bb.get();

        //final int addressSize = bb.get();
        final byte[] address = new byte[bb.get()];
        bb.get(address);
        try {
            ps.senderAddress = InetAddress.getByAddress(address);
        } catch (UnknownHostException ex) {
            // we'll hide this so it's unchecked
            throw new IllegalStateException(ex);
        }

        ps.payload = new byte[bb.remaining()];
        bb.get(ps.payload);

        return ps;
    }

    public PacketStruct() {
    }

    public PacketStruct(PacketStruct ps) {
        this.packetId = ps.packetId;
        this.packetType = ps.packetType;
        this.time = ps.time;
        this.payload = Arrays.copyOf(ps.payload, ps.payload.length);
        this.senderAddress = ps.senderAddress;
    }

    public int getSize() {
        return (8 * 2) // two longs
                + (1 * 1) // one byte
                + 1 // one byte for length
                + (senderAddress != null ? senderAddress.getAddress().length : 0)
                + payload.length;   // payload length
    }

    public void toByteBuffer(ByteBuffer bb) {
        final byte[] address = this.senderAddress != null
                ? this.senderAddress.getAddress()
                : new byte[]{};
        bb.putLong(packetId);
        bb.putLong(time);
        bb.put(packetType);
        bb.put((byte) address.length);
        bb.put(address);
        bb.put(payload);
    }

    public byte[] toBytes() {
        final int size = getSize();
        final byte[] result = new byte[size];
        final ByteBuffer tmpB = ByteBuffer.wrap(result);

        toByteBuffer(tmpB);

        return result;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (this.packetId ^ (this.packetId >>> 32));
        hash = 17 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 17 * hash + this.packetType;
        hash = 17 * hash + Objects.hashCode(this.senderAddress);
        hash = 17 * hash + Arrays.hashCode(this.payload);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PacketStruct other = (PacketStruct) obj;
        if (this.packetId != other.packetId) {
            return false;
        }
        if (this.time != other.time) {
            return false;
        }
        if (this.packetType != other.packetType) {
            return false;
        }
        if (!Objects.equals(this.senderAddress, other.senderAddress)) {
            return false;
        }
        if (!Arrays.equals(this.payload, other.payload)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PacketStruct{" + "packetId=" + packetId + ", time=" + time
                + ", packetType=" + packetType + ", senderAddress="
                + senderAddress + ", payload=" + Arrays.toString(payload) + '}';
    }

}
