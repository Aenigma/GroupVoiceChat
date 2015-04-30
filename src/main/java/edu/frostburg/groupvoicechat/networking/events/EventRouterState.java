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
package edu.frostburg.groupvoicechat.networking.events;

import edu.frostburg.groupvoicechat.commons.Pair;
import edu.frostburg.groupvoicechat.networking.Peer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kevin Raoofi
 */
public class EventRouterState {

    Map<Peer, Integer> pingTable;
    Map<Peer, Pair<String, Long>> pingResponseTable;

    final List<Peer> peerList;

    public EventRouterState() {
        pingTable = new HashMap<>();
        pingResponseTable = new HashMap<>();
        peerList = new ArrayList<>();
    }

    public void addPeer(Peer p) {
        peerList.add(p);
    }

    public void removePeer(Peer p) {
        peerList.remove(p);
    }

    public List<Peer> getPeerList() {
        return peerList;
    }

    /**
     *
     * @param p peer to look up
     * @return -1 if not in ping table, otherwise the ping of the peer
     */
    public int getPingTime(Peer p) {
        Integer result = pingTable.get(p);
        if (result == null) {
            return -1;
        }

        return result;
    }

    public void setPingTime(Peer p, int time) {

    }
}
