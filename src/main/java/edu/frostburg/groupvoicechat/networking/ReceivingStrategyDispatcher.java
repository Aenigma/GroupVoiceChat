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

import edu.frostburg.groupvoicechat.networking.events.EventWrapper;
import edu.frostburg.groupvoicechat.networking.events.EventHandler;

/**
 *
 * @author Kevin Raoofi
 */
public class ReceivingStrategyDispatcher implements EventHandler<PacketContext> {

    private final PacketDecoder[] pdMap;

    public ReceivingStrategyDispatcher(PacketDecoder[] pd) {
        this.pdMap = pd;
    }

    public PacketDecoder getDecoder(byte packetType) {
        return pdMap[packetType];
    }

    @Override
    public void onEvent(EventWrapper<PacketContext> e) {
        PacketContext p = e.getContext();
        PacketDecoder pd = getDecoder(p.getPacketType());
        pd.processPacket(p);
    }

}
