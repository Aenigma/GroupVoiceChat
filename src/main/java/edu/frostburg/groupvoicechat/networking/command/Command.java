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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kevin Raoofi
 */
public final class Command {

    static final Pattern p = Pattern.compile("^(\\w+)\\s*(.*)$");

    private final String name;
    private final String arguments;

    /**
     * Creates using line of input representing a command
     *
     * @param rawValue String containing the command and its arguments in one
     * string
     */
    public Command(String rawValue) {
        Matcher m = p.matcher(rawValue);
        m.matches();
        this.name = m.group(1);
        this.arguments = m.group(2);
    }

    /**
     * Creates using line of input representing a command*
     *
     * @param ba
     */
    public Command(byte[] ba) {
        this(new String(ba));
    }

    /**
     * Creates using line of input representing a command
     *
     * @param ba byte array
     * @param from starting pointer of array
     * @param to ending pointer of array
     */
    public Command(byte[] ba, int from, int to) {
        this(new String(ba, from, to - from));
    }

    /**
     * Gets the name of the command
     *
     * @return name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a string with all the arguments for the command
     *
     * @return arguments string
     */
    public String getArguments() {
        return arguments;
    }

    /**
     * Creates a new CommanReader instance to process the arguments of this
     * command
     *
     * @return CommanReader instance to process the arguments of this command
     */
    public CommandReader newReader() {
        return new CommandReader(this);
    }

    @Override
    public String toString() {
        return "Command{" + "name=" + name + ", arguments=" + arguments + '}';
    }

}
