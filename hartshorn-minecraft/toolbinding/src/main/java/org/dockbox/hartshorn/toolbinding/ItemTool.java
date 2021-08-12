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

package org.dockbox.hartshorn.toolbinding;

import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.ClickType;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Sneaking;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemTool {

    private final Text name;
    private final List<Text> lore;
    private final Consumer<ToolInteractionContext> consumer;
    private final List<Predicate<ToolInteractionContext>> filters;
    private final List<Consumer<Item>> modifiers;

    public ItemTool(
            final Text name,
            final List<Text> lore,
            final Consumer<ToolInteractionContext> consumer,
            final List<Predicate<ToolInteractionContext>> filters,
            final List<Consumer<Item>> modifiers
    ) {
        this.name = name;
        this.lore = lore;
        this.consumer = consumer;
        this.filters = filters;
        this.modifiers = modifiers;
    }

    public static void reset(final Item item) {
        item.removeDisplayName();
        item.removeLore();
    }

    public static ToolBuilder builder() {
        return new ToolBuilder();
    }

    public boolean accepts(final ToolInteractionContext context) {
        return this.filters.stream().allMatch(predicate -> predicate.test(context));
    }

    public void perform(final ToolInteractionContext context) {
        this.consumer.accept(context);
    }

    protected void prepare(final Item item) {
        if (null != this.name) item.displayName(this.name);
        if (null != this.lore) item.lore(this.lore);
        this.modifiers.forEach(modifiers -> modifiers.accept(item));
    }

    @SuppressWarnings("unused")
    public static final class ToolBuilder {
        private final List<Predicate<ToolInteractionContext>> filters = HartshornUtils.emptyConcurrentList();
        private final List<Consumer<Item>> modifiers = HartshornUtils.emptyConcurrentList();
        private Consumer<ToolInteractionContext> consumer;
        private Text name;
        private List<Text> lore;

        private ToolBuilder() {}

        public ToolBuilder perform(final Consumer<ToolInteractionContext> consumer) {
            this.consumer = consumer;
            return this;
        }

        public ToolBuilder resetFilters() {
            this.filters.clear();
            return this;
        }

        public ToolBuilder only(final Player player) {
            this.filters.add(e -> e.player().equals(player));
            return this;
        }

        public ToolBuilder only(final UUID playerId) {
            this.filters.add(e -> e.player().uniqueId().equals(playerId));
            return this;
        }

        public ToolBuilder only(@NonNls final String player) {
            this.filters.add(e -> e.player().name().equals(player));
            return this;
        }

        public ToolBuilder only(final Sneaking sneaking) {
            this.filters.add(e -> Sneaking.EITHER == sneaking || sneaking == e.sneaking());
            return this;
        }

        public ToolBuilder only(final ClickType clickType) {
            this.filters.add(e -> ClickType.EITHER == clickType || clickType == e.type());
            return this;
        }

        public ToolBuilder only(final Hand hand) {
            this.filters.add(e -> Hand.EITHER == hand || hand == e.hand());
            return this;
        }

        public ToolBuilder only(final Predicate<ToolInteractionContext> predicate) {
            this.filters.add(predicate);
            return this;
        }

        public ToolBuilder name(final Text name) {
            this.name = name;
            return this;
        }

        public ToolBuilder lore(final List<Text> lore) {
            this.lore = lore;
            return this;
        }

        public ToolBuilder modify(final Consumer<Item> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public ItemTool build() {
            return new ItemTool(this.name, this.lore, this.consumer, this.filters, this.modifiers);
        }
    }
}
