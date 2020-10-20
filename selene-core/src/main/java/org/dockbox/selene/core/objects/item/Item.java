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

package org.dockbox.selene.core.objects.item;

import org.dockbox.selene.core.objects.ReferenceHolder;
import org.dockbox.selene.core.objects.keys.KeyHolder;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.construct.ConstructionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public abstract class Item<T> extends ReferenceHolder<T> implements KeyHolder<Item> {

    protected Item(@NotNull T reference) {
        super(Optional.of(reference));
    }

    protected Item(String id, int amount) {
        super(Optional.empty());
        T type = this.getById(id, amount);
        super.setReference(Optional.of(type));
    }

    protected abstract T getById(String id, int amount);

    public abstract Text getDisplayName();

    public abstract List<Text> getLore();

    public abstract int getAmount();

    public abstract void setDisplayName(Text displayName);

    public abstract void setLore(List<Text> lore);

    public abstract void addLore(Text lore);

    public abstract void setAmount(int amount);

    public static Item<?> of(String id, int amount) {
        return Selene.getInstance(ConstructionUtil.class).item(id, amount);
    }

    public static Item<?> of(String id) {
        return Selene.getInstance(ConstructionUtil.class).item(id);
    }

}
