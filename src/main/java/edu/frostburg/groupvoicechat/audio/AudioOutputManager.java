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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class AudioOutputManager {

    private final SourceDataLine sdl;
    final AudioFormat af;
    final ExecutorService addexec;
    final ExecutorService playAudio;

    public AudioOutputManager(AudioFormat af) throws LineUnavailableException {
        this.addexec = Executors.newCachedThreadPool();
        this.playAudio = Executors.newSingleThreadExecutor();
        this.af = af;
        this.sdl = AudioSystem.getSourceDataLine(af);
        this.sdl.open();
        this.sdl.start();
    }

    public void add(final byte[] pcm) {
        this.addexec.submit(() -> {
            playAudio.submit(() -> {
                sdl.write(pcm, 0, pcm.length);
            });
        });
    }

    public void stop() {
        this.addexec.shutdown();
        this.playAudio.shutdown();
        this.sdl.close();
        this.sdl.stop();
    }

}
