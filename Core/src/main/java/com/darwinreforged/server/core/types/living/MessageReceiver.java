package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.chat.Text;

/**
 The interface Message receiver.
 */
public interface MessageReceiver {

    /**
     Send message.

     @param message
     the message
     */
    void sendMessage(String message);

    /**
     Send message.

     @param translation
     the translation
     */
    void sendMessage(Translations translation);

    /**
     Send message.

     @param text
     the text
     */
    void sendMessage(Text text);

    /**
     Send message.

     @param message
     the message
     @param permission
     the permission
     */
    void sendMessage(String message, String permission);

    /**
     Send message.

     @param message
     the message
     @param permission
     the permission
     */
    void sendMessage(String message, Permissions permission);

    /**
     Send message.

     @param translation
     the translation
     @param permission
     the permission
     */
    void sendMessage(Translations translation, String permission);

    /**
     Send message.

     @param translation
     the translation
     @param permission
     the permission
     */
    void sendMessage(Translations translation, Permissions permission);

    /**
     Send message.

     @param text
     the text
     @param permission
     the permission
     */
    void sendMessage(Text text, String permission);

    /**
     Send message.

     @param text
     the text
     @param permission
     the permission
     */
    void sendMessage(Text text, Permissions permission);

}
