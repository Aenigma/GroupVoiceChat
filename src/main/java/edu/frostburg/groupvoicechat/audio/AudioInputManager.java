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

import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.sockets.PacketSender;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Kevin Raoofi
 */
class AudioInputManager implements Runnable {

    /** */
    private static final PacketSender ps = new PacketSender();

    /** */
    private final InetSocketAddress receiver;

    private final AudioFormat af;

    private final int bufferSizePerSecond;

    private final int bufferSize;

    /** */
    private final TargetDataLine tdl;

    /** */
    private volatile boolean keepListening = true;

    /** The buffer used to process input; should take up to 20ms of input */
    private final byte[] inBuf;

    /** */
    private final ExecutorService es;

    /** Part of the state */
    private final AtomicLong packetId;

    /**
     *
     *
     * @param receiver
     * @param af
     */
    public AudioInputManager(InetSocketAddress receiver,
            AudioFormat af, AtomicLong packetId) throws
            LineUnavailableException {
        // parameter pass
        this.af = af;
        this.receiver = receiver;
        this.packetId = packetId;

        // audioformat
        this.bufferSizePerSecond = (int) af.getSampleRate()
                * af.getFrameSize();
        this.bufferSize = bufferSizePerSecond / 50;
        this.inBuf = new byte[bufferSize];

        // tdl setup
        this.tdl = AudioSystem.getTargetDataLine(this.af);
        this.tdl.open(this.af, bufferSize * 2);

        // executor setup
        this.es = Executors.newSingleThreadExecutor();
    }

    public void start() {
        this.es.submit(this);

        this.tdl.start();
    }

    public void stop() {
        this.keepListening = false;
        this.es.shutdown();

        this.tdl.stop();
        this.tdl.close();
    }

    @Override
    public void run() {
        while (keepListening) {
            final int bytesFromMic = tdl.read(inBuf, 0, inBuf.length);

            if (bytesFromMic == 0) {
                continue;
            }

            final PacketStruct packet = new PacketStruct();
            packet.packetId = getNextId();
            packet.packetType = PacketStruct.PACKET_TYPE_AUDIO;
            packet.payload = Arrays.copyOf(inBuf, bytesFromMic);
            packet.time = System.currentTimeMillis();

            try {

                ps.send(receiver.getAddress(), receiver.getPort(), packet);
            } catch (IOException ex) {
                Logger.getLogger(AudioInputManager.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    private long getNextId() {
        return this.packetId.incrementAndGet();
    }

}
