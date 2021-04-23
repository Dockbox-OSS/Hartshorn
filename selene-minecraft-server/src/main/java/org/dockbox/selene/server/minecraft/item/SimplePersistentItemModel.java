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

package org.dockbox.selene.server.minecraft.item;

import org.dockbox.selene.api.entity.annotations.Extract;
import org.dockbox.selene.api.entity.annotations.Extract.Behavior;
import org.dockbox.selene.api.entity.annotations.Metadata;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.keys.PersistentDataKey;
import org.dockbox.selene.api.keys.StoredPersistentKey;
import org.dockbox.selene.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.selene.util.SeleneUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "item")
public class SimplePersistentItemModel extends PersistentItemModel {

    private String id;
    private int meta;
    private Text title;
    private List<Text> lore;
    private int amount;
    private List<Enchant> enchantments;
    private Map<String, Object> persistentData;

    public SimplePersistentItemModel(Item item) {
        this.id = item.getId();
        this.meta = item.getMeta();
        this.title = item.getDisplayName();
        this.lore = item.getLore();
        this.amount = item.getAmount();
        this.enchantments = new ArrayList<>(item.getEnchantments());
        this.persistentData = SeleneUtils.emptyMap();
        for (Entry<PersistentDataKey<?>, Object> persistentEntry : item.getPersistentData().entrySet()) {
            this.persistentData.put(persistentEntry.getKey().getDataKeyId(), persistentEntry.getValue());
        }
    }

    public int getMeta() {
        return this.meta;
    }

    public String getId() {
        return this.id;
    }

    public Map<String, Object> getPersistentData() {
        return this.persistentData;
    }

    @Override
    public Class<? extends Item> getCapableType() {
        return Item.class;
    }

    @Override
    public Item toPersistentCapable() {
        return this.repopulate(Item.of(this.id, this.meta));
    }

    protected Item repopulate(Item item) {
        item.setDisplayName(this.getTitle());
        item.setLore(this.getLore());
        item.setAmount(this.getAmount());
        for (Enchant enchantment : this.getEnchantments())
            item.addEnchant(enchantment);
        for (Entry<String, Object> persistentEntry : this.persistentData.entrySet())
            item.set(StoredPersistentKey.of(persistentEntry.getKey()), persistentEntry.getValue());
        return item;
    }

    public Text getTitle() {
        return this.title;
    }

    public List<Text> getLore() {
        return this.lore;
    }

    public int getAmount() {
        return this.amount;
    }

    public List<Enchant> getEnchantments() {
        return this.enchantments;
    }
}
