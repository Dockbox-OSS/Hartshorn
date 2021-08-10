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

import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.StoredPersistentKey;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import lombok.Getter;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Getter
public class PersistentItemModelImpl implements PersistentItemModel {

    private String id;
    private Text title;
    private List<Text> lore;
    private int amount;
    private List<Enchant> enchantments;
    private Map<String, Object> persistentData;

    public PersistentItemModelImpl(final Item item) {
        this.id = item.id();
        this.title = item.displayName();
        this.lore = item.lore();
        this.amount = item.amount();
        this.enchantments = new ArrayList<>(item.enchantments());
        this.persistentData = HartshornUtils.emptyMap();
        for (final Entry<PersistentDataKey<?>, Object> persistentEntry : item.data().entrySet()) {
            this.persistentData.put(persistentEntry.getKey().id(), persistentEntry.getValue());
        }
    }

    @Override
    public Class<? extends Item> type() {
        return Item.class;
    }

    @Override
    public Item restore() {
        return this.repopulate(Item.of(this.id));
    }

    protected Item repopulate(final Item item) {
        item.displayName(this.title());
        item.lore(this.lore());
        item.amount(this.amount());
        for (final Enchant enchantment : this.enchantments())
            item.addEnchant(enchantment);
        for (final Entry<String, Object> persistentEntry : this.persistentData.entrySet())
            item.set(StoredPersistentKey.of(persistentEntry.getKey()), persistentEntry.getValue());
        return item;
    }
}