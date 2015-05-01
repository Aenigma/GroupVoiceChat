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

import edu.frostburg.groupvoicechat.networking.protocol.AbstractPacketDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Raoofi
 */
public class InvalidPacketDecoder extends AbstractPacketDecoder {

    private static final Logger LOG
            = Logger.getLogger(InvalidPacketDecoder.class.getName());

    @Override
    public void processPacket(PacketContext pc) {
        super.processPacket(pc);

        LOG.log(Level.SEVERE, "Invalid packet decoder selected: {0}", pc);
    }

}
