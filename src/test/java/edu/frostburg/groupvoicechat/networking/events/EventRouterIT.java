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
package edu.frostburg.groupvoicechat.networking.events;

import edu.frostburg.groupvoicechat.networking.PacketContext;
import edu.frostburg.groupvoicechat.networking.PacketDecoder;
import edu.frostburg.groupvoicechat.networking.PacketStruct;
import edu.frostburg.groupvoicechat.networking.ReceivingStrategyDispatcher;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
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
public class EventRouterIT {

    static EventRouter<EventRouterState> er;
    static Set<String> messages;

    @BeforeClass
    public static void setUpClass() {
        er = new EventRouter<>(new EventRouterState());
        messages = new HashSet<>();

        EventHandler<PacketContext> mockEventHandler
                = new MockPacketEventHandler();

        er.register(mockEventHandler, EventType.PACKET);
    }

    @AfterClass
    public static void tearDownClass() {
        er.shutdown();
        er = null;
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testStuff() throws InterruptedException, ExecutionException {
        EventWrapper<PacketContext> ewpc = new EventWrapper<>(EventType.PACKET);
        ewpc.setContext(new PacketContext(new PacketStruct(), er));

        er.addEvent(ewpc)
                .get();
    }

    /**
     * Test of addEvent method, of class EventRouter.
     */
    //@Test
    public void testAddEvent() {
        System.out.println("addEvent");
        EventRouter instance = new EventRouter();
        instance.addEvent(null);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addScheduledEvent method, of class EventRouter.
     */
//    @Test
    public void testAddScheduledEvent() {
        System.out.println("addScheduledEvent");
        EventRouter instance = new EventRouter();
        //instance.addScheduledEvent(null);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addWithFollowUp method, of class EventRouter.
     */
//    @Test
    public void testAddWithFollowUp() {
        System.out.println("addWithFollowUp");
        EventRouter instance = new EventRouter();
        //instance.addWithFollowUp(null);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of register method, of class EventRouter.
     */
//    @Test
    public void testRegister() {
        System.out.println("register");
        EventHandler eh = null;
        EventType et = null;
        EventRouter instance = new EventRouter();
//        instance.register(eh, et);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shutdown method, of class EventRouter.
     */
//    @Test
    public void testShutdown() {
        System.out.println("shutdown");
        EventRouter instance = new EventRouter();
        instance.shutdown();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    static class MockPacketEventHandler implements EventHandler<PacketContext> {

        static final ReceivingStrategyDispatcher rsd;

        static {
            PacketDecoder[] pds = new PacketDecoder[128];
            Arrays.fill(pds, new UnrecognizedPacketHandler());
            rsd = new ReceivingStrategyDispatcher(pds);
        }

        @Override
        public void onEvent(EventWrapper<PacketContext> e) {
            PacketContext context = e.getContext();

        }

        private static class UnrecognizedPacketHandler implements PacketDecoder {

            public UnrecognizedPacketHandler() {
            }

            @Override
            public void processPacket(PacketContext pc) {
            }
        }

    }

}
