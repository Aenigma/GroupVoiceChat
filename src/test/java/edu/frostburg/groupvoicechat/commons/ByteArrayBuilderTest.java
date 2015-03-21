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

import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Kevin Raoofi
 */
public class ByteArrayBuilderTest {

    static Random r;

    @BeforeClass
    public static void setUpClass() {
        r = new Random();
    }

    @AfterClass
    public static void tearDownClass() {
        r = null;
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of put method, of class ByteArrayBuilder.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        byte b = (byte) r.nextInt();
        ByteArrayBuilder instance = new ByteArrayBuilder();
        instance.put(b);
        byte result = ByteBuffer.wrap(instance.build())
                .get();
        assertEquals(b, result);
    }

    /**
     * Test of putLong method, of class ByteArrayBuilder.
     */
    @Test
    public void testPutLong() {
        System.out.println("putLong");

        long l = r.nextLong();
        ByteArrayBuilder instance = new ByteArrayBuilder();
        instance.putLong(l);
        long result = ByteBuffer.wrap(instance.build())
                .getLong();
        assertEquals(l, result);
    }

    /**
     * Test of putInt method, of class ByteArrayBuilder.
     */
    @Test
    public void testPutInt() {
        System.out.println("putInt");
        int i = r.nextInt();
        ByteArrayBuilder instance = new ByteArrayBuilder();
        instance.putInt(i);
        int result = ByteBuffer.wrap(instance.build())
                .getInt();
        assertEquals(i, result);
    }

    /**
     * Test of putDouble method, of class ByteArrayBuilder.
     */
    @Test
    public void testPutDouble() {
        System.out.println("putDouble");
        double d = r.nextDouble();
        ByteArrayBuilder instance = new ByteArrayBuilder();
        instance.putDouble(d);
        double result = ByteBuffer.wrap(instance.build())
                .getDouble();
        assertEquals(d, result, Double.MIN_NORMAL);
    }

    /**
     * Test of putFloat method, of class ByteArrayBuilder.
     */
    @Test
    public void testPutFloat() {
        System.out.println("putFloat");
        float f = r.nextFloat();
        ByteArrayBuilder instance = new ByteArrayBuilder();
        instance.putFloat(f);

        float result = ByteBuffer.wrap(instance.build())
                .getFloat();
        assertEquals(f, result, Float.MIN_NORMAL);
    }

    /**
     * Test of putShort method, of class ByteArrayBuilder.
     */
    @Test
    public void testPutShort() {
        System.out.println("putShort");
        short s = (short) r.nextInt();
        ByteArrayBuilder instance = new ByteArrayBuilder();
        instance.putShort(s);
        short result = ByteBuffer.wrap(instance.build())
                .getShort();
        assertEquals(s, result);
    }

    @Test
    public void testSequences() {
        System.out.println("putShort");
        short s = 100;
        long l = 1023;
        byte b = 97;
        int i = 11;
        double d = 230;

        ByteArrayBuilder instance = new ByteArrayBuilder();

        instance.put(b);
        instance.putLong(l);
        instance.putDouble(d);
        instance.putLong(l);
        instance.putInt(i);
        instance.putShort(s);

        ByteBuffer bb = ByteBuffer.wrap(instance.build());

        assertEquals(b, bb.get());
        assertEquals(l, bb.getLong());
        assertEquals(d, bb.getDouble(), Double.MIN_NORMAL);
        assertEquals(l, bb.getLong());
        assertEquals(i, bb.getInt());
        assertEquals(s, bb.getShort());
    }
}
