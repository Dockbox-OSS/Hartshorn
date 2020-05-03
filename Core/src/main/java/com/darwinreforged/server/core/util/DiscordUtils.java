package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.entities.DiscordEmbed;
import com.darwinreforged.server.core.init.AbstractUtility;

@AbstractUtility("Discord connection and messaging utilities")
public abstract class DiscordUtils {

    public abstract void sendToChannel(String message, String channel);

    public abstract void sendToChannel(String message, long channel);

    public abstract void sendToChannel(DiscordEmbed embed, String channel);

    public abstract void sendToChannel(DiscordEmbed embed, long channel);
}
