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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.keys.Keys;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.RemovableKey;
import org.dockbox.hartshorn.api.keys.TransactionResult;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerInteractEvent;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Sneaking;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

@Service
@Posting(ToolInteractionEvent.class)
public class ToolBinding {

    static final PersistentDataKey<String> PERSISTENT_TOOL = Keys.persistent(String.class, "Tool Binding", ToolBinding.class);
    public static final RemovableKey<Item, ItemTool> TOOL = Keys.builder(Item.class, ItemTool.class)
            .withSetter((item, tool) -> Hartshorn.context().get(ToolBinding.class).tool(item, tool))
            .withGetterSafe(item -> Hartshorn.context().get(ToolBinding.class).get(item))
            .withRemover(item -> Hartshorn.context().get(ToolBinding.class).removeTool(item))
            .build();
    private final Map<String, ItemTool> registry = HartshornUtils.emptyConcurrentMap();

    @Inject
    private ToolBindingResources resources;

    private TransactionResult tool(final Item item, final ItemTool tool) {
        if (item.isBlock()) return TransactionResult.fail(this.resources.blockError());
        if (item.isAir()) return TransactionResult.fail(this.resources.handError());
        if (item.get(PERSISTENT_TOOL).present()) return TransactionResult.fail(this.resources.duplicateError());

        final String bindingId = UUID.randomUUID().toString();

        final TransactionResult result = item.set(PERSISTENT_TOOL, bindingId);
        if (result.successful()) {
            this.registry.put(bindingId, tool);
            tool.prepare(item);
        }

        return result;
    }

    private void removeTool(final Item item) {
        final Exceptional<String> identifier = item.get(PERSISTENT_TOOL);
        if (identifier.absent()) return;

        final Exceptional<ItemTool> tool = this.get(item);
        if (tool.absent()) return;

        item.remove(PERSISTENT_TOOL);
        this.registry.remove(identifier.get());
        ItemTool.reset(item);
    }

    private Exceptional<ItemTool> get(final Item item) {
        final Exceptional<String> identifier = item.get(PERSISTENT_TOOL);
        if (identifier.absent()) return Exceptional.empty();

        final String registryIdentifier = identifier.get();
        if (!this.registry.containsKey(registryIdentifier)) return Exceptional.empty();
        final ItemTool itemTool = this.registry.get(registryIdentifier);

        return Exceptional.of(itemTool);
    }

    @Listener
    public void on(final PlayerInteractEvent<?> event) {
        final Item itemInHand = event.subject().itemInHand(event.hand());
        if (itemInHand.isAir() || itemInHand.isBlock()) return;

        final Exceptional<String> identifier = itemInHand.get(PERSISTENT_TOOL);
        if (identifier.absent()) return;
        if (!this.registry.containsKey(identifier.get())) return;
        final ItemTool tool = this.registry.get(identifier.get());

        final ToolInteractionEvent toolInteractionEvent = new ToolInteractionEvent(
                event.subject(),
                itemInHand,
                tool,
                event.hand(),
                event.clickType(),
                event.subject().sneaking() ? Sneaking.SNEAKING : Sneaking.STANDING,
                event.target());

        final ToolInteractionContext context = new ToolInteractionContext(toolInteractionEvent);

        if (tool.accepts(context)) {
            toolInteractionEvent.post();
            if (toolInteractionEvent.cancelled()) return;
            tool.perform(context);
            event.cancelled(true); // To prevent block/entity damage
        }
    }
}
