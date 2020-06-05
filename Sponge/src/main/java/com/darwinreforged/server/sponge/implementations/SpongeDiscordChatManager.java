package com.darwinreforged.server.sponge.implementations;

import com.darwinreforged.server.core.chat.DiscordChatManager;
import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.api.JDA;

public class SpongeDiscordChatManager implements DiscordChatManager {

    @Override
    public JDA getJda() {
        return MagiBridge.getInstance().getJDA();
    }
}
