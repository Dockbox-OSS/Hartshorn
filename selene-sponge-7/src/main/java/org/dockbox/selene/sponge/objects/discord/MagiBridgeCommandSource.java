/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.objects.discord;

import com.magitechserver.magibridge.util.BridgeCommandSource;

import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.i18n.text.pagination.Pagination;
import org.dockbox.selene.commands.source.DiscordCommandSource;
import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

public class MagiBridgeCommandSource implements DiscordCommandSource {

    private final BridgeCommandSource bridge;

    @AutoWired
    public MagiBridgeCommandSource(TextChannel channel) {
        this.bridge = new BridgeCommandSource(channel.getId(), Sponge.getServer().getConsole());
    }

    public MagiBridgeCommandSource(BridgeCommandSource bridge) {
        this.bridge = bridge;
    }

    @Override
    public void execute(@NotNull String command) {
        Sponge.getCommandManager().process(this.bridge, command);
    }

    @Override
    public void send(@NotNull ResourceEntry text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(text.asText()));
    }

    @Override
    public void send(@NotNull Text text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(text.asText()));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void send(@NotNull Pagination pagination) {
        throw new UnsupportedOperationException("Pagination not supported for virtual command source");
    }
}
