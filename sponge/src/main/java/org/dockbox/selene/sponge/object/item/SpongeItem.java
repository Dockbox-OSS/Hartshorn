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

import org.dockbox.selene.core.objects.ReferenceHolder;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongeItem extends ReferenceHolder<ItemStack> implements Item {

    public SpongeItem(ItemStack initialValue) {
        super(Optional.ofNullable(initialValue));
    }

    public SpongeItem(String id) {
        this(id, 1);
    }

    public SpongeItem(String id, int amount) {
        this(Sponge.getRegistry()
                .getType(ItemType.class, id.replaceFirst("minecraft:", ""))
                .map(it -> ItemStack.of(it, amount))
                .orElse(ItemStack.of(ItemTypes.AIR, amount))
        );
    }

    @Override
    public Text getDisplayName() {
        Optional<org.spongepowered.api.text.Text> dno = this.getReference().map(i -> i.get(Keys.DISPLAY_NAME)).get();
        return dno.map(SpongeConversionUtil::fromSponge).orElseGet(Text::of);
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
    public Function<ItemStack, Optional<ItemStack>> getUpdateReferenceTask() {
        return Optional::ofNullable;
    }
}
