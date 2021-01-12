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

import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Map;
import java.util.Optional;

public class SpongeItemDataManipulatorBuilder extends AbstractDataBuilder<MutableSpongeItemData> implements DataManipulatorBuilder<MutableSpongeItemData, ImmutableSpongeItemData> {

    public SpongeItemDataManipulatorBuilder() {
        super(MutableSpongeItemData.class, 1);
    }

    @Override
    public @NotNull MutableSpongeItemData create() {
        return new MutableSpongeItemData();
    }

    @Override
    public @NotNull Optional<MutableSpongeItemData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(MutableSpongeItemData.class).orElseGet(this::create));
    }

    @Override
    protected @NotNull Optional<MutableSpongeItemData> buildContent(DataView container) throws InvalidDataException {
        if(container.contains(SpongeItem.ITEM_KEY.getQuery())) {
            final Map<?, ?> unsafeData = container.getMap(SpongeItem.ITEM_KEY.getQuery()).get();
            Map<String, Object> safeData = SeleneUtils.emptyMap();
            unsafeData.forEach((k, v) -> safeData.put(k.toString(), v));

            MutableSpongeItemData data = this.create();
            data.fillData(safeData);
            return Optional.of(data);
        }
        return Optional.empty();
    }
}
