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
package edu.frostburg.groupvoicechat.examples;

import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Kevin Raoofi
 */
public class PcmReceiverExample implements Runnable {

    /** logger */
    private static final Logger LOG
            = Logger.getLogger(PcmReceiverExample.class.getName());

    /* Constant definitions */
    // PCM unsigned AudioFormat; you should know that this format is the
    // default format assumed by alsautil's aplay
    static final AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_UNSIGNED,
            8000, // Sample Rate
            8, // Sample size in bits
            1, // Number of channels
            1, // Frame size in bytes
            8000, // Frames per second
            false); // Is big endian?

    /**
     * the number of bytes required to represent a second of audio data is
     * dependent on the audio format
     */
    static final int bufferSizePerSecond = (int) audioFormat.getSampleRate()
            * audioFormat.getFrameSize();

    /**
     * the port to listen on
     */
    static final int port = 9999;

    /** The max size of a UDP payload is a little less than this.. who cares
     * about a bit of wasted space */
    private final ByteBuffer bb = ByteBuffer.allocate(1 << 16);

    private final HashMap<InetSocketAddress, PriorityBlockingQueue<PacketStruct>> audioQueue;

    private final SourceDataLine sdl;

    public PcmReceiverExample() throws LineUnavailableException {
        this.audioQueue = new HashMap<>();
        this.sdl = AudioSystem.getSourceDataLine(audioFormat);
        this.sdl.open();
        this.sdl.start();
    }

    public CheckAudioQueues getAudioPlayBackRunner() throws
            LineUnavailableException {
        return new CheckAudioQueues(new PlayBackAudio());
    }

    @Override
    public void run() {
        try {
            DatagramChannel dc = DatagramChannel.open();
            dc.bind(new InetSocketAddress(port));

            while (true) {
                InetSocketAddress sender = (InetSocketAddress) dc.receive(bb);
                bb.flip();

                PacketStruct ps = PacketStruct.fromByteBuffer(bb);

                //this.sdl.write(ps.payload, 0, ps.payload.length);
                if (ps.packetType == PacketStruct.PACKET_TYPE_AUDIO) {
                    PriorityBlockingQueue<PacketStruct> pq = getPq(sender);
                    pq.add(ps);
                    //LOG.log(Level.SEVERE, "Got packet: {0}", ps);
                }

                bb.clear();
            }

        } catch (IOException ex) {
            Logger.getLogger(PcmReceiverExample.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private PriorityBlockingQueue<PacketStruct> getPq(InetSocketAddress isa) {
        PriorityBlockingQueue<PacketStruct> pq = audioQueue.get(isa);

        if (pq == null) {
            pq = new PriorityBlockingQueue<>(64, new PacketStructComparator());
            audioQueue.put(isa, pq);
        }

        return pq;
    }

    public static void main(String... args) throws LineUnavailableException {
        ExecutorService es = Executors.newCachedThreadPool();
        PcmReceiverExample pcmReceiverExample = new PcmReceiverExample();
        es.submit(pcmReceiverExample);

        pcmReceiverExample.getAudioPlayBackRunner()
                .run();
    }

    public class CheckAudioQueues implements Runnable {

        static final long checkIntervalInMs = 20;

        final PlayBackAudio pba;

        CheckAudioQueues(PlayBackAudio pba) {
            this.pba = pba;
        }

        @Override
        public void run() {
            ScheduledExecutorService ses = Executors
                    .newScheduledThreadPool(2);

            // i'm not sure which is better; fixed delay or fixed rate...
            ses.scheduleWithFixedDelay(pba,
                    0,
                    checkIntervalInMs, TimeUnit.MILLISECONDS);

            LOG.log(Level.SEVERE, "Started periodic check service");
        }

    }

    class PlayBackAudio implements Runnable {

        @Override
        public void run() {
            for (PriorityBlockingQueue<PacketStruct> pbq : audioQueue.values()) {
                final long threshold = System.currentTimeMillis();
                if (pbq.peek().time < threshold) {
                    LOG.log(Level.SEVERE, "Got enough audio data...");
                    byte[] lb = pbq.stream()
                            .filter(ps -> ps.time < threshold)
                            .map((ps -> ps.payload))
                            .reduce(
                                    (byte[] ba1, byte[] ba2) -> {
                                LOG.log(Level.SEVERE, "reduction!");
                                // concatenate the arrays 
                                byte[] result
                                        = new byte[ba1.length + ba2.length];
                                System.arraycopy(ba1, 0, result, 0, ba1.length);
                                System.arraycopy(ba2, 0, result, ba1.length,
                                        ba2.length);
                                return result;
                            })
                            .get();

                    LOG.log(Level.SEVERE, "playing audio");
                    sdl.write(lb, 0, lb.length);
                } else {
                    LOG.log(Level.SEVERE, "Not enough audio data...");
                }
            }
        }

    }

    static class PacketStructComparator implements Comparator<PacketStruct> {

        public PacketStructComparator() {
        }

        @Override
        public int compare(PacketStruct o1, PacketStruct o2) {
            return (int) (o1.time - o2.time);
        }
    }
}
