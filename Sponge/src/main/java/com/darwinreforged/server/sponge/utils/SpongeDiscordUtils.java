package com.darwinreforged.server.sponge.utils;

import com.darwinreforged.server.core.init.UtilityImplementation;
import com.darwinreforged.server.core.util.DiscordUtils;
import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.api.JDA;

@UtilityImplementation(DiscordUtils.class)
public class SpongeDiscordUtils extends DiscordUtils {

    @Override
    protected JDA getJda() {
        return (JDA) MagiBridge.jda;
    }
}
