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

package org.dockbox.selene.dave.models;

import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.dave.DaveUtils;
import org.jetbrains.annotations.NonNls;

import java.util.List;

@Metadata(alias = "dave-triggers")
public class DaveTriggers {

    private final List<DaveTrigger> triggers = SeleneUtils.emptyList();

    public DaveTriggers() {}

    public Exceptional<DaveTrigger> getMatchingTrigger(String message) {
        return DaveUtils.findMatching(this, message);
    }

    public void addTrigger(DaveTrigger trigger) {
        this.triggers.add(trigger);
    }

    public Exceptional<DaveTrigger> findById(@NonNls String id) {
        for (DaveTrigger trigger : this.triggers) {
            if (trigger.getId().equals(id)) return Exceptional.of(trigger);
        }
        return Exceptional.empty();
    }

    public List<DaveTrigger> getTriggers() {
        return this.triggers;
    }
}
