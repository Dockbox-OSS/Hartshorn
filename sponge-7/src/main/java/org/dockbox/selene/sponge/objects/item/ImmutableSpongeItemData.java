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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;

import java.util.Map;

public class ImmutableSpongeItemData extends AbstractImmutableData<ImmutableSpongeItemData, MutableSpongeItemData> {

    private final Map<String, Object> data = SeleneUtils.emptyMap();

    @Override
    public @NotNull MutableSpongeItemData asMutable() {
        MutableSpongeItemData data = new MutableSpongeItemData();
        data.fillData(this.data);
        return data;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public @NotNull DataContainer toContainer() {
        return super.toContainer().set(SpongeItem.ITEM_KEY, this.data);
    }

    @Override
    protected void registerGetters() {
        this.registerFieldGetter(SpongeItem.ITEM_KEY, () -> this.data);
        this.registerKeyValue(SpongeItem.ITEM_KEY, () ->
                Sponge.getRegistry().getValueFactory().createMapValue(
                        SpongeItem.ITEM_KEY,
                        this.data,
                        SeleneUtils.emptyMap()).asImmutable()
        );
    }

    public void fillData(Map<String, Object> data) {
        this.data.clear();
        this.data.putAll(data);
    }

}
