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

package org.dockbox.selene.palswap;

import com.google.inject.Inject;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.command.context.CommandArgument;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.player.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Module(id = "blockregistry", name = "Block Registry",
        description = "Block Registry Testing",
        authors = "pumbas600")
public class BlockRegistryTestExtension {

    private static BlockRegistryTestExtension instance;

    @Inject
    public Logger logger;

    public BlockRegistryTestExtension() {
        instance = this;
    }

    @Command(aliases = "blockid", usage = "blockid <id>")
    public void blockID(@NotNull Player player, CommandContext context) {
        Exceptional<CommandArgument<String>> eID = context.argument("id");

        if (eID.isAbsent()) return;

        String id = eID.get().getValue();

        Item item = Item.of(id);
        this.logger.info(item.getDisplayName().toStringValue());

        player.getInventory().give(Item.of(id));
    }

    @Command(aliases = "blockmeta", usage = "blockmeta <id> <meta{Integer}>")
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
