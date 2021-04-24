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

package org.dockbox.selene.playeractions;

import org.dockbox.selene.api.entity.annotations.Accessor;
import org.dockbox.selene.api.entity.annotations.Extract;
import org.dockbox.selene.api.entity.annotations.Extract.Behavior;
import org.dockbox.selene.persistence.AbstractConfiguration;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;

import javax.inject.Singleton;

@Singleton
@Extract(Behavior.KEEP)
@SuppressWarnings("FieldMayBeFinal")
public class PlayerActionConfiguration extends AbstractConfiguration<PlayerActionConfiguration> {

    @Accessor(getter = "getTeleportWhitelist")
    private List<String> teleportWhitelist = SeleneUtils.emptyList();

    public List<String> getTeleportWhitelist() {
        return this.teleportWhitelist;
    }

    @Override
    protected Class<?> getOwnerType() {
        return PlayerActions.class;
    }
}


