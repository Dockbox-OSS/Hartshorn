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

package org.dockbox.selene.core.impl.server.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.dockbox.selene.core.annotations.entity.Metadata;
import org.dockbox.selene.core.annotations.module.Module;
import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.server.IntegratedModule;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.config.Environment;
import org.dockbox.selene.core.server.config.ExceptionLevels;
import org.dockbox.selene.core.server.config.GlobalConfig;
import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.Reflect;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Singleton
@Metadata(alias = "config")
public class SimpleGlobalConfig implements GlobalConfig, InjectableType
{

    @Inject
    private transient FileManager fileManager;
    @Inject
    private transient IntegratedModule integratedModule;

    private Language defaultLanguage = Language.EN_US;
    private boolean stacktracesAllowed = true;
    private boolean friendlyExceptions = true;
    private final Environment environment = Environment.DEVELOPMENT;
    private boolean isConstructed;

    @NotNull
    @Override
    public Language getDefaultLanguage()
    {
        return this.defaultLanguage;
    }

    @Override
    public boolean getStacktracesAllowed()
    {
        return this.stacktracesAllowed;
    }

    @NotNull
    @Override
    public ExceptionLevels getExceptionLevel()
    {
        if (null == Selene.getServer()) return ExceptionLevels.NATIVE;
        return this.friendlyExceptions ? ExceptionLevels.FRIENDLY : ExceptionLevels.MINIMAL;
    }

    @NotNull
    @Override
    public Environment getEnvironment()
    {
        return this.environment;
    }

    @Override
    public boolean canEnable()
    {
        return !this.isConstructed;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties)
    {
        Module module = Reflect.getModule(this.integratedModule.getClass());
        if (null == module)
        {
            throw new IllegalStateException("Integrated module not annotated as such.");
        }

        Path configPath = this.fileManager.getConfigFile(module);
        GlobalConfig globalConfig = this.fileManager
                .read(configPath, SimpleGlobalConfig.class)
                .orNull();
        this.copyValues(globalConfig);
    }

    private void copyValues(GlobalConfig config)
    {
        if (null != config)
        {
            this.defaultLanguage = config.getDefaultLanguage();
            this.friendlyExceptions = ExceptionLevels.FRIENDLY == config.getExceptionLevel();
            this.stacktracesAllowed = config.getStacktracesAllowed();
        }
        this.isConstructed = true;
    }
}
