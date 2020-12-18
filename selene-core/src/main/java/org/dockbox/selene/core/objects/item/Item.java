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

import com.sk89q.worldedit.blocks.BaseBlock;

import org.dockbox.selene.core.ConstructionUtil;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.ReferenceHolder;
import org.dockbox.selene.core.objects.keys.KeyHolder;
import org.dockbox.selene.core.objects.keys.PersistentDataHolder;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class Item<T> extends ReferenceHolder<T> implements KeyHolder<Item>, PersistentDataHolder {

    public static Item<?> AIR = Item.of("minecraft:air");

    private String id;

    protected Item(@NotNull T reference) {
        super(Exceptional.of(reference));
        this.id = this.getId();
    }

    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected Item(String id, int meta) {
        super(Exceptional.empty());
        this.id = id;
        T type = this.getById(id, meta);
        super.setReference(Exceptional.of(type));
    }

    protected abstract T getById(String id, int meta);

    /**
     * @param id
     *         The fully qualified identifier of a block, e.g. {@code minecraft:stone}
     * @param meta
     *         The unsafe damage, or meta. Constraints to range 0-15
     *
     * @return The item instance, or {@link Item#AIR}
     *
     * @deprecated Note that the use of unsafe damage (meta) is deprecated, and should be avoided. As of 1.13 this will no
     *         longer be available!
     *         <p>
     *         See {@link ConstructionUtil#item(String, int)}
     */
    @Deprecated
    public static Item<?> of(String id, int meta) {
        return Selene.getInstance(ConstructionUtil.class).item(id, meta);
    }

    public static Item<?> of(String id) {
        return Selene.getInstance(ConstructionUtil.class).item(id);
    }

    /**
     * @param baseBlock
     *         The {@link BaseBlock} instance to use when creating the item.
     *
     * @return The item instance, or {@link Item#AIR}
     *
     * @deprecated Note that WorldEdit rewrote their API for 1.13+, and that package/class names changes.
     *         <p>
     *         See {@link ConstructionUtil#item(BaseBlock)}
     */
    @Deprecated
    public static Item<?> of(BaseBlock baseBlock) {
        return Selene.getInstance(ConstructionUtil.class).item(baseBlock);
    }

    public Text getDisplayName() {
        return this.getDisplayName(Selene.getServer().getGlobalConfig().getDefaultLanguage());
    }

    public abstract Text getDisplayName(Language language);

    public abstract void setDisplayName(Text displayName);

    public abstract List<Text> getLore();

    public abstract void setLore(List<Text> lore);

    public abstract int getAmount();

    public abstract void setAmount(int amount);

    public abstract void removeDisplayName();

    public abstract void addLore(Text lore);

    public abstract void removeLore();

    public Item stack() {
        this.setAmount(this.getStackSize());
        return this;
    }

    public abstract int getStackSize();

    public abstract List<Enchant> getEnchantments();

    public abstract void addEnchant(Enchant enchant);

    public abstract void removeEnchant(Enchant enchant);

    public abstract boolean isBlock();

}
