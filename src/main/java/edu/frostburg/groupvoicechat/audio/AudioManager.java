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
package edu.frostburg.groupvoicechat.audio;

import edu.frostburg.groupvoicechat.networking.PacketContext;
import edu.frostburg.groupvoicechat.networking.PacketDecoder;
import edu.frostburg.groupvoicechat.networking.events.EventRouter;
import edu.frostburg.groupvoicechat.networking.events.EventRouterState;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author Kevin Raoofi
 */
public class AudioManager {

    /** PCM unsigned AudioFormat; you should know that this format is the
     * default format assumed by alsautil's aplay */
    static final AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_UNSIGNED,
            8000, // Sample Rate
            8, // Sample size in bits
            1, // Number of channels
            1, // Frame size in bytes
            8000, // Frames per second
            false); // Is big endian?

    /** Keeps track of all input managers */
    private final Collection<AudioInputManager> aims = new ArrayList<>();

    /** Keeps track of all output managers */
    private final Collection<AudioOutputManager> aoms = new ArrayList<>();

    private final EventRouter<EventRouterState> er;

    /**
     * The event router responsible for managing this instance
     *
     * @param er
     */
    public AudioManager(EventRouter<EventRouterState> er) {
        this.er = er;
    }

    public PacketDecoder newAudioOutput() throws
            LineUnavailableException {
        AudioOutputManager audioOutputManager
                = new AudioOutputManager(audioFormat);

        AudioOutputEventHandler audioOutputEventHandler
                = new AudioOutputEventHandler(audioOutputManager);

        return audioOutputEventHandler;
    }

    public void newAudioInput(InetSocketAddress sendTo) throws
            LineUnavailableException {
        final AudioInputManager audioInputManager
                = new AudioInputManager(sendTo, audioFormat,
                        this.er.getState()
                        .getSendPacketId());
    }

    /**
     *
     */
    static class AudioOutputEventHandler implements PacketDecoder {

        final AudioOutputManager aom;

        public AudioOutputEventHandler(AudioOutputManager aom) {
            this.aom = aom;
        }

        @Override
        public void processPacket(PacketContext pc) {
            final byte[] payload = pc.getPayload();

            this.aom.add(payload);
        }
    }

    public void stop() {
        for (AudioInputManager aim : aims) {
            aim.stop();
        }

        for (AudioOutputManager aom : aoms) {
            aom.stop();
        }
    }

}
