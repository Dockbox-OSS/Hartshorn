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

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.objects.item.storage.MinecraftItems;
import org.dockbox.selene.core.objects.keys.KeyHolder;
import org.dockbox.selene.core.objects.keys.PersistentDataHolder;
import org.dockbox.selene.core.objects.profile.Profile;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.List;

public interface Item extends KeyHolder<Item>, PersistentDataHolder {

    String getId();
    Text getDisplayName();
    Text getDisplayName(Language language);
    void setDisplayName(Text displayName);
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
    void setProfile(Profile profile);
    Item stack();
    @Deprecated
    Item withMeta(int meta);

    /**
     * @param id
     *         The fully qualified identifier of a block, e.g. {@code minecraft:stone}
     * @param meta
     *         The unsafe damage, or meta. Constraints to range 0-15
     *
     * @return The item instance, or {@link MinecraftItems#getAir()}
     *
     * @deprecated Note that the use of unsafe damage (meta) is deprecated, and should be avoided. As of 1.13 this will no
     *         longer be available!
     */
    @Deprecated
    static Item of(String id, int meta) {
        return SeleneUtils.INJECT.getInstance(ItemFactory.class).create(id, meta);
    }

    static Item of(String id) {
        return Item.of(id, 0);
    }

    /**
     * @param baseBlock
     *         The {@link BaseBlock} instance to use when creating the item.
     *
     * @return The item instance, or {@link MinecraftItems#getAir()}
     *
     * @deprecated Note that WorldEdit rewrote their API for 1.13+, and that package/class names changes.
     */
    @Deprecated
    static Item of(BaseBlock baseBlock) {
        return SeleneUtils.INJECT.getInstance(ItemFactory.class).create(baseBlock);
    }

}
