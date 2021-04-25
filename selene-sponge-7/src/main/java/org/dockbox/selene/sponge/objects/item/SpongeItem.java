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

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extension.input.ParserContext;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.keys.PersistentDataKey;
import org.dockbox.selene.api.keys.TransactionResult;
import org.dockbox.selene.di.Bindings;
import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.server.minecraft.item.Enchant;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.ReferencedItem;
import org.dockbox.selene.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.selene.server.minecraft.players.Profile;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.objects.composite.SpongeComposite;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.dockbox.selene.sponge.util.SpongeWorldEditService;
import org.dockbox.selene.util.SeleneUtils;
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
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeItem extends ReferencedItem<ItemStack> implements SpongeComposite {

    public SpongeItem(@NotNull ItemStack initialValue) {
        super(initialValue);
    }

    SpongeItem() {
        super(null);
        throw Bindings.requireAutowiring();
    }

    @AutoWired
    public SpongeItem(String id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean isAir() {
        if (this.equals(MinecraftItems.getInstance().getAir())) return true;
        else {
            return this.getReference()
                    .map(itemStack -> itemStack.isEmpty() || itemStack.getType().getType() == ItemTypes.AIR)
                    .or(true);
        }
    }

    @Override
    public void setDisplayName(Text displayName) {
        this.getReference().present(i -> i.offer(Keys.DISPLAY_NAME, SpongeConversionUtil.toSponge(displayName)));
    }

    @Override
    public Text getDisplayName(Language language) {
        Exceptional<ItemStack> ref = this.getReference();
        Exceptional<Text> name = Exceptional.of(ref.map(i -> i.get(Keys.DISPLAY_NAME)).get())
                .map(SpongeConversionUtil::fromSponge);
        if (name.present()) return name.get();

        Exceptional<String> translatedName = ref.map(i -> i.getTranslation().get());
        if (translatedName.present()) return Text.of(translatedName.get());

        return Text.of(ref.map(i -> i.getType().getId())
                .or(DefaultResource.UNKNOWN.translate(language).asString()));
    }

    @Override
    public List<Text> getLore() {
        List<org.spongepowered.api.text.Text> sl = this.getReference().map(i -> i.get(Keys.ITEM_LORE)).get().orElseGet(ArrayList::new);
        return sl.stream().map(SpongeConversionUtil::fromSponge).collect(Collectors.toList());
    }

    @Override
    public void setLore(List<Text> lore) {
        this.getReference().present(i ->
                i.offer(Keys.ITEM_LORE, lore.stream()
                        .map(SpongeConversionUtil::toSponge)
                        .collect(Collectors.toList()))
        );
    }

    @Override
    public int getAmount() {
        return this.getReference().map(ItemStack::getQuantity).or(1);
    }

    @Override
    public void setAmount(int amount) {
        this.getReference().present(i -> i.setQuantity(amount));
    }

    @Override
    public void removeDisplayName() {
        this.getReference().present(i -> i.remove(DisplayNameData.class));
    }

    @Override
    public void addLore(Text lore) {
        List<Text> existing = this.getLore();
        existing.add(lore);
        this.setLore(existing);
    }

    @Override
    public void removeLore() {
        this.getReference().present(i -> i.remove(LoreData.class));
    }

    @Override
    public int getStackSize() {
        return this.getReference().map(ItemStack::getMaxStackQuantity).or(DEFAULT_STACK_SIZE);
    }

    @Override
    public Set<Enchant> getEnchantments() {
        List<org.spongepowered.api.item.enchantment.Enchantment> enchantments =
                this.getReference()
                        .map(i -> i.get(Keys.ITEM_ENCHANTMENTS).orElse(SeleneUtils.emptyList()))
                        .or(SeleneUtils.emptyList());
        return enchantments.stream()
                .map(SpongeConversionUtil::fromSponge)
                .filter(Exceptional::present)
                .map(Exceptional::get)
                .collect(Collectors.toSet());
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
                .or(false);
    }

    @Override
    public boolean isHead() {
        return this.getReference()
                .map(itemStack -> itemStack.getType().getType() == ItemTypes.SKULL)
                .or(false);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public Item setProfile(Profile profile) {
        if (this.isHead() && profile instanceof SpongeProfile) {
            this.getReference().present(itemStack -> {
                SkullData skullData =
                        Sponge.getGame().getDataManager()
                                .getManipulatorBuilder(SkullData.class)
                                .get()
                                .create()
                                .set(Keys.SKULL_TYPE, SkullTypes.PLAYER);
                itemStack.offer(skullData);

                RepresentedPlayerData representedPlayerData =
                        Sponge.getGame().getDataManager()
                                .getManipulatorBuilder(RepresentedPlayerData.class)
                                .get()
                                .create()
                                .set(Keys.REPRESENTED_PLAYER, ((SpongeProfile) profile).getGameProfile());
                itemStack.offer(representedPlayerData);
            });
        }
        return this;
    }

    @Override
    public Item withMeta(int meta) {
        //noinspection deprecation
        return Item.of(SpongeItem.this.getId(), meta);
    }

    @Override
    public String getId() {
        if (this.referenceExists()) {
            this.setId(this.getReference().get().getType().getId());
        }
        return super.getId();
    }

    @Override
    protected ItemStack getById(String id, int meta) {
        ItemStack stack = Sponge.getGame()
                .getRegistry()
                .getType(ItemType.class, id)
                .map(ItemStack::of)
                .orElse(ItemStack.empty());

        stack = ItemStack.builder()
                .fromContainer(stack.toContainer().set(Constants.ItemStack.DAMAGE_VALUE, meta))
                .build();

        return stack;
    }

    @Override
    public int getMeta() {
        return (int) this.getReference()
                .map(stack -> stack.toContainer().get(Constants.ItemStack.DAMAGE_VALUE).orElse(0))
                .or(0);
    }

    @Override
    public int getIdNumeric() {
        // ID is typically used for WorldEdit hooks, to ensure this is consistent we delegate this to WorldEdit directly.
        // By providing null to the context, the console actor will be used to prepare context.
        ParserContext context = SpongeWorldEditService.prepareContext(null);
        return SpongeConversionUtil.toWorldEdit(this, context).map(BaseBlock::getId).or(0);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void performOnEnchantmentData(
            Enchant enchant, BiConsumer<EnchantmentData, Enchantment> action) {
        this.getReference().present(itemStack -> {
            EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class).get();
            @NotNull
            Exceptional<org.spongepowered.api.item.enchantment.Enchantment> enchantment = SpongeConversionUtil.toSponge(enchant);
            enchantment.present(e -> action.accept(enchantmentData, e));
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (obj instanceof SpongeItem) {
            SpongeItem that = (SpongeItem) obj;
            Exceptional<ItemStack> thisReference = this.getReference();
            Exceptional<ItemStack> thatReference = that.getReference();
            if (thisReference.absent() || thatReference.absent()) return false;
            ItemStack thisStack = thisReference.get().copy();
            ItemStack thatStack = thatReference.get().copy();
            thatStack.setQuantity(1);
            thisStack.setQuantity(1);
            return thisStack.equalTo(thatStack);
        }
        return false;
    }

    @Override
    public Function<ItemStack, Exceptional<ItemStack>> getUpdateReferenceTask() {
        return Exceptional::of;
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
}
