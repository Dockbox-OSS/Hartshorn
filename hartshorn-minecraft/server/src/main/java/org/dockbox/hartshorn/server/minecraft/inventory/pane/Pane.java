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

package org.dockbox.hartshorn.server.minecraft.inventory.pane;

import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.inventory.context.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.context.RenameContext;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a inventory pane. Depending on the implementation this may display any type of
 * inventory UI.
 */
public interface Pane {
    /**
     * Open the pane for a given player.
     *
     * @param player
     *         The player to show the pane to.
     */
    void open(Player player);

    void onClick(int index, Function<ClickContext, Boolean> onClick);

    void close(Player player);

    static Pane rename(final Consumer<RenameContext> callback) {
        final Item paper = ItemTypes.PAPER.item().displayName(Text.of("Enter name..."));
        return rename(paper, Text.of("&4Rename"), callback);
    }

    static Pane rename(final Item item, final Text title, final Consumer<RenameContext> callback) {
        return InventoryLayout.builder(InventoryType.ANVIL)
                .set(item, 0)
                .toStaticPaneBuilder()
                .lock(true)
                .title(title)
                .onClickOutput(context -> {
                    final Item out = context.item();
                    final RenameContext renameContext = new RenameContext(context, out.displayName());
                    callback.accept(renameContext);
                    context.close();
                    return false;
                }).build();
    }
}
