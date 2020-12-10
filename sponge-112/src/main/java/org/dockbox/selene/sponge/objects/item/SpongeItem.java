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

package org.dockbox.selene.sponge.objects.item;

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Enchant;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.keys.PersistentDataKey;
import org.dockbox.selene.core.objects.keys.TransactionResult;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeItem extends Item<ItemStack> {


    public static final String ID = "item_data";
    public static final String NAME = "Selene Item Data";
    public static final String QUERY = "SeleneItemData";

    public static Key<MapValue<String, Object>> ITEM_KEY;

    public static final int DEFAULT_STACK_SIZE = 64;

    public SpongeItem(@NotNull ItemStack initialValue) {
        super(initialValue);
    }

    public SpongeItem(String id) {
        super(id, 1);
    }

    public SpongeItem(String id, int amount) {
        super(id, amount);
    }

    @Override
    protected ItemStack getById(String id, int amount) {
        if (this.isNumericalId(id)) {
            Selene.log().warn("The usage of numerical ID's is not recommended! This will be removed as of 1.13");
        }

        ItemStack itemStack = Sponge.getGame().getRegistry()
                .getType(ItemType.class, id)
                .map(it -> ItemStack.of(it, amount))
                .orElse(ItemStack.empty());

        return itemStack;
    }

    private boolean isNumericalId(String id) {
        return id.matches("[0-9|:]+");
    }

    @Override
    public Text getDisplayName(Language language) {
        Exceptional<ItemStack> ref = this.getReference();
        Exceptional<Text> name = Exceptional.of(ref.map(i -> i.get(Keys.DISPLAY_NAME)).get()).map(SpongeConversionUtil::fromSponge);
        if (name.isPresent()) return name.get();

        Exceptional<String> translatedName = ref.map(i -> i.getTranslation().get());
        if (translatedName.isPresent()) return Text.of(translatedName.get());

        return Text.of(ref.map(i -> i.getItem().getId()).orElse(IntegratedResource.UNKNOWN.getValue(language)));
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
    public Class<?> getReferenceType() {
        return ItemStack.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Exceptional<T> get(PersistentDataKey<T> dataKey) {
        Exceptional<MutableSpongeItemData> result = this.getReference()
                .map(itemStack -> itemStack.get(MutableSpongeItemData.class).orElse(null));

        if (result.isAbsent()) return Exceptional.empty();

        MutableSpongeItemData data = result.get();
        if (!data.getData().containsKey(dataKey.getDataKeyId())) return Exceptional.empty();

        Object value = data.getData().get(dataKey.getDataKeyId());
        if (SeleneUtils.isAssignableFrom(dataKey.getDataType(), value.getClass()))
            return Exceptional.of((T) value);

        return Exceptional.empty();
    }

    @Override
    public <T> TransactionResult set(PersistentDataKey<T> dataKey, T value) {
        return this.getReference().map(itemStack -> {
            Map<String, Object> data = itemStack.get(MutableSpongeItemData.class).orElse(new MutableSpongeItemData()).getData();
            data.put(dataKey.getDataKeyId(), value);

            MutableSpongeItemData spongeItemData = new MutableSpongeItemData();
            spongeItemData.fillData(data);
            DataTransactionResult result = itemStack.offer(spongeItemData);
            if (result.isSuccessful()) return TransactionResult.success();
            else return TransactionResult.fail("Could not apply key to this item");
        }).orElseGet(() -> TransactionResult.fail("Item reference lost"));
    }

    @Override
    public <T> void remove(PersistentDataKey<T> dataKey) {
        this.getReference().ifPresent(itemStack -> {
            Optional<MutableSpongeItemData> result = itemStack.get(MutableSpongeItemData.class);
            if (!result.isPresent()) return; // No data to remove

            MutableSpongeItemData data = result.get();
            if (!data.getData().containsKey(dataKey.getDataKeyId())) return; // Already removed

            data.getData().remove(dataKey.getDataKeyId());

            itemStack.offer(data);
        });
    }
}
