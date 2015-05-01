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
import edu.frostburg.groupvoicechat.networking.command.Command;
import edu.frostburg.groupvoicechat.networking.command.CommandReader;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;

/**
 *
 * @author Kevin Raoofi
 */
public class ServerConnectionPacketDecoder extends ConnectionPacketDecoder {

    @Override
    public void processCommand(PacketContext pc, Command c) {
        super.processCommand(pc, c);

        CommandReader cr = c.newReader();
        switch (c.getName()
                .toUpperCase()) {
            case "JOIN":
                join(pc, cr);
                break;
            case "QUIT":
                quit(pc, cr);
                break;
        }
    }

    public void join(PacketContext pc, CommandReader cr) {
        final EventRouter<EventRouterState> eventRouter = pc.getEventRouter();
        final EventRouterState state = eventRouter.getState();

        final String username = cr.nextArgument();
        final Peer p = new Peer(state.getPeerId()
                .getAndIncrement(),
                pc.getAddress());

        p.setUsername(username);

        state.addPeer(p);

    }

    public void quit(PacketContext pc, CommandReader cr) {
        final EventRouter<EventRouterState> eventRouter = pc.getEventRouter();
        final EventRouterState state = eventRouter.getState();

        state.removePeer(pc.getSender());
    }

}
