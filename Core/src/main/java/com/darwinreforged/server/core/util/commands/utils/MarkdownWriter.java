package com.darwinreforged.server.core.util.commands.utils;

import com.darwinreforged.server.core.util.commands.command.CommandExecutor;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public class MarkdownWriter implements Closeable {

    private final Appendable appendable;

    public MarkdownWriter(Appendable appendable) {
        this.appendable = appendable;
    }

    public MarkdownWriter writeHeaders() {
        write("| Command | Permission | Description |").newLine();
        write("| :------ | :--------- | :---------- |");
        return this;
    }

    public MarkdownWriter writeCommand(CommandExecutor executor) {
        String command = ifEmpty(executor.getUsage().value(), "undefined");
        String permission = ifEmpty(executor.getPermission().value(), " ");
        String description = ifEmpty(executor.getDescription().value(), "undefined");
        return newLine().write(String.format("| `%s` | `%s` | %s |", command, permission, description));
    }

    private MarkdownWriter write(String string) {
        try {
            appendable.append(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private MarkdownWriter newLine() {
        write("\n");
        return this;
    }

    private String ifEmpty(String in, String other) {
        return in.isEmpty() ? other : in;
    }

    @Override
    public void close() throws IOException {
        if (appendable instanceof Flushable) {
            Flushable flushable = (Flushable) appendable;
            flushable.flush();
        }
        if (appendable instanceof Closeable) {
            Closeable closeable = (Closeable) appendable;
            closeable.close();
        }
    }
}
