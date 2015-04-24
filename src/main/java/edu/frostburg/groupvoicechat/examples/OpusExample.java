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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

/**
 *
 * @author Kevin Raoofi
 */
public class OpusExample {

    /**
     * Opus only supports certain sample sizes. Lower sample sizes may be
     * automatically handled by Opus when it can.
     *
     * @see
     * <a href="http://www.opus-codec.org/docs/html_api-1.1.0/group__opus__encoder.html#gaa89264fd93c9da70362a0c9b96b9ca88">opus_create
     * documentation</a>
     */
    protected static final int[] VALID_SAMPLE_RATES = new int[]{
        8000,
        12000,
        16000,
        24000,
        48000};

    /**
     *
     */
    private static final int SAMPLE_RATE = VALID_SAMPLE_RATES[0];

    /**
     * Determined by the frame size in milliseconds. Thus, the number of bytes
     * required to represent that depends on the sample rate.
     */
    protected static final int[] VALID_FRAME_SIZES = new int[]{
        SAMPLE_RATE / 400, // 2.5ms
        SAMPLE_RATE / 200, // 5ms
        SAMPLE_RATE / 100, // 10ms
        SAMPLE_RATE / 50, // 20ms
        SAMPLE_RATE / 25, // 40ms
        (SAMPLE_RATE * 50) / 3 // 60ms
    };

    private static final int FRAME_SIZE = VALID_FRAME_SIZES[4];

    public static void main(String... args) throws IOException {
        /* Note that the sample rate is expressed as a float, but opus expects
         * them as an int. Note, also, that libjitsi has only been tested with 1
         * channel */
        final AudioFormat af
                = new AudioFormat((float) SAMPLE_RATE, 16, 1, true, false);

        System.out.println("Sample Rate: " + af.getSampleRate());
        System.out.println("Frame Rate: " + af.getFrameRate());
        System.out.println("Frame Size: " + af.getFrameSize());

        final byte[] input = new byte[FRAME_SIZE];
        final byte[] output = new byte[FRAME_SIZE];

        Opus.assertOpusIsFunctional();
        final long encPtr = Opus.encoder_create((int) af.getSampleRate(), af
                .getChannels());

        if (encPtr == 0) {
            throw new RuntimeException("Failed creating an encoder: " + encPtr);
        }

        int encode_result = Opus.encode(encPtr,
                input, 0, FRAME_SIZE,
                output, 0, FRAME_SIZE);

        if (encode_result < 0) {
            throw new RuntimeException("Failed encoding; got: " + encode_result);
        }

        Opus.encoder_destroy(encPtr);

        System.out.println(Arrays.toString(input));
        System.out
                .println(Arrays.toString(Arrays.copyOf(output, encode_result)));
        Files.write(Paths.get("./out.opus"),
                Arrays.copyOf(output, encode_result));

    }
}
