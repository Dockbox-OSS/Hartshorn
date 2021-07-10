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

package org.dockbox.hartshorn.server.minecraft.item;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.KeyHolder;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.persistence.PersistentCapable;
import org.dockbox.hartshorn.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.jetbrains.annotations.NonNls;

import java.util.List;
import java.util.Set;

public interface Item extends KeyHolder<Item>, PersistentDataHolder, PersistentCapable<PersistentItemModel> {

    /**
     * @param id
     *         The fully qualified identifier of a block, e.g. {@code minecraft:stone}
     *
     * @return The item instance, or {@link MinecraftItems#getAir()}
     */
    static Item of(@NonNls String id) {
        Item item = Hartshorn.context().get(Item.class, id);
        if (!MinecraftItems.getInstance().getAirId().equals(id) && item.isAir()) {
            item = MinecraftItems.getInstance().getCustom(id);
        }
        return item;
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

    Set<Enchant> getEnchantments();

    void addEnchant(Enchant enchant);

    void removeEnchant(Enchant enchant);

    boolean isBlock();

    boolean isHead();

    Item setProfile(Profile profile);

    Item stack();

    Exceptional<String> category();
}
