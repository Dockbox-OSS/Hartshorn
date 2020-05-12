package com.darwinreforged.server.core.commands;

public class ParseResult {

    private final String message;
    private final boolean success;

    public ParseResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
