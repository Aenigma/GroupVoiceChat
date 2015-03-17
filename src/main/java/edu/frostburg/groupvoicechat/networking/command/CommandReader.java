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

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Kevin Raoofi
 */
public class CommandReader {

    /** Pattern which greedy-matches an argument */
    private static final Pattern argPattern = Pattern.compile("\\s*\\w+\\s*");
    /** Pattern which matches any non-empty string */
    private static final Pattern allPattern = Pattern.compile(".+");

    /** The command this is reading */
    private final Command command;
    /** Scanner used to read from the command argument list */
    private final Scanner s;
    /** Increments with every number read */
    private int numRead;

    /**
     *
     * @param command command to read
     */
    public CommandReader(Command command) {
        this.command = command;
        this.s = new Scanner(this.command.getArguments());
        this.numRead = 0;
    }

    public String nextArgument() {
        numRead++;
        return s.findInLine(argPattern)
                .trim();
    }

    public String getVarAll() {
        return s.findInLine(allPattern)
                .trim();
    }

    public boolean hasNext() {
        return s.hasNext();
    }

    public String getCommandName() {
        return this.command.getName();
    }

    public Command getCommand() {
        return command;
    }

    public void reset() {
        this.s.reset();
    }

    @Override
    public String toString() {
        return "CommandReader{" + "command=" + command + ", s=" + s
                + ", numRead=" + numRead + '}';
    }

}
