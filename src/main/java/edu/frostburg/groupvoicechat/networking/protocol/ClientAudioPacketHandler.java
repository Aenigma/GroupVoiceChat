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
import edu.frostburg.groupvoicechat.networking.PacketDecoder;
import edu.frostburg.groupvoicechat.networking.Peer;
import edu.frostburg.groupvoicechat.networking.events.EventHandler;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Kevin Raoofi
 */
public class ClientAudioPacketHandler extends AbstractPacketDecoder {

    private final EventRouter<EventRouterState> er;
    private final Map<InetAddress, PacketDecoder> addressHandlerMap;

    public ClientAudioPacketHandler(EventRouter<EventRouterState> er) {
        this.er = er;
        this.addressHandlerMap = new HashMap<>();
    }

    public boolean register(Peer p, PacketDecoder handler) {
        final InetSocketAddress isa = p
                .getIsa()
                .get();

        if (isa == null) {
            return false;
        }

        this.addressHandlerMap.put(isa.getAddress(), handler);

        return true;
    }

    /**
     *
     * @param p peer to unregister
     * @return the event handler or null if peer contains no
     * {@link Peer#getIsa()} or if not already registered
     */
    public PacketDecoder unregister(Peer p) {
        final InetSocketAddress isa = p
                .getIsa()
                .get();
        if (isa == null) {
            return null;
        }
        return addressHandlerMap.remove(isa.getAddress());
    }

    @Override
    public void processPacket(PacketContext pc) {
        super.processPacket(pc);
        final InetAddress senderAddress = pc.getSenderAddress();
        final List<Peer> peerList = this.er.getState()
                .getPeerList();
        for (final Peer p : peerList) {
            p.getIsa()
                    .ifPresent((isa) -> {
                final PacketDecoder pd = addressHandlerMap
                        .get(isa.getAddress());
                er.submitAsync(() -> {
                    pd.processPacket(pc);
                });
            });
        }
    }

}
