package com.darwinreforged.server.core.util.commands.command;


public class CommandException extends Exception implements Comparable<CommandException> {

    private int priority;
    private String args;

    public CommandException(String message, Object... args) {
        super(String.format(message, args));
        this.priority = 0;
    }

    public CommandException args(String usage) {
        this.args = usage;
        return this;
    }

    public CommandException priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String getMessage() {
        if (args == null) {
            return super.getMessage();
        }
        return String.format("%s. Expected args: %s", super.getMessage(), args);
    }

    @Override
    public int compareTo(CommandException e) {
        return Integer.compare(priority, e.priority);
    }
}
