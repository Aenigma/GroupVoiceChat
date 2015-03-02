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

import java.util.concurrent.locks.LockSupport;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Kevin Raoofi
 */
public class AudioFeedBackExample {

    static final AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            (float) 48000, 16, 1, 2, 48000, false);
    static final int BUFFER_SIZE = 16000;
    static final int LINE_BUFFER_SIZE = 2 << 12;

    static final int maxBytesRead = 2 << 25;

    public static void main(String... args) throws LineUnavailableException {
        SourceDataLine sdl = AudioSystem.getSourceDataLine(audioFormat);
        TargetDataLine tdl = AudioSystem.getTargetDataLine(audioFormat);

        tdl.open(audioFormat, LINE_BUFFER_SIZE);
        sdl.open(audioFormat, LINE_BUFFER_SIZE);

        tdl.start();
        sdl.start();

        DataLine.Info dli = new DataLine.Info(TargetDataLine.class,
                audioFormat);

        final byte[] input = new byte[LINE_BUFFER_SIZE];
        final byte[] output = new byte[LINE_BUFFER_SIZE];

        long totalBytesProcessed = 0;

        while (totalBytesProcessed < maxBytesRead) {
            while (tdl.available() < 1) {
                LockSupport.parkNanos(1000);
            }
            final int length = Math.min(tdl.available(), input.length);
            final int bytesRead = tdl.read(input, 0, length);
            if (bytesRead > 0 || true) {
                totalBytesProcessed += bytesRead;
                sdl.write(input, 0, Math.min(bytesRead, output.length));
                System.out.println(bytesRead + " l:" + length);
            }
        }

        tdl.stop();
        tdl.close();

        sdl.stop();
        sdl.close();
    }
}
