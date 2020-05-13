package com.darwinreforged.server.core.commands;

/**
 The type Parse result.
 */
public class ParseResult {

    private final String message;
    private final boolean success;

    /**
     Instantiates a new Parse result.

     @param message
     the message
     @param success
     the success
     */
    public ParseResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     Gets message.

     @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     Is success boolean.

     @return the boolean
     */
    public boolean isSuccess() {
        return success;
    }
}
