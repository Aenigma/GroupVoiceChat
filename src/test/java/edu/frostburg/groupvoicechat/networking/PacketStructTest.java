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
package edu.frostburg.groupvoicechat.networking;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Kevin Raoofi
 */
public class PacketStructTest {

    private static ByteBuffer bb;

    public PacketStructTest() {

    }

    @BeforeClass
    public static void setUpClass() {
        bb = ByteBuffer.allocate(1 << 16);
    }

    @AfterClass
    public static void tearDownClass() {
        bb = null;
    }

    @Before
    public void setUp() {
        bb.clear();
    }

    @After
    public void tearDown() {
        bb.clear();
    }

    @Test
    public void toAndFromByteBuffer() throws Exception {
        System.out.println("toAndFromByteBuffer");
        final PacketStruct ps = new PacketStruct();
        final Random r = new Random(0L);
        final byte[] payload = new byte[64];

        ps.packetId = r.nextLong();
        ps.time = r.nextLong();
        ps.packetType = (byte) r.nextInt();
        r.nextBytes(payload);

        ps.payload = payload;
        ps.senderAddress = InetAddress
                .getByAddress(InetAddress
                        .getLocalHost()
                        .getAddress());

        ps.toByteBuffer(bb);
        bb.flip();

        final PacketStruct result = PacketStruct.fromByteBuffer(bb);

        System.out.println(ps);
        System.out.println(result);

        assertTrue("PacketStruct is the same reference as the original",
                ps != result);
        assertEquals(ps, result);
    }

    @Test
    public void toAndFromByteBufferTransitive() throws Exception {
        System.out.println("toAndFromByteBufferTransitive");
        final PacketStruct original = new PacketStruct();
        final Random r = new Random(0L);
        final byte[] payload = new byte[64];

        original.packetId = r.nextLong();
        original.time = r.nextLong();
        original.packetType = (byte) r.nextInt();
        r.nextBytes(payload);

        original.payload = payload;
        original.senderAddress = InetAddress
                .getByAddress(InetAddress
                        .getLocalHost()
                        .getAddress());

        PacketStruct next = original;

        for (int i = 0; i < 100; i++) {
            next.toByteBuffer(bb);
            bb.flip();
            next = PacketStruct.fromByteBuffer(bb);
            bb.flip();
            assertEquals("Transitive equivalence failed at " + i, original, next);

        }
    }

    /**
     * Test of fromByteBuffer method, of class PacketStruct.
     */
    public void testFromByteBuffer() {
        System.out.println("fromByteBuffer");
        ByteBuffer bb = null;
        PacketStruct expResult = null;
        PacketStruct result = PacketStruct.fromByteBuffer(bb);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class PacketStruct.
     *
     * @throws java.net.UnknownHostException
     */
    @Test
    public void testGetSize() throws UnknownHostException {
        System.out.println("getSize");
        final PacketStruct ps = new PacketStruct();
        ps.packetId = 0L;
        ps.time = 0L;
        ps.packetType = PacketStruct.PACKET_TYPE_ERROR;
        ps.payload = new byte[1];

        ps.senderAddress = Inet4Address.getLocalHost();
        assertEquals("IPv4 address size does not match!", 23, ps.getSize());

        ps.senderAddress = InetAddress.getByName("::1");
        assertEquals("IPv6 address size does not match!", 35, ps.getSize());

    }

    /**
     * Test of toByteBuffer method, of class PacketStruct.
     */
    public void testToByteBuffer() {
        System.out.println("toByteBuffer");
        ByteBuffer bb = null;
        PacketStruct instance = new PacketStruct();
        instance.toByteBuffer(bb);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toBytes method, of class PacketStruct.
     */
    @Test
    public void testToBytes() {
        System.out.println("toBytes");
        final PacketStruct ps = new PacketStruct();
        ps.packetId = 0L;
        ps.time = 0L;
        ps.packetType = PacketStruct.PACKET_TYPE_AUDIO;
        ps.payload = new byte[1];
        ps.toBytes();
    }

    /**
     * Test of equals method, of class PacketStruct.
     */
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        PacketStruct instance = new PacketStruct();
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class PacketStruct.
     */
    public void testToString() {
        System.out.println("toString");
        PacketStruct instance = new PacketStruct();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
