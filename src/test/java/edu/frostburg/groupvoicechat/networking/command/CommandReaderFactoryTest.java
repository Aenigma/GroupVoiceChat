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
package edu.frostburg.groupvoicechat.networking.command;

import edu.frostburg.groupvoicechat.networking.command.CommandReaderFactory;
import java.util.Arrays;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Kevin Raoofi
 */
public class CommandReaderFactoryTest {

    private final CommandReaderFactory crf;

    public CommandReaderFactoryTest() {
        crf = new CommandReaderFactory(Arrays.asList("command", "TeStCmD"));
    }

    /**
     * Test of create method, of class CommandReaderFactory.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        String s = "command foo bar baz";
        crf.create(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNotInSet() {
        System.out.println("create_not_in_set");
        String s = "CMD foo bar baz";
        crf.create(s);
    }

    /**
     * Test of contains method, of class CommandReaderFactory.
     */
    @Test
    public void testContains_String() {
        assertTrue("", crf.contains("testCMD"));
        assertTrue("", !crf.contains("asdasd"));
    }

}
