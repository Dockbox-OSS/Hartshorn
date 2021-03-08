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

package org.dockbox.selene.sponge.objects.composite;

import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Map;
import java.util.Optional;

public class CompositeDataManipulatorBuilder extends AbstractDataBuilder<MutableCompositeData>
        implements DataManipulatorBuilder<MutableCompositeData, ImmutableCompositeData> {

    public CompositeDataManipulatorBuilder() {
        super(MutableCompositeData.class, 1);
    }

    @Override
    protected @NotNull Optional<MutableCompositeData> buildContent(DataView container)
            throws InvalidDataException {
        if (container.contains(Composite.ITEM_KEY.getQuery())) {
            @SuppressWarnings("OptionalGetWithoutIsPresent") final Map<?, ?> unsafeData = container.getMap(Composite.ITEM_KEY.getQuery()).get();
            Map<String, Object> safeData = SeleneUtils.emptyMap();
            unsafeData.forEach((k, v) -> safeData.put(k.toString(), v));

            MutableCompositeData data = this.create();
            data.fillData(safeData);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    @Override
    public @NotNull MutableCompositeData create() {
        return new MutableCompositeData();
    }

    @Override
    public @NotNull Optional<MutableCompositeData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(MutableCompositeData.class).orElseGet(this::create));
    }
}
