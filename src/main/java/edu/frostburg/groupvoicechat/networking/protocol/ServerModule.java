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
package edu.frostburg.groupvoicechat.networking.protocol;

import edu.frostburg.groupvoicechat.networking.InvalidPacketDecoder;
import edu.frostburg.groupvoicechat.networking.PacketDecoder;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.ReceivingStrategyDispatcher;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;
import edu.frostburg.groupvoicechat.networking.events.EventType;

import static edu.frostburg.groupvoicechat.networking.PacketStruct.*;

/**
 * This class is responsible for the initialization of a server.
 *
 * @author Kevin Raoofi
 */
public class ServerModule {

    private static final int largestPacketId;

    private static final PacketDecoder invalidDecoder
            = new InvalidPacketDecoder();

    static {
        byte[] packetIds = new byte[]{
            PacketStruct.PACKET_TYPE_AUDIO,
            PacketStruct.PACKET_TYPE_CONNECTION,
            PacketStruct.PACKET_TYPE_ERROR,
            PacketStruct.PACKET_TYPE_STATUS,
            PacketStruct.PACKET_TYPE_TEXT
        };

        int tmpLargestPacketId = packetIds[0];
        for (byte b : packetIds) {
            tmpLargestPacketId = Math.max(tmpLargestPacketId, b);
        }
        largestPacketId = tmpLargestPacketId;
    }

    private final EventRouter<EventRouterState> eventRouter;
    private final PacketDecoder[] pds = new PacketDecoder[largestPacketId];

    public ServerModule() {
        this.eventRouter = new EventRouter<>(new EventRouterState());
    }

    public void addServerEventHandlers() {

        final ServerRelayDecoder srd = new ServerRelayDecoder();
        pds[PACKET_TYPE_AUDIO] = srd;
        pds[PACKET_TYPE_TEXT] = srd;

        pds[PACKET_TYPE_CONNECTION] = new ServerConnectionPacketDecoder();

        pds[PACKET_TYPE_ERROR] = invalidDecoder;
        pds[PACKET_TYPE_STATUS] = invalidDecoder;

        this.eventRouter.register(new ReceivingStrategyDispatcher(pds),
                EventType.PACKET);
    }
}
