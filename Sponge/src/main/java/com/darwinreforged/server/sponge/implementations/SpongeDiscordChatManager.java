package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.chat.DiscordChatManager;
import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.api.JDA;

public class SpongeDiscordChatManager extends DiscordChatManager {

    @Override
    protected JDA getJda() {
        return MagiBridge.jda;
    }
}
