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

package org.dockbox.selene.persistence;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.entity.annotations.Extract;
import org.dockbox.selene.api.entity.annotations.Extract.Behavior;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;

import java.nio.file.Path;

import javax.inject.Inject;

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
        Selene.log().info("Transferred configuration for type " + this.getClass().getSimpleName());
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
