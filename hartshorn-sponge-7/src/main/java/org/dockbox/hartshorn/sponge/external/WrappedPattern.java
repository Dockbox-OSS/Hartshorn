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
import org.dockbox.hartshorn.worldedit.region.Pattern;
import org.jetbrains.annotations.NotNull;

public class WrappedPattern
        implements Pattern, Wrapper<com.sk89q.worldedit.function.pattern.Pattern> {

    private com.sk89q.worldedit.function.pattern.Pattern pattern;

    public WrappedPattern(com.sk89q.worldedit.function.pattern.Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.pattern.Pattern> getReference() {
        return Exceptional.of(this.pattern);
    }

    @Override
    public void setReference(
            @NotNull Exceptional<com.sk89q.worldedit.function.pattern.Pattern> reference) {
        reference.present(pattern -> this.pattern = pattern);
    }

    @Override
    public Exceptional<com.sk89q.worldedit.function.pattern.Pattern> constructInitialReference() {
        return Exceptional.none();
    }
}