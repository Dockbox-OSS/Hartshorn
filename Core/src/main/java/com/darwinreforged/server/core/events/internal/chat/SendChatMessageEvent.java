package com.darwinreforged.server.core.events.internal.chat;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.util.CancellableEvent;

/**
 The type Send chat message event.
 */
public class SendChatMessageEvent extends CancellableEvent {

    private String message;
    private final boolean isGlobalChat;

    /**
     Instantiates a new Send chat message event.

     @param target
     the target
     @param message
     the message
     @param isGlobalChat
     the is global chat
     */
    public SendChatMessageEvent(Target target, String message, boolean isGlobalChat) {
        super(target);
        this.message = message;
        this.isGlobalChat = isGlobalChat;
    }

    /**
     Gets message.

     @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     Sets message.

     @param message
     the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     Is global chat boolean.

     @return the boolean
     */
    public boolean isGlobalChat() {
        return isGlobalChat;
    }
}
