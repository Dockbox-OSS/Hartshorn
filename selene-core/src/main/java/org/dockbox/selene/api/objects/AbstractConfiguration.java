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

package org.dockbox.selene.api.objects;

import com.google.inject.Inject;

import org.dockbox.selene.api.annotations.entity.Extract;
import org.dockbox.selene.api.annotations.entity.Extract.Behavior;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;

import java.nio.file.Path;

public abstract class AbstractConfiguration<C extends AbstractConfiguration<C>> implements InjectableType {

    @Inject
    @Extract(Behavior.SKIP)
    private FileManager fileManager;

    @Extract(Behavior.SKIP)
    private boolean isConstructed;

    @Override
    public boolean canEnable() {
        return !this.isConstructed;
    }

    public Exceptional<Boolean> save() {
        return this.fileManager.write(this.getConfigFile(), this);
    }

    protected void transferOrReuse() {
        ModuleContainer module = Reflect.module(this.getModuleClass());
        if (null == module) {
            throw new IllegalArgumentException("Provided module not annotated as such.");
        }

        Path configPath = this.getConfigFile();
        @SuppressWarnings("unchecked") C config = (C) this.fileManager.read(configPath, this.getClass()).orNull();
        SeleneUtils.shallowCopy(config, this);
        this.isConstructed = true;
    }

    protected Path getConfigFile() {
        return this.fileManager.getConfigFile(this.getModuleClass());
    }

    protected abstract Class<?> getModuleClass();

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        this.transferOrReuse();
    }
}
