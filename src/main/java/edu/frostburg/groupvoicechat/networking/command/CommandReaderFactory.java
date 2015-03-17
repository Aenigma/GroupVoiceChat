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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles case insensitive checking for command names.
 *
 * @author Kevin Raoofi
 */
public class CommandReaderFactory {

    private final Set<String> cmdSet;

    /**
     * Creates a command reader factory with the given set
     *
     * @param cs collection of strings allowed for command names
     */
    public CommandReaderFactory(final Collection<String> cs) {
        this.cmdSet = new HashSet<>(cs.size());
        for (String s : cs) {
            cmdSet.add(s.toUpperCase());
        }
    }

    /**
     * Constructs a factory which allows any command string to be accepted.
     */
    CommandReaderFactory() {
        this.cmdSet = null;
    }

    /**
     * Creates a command reader based on the given string.
     *
     * @param s string to read command from
     * @return command reader to read the command represented in the string
     * @throws IllegalArgumentException {@link #contains(java.lang.String)}
     * returns false
     */
    public CommandReader create(String s) throws IllegalArgumentException {
        Command c = new Command(s.toUpperCase());
        if (!contains(c)) {
            throw new IllegalArgumentException("Command name not in set!");
        }
        return new CommandReader(c);
    }

    /**
     * Determines whether or not the command is in the set
     *
     * @param s string to check for
     * @return true if in set, false otherwise
     */
    public boolean contains(String s) {
        if (cmdSet == null) {
            return true;
        }

        return cmdSet.contains(s.toUpperCase());
    }

    /**
     * Determines whether or not the command is in the set
     *
     * @param c command with command name to check for
     * @return true if in set, false otherwise
     */
    public boolean contains(Command c) {
        return contains(c.getName());
    }

    /**
     * Returns the set of allowed command names
     *
     * @return the command set
     */
    public Set<String> getCmdSet() {
        return cmdSet;
    }

}
