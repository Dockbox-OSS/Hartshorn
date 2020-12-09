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

import com.google.common.reflect.TypeToken;

import org.dockbox.selene.core.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Map;
import java.util.Optional;

public class SpongeItemData extends AbstractData<SpongeItemData, SpongeItemData> implements ImmutableDataManipulator<SpongeItemData, SpongeItemData> {

    public static final Key<MapValue<String, Object>> ITEM_KEY = Key.builder()
            .type(new TypeToken<MapValue<String, Object>>() {
            })
            .query(DataQuery.of("SeleneItemData"))
            .id("selene:item_data")
            .name("Selene Item Data")
            .build();

    private final Map<String, Object> data = SeleneUtils.emptyConcurrentMap();

    @Override
    protected void registerGettersAndSetters() {
        this.registerFieldGetter(ITEM_KEY, () -> this.data);
        this.registerFieldSetter(ITEM_KEY, this::fillData);
        this.registerKeyValue(ITEM_KEY, () ->
                Sponge.getRegistry().getValueFactory().createMapValue(ITEM_KEY, this.data));
    }

    @Override
    public @NotNull Optional<SpongeItemData> fill(@NotNull DataHolder dataHolder, @NotNull MergeFunction overlap) {
        SpongeItemData itemData = overlap.merge(this, dataHolder.get(SpongeItemData.class).orElse(null));
        this.fillData(itemData.data);
        return Optional.of(this);
    }

    @Override
    public @NotNull Optional<SpongeItemData> from(@NotNull DataContainer container) {
        return Optional.of(this);
    }

    @Override
    public @NotNull SpongeItemData copy() {
        SpongeItemData itemData = new SpongeItemData();
        itemData.fillData(this.data);
        return itemData;
    }

    @Override
    public @NotNull SpongeItemData asMutable() {
        return this;
    }

    @Override
    public @NotNull SpongeItemData asImmutable() {
        return this;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    private void fillData(Map<String, Object> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    @Override
    public @NotNull DataContainer toContainer() {
        return super.toContainer().set(ITEM_KEY, this.data);
    }
}
