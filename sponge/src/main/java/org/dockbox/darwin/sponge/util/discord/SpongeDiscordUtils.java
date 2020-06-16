package org.dockbox.darwin.sponge.util.discord;

import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.api.JDA;

import org.dockbox.darwin.core.util.discord.DiscordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpongeDiscordUtils implements DiscordUtils {

    @NotNull
    @Override
    public Optional<JDA> getJDA() {
        return Optional.ofNullable(MagiBridge.getInstance().getJDA());
    }
}
