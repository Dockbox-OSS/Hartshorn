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

package org.dockbox.selene.integrated.tools;

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.annotations.i18n.Resources;
import org.dockbox.selene.core.events.player.interact.PlayerInteractEvent;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.entry.Resource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.RemovableKey;
import org.dockbox.selene.core.objects.keys.TransactionResult;
import org.dockbox.selene.core.objects.keys.data.StringPersistentDataKey;
import org.dockbox.selene.core.objects.player.Sneaking;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("rawtypes")
@Extension(id = "toolbinding", name = "Tool Binding",
           description = "Adds the ability to bind commands to tools and items",
           authors = "GuusLieben", uniqueId = "287292f6-05f1-46a1-815b-b180f1488854")
@Resources(responsibleExtension = ToolBindingExtension.class)
public class ToolBindingExtension {

    private static ToolBindingExtension instance;

    public static final RemovableKey<Item, ItemTool> TOOL = SeleneUtils.checkedDynamicKeyOf(
            // Not possible to use method references here due to instance being initialized later
            (item, tool) -> instance.setTool(item, tool),
            item -> instance.getTool(item),
            item -> instance.removeTool(item)
    );

    private static final PersistentDataKey<String> PERSISTENT_TOOL =
            StringPersistentDataKey.of("Tool Binding", ToolBindingExtension.class);

    private static final ResourceEntry TOOL_ERROR_BLOCK = new Resource("Tool cannot be bound to blocks", "toolbinding.error.block");
    private static final ResourceEntry TOOL_ERROR_HAND = new Resource("Tool cannot be bound to hand", "toolbinding.error.hand");
    private static final ResourceEntry TOOL_ERROR_DUPLICATE = new Resource("There is already a tool bound to this item", "toolbinding.error.duplicate");

    private final Map<String, ItemTool> registry = SeleneUtils.emptyConcurrentMap();

    public ToolBindingExtension() {
        instance = this;
    }

    private TransactionResult setTool(Item<?> item, ItemTool tool) {
        if (item.isBlock()) return TransactionResult.fail(TOOL_ERROR_BLOCK);
        if (item == Item.AIR) return TransactionResult.fail(TOOL_ERROR_HAND);
        if (item.get(PERSISTENT_TOOL).isPresent())
            return TransactionResult.fail(TOOL_ERROR_DUPLICATE);

        String bindingId = UUID.randomUUID().toString();

        TransactionResult result = item.set(PERSISTENT_TOOL, bindingId);
        if (result.isSuccessfull()) {
            this.registry.put(bindingId, tool);
            tool.prepare(item);
        }

        return result;
    }

    private Exceptional<ItemTool> getTool(Item item) {
        Exceptional<String> identifier = item.get(PERSISTENT_TOOL);
        if (identifier.isAbsent()) return Exceptional.empty();

        String registryIdentifier = identifier.get();
        if (!this.registry.containsKey(registryIdentifier)) return Exceptional.empty();
        ItemTool itemTool = this.registry.get(registryIdentifier);

        return Exceptional.ofNullable(itemTool);
    }

    private void removeTool(Item item) {
        Exceptional<String> identifier = item.get(PERSISTENT_TOOL);
        if (identifier.isAbsent()) return;

        Exceptional<ItemTool> tool = this.getTool(item);
        if (tool.isAbsent()) return;

        item.remove(PERSISTENT_TOOL);
        this.registry.remove(identifier.get());
        tool.get().reset(item);
    }

    @Listener
    public void onPlayerInteracted(PlayerInteractEvent event) {
        Item<?> itemInHand = event.getTarget().getItemInHand(event.getHand());
        if (itemInHand == Item.AIR || itemInHand.isBlock()) return;

        Exceptional<String> identifier = itemInHand.get(PERSISTENT_TOOL);
        if (identifier.isAbsent()) return;
        if (!this.registry.containsKey(identifier.get())) return;
        ItemTool tool = this.registry.get(identifier.get());

        ToolInteractionEvent toolInteractionEvent = new ToolInteractionEvent(
                event.getTarget(),
                itemInHand,
                tool,
                event.getHand(),
                event.getClientClickType(),
                event.getTarget().isSneaking() ? Sneaking.SNEAKING : Sneaking.STANDING);

        if (tool.accepts(toolInteractionEvent)) {
            toolInteractionEvent.post();
            if (toolInteractionEvent.isCancelled()) return;
            tool.perform(event.getTarget(), itemInHand);
            event.setCancelled(true); // To prevent block/entity damage
        }
    }

}
