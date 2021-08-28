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

package org.dockbox.hartshorn.dave.models;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.dave.DaveUtils;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.util.List;

import lombok.Getter;

public class DaveTriggers {

    @Getter private final List<DaveTrigger> triggers = HartshornUtils.emptyList();

    public Exceptional<DaveTrigger> matching(final String message) {
        return DaveUtils.findMatching(this, message);
    }

    public void add(final DaveTrigger trigger) {
        this.triggers.add(trigger);
    }

    public Exceptional<DaveTrigger> find(@NonNls final String id) {
        for (final DaveTrigger trigger : this.triggers) {
            if (trigger.id().equals(id)) return Exceptional.of(trigger);
        }
        return Exceptional.empty();
    }
}
