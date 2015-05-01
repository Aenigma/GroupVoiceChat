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

import edu.frostburg.groupvoicechat.networking.PacketContext;
import edu.frostburg.groupvoicechat.networking.Peer;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;
import edu.frostburg.groupvoicechat.networking.sockets.PacketSender;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Raoofi
 */
public class ServerRelayDecoder extends AbstractPacketDecoder {

    final PacketSender pSender = new PacketSender();

    @Override
    public void processPacket(PacketContext pc) {
        final Peer sender = pc.getSender();
        final EventRouter<EventRouterState> eventRouter = pc.getEventRouter();

        /* TODO: you know, i'm getting sick of this casting thing; this doesn't
         * have o be like this
         */
        final EventRouterState state = eventRouter.getState();
        for (Peer p : state.getPeerList()) {
            if (!sender.equals(p)) {
                try {
                    InetSocketAddress isa = p.getIsa()
                            .get();

                    pSender.send(isa.getAddress(), isa.getPort(),
                            pc.getPacketStruct());
                } catch (IOException | NoSuchElementException ex) {
                    Logger.getLogger(ServerRelayDecoder.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
