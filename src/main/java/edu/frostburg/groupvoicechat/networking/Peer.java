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

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents and endpoint that the service is communicating with.
 *
 * @author Kevin Raoofi
 */
public class Peer {

    /** Must be unique */
    final int peerId;

    int pingTime;

    InetSocketAddress isa;

    // maybe this would be better off as a standalone object?
    final Map<String, Long> pingRequests;

    public Peer(int peerId) {
        this.peerId = peerId;
        this.pingRequests = new HashMap<>();
    }

    /**
     *
     * @param challenge
     * @param time time since the Epoch in ms
     */
    public void addPingRequest(String challenge, long time) {
        pingRequests.put(challenge, time);
    }

    /**
     * Adds a ping request with
     *
     * @param challenge
     */
    public void addPingRequest(String challenge) {
        addPingRequest(challenge, System.currentTimeMillis());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.peerId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Peer other = (Peer) obj;
        if (this.peerId != other.peerId) {
            return false;
        }
        return true;
    }
}
