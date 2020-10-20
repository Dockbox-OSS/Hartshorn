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

package org.dockbox.selene.core.impl.objects.keys;

import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.Key;
import org.dockbox.selene.core.text.Text;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public final class ItemKeys<R> extends Key<Item, R> {

    public static final ItemKeys<Text> DISPLAY_NAME = new ItemKeys<>(Item::setDisplayName, Item::getDisplayName);
    public static final ItemKeys<Integer> AMOUNT = new ItemKeys<>(Item::setAmount, Item::getAmount);

    private ItemKeys(BiConsumer<Item, R> setter, Function<Item, R> getter) {
        super(setter, getter);
    }

}
