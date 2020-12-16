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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;

import java.util.Map;
import java.util.Optional;

public class MutableSpongeItemData extends AbstractData<MutableSpongeItemData, ImmutableSpongeItemData> {


    public MutableSpongeItemData() {
        this.registerGettersAndSetters();
    }

    private final Map<String, Object> data = SeleneUtils.emptyMap();

    @Override
    protected void registerGettersAndSetters() {
        this.registerFieldGetter(SpongeItem.ITEM_KEY, () -> this.data);
        this.registerFieldSetter(SpongeItem.ITEM_KEY, this::fillData);
        this.registerKeyValue(SpongeItem.ITEM_KEY, () ->
                Sponge.getRegistry().getValueFactory().createMapValue(SpongeItem.ITEM_KEY, this.data, SeleneUtils.emptyMap()));
    }

    @Override
    public @NotNull Optional<MutableSpongeItemData> fill(@NotNull DataHolder dataHolder, @NotNull MergeFunction overlap) {
        MutableSpongeItemData itemData = overlap.merge(this, dataHolder.get(MutableSpongeItemData.class).orElse(null));
        this.fillData(itemData.data);
        return Optional.of(this);
    }

    @Override
    public boolean supports(@NotNull Key<?> key) {
        return SpongeItem.ITEM_KEY == key;
    }

    @Override
    public @NotNull Optional<MutableSpongeItemData> from(@NotNull DataContainer container) {
        return Optional.of(this);
    }

    @Override
    public @NotNull MutableSpongeItemData copy() {
        MutableSpongeItemData itemData = new MutableSpongeItemData();
        itemData.fillData(this.data);
        return itemData;
    }

    @Override
    public @NotNull ImmutableSpongeItemData asImmutable() {
        ImmutableSpongeItemData data = new ImmutableSpongeItemData();
        data.fillData(this.data);
        return data;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public void fillData(Map<String, Object> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    @Override
    public @NotNull DataContainer toContainer() {
        return super.toContainer().set(SpongeItem.ITEM_KEY, this.data);
    }

    public Map<String, Object> getData() {
        return this.data;
    }
}
