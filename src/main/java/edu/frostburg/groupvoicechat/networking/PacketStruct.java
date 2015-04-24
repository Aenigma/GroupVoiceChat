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

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
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
    public byte[] payload;

    public static PacketStruct fromByteBuffer(ByteBuffer bb) {

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
        ps.payload = new byte[bb.remaining()];
        bb.get(ps.payload);

        return ps;
    }

    public int getSize() {
        return (8 * 2) // two longs
                + (1 * 1) // one byte
                + payload.length;   // payload length
    }

    public void toByteBuffer(ByteBuffer bb) {
        bb.putLong(packetId);
        bb.putLong(time);
        bb.put(packetType);
        bb.put(payload);
    }

    public void toBytes() {
        int size = getSize();
        ByteBuffer tmpB = ByteBuffer.allocate(size);
        byte[] result = new byte[size];

        tmpB.clear();
        tmpB.putLong(packetId);
        tmpB.putLong(time);
        tmpB.put(packetType);
        tmpB.put(payload);

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.packetId ^ (this.packetId >>> 32));
        hash = 41 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 41 * hash + this.packetType;
        hash = 41 * hash + Arrays.hashCode(this.payload);
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
        if (!Arrays.equals(this.payload, other.payload)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PacketStruct{" + "packetId=" + packetId + ", time=" + time
                + ", packetType=" + packetType + ", payload=" + Arrays.toString(
                        payload) + '}';
    }

}
