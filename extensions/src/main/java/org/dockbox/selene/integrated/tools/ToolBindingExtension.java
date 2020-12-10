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
import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.annotations.event.Listener;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.events.player.interact.PlayerInteractEvent;
import org.dockbox.selene.core.impl.objects.keys.GenericKey;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.Key;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.TransactionResult;
import org.dockbox.selene.core.objects.keys.data.StringPersistentDataKey;
import org.dockbox.selene.core.objects.player.ClickType;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.player.Sneaking;
import org.dockbox.selene.core.text.Text;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("rawtypes")
@Extension(id = "toolbinding", name = "Tool Binding",
           description = "Adds the ability to bind commands to tools and items",
           authors = "GuusLieben", uniqueId = "287292f6-05f1-46a1-815b-b180f1488854")
public class ToolBindingExtension {

    private static ToolBindingExtension instance;

    public static final Key<Item, ItemTool> TOOL = GenericKey.ofChecked(
            // Not possible to use method references here due to instance being initialized later
            (item, tool) -> instance.applyToolKey(item, tool),
            item -> instance.obtainToolKey(item)
    );
    private static final PersistentDataKey<String> PERSISTENT_TOOL =
            StringPersistentDataKey.of("toolbinding", ToolBindingExtension.class);

    private final Map<String, ItemTool> registry = SeleneUtils.emptyConcurrentMap();

    public ToolBindingExtension() {
        instance = this;
    }

    public TransactionResult applyToolKey(Item<?> item, ItemTool tool) {
        if (item.isBlock()) return TransactionResult.fail("Tool cannot be bound to blocks");
        if (item == Item.AIR) return TransactionResult.fail("Tool cannot be bound to hand");
        if (item.get(PERSISTENT_TOOL).isPresent())
            return TransactionResult.fail("There is already a tool bound to this item");

        String bindingId = UUID.randomUUID().toString();

        TransactionResult result = item.set(PERSISTENT_TOOL, bindingId);
        if (result.isSuccessfull()) {
            this.registry.put(bindingId, tool);
            tool.prepare(item);
        }

        return result;
    }

    public Exceptional<ItemTool> obtainToolKey(Item item) {
        Exceptional<String> identifier = item.get(PERSISTENT_TOOL);
        if (identifier.isAbsent()) return Exceptional.empty();

        String registryIdentifier = identifier.get();
        if (!this.registry.containsKey(registryIdentifier)) return Exceptional.empty();
        ItemTool itemTool = this.registry.get(registryIdentifier);

        return Exceptional.ofNullable(itemTool);
    }

    }

    public static ItemTool obtainToolKey(Item<?> item) {
        // TODO
        return new ItemTool();
    }

}
