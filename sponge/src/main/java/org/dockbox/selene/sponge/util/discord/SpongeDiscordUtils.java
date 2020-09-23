/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.util.discord;

import com.magitechserver.magibridge.MagiBridge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.selene.core.util.discord.DiscordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class SpongeDiscordUtils extends DiscordUtils {

    @NotNull
    @Override
    public Optional<JDA> getJDA() {
        return Optional.ofNullable(MagiBridge.getInstance().getJDA());
    }

    @NotNull
    @Override
    public Optional<TextChannel> getGlobalTextChannel() {
        String channelId = MagiBridge.getInstance().getConfig().CHANNELS.MAIN_CHANNEL;
        return this.getJDA().map(jda -> jda.getTextChannelById(channelId));
    }


}
