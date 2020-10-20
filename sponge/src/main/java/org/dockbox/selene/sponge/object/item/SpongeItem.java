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

package org.dockbox.selene.sponge.object.item;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeItem extends Item<ItemStack> {

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
        Optional<Text> name = ref.map(i -> i.get(Keys.DISPLAY_NAME)).get().map(SpongeConversionUtil::fromSponge);
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
            this.setId(this.getReference().get().getItem().getId());
        }
        return super.getId();
    }

    @Override
    public int getStackSize() {
        return this.getReference().map(ItemStack::getMaxStackQuantity).orElse(DEFAULT_STACK_SIZE);
    }

    @Override
    public Function<ItemStack, Optional<ItemStack>> getUpdateReferenceTask() {
        return Optional::ofNullable;
    }

    @Override
    public Class<?> getReferenceType() {
        return ItemStack.class;
    }
}
