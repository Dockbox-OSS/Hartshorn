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

package org.dockbox.hartshorn.palswap;

import org.dockbox.hartshorn.api.HartshornInformation;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandParameter;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Service(disabled = true)
public class BlockRegistryTestService {

    private static BlockRegistryTestService instance;

    public Logger logger;

    public BlockRegistryTestService() {
        instance = this;
    }

    @Command(value = "blockid", arguments = "<id>", permission = HartshornInformation.GLOBAL_BYPASS)
    public void blockID(@NotNull Player player, CommandContext context) {
        Exceptional<CommandParameter<String>> eID = context.argument("id");

        if (eID.absent()) return;

        String id = eID.get().getValue();

        Item item = Item.of(id);
        this.logger.info(item.getDisplayName().toStringValue());

        player.getInventory().give(Item.of(id));
    }

    @Command(value = "blockmeta", arguments = "<id> <meta{Integer}>", permission = HartshornInformation.GLOBAL_BYPASS)
    public void blockMeta(@NotNull Player player, CommandContext context) {

        String id = context.get("id");
        int meta = context.get("meta");

        Item item = Item.of(id, meta);
        this.logger.info("Is block: " + item.isBlock());
        this.logger.info(item.getDisplayName(Language.EN_US).toStringValue());
        this.logger.info(item.getId());

        player.getInventory().give(Item.of(id));
    }
}
