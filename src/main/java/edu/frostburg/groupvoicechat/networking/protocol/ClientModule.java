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

import edu.frostburg.groupvoicechat.audio.AudioManager;
import edu.frostburg.groupvoicechat.gui.ChatWindow;
import edu.frostburg.groupvoicechat.networking.InvalidPacketDecoder;
import edu.frostburg.groupvoicechat.networking.PacketDecoder;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.ReceivingStrategyDispatcher;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;
import edu.frostburg.groupvoicechat.networking.events.EventType;
import java.awt.EventQueue;

import static edu.frostburg.groupvoicechat.networking.PacketStruct.*;

/**
 * This class is responsible for the initialization of a server.
 *
 *
 * @author Kevin Raoofi
 */
public class ClientModule {

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

    private final ChatWindow cw;

    public ClientModule() {
        this.eventRouter = new EventRouter<>(new EventRouterState());
        eventRouter.getState()
                .setEventRouter(eventRouter);
        eventRouter.getState()
                .setAudioManager(new AudioManager(eventRouter));

        cw = new ChatWindow(eventRouter);
    }

    public void startGui() {
        EventQueue.invokeLater(() -> {
            cw.pack();
            cw.setMinimumSize(cw.getPreferredSize());
            cw.setVisible(true);
        });
    }

    public void addEventHandlers() {
        pds[PACKET_TYPE_AUDIO] = new ClientAudioPacketHandler(eventRouter);
        pds[PACKET_TYPE_CONNECTION] = invalidDecoder;
        pds[PACKET_TYPE_ERROR] = invalidDecoder;
        pds[PACKET_TYPE_STATUS] = invalidDecoder; // TODO: make a status handler
        pds[PACKET_TYPE_TEXT] = new ClientTextHandler(cw);

        this.eventRouter.register(new ReceivingStrategyDispatcher(pds),
                EventType.PACKET);
    }

}
