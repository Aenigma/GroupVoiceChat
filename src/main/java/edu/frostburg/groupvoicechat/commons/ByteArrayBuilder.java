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
package edu.frostburg.groupvoicechat.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Raoofi
 */
public class ByteArrayBuilder {

    static final ThreadLocal<ByteBuffer> tlb = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(32);
        }
    };

    public final ByteArrayOutputStream baos;

    public ByteArrayBuilder() {
        baos = new ByteArrayOutputStream();
    }

    public void put(byte b) {
        baos.write(b);
    }

    public void putLong(long l) {
        final ByteBuffer bb = tlb.get();
        bb.clear();
        bb.putLong(l);
        bb.flip();

        writeByteBufferToBaos();
    }

    private void writeByteBufferToBaos() {
        final ByteBuffer bb = tlb.get();
        final byte[] tmp;
        tmp = new byte[bb.remaining()];
        bb.get(tmp);
        try {
            baos.write(tmp);
        } catch (IOException ex) {
            Logger.getLogger(ByteArrayBuilder.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    public void putInt(int i) {
        final ByteBuffer bb = tlb.get();
        bb.clear();
        bb.putInt(i);
        bb.flip();

        writeByteBufferToBaos();

    }

    public void putDouble(double d) {
        final ByteBuffer bb = tlb.get();
        bb.clear();
        bb.putDouble(d);
        bb.flip();

        writeByteBufferToBaos();
    }

    public void putFloat(float f) {
        final ByteBuffer bb = tlb.get();
        bb.clear();
        bb.putFloat(f);
        bb.flip();

        writeByteBufferToBaos();

    }

    public void putShort(short s) {
        final ByteBuffer bb = tlb.get();
        bb.clear();
        bb.putShort(s);
        bb.flip();

        writeByteBufferToBaos();
    }

    public byte[] build() {
        return baos.toByteArray();
    }
}
