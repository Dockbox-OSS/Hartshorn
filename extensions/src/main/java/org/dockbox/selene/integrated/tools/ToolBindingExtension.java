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

import com.google.inject.Inject;

import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.impl.objects.keys.GenericKey;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.Key;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.data.StringPersistentDataKey;

@Extension(id = "toolbinding", name = "Tool Binding",
           description = "Adds the ability to bind commands to tools and items",
           authors = "GuusLieben", uniqueId = "287292f6-05f1-46a1-815b-b180f1488854")
public class ToolBindingExtension {

    // TODO GuusLieben, implementation of external key registration
    @SuppressWarnings("rawtypes")
    public static final Key<Item, ItemTool> TOOL = new GenericKey<>(
            ToolBindingExtension::applyToolKey,
            ToolBindingExtension::obtainToolKey
    );

    @Inject
    private Extension extension;

    private final PersistentDataKey<String> PERSISTENT_TOOL =
            StringPersistentDataKey.of("toolbinding", this.extension);

    public static void applyToolKey(Item<?> item, ItemTool tool) {
        if (item.isBlock()) return;
        // TODO
    }

    public static ItemTool obtainToolKey(Item<?> item) {
        // TODO
        return new ItemTool();
    }

}
