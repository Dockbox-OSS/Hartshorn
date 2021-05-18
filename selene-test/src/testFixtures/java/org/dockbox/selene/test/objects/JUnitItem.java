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

package org.dockbox.selene.test.objects;

import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.server.minecraft.item.Enchant;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.SimplePersistentItemModel;
import org.dockbox.selene.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.selene.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class JUnitItem implements Item, JUnitPersistentDataHolder {

    @Getter
    private final String id;
    @Getter
    private final int meta;
    private final UUID persistentDataId = UUID.randomUUID();
    private final Set<Enchant> enchants = SeleneUtils.emptySet();
    @Getter
    private final List<Text> lore = SeleneUtils.emptyList();

    @Getter
    private Profile profile;
    @Getter @Setter
    private int amount = 1;
    @Getter @Setter
    private Text displayName;
    private boolean treatAsBlock = false;

    @AutoWired
    public JUnitItem(String id, int meta) {
        this.id = id;
        this.meta = meta;
        this.displayName = Text.of(id);
    }

    @Override
    public boolean isAir() {
        return this.equals(MinecraftItems.getInstance().getAir());
    }

    @Override
    public Text getDisplayName(Language language) {
        return this.displayName;
    }

    @Override
    public void setLore(List<Text> lore) {
        this.lore.clear();
        this.lore.addAll(lore);
    }

    @Override
    public void removeDisplayName() {
        this.displayName = Text.of(this.getId());
    }

    @Override
    public void addLore(Text lore) {
        this.lore.add(lore);
    }

    @Override
    public void removeLore() {
        this.lore.clear();
    }

    @Override
    public Set<Enchant> getEnchantments() {
        return SeleneUtils.asUnmodifiableSet(this.enchants);
    }

    @Override
    public void addEnchant(Enchant enchant) {
        this.enchants.add(enchant);
    }

    @Override
    public void removeEnchant(Enchant enchant) {
        this.enchants.remove(enchant);
    }

    @Override
    public boolean isBlock() {
        return this.treatAsBlock;
    }

    public Item treatAsBlock() {
        this.treatAsBlock = true;
        return this;
    }

    @Override
    public Item setProfile(Profile profile) {
        if (this.isHead()) {
            this.profile = profile;
        }
        return this;
    }

    @Override
    public boolean isHead() {
        return MinecraftItems.getInstance().getSkeletonSkull().getId().equals(this.getId());
    }

    @Override
    public Item stack() {
        this.setAmount(this.getStackSize());
        return this;
    }

    @Override
    public int getStackSize() {
        return 64;
    }

    @Override
    public Item withMeta(int meta) {
        return new JUnitItem(this.id, meta);
    }

    @Override
    public int getIdNumeric() {
        return -1;
    }

    @Override
    public UUID getUniqueId() {
        return this.persistentDataId;
    }

    @Override
    public String getName() {
        return this.getDisplayName().toString();
    }

    @Override
    public Class<? extends PersistentItemModel> getModelClass() {
        return SimplePersistentItemModel.class;
    }

    @Override
    public PersistentItemModel toPersistentModel() {
        return new SimplePersistentItemModel(this);
    }
}
