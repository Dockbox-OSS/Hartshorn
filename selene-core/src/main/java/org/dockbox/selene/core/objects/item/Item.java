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

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.objects.ReferenceHolder;
import org.dockbox.selene.core.objects.keys.KeyHolder;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.construct.ConstructionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class Item<T> extends ReferenceHolder<T> implements KeyHolder<Item> {

    private String id;

    protected Item(@NotNull T reference) {
        super(Exceptional.of(reference));
        this.id = this.getId();
    }

    protected Item(String id, int amount) {
        super(Exceptional.empty());
        this.id = id;
        T type = this.getById(id, amount);
        super.setReference(Exceptional.of(type));
    }

    protected abstract T getById(String id, int amount);

    public abstract Text getDisplayName(Language language);

    public Text getDisplayName() {
        return this.getDisplayName(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    public abstract List<Text> getLore();

    public abstract int getAmount();

    public abstract void setDisplayName(Text displayName);

    public abstract void setLore(List<Text> lore);

    public abstract void addLore(Text lore);

    public abstract void setAmount(int amount);

    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public Item stack() {
        this.setAmount(this.getStackSize());
        return this;
    }

    public abstract int getStackSize();

    public abstract List<Enchant> getEnchantments();

    public abstract void addEnchant(Enchant enchant);

    public abstract void removeEnchant(Enchant enchant);

    public static Item<?> of(String id, int amount) {
        return Selene.getInstance(ConstructionUtil.class).item(id, amount);
    }

    public static Item<?> of(String id) {
        return Selene.getInstance(ConstructionUtil.class).item(id);
    }

}
