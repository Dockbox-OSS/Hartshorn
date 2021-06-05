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

package org.dockbox.hartshorn.worldmanagement;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.persistence.FileManager;

import java.util.List;

import javax.inject.Inject;

public class WorldManagementConfig {

    @Inject
    private transient FileManager fileManager;

    @Value(value = "services.world-management.target.position.x", or = "0")
    private long x;
    @Value(value = "services.world-management.target.position.y", or = "64")
    private long y;
    @Value(value = "services.world-management.target.position.z", or = "0")
    private long z;

    @Value(value = "services.world-management.target.world", or = "worlds")
    private String portalWorldTarget;

    @Value(value = "services.world-management.unload.max", or = "10")
    private int maximumWorldsToUnload;

    @Value("services.world-management.unload.blacklist")
    private List<String> unloadBlacklist;

    @Value(value = "services.world-management.unload.delay", or = "2")
    private int unloadDelay;

    public Vector3N getPortalPosition() {
        return Vector3N.of(this.x, this.y, this.z);
    }

    public String getPortalWorldTarget() {
        return this.portalWorldTarget;
    }

    public int getMaximumWorldsToUnload() {
        return this.maximumWorldsToUnload;
    }

    public List<String> getUnloadBlacklist() {
        return this.unloadBlacklist;
    }

    public int getUnloadDelay() {
        return this.unloadDelay;
    }
}
