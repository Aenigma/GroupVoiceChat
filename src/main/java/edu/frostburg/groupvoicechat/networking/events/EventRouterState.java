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

import edu.frostburg.groupvoicechat.audio.AudioManager;
import edu.frostburg.groupvoicechat.commons.Pair;
import edu.frostburg.groupvoicechat.networking.PacketDecoder;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.Peer;
import edu.frostburg.groupvoicechat.networking.ReceivingStrategyDispatcher;
import edu.frostburg.groupvoicechat.networking.protocol.ClientAudioPacketHandler;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author Kevin Raoofi
 */
public class EventRouterState {

    final Map<Peer, Integer> pingTable;
    final Map<Peer, Pair<String, Long>> pingResponseTable;

    final AtomicLong sendPacketId = new AtomicLong(0);
    final AtomicInteger peerId = new AtomicInteger(0);

    final List<Peer> peerList;

    final Map<InetAddress, Peer> peerLookup = new HashMap<>();

    EventRouter<EventRouterState> eventRouter;

    /* TODO: audiomanager should have been handling addition to
     * clientaudiopackethandler... */
    /*
     * TODO: this never can shut down...
     */
    AudioManager audioManager;

    public EventRouterState() {
        pingTable = new HashMap<>();
        pingResponseTable = new HashMap<>();
        peerList = new ArrayList<>();

    }

    public EventRouter<EventRouterState> getEventRouter() {
        return eventRouter;
    }

    public void setEventRouter(EventRouter<EventRouterState> eventRouter) {
        this.eventRouter = eventRouter;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void addPeer(Peer p) {
        peerList.add(p);

        if (p.getIsa()
                .isPresent()) {
            peerLookup.put(p.getIsa()
                    .get()
                    .getAddress(), p);
        }

        addNewAudioOutput(p);

    }

    private ClientAudioPacketHandler getAudioPacketDecoderHandler() {
        final ReceivingStrategyDispatcher eventHandler
                = (ReceivingStrategyDispatcher) eventRouter
                .getEventHandler(EventType.PACKET);

        final ClientAudioPacketHandler decoder
                = (ClientAudioPacketHandler) eventHandler
                .getDecoder(PacketStruct.PACKET_TYPE_AUDIO);

        return decoder;
    }

    private void addNewAudioOutput(Peer p) {
        if (audioManager == null || eventRouter == null) {
            return;
        }

        try {
            final PacketDecoder peerAudioOutput = audioManager.newAudioOutput();

            final ClientAudioPacketHandler decoder
                    = getAudioPacketDecoderHandler();

            decoder.register(p, peerAudioOutput);

        } catch (LineUnavailableException ex) {
            Logger.getLogger(EventRouterState.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

    private void clearAudioOutut(Peer p) {
        if (audioManager == null || eventRouter == null) {
            return;
        }

        final ClientAudioPacketHandler decoder
                = getAudioPacketDecoderHandler();

        decoder.unregister(p);

    }

    public void removePeer(Peer p) {
        peerList.remove(p);
        if (p.getIsa()
                .isPresent()) {
            peerLookup.remove(p.getIsa()
                    .get()
                    .getAddress());
        }

        clearAudioOutut(p);
    }

    public Peer lookUpPeer(InetAddress ia) {
        return peerLookup.get(ia);
    }

    public List<Peer> getPeerList() {
        return peerList;
    }

    public AtomicLong getSendPacketId() {
        return sendPacketId;
    }

    public AtomicInteger getPeerId() {
        return peerId;
    }

    /**
     *
     * @param p peer to look up
     * @return -1 if not in ping table, otherwise the ping of the peer
     */
    private int getPingTime(Peer p) {
        if (true) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        Integer result = pingTable.get(p);
        if (result == null) {
            return -1;
        }

        return result;
    }

    private void setPingTime(Peer p, int time) {
        if (true) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
