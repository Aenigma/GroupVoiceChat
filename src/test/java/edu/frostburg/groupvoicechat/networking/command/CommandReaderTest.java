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

import edu.frostburg.groupvoicechat.networking.command.CommandReader;
import edu.frostburg.groupvoicechat.networking.command.Command;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Kevin Raoofi
 */
public class CommandReaderTest {

    private static final String[] cmdStrings = new String[]{
        "COMMAND foo bar baz",
        "COMMAND      foo\t bar    \t baz",
        "COMMAND         foo\tbar\tbaz",
        "COMMAND\tfoo     bar      baz"
    };

    private static final String[] ans = new String[]{
        "foo", "bar", "baz"
    };

    private final Command[] cmds;

    public CommandReaderTest() {
        cmds = new Command[cmdStrings.length];

        for (int i = 0; i < cmds.length; i++) {
            cmds[i] = new Command(cmdStrings[i]);
        }
    }

    /**
     * Test of nextArgument method, of class CommandReader.
     */
    @Test
    public void testNextArgument() {
        System.out.println("nextArgument");
        for (Command c : cmds) {
            CommandReader instance = c.newReader();
            for (String expResult : ans) {
                String result = instance.nextArgument();
                assertEquals(expResult, result);
            }
        }
    }

    /**
     * Test of getVarAll method, of class CommandReader.
     */
    @Test
    public void testGetVarAll() {
        System.out.println("getVarAll");

        CommandReader instance;
        String expResult;
        String result;

        instance = cmds[0].newReader();
        expResult = "foo bar baz";
        result = instance.getVarAll();
        assertEquals(expResult, result);

        instance = cmds[0].newReader();
        instance.nextArgument();
        expResult = "bar baz";
        result = instance.getVarAll();
        assertEquals(expResult, result);

        instance = cmds[0].newReader();
        instance.nextArgument();
        instance.nextArgument();
        expResult = "baz";
        result = instance.getVarAll();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasNext method, of class CommandReader.
     */
    @Test
    public void testHasNext() {
        System.out.println("hasNext");

        // check this doesn't make exceptions...
        for (Command c : cmds) {
            CommandReader instance = c.newReader();
            while (instance.hasNext()) {
                instance.nextArgument();
            }
        }

        for (Command c : cmds) {
            CommandReader instance = c.newReader();
            instance.getVarAll();
            assertTrue("CommandReader has input after #getVarAll()", !instance
                    .hasNext());
        }
    }
}
