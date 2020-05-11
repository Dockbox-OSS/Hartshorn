package com.darwinreforged.server.core.events.internal.chat;

import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.events.CancellableEvent;

public class SendChatMessageEvent extends CancellableEvent {

    private String message;
    private final boolean isGlobalChat;

    public SendChatMessageEvent(Target target, String message, boolean isGlobalChat) {
        super(target);
        this.message = message;
        this.isGlobalChat = isGlobalChat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isGlobalChat() {
        return isGlobalChat;
    }
}
