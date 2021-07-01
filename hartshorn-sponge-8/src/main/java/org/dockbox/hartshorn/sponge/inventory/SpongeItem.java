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

package org.dockbox.hartshorn.sponge.inventory;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.TransactionResult;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.item.Enchant;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ReferencedItem;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpongeItem extends ReferencedItem<ItemStack> {

    public SpongeItem(@NotNull ItemStack reference) {
        super(reference);
    }

    @Wired
    public SpongeItem(String id, int meta) {
        super(id, meta);
    }

    @Override
    public <T> Exceptional<T> get(PersistentDataKey<T> dataKey) {
        return null;
    }

    @Override
    public <T> TransactionResult set(PersistentDataKey<T> dataKey, T value) {
        return null;
    }

    @Override
    public <T> void remove(PersistentDataKey<T> dataKey) {

    }

    @Override
    public Map<PersistentDataKey<?>, Object> getPersistentData() {
        return null;
    }

    @Override
    public boolean isAir() {
        if (this.equals(MinecraftItems.getInstance().getAir())) return true;
        else {
            return this.getReference()
                    .map(itemStack -> itemStack.isEmpty() || itemStack.type() == ItemTypes.AIR.get())
                    .or(true);
        }
    }

    @Override
    public void setDisplayName(Text displayName) {
        this.getReference().present(i -> i.offer(Keys.DISPLAY_NAME, SpongeConvert.toSponge(displayName)));
    }

    @Override
    public Text getDisplayName(Language language) {
        return null;
    }

    @Override
    public List<Text> getLore() {
        return null;
    }

    @Override
    public void setLore(List<Text> lore) {

    }

    @Override
    public int getAmount() {
        return 0;
    }

    @Override
    public void setAmount(int amount) {

    }

    @Override
    public void removeDisplayName() {

    }

    @Override
    public void addLore(Text lore) {

    }

    @Override
    public void removeLore() {

    }

    @Override
    public int getStackSize() {
        return 0;
    }

    @Override
    public Set<Enchant> getEnchantments() {
        return null;
    }

    @Override
    public void addEnchant(Enchant enchant) {

    }

    @Override
    public void removeEnchant(Enchant enchant) {

    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public boolean isHead() {
        return false;
    }

    @Override
    public Item setProfile(Profile profile) {
        return null;
    }

    @Override
    public Item withMeta(int meta) {
        return null;
    }

    @Override
    public int getMeta() {
        return 0;
    }

    @Override
    public int getIdNumeric() {
        return 0;
    }

    @Override
    protected ItemStack getById(String id, int meta) {
        return null;
    }
}
