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
import java.util.HashMap;
import java.util.Map;

/**
 * Gets an audio packet and then hands it off so that it is played in order of
 * the audiooutput manager specific to client
 *
 * @author Kevin Raoofi
 */
public class AudioPacketDispatcher implements PacketDecoder {

    final Map<Peer, PacketDecoder> perPeerPdLookup = new HashMap<>();

    public void register(Peer p, PacketDecoder pd) {
        perPeerPdLookup.put(p, pd);
    }

    public PacketDecoder unregister(Peer p) {
        return perPeerPdLookup.remove(p);
    }

    @Override
    public void processPacket(PacketContext pc) {
        final Peer sender = pc.getSender();
        final PacketDecoder pd = perPeerPdLookup.get(sender);

        if (pd != null) {
            pc.getEventRouter()
                    .submitAsync(() -> {
                pd.processPacket(pc);
            });
        }
    }

}
