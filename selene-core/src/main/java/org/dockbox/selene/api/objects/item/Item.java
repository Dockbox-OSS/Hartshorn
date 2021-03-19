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

package org.dockbox.selene.api.objects.item;

import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.objects.item.persistence.PersistentItemModel;
import org.dockbox.selene.api.objects.item.storage.MinecraftItems;
import org.dockbox.selene.api.objects.keys.KeyHolder;
import org.dockbox.selene.api.objects.keys.PersistentDataHolder;
import org.dockbox.selene.api.objects.persistence.PersistentCapable;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.jetbrains.annotations.NonNls;

import java.util.List;

public interface Item extends KeyHolder<Item>, PersistentDataHolder, PersistentCapable<PersistentItemModel> {

    /**
     * @param id
     *         The fully qualified identifier of a block, e.g. {@code minecraft:stone}
     *
     * @return The item instance, or {@link MinecraftItems#getAir()}
     */
    static Item of(@NonNls String id) {
        Item item = Item.of(id, 0);
        if (!Selene.getItems().getAirId().equals(id) && item.isAir()) {
            item = Selene.getItems().getCustom(id);
        }
        return item;
    }

    /**
     * @param id
     *         The fully qualified identifier of a block, e.g. {@code minecraft:stone}
     * @param meta
     *         The unsafe damage, or meta. Constraints to range 0-15
     *
     * @return The item instance, or {@link MinecraftItems#getAir()}
     * @deprecated Note that the use of unsafe damage (meta) is deprecated, and should be avoided. As
     *         of 1.13 this will no longer be available!
     */
    @Deprecated
    static Item of(String id, int meta) {
        return Selene.provide(ItemFactory.class).create(id, meta);
    }

    boolean isAir();

    String getId();

    Text getDisplayName();

    void setDisplayName(Text displayName);

    Text getDisplayName(Language language);

    List<Text> getLore();

    void setLore(List<Text> lore);

    int getAmount();

    void setAmount(int amount);

    void removeDisplayName();

    void addLore(Text lore);

    void removeLore();

    int getStackSize();

    List<Enchant> getEnchantments();

    void addEnchant(Enchant enchant);

    void removeEnchant(Enchant enchant);

    boolean isBlock();

    boolean isHead();

    Item setProfile(Profile profile);

    Item stack();

    Item withMeta(int meta);

    int getMeta();

    int getIdNumeric();
}
