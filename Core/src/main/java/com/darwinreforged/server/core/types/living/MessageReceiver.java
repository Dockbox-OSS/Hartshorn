package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.chat.Text;
import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.translations.Translation;

/**
 The interface Message receiver.
 */
public interface MessageReceiver {

    /**
     Send message.

     @param message
     the message
     */
    void sendMessage(String message, boolean plain);

    /**
     Send message.

     @param translation
     the translation
     */
    void sendMessage(Translation translation, boolean plain);

    /**
     Send message.

     @param text
     the text
     */
    void sendMessage(Text text, boolean plain);

    /**
     Send message.

     @param message
     the message
     @param permission
     the permission
     */
    void sendMessage(String message, String permission, boolean plain);

    /**
     Send message.

     @param message
     the message
     @param permission
     the permission
     */
    void sendMessage(String message, Permissions permission, boolean plain);

    /**
     Send message.

     @param translation
     the translation
     @param permission
     the permission
     */
    void sendMessage(Translation translation, String permission, boolean plain);

    /**
     Send message.

     @param translation
     the translation
     @param permission
     the permission
     */
    void sendMessage(Translation translation, Permissions permission, boolean plain);

    /**
     Send message.

     @param text
     the text
     @param permission
     the permission
     */
    void sendMessage(Text text, String permission, boolean plain);

    /**
     Send message.

     @param text
     the text
     @param permission
     the permission
     */
    void sendMessage(Text text, Permissions permission, boolean plain);

}
