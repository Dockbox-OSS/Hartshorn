package com.darwinreforged.server.core.types.living;

import com.darwinreforged.server.core.resources.Permissions;
import com.darwinreforged.server.core.resources.Translations;
import com.darwinreforged.server.core.types.chat.Text;

public interface MessageReceiver {

    void sendMessage(String message);

    void sendMessage(Translations translation);

    void sendMessage(Text text);

    void sendMessage(String message, String permission);

    void sendMessage(String message, Permissions permission);

    void sendMessage(Translations translation, String permission);

    void sendMessage(Translations translation, Permissions permission);

    void sendMessage(Text text, String permission);

    void sendMessage(Text text, Permissions permission);

}
