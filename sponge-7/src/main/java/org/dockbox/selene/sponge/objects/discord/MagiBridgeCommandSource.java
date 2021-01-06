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

package org.dockbox.selene.sponge.objects.discord;

import com.magitechserver.magibridge.util.BridgeCommandSource;

import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.command.source.DiscordCommandSource;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.pagination.Pagination;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

public class MagiBridgeCommandSource extends DiscordCommandSource {

    private final BridgeCommandSource bridge;

    public MagiBridgeCommandSource(BridgeCommandSource bridge) {this.bridge = bridge;}

    @Override
    public void execute(@NotNull String command) {
        Sponge.getCommandManager().process(this.bridge, command);
    }

    @Override
    public void send(@NotNull ResourceEntry text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)));
    }

    @Override
    public void send(@NotNull Text text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void sendWithPrefix(@NotNull ResourceEntry text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(Text.of(text)));
    }

    @Override
    public void sendWithPrefix(@NotNull Text text) {
        this.bridge.sendMessage(SpongeConversionUtil.toSponge(text));
    }

    @Override
    public void sendPagination(@NotNull Pagination pagination) {
        // TODO
    }


}
