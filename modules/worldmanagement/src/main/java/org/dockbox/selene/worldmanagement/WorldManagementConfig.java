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

package org.dockbox.selene.worldmanagement;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.entity.Accessor;
import org.dockbox.selene.api.annotations.entity.Extract;
import org.dockbox.selene.api.annotations.entity.Extract.Behavior;
import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "worldmanagement")
public class WorldManagementConfig implements InjectableType {

    @Inject
    @Extract(Behavior.SKIP)
    private transient FileManager fileManager;

    @Extract(Behavior.SKIP)
    private transient boolean isConstructed;

    @Accessor(getter = "getPortalPosition")
    private Vector3N portalPosition = new Vector3N(0, 64, 0);

    @Accessor(getter = "getPortalWorldTarget")
    private String portalWorldTarget = "worlds";

    @Accessor(getter = "getMaximumWorldsToUnload")
    private int maximumWorldsToUnload = 10;

    @Accessor(getter = "getUnloadBlacklist")
    private List<String> unloadBlacklist = new ArrayList<>();

    @Accessor(getter = "getUnloadDelay")
    private int unloadDelay = 2;

    public Vector3N getPortalPosition() {
        return portalPosition;
    }

    public String getPortalWorldTarget() {
        return portalWorldTarget;
    }

    public int getMaximumWorldsToUnload() {
        return maximumWorldsToUnload;
    }

    public List<String> getUnloadBlacklist() {
        return unloadBlacklist;
    }


    public int getUnloadDelay() {
        return unloadDelay;
    }

    @Override
    public boolean canEnable() {
        return !this.isConstructed;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Path configPath = this.fileManager.getConfigFile(Reflect.getModule(WorldManagement.class));
        WorldManagementConfig config = this.fileManager.read(configPath, WorldManagementConfig.class).orElse(this);
        SeleneUtils.shallowCopy(config, this);
        this.isConstructed = true;
    }
}
