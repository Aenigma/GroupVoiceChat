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
package edu.frostburg.groupvoicechat;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import org.jitsi.impl.neomedia.codec.audio.opus.Opus;
import org.jitsi.service.libjitsi.LibJitsi;

/**
 *
 * @author Kevin Raoofi
 */
public class BufferTransformer {

    public BufferTransformer() {

    }

    public void testCodeOfAMadMan() {
        long encoder = Opus.encoder_create(1, 1);

        byte[] input = new byte[65536];
        byte[] output = new byte[65536];

        AudioFormat format = new AudioFormat(48000.0f, 16, 2, true, true);
        try {
            TargetDataLine tdl = AudioSystem.getTargetDataLine(format);
            DataLine.Info dli = new DataLine.Info(TargetDataLine.class, format);
            System.out.println(Arrays.toString(dli.getFormats()));
            Mixer.Info[] mis = AudioSystem.getMixerInfo();

            while (true) {
                int bytes = tdl.read(input, 0, input.length);
                if (bytes > 0) {
                    System.out.println(bytes);
                }
            }
        } catch (LineUnavailableException ex) {
            Logger.getLogger(BufferTransformer.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

//        Opus.encode(encoder,
//                input, 0, input.length,
//                output, 0, output.length);
//        Opus.encoder_destroy(encoder);
    }

    public static void main(String... args) {
        System.out.println("kjsadfhlkjhsdfkljhasdkljfhaslkdfhkljsdhfkls");
        System.out.println(LibJitsi.class);
        LibJitsi.start();
        Opus.assertOpusIsFunctional();
        new BufferTransformer().testCodeOfAMadMan();
        LibJitsi.stop();
        System.out.println("kjsadfhlkjhsdfkljhasdkljfhaslkdfhkljsdhfkls");
    }
}
