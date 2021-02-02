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

package org.dockbox.selene.sponge.objects.item;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.impl.objects.item.ReferencedItem;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Enchant;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.TransactionResult;
import org.dockbox.selene.core.objects.profile.Profile;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.objects.composite.SpongeComposite;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.SkullData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeItem extends ReferencedItem<ItemStack> implements SpongeComposite {

    public static final int DEFAULT_STACK_SIZE = 64;

    public SpongeItem(@NotNull ItemStack initialValue) {
        super(initialValue);
    }

    @AssistedInject
    public SpongeItem(@Assisted String id, @Assisted int meta) {
        super(id, meta);
    }

    @Override
    protected ItemStack getById(String id, int meta) {
        ItemStack stack = Sponge.getGame().getRegistry()
                .getType(ItemType.class, id)
                .map(ItemStack::of)
                .orElse(ItemStack.empty());

        stack = ItemStack.builder()
                .fromContainer(stack.toContainer().set(Constants.ItemStack.DAMAGE_VALUE, meta))
                .build();

        return stack;
    }

    @Override
    public Text getDisplayName(Language language) {
        Exceptional<ItemStack> ref = this.getReference();
        Exceptional<Text> name = Exceptional.of(ref.map(i -> i.get(Keys.DISPLAY_NAME)).get()).map(SpongeConversionUtil::fromSponge);
        if (name.isPresent()) return name.get();

        Exceptional<String> translatedName = ref.map(i -> i.getTranslation().get());
        if (translatedName.isPresent()) return Text.of(translatedName.get());

        return Text.of(ref.map(i -> i.getType().getId()).orElse(IntegratedResource.UNKNOWN.translate(language).asString()));
    }

    @Override
    public List<Text> getLore() {
        List<org.spongepowered.api.text.Text> sl = this.getReference().map(i -> i.get(Keys.ITEM_LORE)).get().orElseGet(ArrayList::new);
        return sl.stream().map(SpongeConversionUtil::fromSponge).collect(Collectors.toList());
    }

    @Override
    public int getAmount() {
        return this.getReference().map(ItemStack::getQuantity).orElse(1);
    }

    @Override
    public void setDisplayName(Text displayName) {
        this.getReference().ifPresent(i -> i.offer(Keys.DISPLAY_NAME, SpongeConversionUtil.toSponge(displayName)));
    }

    @Override
    public void removeDisplayName() {
        this.getReference().ifPresent(i -> i.remove(DisplayNameData.class));
    }

    @Override
    public void setLore(List<Text> lore) {
        this.getReference().ifPresent(i -> i.offer(Keys.ITEM_LORE, lore.stream().map(SpongeConversionUtil::toSponge).collect(Collectors.toList())));
    }

    @Override
    public void addLore(Text lore) {
        List<Text> existing = this.getLore();
        existing.add(lore);
        this.setLore(existing);
    }

    @Override
    public void removeLore() {
        this.getReference().ifPresent(i -> i.remove(LoreData.class));
    }

    @Override
    public void setAmount(int amount) {
        this.getReference().ifPresent(i -> i.setQuantity(amount));
    }

    @Override
    public String getId() {
        if (this.referenceExists()) {
            this.setId(this.getReference().get().getType().getId());
        }
        return super.getId();
    }

    @Override
    public int getStackSize() {
        return this.getReference().map(ItemStack::getMaxStackQuantity).orElse(DEFAULT_STACK_SIZE);
    }

    @Override
    public List<Enchant> getEnchantments() {
        List<org.spongepowered.api.item.enchantment.Enchantment> enchantments = this.getReference()
                .map(i -> i.get(Keys.ITEM_ENCHANTMENTS).orElse(SeleneUtils.emptyList()))
                .orElse(SeleneUtils.emptyList());
        return enchantments.stream().map(SpongeConversionUtil::fromSponge).filter(Exceptional::isPresent).map(Exceptional::get).collect(Collectors.toList());
    }

    @Override
    public void addEnchant(Enchant enchant) {
        this.performOnEnchantmentData(enchant, (EnchantmentData::addElement));
    }

    @Override
    public void removeEnchant(Enchant enchant) {
        this.performOnEnchantmentData(enchant, (EnchantmentData::remove));
    }

    @Override
    public boolean isBlock() {
        return this.getReference()
                .map(itemStack -> itemStack.getType().getBlock().isPresent())
                .orElse(false);
    }

    @Override
    public boolean isHead() {
        return this.getReference()
                .map(itemStack -> itemStack.getType().getType() == ItemTypes.SKULL)
                .orElse(false);
    }

    @Override
    public boolean isAir() {
        if (this.equals(Selene.getItems().getAir())) return true;
        else {
            return this.getReference()
                    .map(itemStack -> itemStack.isEmpty() || itemStack.getType().getType() == ItemTypes.AIR)
                    .orElse(true);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public Item setProfile(Profile profile) {
        if (this.isHead() && profile instanceof SpongeProfile) {
            this.getReference().ifPresent(itemStack -> {
                SkullData skullData = Sponge.getGame()
                        .getDataManager()
                        .getManipulatorBuilder(SkullData.class)
                        .get().create()
                        .set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                itemStack.offer(skullData);

                RepresentedPlayerData representedPlayerData = Sponge.getGame()
                        .getDataManager()
                        .getManipulatorBuilder(RepresentedPlayerData.class)
                        .get().create()
                        .set(Keys.REPRESENTED_PLAYER, ((SpongeProfile) profile).getGameProfile());
                itemStack.offer(representedPlayerData);
            });
        }
        return this;
    }

    @Override
    public Item withMeta(int meta) {
        return Item.of(SpongeItem.this.getId(), meta);
    }

    @Override
    public int getMeta() {
        return (int) this.getReference()
                .map(stack -> stack.toContainer()
                        .get(Constants.ItemStack.DAMAGE_VALUE)
                        .orElse(0)
                ).orElse(0);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void performOnEnchantmentData(Enchant enchant, BiConsumer<EnchantmentData, Enchantment> action) {
        this.getReference().ifPresent(itemStack -> {
            EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class).get();
            @NotNull Exceptional<org.spongepowered.api.item.enchantment.Enchantment> enchantment =
                    SpongeConversionUtil.toSponge(enchant);
            enchantment.ifPresent(e -> action.accept(enchantmentData, e));
        });
    }

    @Override
    public Function<ItemStack, Exceptional<ItemStack>> getUpdateReferenceTask() {
        return Exceptional::ofNullable;
    }

    @Override
    public <T> Exceptional<T> get(PersistentDataKey<T> dataKey) {
        return SpongeComposite.super.get(dataKey);
    }

    @Override
    public <T> TransactionResult set(PersistentDataKey<T> dataKey, T value) {
        return SpongeComposite.super.set(dataKey, value);
    }

    @Override
    public <T> void remove(PersistentDataKey<T> dataKey) {
        SpongeComposite.super.remove(dataKey);
    }

    @Override
    public Exceptional<? extends DataHolder> getDataHolder() {
        return this.getReference();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (obj instanceof SpongeItem) {
            SpongeItem that = (SpongeItem) obj;
            Exceptional<ItemStack> thisReference = this.getReference();
            Exceptional<ItemStack> thatReference = that.getReference();
            if (thisReference.isAbsent() || thatReference.isAbsent()) return false;
            ItemStack thisStack = thisReference.get().copy();
            ItemStack thatStack = thatReference.get().copy();
            thatStack.setQuantity(1);
            thisStack.setQuantity(1);
            return thisStack.equalTo(thatStack);
        }
        return false;
    }
}
