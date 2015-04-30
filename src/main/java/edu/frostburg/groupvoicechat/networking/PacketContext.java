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

import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * This class contains a packet and all of its metadata required to process it
 * as an event.
 *
 *
 * @author Kevin Raoofi
 */
public final class PacketContext {

    /* This class should be expanded to include any and all */
    public static final int ID_UNSET = -1;

    final PacketStruct packetStruct;
    final EventRouter eventRouter;

    InetAddress address;
    int port;

    int senderId;
    int receiverId;
    /**
     * Signifies who sent the packet.
     *
     * Note that sender and receiver should, generally, never be both true.
     */
    Peer sender;
    /**
     * Signifies who the packet is destined to be sent to.
     *
     * Note that sender and receiver should, generally, never be both true.
     */
    Peer receiver;

    public PacketContext(PacketStruct packetStruct, EventRouter eventRouter) {
        this.packetStruct = packetStruct;
        this.eventRouter = eventRouter;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public PacketStruct getPacketStruct() {
        return packetStruct;
    }

    public long getPacketID() {
        return packetStruct.packetId;
    }

    public void setPacketID(long packetId) {
        packetStruct.packetId = packetId;
    }

    public long getTime() {
        return packetStruct.time;
    }

    public void setTime(long time) {
        packetStruct.time = time;
    }

    public byte getPacketType() {
        return packetStruct.packetType;
    }

    public void setPacketType(byte packetType) {
        packetStruct.packetType = packetType;
    }

    public byte[] getPayload() {
        return packetStruct.payload;
    }

    public void setPayload(byte[] payload) {
        packetStruct.payload = payload;
    }

    public Peer getSender() {
        return sender;
    }

    public void setSender(Peer sender) {
        this.sender = sender;
    }

    public Peer getReceiver() {
        return receiver;
    }

    public void setReceiver(Peer receiver) {
        this.receiver = receiver;
    }

    public EventRouter getEventRouter() {
        return eventRouter;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void toByteBuffer(ByteBuffer bb) {
        packetStruct.toByteBuffer(bb);
    }

    public InetAddress getSenderAddress() {
        return packetStruct.senderAddress;
    }

    public void setSenderAddress(InetAddress senderAddress) {
        packetStruct.senderAddress = senderAddress;
    }

}
