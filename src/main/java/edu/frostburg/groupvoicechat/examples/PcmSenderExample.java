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

import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.sockets.PacketSender;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Kevin Raoofi
 */
public class PcmSenderExample implements Runnable {

    /**
     * logging context
     */
    private static final Logger LOG
            = Logger.getLogger(PcmSenderExample.class.getName());

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
     * This should be 20ms of data because 1000Hz / 50 = 20Hz
     */
    static final int bufferSize = bufferSizePerSecond / 50;

    /**
     * the port to connect to
     */
    static final int port = 9999;

    /* Instance variables */
    /**
     * packet sender used by instance
     */
    final PacketSender pSender = new PacketSender();

    /**
     * index for packet is atomic since in a real world case it may a shared
     * state
     */
    final AtomicLong al = new AtomicLong(0);

    /**
     * the recipient is immutable here
     */
    final InetAddress recipient;

    /** The buffer used to process input; should take up to 20ms of input */
    final byte[] inBuf = new byte[bufferSize];

    /** Everything not related to sending the actual audio packet is done here. */
    final AudioBlockProcessor[] abps;

    /**
     * Not used but could conceivably used to stop the audio loop from an
     * external resource
     */
    private volatile boolean continueLooping = false;

    public PcmSenderExample() throws LineUnavailableException,
            UnknownHostException, IOException {
        this(InetAddress.getByName("127.0.0.1"));
    }

    public PcmSenderExample(InetAddress recipient) throws
            LineUnavailableException, IOException {
        this(recipient,
                new WriteToFile(Paths.get("test.pcm")),
                new PlayBackAudio(audioFormat));
    }

    /**
     * Exposes the AudioBlock processor interface and allows for custom
     * behaviors for processing audio blocks.
     *
     * @param recipient to whom to send the UDP packets to
     * @param abps audioblockprocessors to do extra things such as help logging
     */
    PcmSenderExample(InetAddress recipient, AudioBlockProcessor... abps) {
        this.recipient = recipient;
        this.abps = abps;
    }

    /**
     * Starts listening and sending audio data
     */
    @Override
    public void run() {
        if (continueLooping) {
            throw new IllegalStateException("Already running!");
        }
        continueLooping = true;

        long lastSentTime = System.currentTimeMillis();

        try (
                final TargetDataLine tdl
                = AudioSystem.getTargetDataLine(audioFormat)) {
            tdl.open(audioFormat, bufferSize * 2);
            tdl.start();

            while (continueLooping) {
                final int bytesFromMic = tdl
                        .read(inBuf, 0, inBuf.length);
                if (bytesFromMic == 0) {
                    continue;
                }

                final PacketStruct ps = new PacketStruct();
                ps.packetId = al.getAndIncrement();
                ps.packetType = PacketStruct.PACKET_TYPE_AUDIO;
                ps.payload = Arrays.copyOf(inBuf, bytesFromMic);
                ps.time = System.currentTimeMillis();

                pSender.send(recipient, port, ps);
                LOG.log(Level.SEVERE,
                        "Read {0} bytes; sent with {1} delay",
                        new Object[]{bytesFromMic, ps.time
                            - lastSentTime});
                lastSentTime = ps.time;

                // post processing hooks
                for (AudioBlockProcessor abp : abps) {
                    abp.process(ps.payload);
                }
            }

        } catch (LineUnavailableException | IOException ex) {
            Logger.getLogger(PcmSenderExample.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Can be called externally to stop the audio loop.
     */
    public void stop() {
        this.continueLooping = false;
    }

    public static void main(String... args) throws IOException,
            LineUnavailableException {
        // we are setting it here so that playback and testfile generation is
        // refrained from
        final PcmSenderExample pcmSenderExample = new PcmSenderExample(
                InetAddress.getByName("127.0.0.1"), new AudioBlockProcessor[]{});
        pcmSenderExample.run();
    }

    /**
     * This interface is private because its dependency to {@link AutoCloseable}
     * is but a convenience.
     */
    private static interface AudioBlockProcessor extends AutoCloseable {

        public void process(byte[] buf);
    }

    /**
     * Writes raw PCM data to the given file
     */
    private static class WriteToFile implements AudioBlockProcessor {

        final BufferedOutputStream bos;

        public WriteToFile(final Path destination) throws IOException {
            bos = new BufferedOutputStream(Files.newOutputStream(destination));
        }

        @Override
        public void process(byte[] buf) {
            try {
                bos.write(buf);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void close() throws Exception {
            bos.close();
        }
    }

    /**
     * Sends the audio data to speakers
     */
    private static class PlayBackAudio implements AudioBlockProcessor {

        final AudioFormat af;
        final SourceDataLine sdl;

        public PlayBackAudio(AudioFormat af) throws LineUnavailableException {
            this.af = af;
            this.sdl = AudioSystem.getSourceDataLine(af);
            this.sdl.open();
            this.sdl.start();
        }

        @Override
        public void process(byte[] buf) {
            sdl.write(buf, 0, buf.length);
        }

        @Override
        public void close() throws Exception {
            this.sdl.stop();
            this.sdl.close();
        }
    }
}
