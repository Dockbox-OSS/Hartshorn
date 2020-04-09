package com.darwinreforged.server.core.commands.command;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;


public class Input {

    private final String rawInput;
    private final List<String> args;
    private int pos = -1;

    public Input(String input) {
        this.args = parse(input, true);
        this.rawInput = input;
    }

    private Input(String raw, List<String> args) {
        this.rawInput = raw;
        this.args = args;
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public boolean hasNext() {
        return pos + 1 < args.size();
    }

    public String current() {
        return args.get(Math.min(args.size() - 1, Math.max(0, pos)));
    }

    public String peek() {
        return args.get(pos + 1);
    }

    public String next() throws CommandException {
        if (!hasNext()) {
            throw new CommandException("Not enough args provided: '%s'", rawInput);
        }
        return args.get(++pos);
    }

    public Input trimFirstToken() {
        int index = rawInput.indexOf(' ');
        if (index == -1) {
            return new Input("", Collections.emptyList());
        }

        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (int i = 1; i < args.size(); i++) {
            builder.add(args.get(i));
        }

        return new Input(rawInput.substring(index + 1), builder.build());
    }

    public Input slice(int start) {
        return slice(start, rawInput.length());
    }

    public Input slice(int start, int end) {
        String s = rawInput.substring(start, end);
        return new Input(s);
    }

    public Input replace(String replacement) {
        String find = hasNext() ? peek() : current();
        int cursor = getCursor();
        String start = rawInput.substring(0, cursor);
        String end = rawInput.substring(cursor).replace(find, replacement);
        Input input = new Input(start + end);
        input.setPos(pos);
        return input;
    }

    public int getPos() {
        return pos;
    }

    public int getCursor() {
        if (hasNext()) {
            String next = peek();
            int index = 0;

            for (int i = 0; i < pos + 1; i++) {
                index += args.get(i).length();
                if (i > 0) {
                    index++; // space
                }
            }

            return rawInput.indexOf(next, index);
        }

        return 0;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Input last() {
        pos = args.size() - 2;
        return this;
    }

    public Input reset() {
        pos = -1;
        return this;
    }

    public String getRawInput() {
        return rawInput;
    }

    private static List<String> parse(String args, boolean quotes) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        outer:
        for (int i = 0, end = args.length(); i < end; i++) {
            // skip whitespace
            while (i < end && args.charAt(i) == ' ') {
                if (++i == end) {
                    break outer;
                }
            }

            char c = args.charAt(i);

            // if quoted read until next ' or "
            char stop = (quotes && (c == '\'' || c == '"')) ? c : ' ';

            // if quoted, move forward one position to start reading the inside string
            int from = stop == ' ' ? i : i + 1;
            i = from;

            while (i < end && args.charAt(i) != stop) {
                i++;
            }

            String s = args.substring(from, i);
            builder.add(s);
        }

        if (args.endsWith(" ")) {
            builder.add("");
        }

        return builder.build();
    }
}
