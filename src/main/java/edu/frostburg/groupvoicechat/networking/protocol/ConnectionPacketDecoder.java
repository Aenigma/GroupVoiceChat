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

import edu.frostburg.groupvoicechat.networking.command.Command;
import edu.frostburg.groupvoicechat.networking.command.CommandReader;
import edu.frostburg.groupvoicechat.networking.PacketContext;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;

/**
 *
 * @author Kevin Raoofi
 */
public class ConnectionPacketDecoder extends AbstractPacketDecoder {

    @Override
    public void processPacket(PacketContext pc) {
        super.processPacket(pc);

        PacketStruct ps = pc.getPacketStruct();
        Command c = new Command(pc.getPayload());

        processCommand(pc, c);
    }

    public void processCommand(final PacketContext pc, final Command c) {
        CommandReader cr = c.newReader();
        switch (c.getName()
                .toUpperCase()) {
            case "PING":
                ping(pc, cr);
                break;
            case "PONG":
                pong(pc, cr);
                break;
        }

    }

    public void ping(final PacketContext pc, final CommandReader cr) {
        EventRouter er = pc.getEventRouter();
        er.addEvent(null);
    }

    public void pong(final PacketContext pc, final CommandReader cr) {

    }
}
