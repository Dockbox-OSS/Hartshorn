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

package org.dockbox.hartshorn.sponge.external;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.util.Wrapper;
import org.dockbox.hartshorn.worldedit.region.Mask;
import org.jetbrains.annotations.NotNull;

public class WrappedMask implements Mask, Wrapper<com.sk89q.worldedit.function.mask.Mask> {

    private com.sk89q.worldedit.function.mask.Mask mask;

    public WrappedMask(com.sk89q.worldedit.function.mask.Mask mask) {
        this.mask = mask;
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.mask.Mask> getReference() {
        return Exceptional.of(this.mask);
    }

    @Override
    public void setReference(@NotNull Exceptional<com.sk89q.worldedit.function.mask.Mask> reference) {
        reference.present(mask -> this.mask = mask);
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.mask.Mask> constructInitialReference() {
        return Exceptional.none();
    }
}
