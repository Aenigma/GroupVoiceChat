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

/**
 *
 * @author Kevin Raoofi
 */
public abstract class AbstractPacketDecoder implements PacketDecoder {

    @Override
    public void processPacket(PacketContext pc) {
        updatePingTable(pc);
    }

    public void updatePingTable(PacketContext pc) {
        pc.getEventRouter(); // get state and update ping table
    }

}
