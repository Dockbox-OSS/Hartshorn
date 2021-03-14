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

package org.dockbox.selene.sponge.objects.item.persistence;

import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.entity.Extract;
import org.dockbox.selene.api.annotations.entity.Extract.Behavior;
import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.objects.item.Enchant;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.item.persistence.PersistentItemModel;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.sponge.objects.item.SpongeItem;

import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "item")
public class SpongePersistentItemModel extends PersistentItemModel {

    private String id;
    private int meta;
    private Text title;
    private List<Text> lore;
    private int amount;
    private List<Enchant> enchantments;

    public SpongePersistentItemModel(SpongeItem item) {
        this.id = item.getId();
        this.meta = item.getMeta();
        this.title = item.getDisplayName();
        this.lore = item.getLore();
        this.amount = item.getAmount();
        this.enchantments = item.getEnchantments();
    }

    public int getMeta() {
        return meta;
    }

    public String getId() {
        return id;
    }

    public Text getTitle() {
        return title;
    }

    public List<Text> getLore() {
        return lore;
    }

    public int getAmount() {
        return amount;
    }

    public List<Enchant> getEnchantments() {
        return enchantments;
    }

    @Override
    public Class<? extends Item> getCapableType() {
        return SpongeItem.class;
    }

    @Override
    public Item toPersistentCapable() {
        return repopulate(Item.of(id, meta));
    }

    protected Item repopulate(Item item) {
        item.setDisplayName(this.getTitle());
        item.setLore(this.getLore());
        item.setAmount(this.getAmount());
        for (Enchant enchantment : this.getEnchantments()) item.addEnchant(enchantment);
        return item;
    }
}
